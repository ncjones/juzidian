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

import org.juzidian.dataload.DictionaryResource;
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
 * Android service component for asynchronously downloading and installing
 * dictionaries.
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
public class DictionaryInitServiceComponent extends RoboService implements DictionaryInitService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryInitServiceComponent.class);

	@Inject
	@DictionaryDbPath
	public String dictionaryDbPath;

	@Inject
	private JuzidianDownloadManager downloadManager;

	private final DownloadHandler downloadHandler = new DownloadHandler();

	private final Set<DictionaryInitListener> downloadListeners = synchronizedSet(new HashSet<DictionaryInitListener>());

	/**
	 * The current dictionary initialization status.
	 */
	private volatile DictionaryInitStatus status;

	/**
	 * The initialization status before the current operation started.
	 * <p>
	 * This status should be rolled back to upon failure.
	 */
	private DictionaryInitStatus oldStatus;

	@Override
	public void onCreate() {
		super.onCreate();
		this.status = this.dbExists() ? DictionaryInitStatus.INITIALIZED : DictionaryInitStatus.UNINITIALIZED;
	}

	private boolean dbExists() {
		return new File(this.dictionaryDbPath).exists();
	}

	public DictionaryInitStatus getDictionaryInitStatus() {
		return this.status;
	}

	@Override
	public boolean isDictionaryInitialized() {
		return DictionaryInitStatus.INITIALIZED.equals(this.status);
	}

	@Override
	public boolean isInitializationInProgress() {
		return this.status.isInProgress();
	}

	@Override
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
		this.startService(new Intent(this, DictionaryInitServiceComponent.class));
		this.status = DictionaryInitStatus.DOWNLOADING;
		AsyncTask.execute(new RunnableDictionaryDownloader(dictionaryResource));
	}

	private void doDownload(final DictionaryResource dictionaryResource) {
		LOGGER.debug("Initializing download of dictionary database.");
		try {
			this.downloadManager.startDownload(dictionaryResource.getUrl());
			this.registerReceiver(DictionaryInitServiceComponent.this.downloadHandler, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
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
		IoUtil.copy(gzipInputStream, new FileOutputStream(this.dictionaryDbPath));
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
		new File(this.dictionaryDbPath).delete();
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

	@Override
	public void addListener(final DictionaryInitListener downloadListener) {
		this.downloadListeners.add(downloadListener);
	}

	@Override
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
			DictionaryInitServiceComponent.this.doDownload(this.dictionaryResource);
		}
	}

	private class RunnableDictionaryInstaller implements Runnable {

		@Override
		public void run() {
			DictionaryInitServiceComponent.this.installDownloadedDictionary();
		}

	}

	/**
	 * A binder for a {@link DictionaryInitServiceComponent}.
	 */
	public class Binder extends android.os.Binder {

		/**
		 * @return the {@link DictionaryInitService} for interfacing with the
		 *         bound service component.
		 */
		public DictionaryInitService getService() {
			return DictionaryInitServiceComponent.this;
		}

	}

	private class DownloadHandler extends JuzidianDownloadManagerBroadcastReceiver {

		@Override
		protected void handleDownloadSuccess() {
			DictionaryInitServiceComponent.this.onDownloadSuccess();
		}

		@Override
		protected void handleDownloadFailure() {
			DictionaryInitServiceComponent.this.onDownloadFailure();
		}

	}

}
