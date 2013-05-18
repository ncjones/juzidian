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

import javax.inject.Inject;

import roboguice.receiver.RoboBroadcastReceiver;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;

/**
 * A broadcast receiver which checks for download complete events that match
 * downloads created by a {@link JuzidianDownloadManager} and notifies of
 * success or failure.
 * <p>
 * Sub types must implement success and failure call backs.
 */
public abstract class JuzidianDownloadManagerBroadcastReceiver extends RoboBroadcastReceiver {

	@Inject
	protected JuzidianDownloadManager downloadManager;

	@Override
	public void handleReceive(final Context context, final Intent intent) {
		final String action = intent.getAction();
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
			final long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			if (downloadId == this.downloadManager.getDownloadId()) {
				if (this.downloadManager.isDownloadSuccessful()) {
					this.handleDownloadSuccess();
				} else {
					this.handleDownloadFailure();
				}
			}
		}
	}

	protected abstract void handleDownloadSuccess();

	protected abstract void handleDownloadFailure();

}
