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

import static java.util.Collections.synchronizedSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.inject.Inject;

import org.juzidian.core.dataload.DictionaryResource;
import org.juzidian.util.IoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.service.RoboService;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;

/**
 * Bindable background service for downloading and installing dictionaries.
 * <p>
 * There can only be one download in progress at a time.
 * <p>
 * This class is thread safe.
 * <p>
 * After a client binds to an already-running download service, the conditional
 * registration of a notification listener should be enclosed in a synchronized
 * block which locks on the download service. For example:
 * 
 * <pre>
 * synchronized (downloadService) {
 * 	if (downloadService.isDownloadInProgress()) {
 * 		downloadService.addDownloadListener(listener);
 * 	}
 * }
 * </pre>
 * 
 * This prevents download notifications being sent between the check for a
 * running download and the registration of the listener.
 */
public class DictionaryDownloadService extends RoboService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryDownloadService.class);

	public static final String DICTIONARY_DB_PATH = "/data/data/org.juzidian.android/juzidian-dictionary.db";

	@Inject
	private JuzidianDownloadManager downloadManager;

	private final DownloadHandler downloadHandler = new DownloadHandler();

	private final Set<DictionaryDownloadListener> downloadListeners = synchronizedSet(new HashSet<DictionaryDownloadListener>());

	private boolean downloadInProgress = false;

	/**
	 * Check if a download is currently in progress.
	 * 
	 * @return <code>true</code> if a download has been started and has not yet
	 *         finished.
	 */
	public boolean isDownloadInProgress() {
		return this.downloadInProgress;
	}

	/**
	 * Check if the dictionary database is initialized.
	 * 
	 * @return <code>true</code> if the dictionary database is already
	 *         initialized.
	 */
	public boolean isDbInitialized() {
		return new File(DictionaryDownloadService.DICTIONARY_DB_PATH).exists();
	}

	/**
	 * Finds a compatible dictionary, schedules its download and, when the
	 * download is complete, installs the dictionary.
	 * <p>
	 * The download will be run on a background thread. Any attached
	 * {@link DictionaryDownloadListener} will be notified of significant
	 * events.
	 * 
	 * @throws IllegalStateException if there is already a download in progress.
	 */
	public synchronized void downloadDictionary(final DictionaryResource dictionaryResource) {
		if (this.downloadInProgress) {
			throw new IllegalStateException("Initialization already in progress");
		}
		this.downloadInProgress = true;
		/*
		 * Keep service running while a download is in progress.
		 */
		this.startService(new Intent(this, DictionaryDownloadService.class));
		AsyncTask.execute(new RunnableDictionaryDownloader(dictionaryResource));
	}

	private void doDownload(final DictionaryResource dictionaryResource) {
		LOGGER.debug("Initializing download of dictionary database.");
		this.downloadManager.startDownload(dictionaryResource.getUrl());
		this.registerReceiver(DictionaryDownloadService.this.downloadHandler, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}

	private void onDownloadSuccess() {
		AsyncTask.execute(new RunnableDictionaryInstaller());
	}

	private void installDownloadedDictionary() {
		try {
			this.installDictionary(this.downloadManager.getDownloadedFile());
			this.onInstallSuccess();
		} catch (final IOException e) {
			LOGGER.error("Failed to install dictionary", e);
			this.onInstallFailure();
		}
	}

	private void installDictionary(final ParcelFileDescriptor fileDescriptor) throws IOException {
		LOGGER.debug("Installing dictionary database from downloaded file");
		final InputStream rawInputStream = new ParcelFileDescriptor.AutoCloseInputStream(fileDescriptor);
		final GZIPInputStream gzipInputStream = new GZIPInputStream(rawInputStream);
		IoUtil.copy(gzipInputStream, new FileOutputStream(DICTIONARY_DB_PATH));
	}

	private void onDownloadFailure() {
		LOGGER.error("Download was unsuccessful");
		try {
			this.notifyFailure();
		} finally {
			this.cleanUp();
		}
	}

	private void onInstallSuccess() {
		try {
			this.notifySuccess();
		} finally {
			this.cleanUp();
		}
	}

	private void onInstallFailure() {
		try {
			this.removeDictionary();
			this.notifyFailure();
		} finally {
			this.cleanUp();
		}
	}

	private void removeDictionary() {
		new File(DICTIONARY_DB_PATH).delete();
	}

	private synchronized void notifySuccess() {
		synchronized (this.downloadListeners) {
			for (final DictionaryDownloadListener listener : this.downloadListeners) {
				listener.downloadSuccess();
			}
		}
	}

	private synchronized void notifyFailure() {
		synchronized (this.downloadListeners) {
			for (final DictionaryDownloadListener listener : this.downloadListeners) {
				listener.downloadFailure();
			}
		}
	}

	private void cleanUp() {
		try {
			this.unregisterDownloadReceiver();
			this.downloadManager.clearDownload();
		} catch (final Exception e) {
			LOGGER.warn("Uncaught exception while cleaning up download", e);
		} finally {
			this.downloadInProgress = false;
			this.stopSelf();
		}
	}

	private void unregisterDownloadReceiver() {
		try {
			this.unregisterReceiver(this.downloadHandler);
		} catch (final Exception e) {
			LOGGER.debug("Download receiver could not be unregistered");
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterDownloadReceiver();
		this.downloadListeners.clear();
	}

	@Override
	public IBinder onBind(final Intent intent) {
		LOGGER.debug("Creating dictionary download service binder");
		return new Binder();
	}

	/**
	 * Add a {@link DictionaryDownloadListener} to receive download
	 * notifications of download events.
	 * 
	 * @param downloadListener the listener to receive notifications.
	 */
	public void addDownloadListener(final DictionaryDownloadListener downloadListener) {
		this.downloadListeners.add(downloadListener);
	}

	/**
	 * Remove a {@link DictionaryDownloadListener} so it no longer receives
	 * download events.
	 * 
	 * @param downloadListener the listener to no longer receive notifications.
	 */
	public void removeDownloadListener(final DictionaryDownloadListener downloadListener) {
		this.downloadListeners.remove(downloadListener);
	}

	private final class RunnableDictionaryDownloader implements Runnable {

		private final DictionaryResource dictionaryResource;

		public RunnableDictionaryDownloader(final DictionaryResource dictionaryResource) {
			this.dictionaryResource = dictionaryResource;
		}

		@Override
		public void run() {
			DictionaryDownloadService.this.doDownload(this.dictionaryResource);
		}
	}

	private class RunnableDictionaryInstaller implements Runnable {

		@Override
		public void run() {
			DictionaryDownloadService.this.installDownloadedDictionary();
		}

	}

	public class Binder extends android.os.Binder {

		public DictionaryDownloadService getService() {
			return DictionaryDownloadService.this;
		}

	}

	private class DownloadHandler extends JuzidianDownloadManagerBroadcastReceiver {

		@Override
		protected void handleDownloadSuccess() {
			DictionaryDownloadService.this.onDownloadSuccess();
		}

		@Override
		protected void handleDownloadFailure() {
			DictionaryDownloadService.this.onDownloadFailure();
		}

	}

}
