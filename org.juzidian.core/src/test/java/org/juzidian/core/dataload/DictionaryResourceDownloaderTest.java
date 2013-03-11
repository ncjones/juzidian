/*
 * Copyright Nathan Jones 2013
 *
 * This file is part of Juzidian.
 *
 * Juzidian is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Juzidian is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Juzidian.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.juzidian.core.dataload;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPOutputStream;

import org.junit.Before;
import org.junit.Test;
import org.juzidian.util.HexUtil;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class DictionaryResourceDownloaderTest {

	private DictionaryResourceDownloader downloader;

	private DownloadProgressHandler mockProgressHandler;

	private DictionaryResource mockResource;

	@Before
	public void setUp() {
		this.downloader = new DictionaryResourceDownloader();
		this.mockProgressHandler = Mockito.mock(DownloadProgressHandler.class);
	}

	private static byte[] gzip(final String content) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final GZIPOutputStream gzos = new GZIPOutputStream(baos);
		gzos.write(content.getBytes());
		gzos.close();
		return baos.toByteArray();
	}

	private static String sha1(final byte[] gzipContent) throws NoSuchAlgorithmException {
		final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
		messageDigest.update(gzipContent);
		return HexUtil.bytesToHex(messageDigest.digest());
	}

	private void initMockDictionaryResource(final String content) throws Exception {
		final byte[] gzipContent = gzip(content);
		final String sha1 = sha1(gzipContent);
		MockUrlHandler.delegate = Mockito.mock(MockUrlHandler.class);
		when(MockUrlHandler.delegate.openConnection(Matchers.any(URL.class))).thenReturn(new MockUrlConnection(gzipContent));
		this.mockResource = createMockDictionaryResource("mock://foo", sha1, gzipContent.length);
	}

	private static DictionaryResource createMockDictionaryResource(final String url, final String hash, final int size) {
		final DictionaryResource mock = Mockito.mock(DictionaryResource.class);
		when(mock.getUrl()).thenReturn(url);
		when(mock.getSha1()).thenReturn(hash);
		when(mock.getSize()).thenReturn(size);
		return mock;
	}

	@Test
	public void downloadShouldUnzipContentIntoOutputStream() throws Exception {
		this.initMockDictionaryResource("foo");
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		this.downloader.download(this.mockResource, baos, this.mockProgressHandler);
		assertThat(baos.toString(), equalTo("foo"));
	}

	@Test
	public void downloadShouldNotifyProgress() throws Exception {
		this.initMockDictionaryResource("foo");
		this.downloader.download(this.mockResource, new ByteArrayOutputStream(), this.mockProgressHandler);
		verify(this.mockProgressHandler, atLeastOnce()).handleProgress(anyInt(), anyInt());
	}

	@Test(expected = DictionaryResourceDownloaderException.class)
	public void downloadShouldFailWhenHashcodeMismatches() throws Exception {
		this.initMockDictionaryResource("foo");
		when(this.mockResource.getSha1()).thenReturn("abc123");
		this.downloader.download(this.mockResource, new ByteArrayOutputStream(), this.mockProgressHandler);
	}

	@Test
	public void downloadShouldNotWriteOutputWhenHashcodeMismatches() throws Exception {
		this.initMockDictionaryResource("foo");
		when(this.mockResource.getSha1()).thenReturn("abc123");
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			this.downloader.download(this.mockResource, baos, this.mockProgressHandler);
		} catch (final DictionaryResourceDownloaderException e) {
		}
		assertThat(baos.toString(), equalTo(""));
	}

	@Test
	public void downloadShouldNotifyProgressWhenHashcodeMismatches() throws Exception {
		this.initMockDictionaryResource("foo");
		try {
			this.downloader.download(this.mockResource, new ByteArrayOutputStream(), this.mockProgressHandler);
		} catch (final DictionaryResourceDownloaderException e) {
		}
		verify(this.mockProgressHandler, atLeastOnce()).handleProgress(anyInt(), anyInt());
	}

}
