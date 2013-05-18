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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.inject.Inject;

import org.juzidian.util.IoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.receiver.RoboBroadcastReceiver;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelFileDescriptor;

/**
 * BroadcastReceiver that installs a dictionary database that has been
 * downloaded by a {@link JuzidianDownloadManager}.
 */
public final class DictionaryInstaller extends RoboBroadcastReceiver {

	private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryInstaller.class);

	public static final String DICTIONARY_DB_PATH = "/data/data/org.juzidian.android/juzidian-dictionary.db";

	@Inject
	private JuzidianDownloadManager downloadManager;

	@Override
	public void handleReceive(final Context context, final Intent intent) {
		final String action = intent.getAction();
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
			final long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			if (downloadId == this.downloadManager.getDownloadId()) {
				if (this.downloadManager.isDownloadSuccessful()) {
					this.installDictionary(this.downloadManager.getDownloadedFile());
				} else {
					LOGGER.error("Download was unsuccessful");
				}
				this.downloadManager.clearDownload();
			}
		}
	}

	private void installDictionary(final ParcelFileDescriptor fileDescriptor) {
		LOGGER.debug("Installing dictionary database from downloaded file {}", fileDescriptor);
		final InputStream rawInputStream = new ParcelFileDescriptor.AutoCloseInputStream(fileDescriptor);
		try {
			final GZIPInputStream gzipInputStream = new GZIPInputStream(rawInputStream);
			IoUtil.copy(gzipInputStream, new FileOutputStream(DICTIONARY_DB_PATH));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}
