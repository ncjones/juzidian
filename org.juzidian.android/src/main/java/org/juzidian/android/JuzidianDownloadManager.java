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

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

/**
 * Schedules a file download and provides information about its status.
 * <p>
 * Only a single download is capable of being processed at a time.
 * <p>
 * Internally this class uses the android DownloadManager. To receive download
 * complete events, clients must register a broadcast receiver for the
 * android.intent.action.DOWNLOAD_COMPLETE action.
 */
@Singleton
public class JuzidianDownloadManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(JuzidianDownloadManager.class);

	private final DownloadManager downloadManager;

	private final DownloadRegistry downloadRegistry;

	@Inject
	public JuzidianDownloadManager(final DownloadManager downloadManager, final DownloadRegistry downloadRegistry) {
		this.downloadManager = downloadManager;
		this.downloadRegistry = downloadRegistry;
	}

	/**
	 * Start a download from the given URL.
	 * <p>
	 * Starting a new download will overwrite any existing download.
	 * 
	 * @param url the URL to start a download for.
	 */
	public void startDownload(final String url) {
		LOGGER.debug("Starting download: {}", url);
		final Request request = new DownloadManager.Request(Uri.parse(url));
		request.setTitle("Juzidian Dictionary Database");
		final long downloadId = this.downloadManager.enqueue(request);
		this.downloadRegistry.setCurrentDownloadId(downloadId);
	}

	/**
	 * Get the id of the download that has been started or <code>null</code> if
	 * there is none.
	 * 
	 * @return the id of the current download.
	 */
	public Long getDownloadId() {
		return this.downloadRegistry.getCurrentDownloadId();
	}

	/**
	 * Check if there is a download currently in progress.
	 * <p>
	 * "In progress" means a download has been started and
	 * {@link #clearDownload()} has not yet been called. If this method returns
	 * <code>true</code> the download may have already completed.
	 * 
	 * @return <code>true</code> if a download has been started and not cleared.
	 */
	public boolean isDownloadInProgress() {
		return this.getDownloadId() != null;
	}

	/**
	 * Check if the current download has completed successfully.
	 * 
	 * @return <code>true</code> if the download is completed and has a
	 *         successful status.
	 */
	public boolean isDownloadSuccessful() {
		final long downloadId = this.getDownloadId();
		final DownloadManager.Query query = new DownloadManager.Query();
		query.setFilterById(downloadId);
		final Cursor cursor = this.downloadManager.query(query);
		if (cursor.moveToFirst()) {
			final int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
			return DownloadManager.STATUS_SUCCESSFUL == downloadStatus;
		}
		return false;
	}

	/**
	 * Get the file descriptor for the completed download.
	 * <p>
	 * This operation will fail if the download has not completed successfully.
	 * 
	 * @return a file descriptor for the completed download.
	 */
	public ParcelFileDescriptor getDownloadedFile() {
		final long downloadId = this.getDownloadId();
		LOGGER.debug("Opening downloaded file for download id: {}", downloadId);
		final ParcelFileDescriptor fileDescriptor;
		try {
			fileDescriptor = this.downloadManager.openDownloadedFile(downloadId);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return fileDescriptor;
	}

	/**
	 * Clear the information about the current download.
	 * <p>
	 * This method should be invoked after a completed download has been
	 * processed.
	 */
	public void clearDownload() {
		final Long downloadId = this.getDownloadId();
		LOGGER.debug("Removing download with id: {}", downloadId);
		this.downloadManager.remove(downloadId);
		this.downloadRegistry.setCurrentDownloadId(null);
	}

}
