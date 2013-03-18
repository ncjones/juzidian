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

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Receives broadcasts from a DownloadManager and filters them by an expected
 * download id before notifying a {@link DownloadEventListener}.
 */
final class DownloadBroadcastReceiver extends BroadcastReceiver {

	private final long downloadId;

	private final DownloadEventListener handler;

	public DownloadBroadcastReceiver(final long downloadId, final DownloadEventListener handler) {
		this.downloadId = downloadId;
		this.handler = handler;
	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		final long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
		if (downloadId == this.downloadId) {
			final String action = intent.getAction();
			if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
				this.handler.downloadComplete(this, downloadId);
			}
		}
	}

}
