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
package org.juzidian.dataload;

import java.io.IOException;
import java.io.InputStream;

/**
 * An input stream wrapper which notifies a {@link DownloadProgressHandler} when
 * ever some number of bytes have been read.
 */
class ProgressMonitoringInputStream extends InputStream {

	private static final int NOTIFICATION_INCREMENT_SIZE = 1 << 13;

	private final InputStream delegate;

	private final DownloadProgressHandler handler;

	private final int contentLength;

	private int bytesRead = 0;

	public ProgressMonitoringInputStream(final InputStream delegate, final int contentLength, final DownloadProgressHandler handler) {
		this.delegate = delegate;
		this.contentLength = contentLength;
		this.handler = handler;
	}

	@Override
	public int read() throws IOException {
		final int nextByte = this.delegate.read();
		if (nextByte != -1) {
			this.bytesRead += 1;
		}
		if (this.bytesRead % NOTIFICATION_INCREMENT_SIZE == 0 || nextByte == -1) {
			this.handler.handleProgress(this.contentLength, this.bytesRead);
			this.bytesRead += 0;
		}
		return nextByte;
	}

}