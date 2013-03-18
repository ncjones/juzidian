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
package org.juzidian.android;

/**
 * Handles download events from a {@link DownloadBroadcastReceiver}.
 */
interface DownloadEventListener {

	/**
	 * A file download has completed.
	 * 
	 * @param receiver the {@link DownloadBroadcastReceiver} that received the
	 *        download event.
	 * @param downloadId the DownloadManager file download id.
	 */
	void downloadComplete(DownloadBroadcastReceiver receiver, long downloadId);

}
