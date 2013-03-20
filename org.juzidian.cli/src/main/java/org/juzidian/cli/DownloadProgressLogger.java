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
package org.juzidian.cli;

import org.juzidian.core.dataload.DownloadProgressHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class DownloadProgressLogger implements DownloadProgressHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadProgressLogger.class);

	int previous100KB = 0;

	@Override
	public void handleProgress(final int contentLength, final int bytesReceived) {
		final int current100KB = bytesReceived / 102400;
		if (current100KB > this.previous100KB) {
			this.previous100KB = current100KB;
			LOGGER.info(String.format("Downloaded %dKB / %dKB", bytesReceived / 1024, contentLength / 1024));
		}
	}

}
