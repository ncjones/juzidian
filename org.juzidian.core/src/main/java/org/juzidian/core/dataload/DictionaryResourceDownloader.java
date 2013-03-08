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

import java.io.OutputStream;

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

	}

}
