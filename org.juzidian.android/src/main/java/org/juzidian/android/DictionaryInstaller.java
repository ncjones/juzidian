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

import static org.juzidian.android.DictionaryDownloader.CURRENT_DOWNLOAD_ID;
import static org.juzidian.android.DictionaryDownloader.DOWNLOAD_PREFS;

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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.ParcelFileDescriptor;

/**
 * BroadcastReceiver that installs a dictionary database that has been
 * downloaded by a {@link DictionaryDownloader}.
 */
public final class DictionaryInstaller extends RoboBroadcastReceiver {

	private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryInstaller.class);

	public static final String DICTIONARY_DB_PATH = "/data/data/org.juzidian.android/juzidian-dictionary.db";

	@Inject
	private DownloadManager downloadManager;

	@Override
	public void handleReceive(final Context context, final Intent intent) {
		final String action = intent.getAction();
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
			final long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			final SharedPreferences sharedPreferences = context.getSharedPreferences(DOWNLOAD_PREFS, Context.MODE_PRIVATE);
			final long juzidianDownloadId = sharedPreferences.getLong(CURRENT_DOWNLOAD_ID, 0);
			if (downloadId == juzidianDownloadId) {
				if (this.isDownloadSuccessful(this.downloadManager, downloadId)) {
					this.installDictionary(this.downloadManager, downloadId);
				} else {
					LOGGER.debug("Removing failed dictionary download with id {}", downloadId);
					this.downloadManager.remove(downloadId);
				}
				this.clearCurrentDownload(sharedPreferences);
			}
		}
	}

	private void clearCurrentDownload(final SharedPreferences sharedPreferences) {
		LOGGER.debug("Clearing current dictionary download id");
		final Editor editor = sharedPreferences.edit();
		editor.remove(CURRENT_DOWNLOAD_ID);
		editor.apply();
	}

	private boolean isDownloadSuccessful(final DownloadManager downloadManager, final long downloadId) {
		final DownloadManager.Query query = new DownloadManager.Query();
		query.setFilterById(downloadId);
		final Cursor cursor = downloadManager.query(query);
		if (cursor.moveToFirst()) {
			final int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
			return DownloadManager.STATUS_SUCCESSFUL == downloadStatus;
		}
		return false;
	}

	private void installDictionary(final DownloadManager downloadManager, final long downloadId) {
		LOGGER.debug("Installing dictionary database from download id {}", downloadId);
		final ParcelFileDescriptor fileDescriptor;
		try {
			fileDescriptor = downloadManager.openDownloadedFile(downloadId);
			final InputStream rawInputStream = new ParcelFileDescriptor.AutoCloseInputStream(fileDescriptor);
			final GZIPInputStream gzipInputStream = new GZIPInputStream(rawInputStream);
			IoUtil.copy(gzipInputStream, new FileOutputStream(DICTIONARY_DB_PATH));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}
