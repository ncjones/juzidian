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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;

import org.juzidian.util.IoUtil;

/**
 * Downloads the actual data referred to by a {@link DictionaryResource}.
 */
public class DictionaryResourceDownloader {

	/**
	 * Download a compressed dictionary resource and write it, uncompressed, to
	 * the given output stream.
	 * <p>
	 * The SHA1 hash code of the resource will be verified. If the hash code
	 * does not match then the output stream will not be written to.
	 * 
	 * @param resource a {@link DictionaryResource} to download.
	 * @param out the output stream to write the downloaded data to.
	 * @param handler the {@link DownloadProgressHandler} to receive events for
	 *        download progress.
	 * @throws DictionaryResourceDownloaderException if download fails or a hash
	 *         code does not match.
	 */
	public void download(final DictionaryResource resource, final OutputStream out, final DownloadProgressHandler handler) throws DictionaryResourceDownloaderException {
		final URL url = createUrl(resource);
		try {
			final int contentLength = url.openConnection().getContentLength();
			final InputStream rawInputStream = url.openStream();
			final InputStream progressMonitoringInputStream = new ProgressMonitoringInputStream(rawInputStream, contentLength, handler);
			final MessageDigest messageDigest = getSha1Digest();
			final InputStream digestInputStream = new DigestInputStream(progressMonitoringInputStream, messageDigest);
			final File tempFile = File.createTempFile("juzidian-dictionary-download", null);
			IoUtil.copy(digestInputStream, new FileOutputStream(tempFile));
			this.verifyChecksum(resource, messageDigest.digest());
			final InputStream tempFileInputStream = new FileInputStream(tempFile);
			final InputStream gzipInputStream = new GZIPInputStream(tempFileInputStream);
			IoUtil.copy(gzipInputStream, out);
		} catch (final IOException e) {
			throw new DictionaryResourceDownloaderException(e);
		}
	}

	private void verifyChecksum(final DictionaryResource resource, final byte[] digest) throws DictionaryResourceDownloaderException {
		final String digestHex = HexUtil.bytesToHex(digest);
		if (!digestHex.equals(resource.getSha1())) {
			throw new DictionaryResourceDownloaderException("Checksum mismatch. Expected " + resource.getSha1() + " but was " + digestHex);
		}
	}

	private static MessageDigest getSha1Digest() throws DictionaryResourceDownloaderException {
		try {
			return MessageDigest.getInstance("SHA1");
		} catch (final NoSuchAlgorithmException e) {
			throw new DictionaryResourceDownloaderException(e);
		}
	}

	private static URL createUrl(final DictionaryResource resource) throws DictionaryResourceDownloaderException {
		try {
			return new URL(resource.getUrl());
		} catch (final MalformedURLException e) {
			throw new DictionaryResourceDownloaderException(e);
		}
	}

}
