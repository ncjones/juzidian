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
 * 	if (downloadService.getStatus().isInProgress()) {
 * 		downloadService.addDownloadListener(listener);
 * 	}
 * }
 * </pre>
 * 
 * This prevents download notifications being sent between the check for a
 * running download and the registration of the listener.
 */
public class DictionaryInitService extends RoboService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryInitService.class);

	public static final String DICTIONARY_DB_PATH = "/data/data/org.juzidian.android/juzidian-dictionary.db";

	@Inject
	private JuzidianDownloadManager downloadManager;

	private final DownloadHandler downloadHandler = new DownloadHandler();

	private final Set<DictionaryInitListener> downloadListeners = synchronizedSet(new HashSet<DictionaryInitListener>());

	/**
	 * The current dictionary initialization status.
	 */
	private volatile DictionaryInitStatus status = this.dbExists() ? DictionaryInitStatus.INITIALIZED : DictionaryInitStatus.UNINITIALIZED;

	/**
	 * The initialization status before the current operation started.
	 * <p>
	 * This status should be rolled back to upon failure.
	 */
	private DictionaryInitStatus oldStatus;

	private boolean dbExists() {
		return new File(DictionaryInitService.DICTIONARY_DB_PATH).exists();
	}

	/**
	 * Get the current initialization status of the dictionary database.
	 * 
	 * @return a {@link DictionaryInitStatus}.
	 */
	public DictionaryInitStatus getDictionaryInitStatus() {
		return this.status;
	}

	/**
	 * Check if the initialization status is
	 * {@link DictionaryInitStatus#INITIALIZED}.
	 * 
	 * @return <code>true</code> if the dictionary database is initialized.
	 * @see #getDictionaryInitStatus()
	 */
	public boolean isDictionaryInitialized() {
		return DictionaryInitStatus.INITIALIZED.equals(this.status);
	}

	/**
	 * Check if the initialization status is an "in progress" status.
	 * 
	 * @return <code>true</code> if the dictionary database is in the process of
	 *         being initialized.
	 * @see #getDictionaryInitStatus()
	 */
	public boolean isInitializationInProgress() {
		return this.status.isInProgress();
	}

	/**
	 * Schedules the download and installation of a dictionary.
	 * <p>
	 * The download will be run on a background thread. Any attached
	 * {@link DictionaryInitListener} will be notified of significant
	 * events.
	 * 
	 * @throws IllegalStateException if there is already a download in progress.
	 */
	public synchronized void downloadDictionary(final DictionaryResource dictionaryResource) {
		if (this.status.isInProgress()) {
			throw new IllegalStateException("Initialization already in progress");
		}
		if (dictionaryResource == null) {
			throw new NullPointerException("dictionaryResource is null");
		}
		this.oldStatus = this.status;
		/*
		 * Keep service running while a download is in progress.
		 */
		this.startService(new Intent(this, DictionaryInitService.class));
		this.status = DictionaryInitStatus.DOWNLOADING;
		AsyncTask.execute(new RunnableDictionaryDownloader(dictionaryResource));
	}

	private void doDownload(final DictionaryResource dictionaryResource) {
		LOGGER.debug("Initializing download of dictionary database.");
		try {
			this.downloadManager.startDownload(dictionaryResource.getUrl());
			this.registerReceiver(DictionaryInitService.this.downloadHandler, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		} catch (final Throwable e) {
			LOGGER.error("Failed to schedule dictionary download", e);
			this.onDownloadFailure();
		}
	}

	private void onDownloadSuccess() {
		this.status = DictionaryInitStatus.INSTALLING;
		AsyncTask.execute(new RunnableDictionaryInstaller());
	}

	private void installDownloadedDictionary() {
		try {
			this.installDictionary(this.downloadManager.getDownloadedFile());
			this.onInstallSuccess();
		} catch (final Throwable e) {
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
		this.status = this.oldStatus;
		try {
			this.notifyFailure();
		} finally {
			this.cleanUp();
		}
	}

	private void onInstallSuccess() {
		this.status = DictionaryInitStatus.INITIALIZED;
		try {
			this.notifySuccess();
		} finally {
			this.cleanUp();
		}
	}

	private void onInstallFailure() {
		this.status = this.oldStatus;
		try {
			if (DictionaryInitStatus.UNINITIALIZED.equals(this.status)) {
				this.removeDictionary();
			}
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
			for (final DictionaryInitListener listener : this.downloadListeners) {
				listener.dictionaryInitSuccess();
			}
		}
	}

	private synchronized void notifyFailure() {
		synchronized (this.downloadListeners) {
			for (final DictionaryInitListener listener : this.downloadListeners) {
				listener.dictionaryInitFailure();
			}
		}
	}

	private void cleanUp() {
		try {
			this.unregisterDownloadReceiver();
			this.downloadManager.clearDownload();
		} catch (final Throwable e) {
			LOGGER.warn("Uncaught exception while cleaning up download", e);
		} finally {
			this.oldStatus = null;
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
	 * Add a {@link DictionaryInitListener} to receive download
	 * notifications of download events.
	 * 
	 * @param downloadListener the listener to receive notifications.
	 */
	public void addListener(final DictionaryInitListener downloadListener) {
		this.downloadListeners.add(downloadListener);
	}

	/**
	 * Remove a {@link DictionaryInitListener} so it no longer receives
	 * download events.
	 * 
	 * @param downloadListener the listener to no longer receive notifications.
	 */
	public void removeListener(final DictionaryInitListener downloadListener) {
		this.downloadListeners.remove(downloadListener);
	}

	private final class RunnableDictionaryDownloader implements Runnable {

		private final DictionaryResource dictionaryResource;

		public RunnableDictionaryDownloader(final DictionaryResource dictionaryResource) {
			this.dictionaryResource = dictionaryResource;
		}

		@Override
		public void run() {
			DictionaryInitService.this.doDownload(this.dictionaryResource);
		}
	}

	private class RunnableDictionaryInstaller implements Runnable {

		@Override
		public void run() {
			DictionaryInitService.this.installDownloadedDictionary();
		}

	}

	public class Binder extends android.os.Binder {

		public DictionaryInitService getService() {
			return DictionaryInitService.this;
		}

	}

	private class DownloadHandler extends JuzidianDownloadManagerBroadcastReceiver {

		@Override
		protected void handleDownloadSuccess() {
			DictionaryInitService.this.onDownloadSuccess();
		}

		@Override
		protected void handleDownloadFailure() {
			DictionaryInitService.this.onDownloadFailure();
		}

	}

}
