package org.juzidian.android;

import javax.inject.Inject;

import org.juzidian.core.dataload.DictionaryResource;
import org.juzidian.core.dataload.DictionaryResourceRegistry;
import org.juzidian.core.dataload.DictionaryResourceRegistryService;
import org.juzidian.core.dataload.DictonaryResourceRegistryServiceException;
import org.juzidian.core.datastore.DbDictionaryDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.activity.RoboActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;

public class MainActivity extends RoboActivity {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainActivity.class);

	@Inject
	private DictionaryResourceRegistryService registryService;

	private DictionaryInitService dictionaryInitService;

	private final ServiceConnection downloadServiceConnection = new DownloadServiceConnection();

	private final DictionaryInitListener downloadListener = new DownloadListener();

	private boolean started;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LOGGER.debug("Binding to download service");
		final Intent intent = new Intent(this, DictionaryInitService.class);
		this.bindService(intent, this.downloadServiceConnection, Context.BIND_AUTO_CREATE);
	}

	private void onDownloadServiceConnected(final DictionaryInitService dictionaryInitService) {
		LOGGER.debug("download service connected");
		this.dictionaryInitService = dictionaryInitService;
		synchronized (dictionaryInitService) {
			if (dictionaryInitService.isDictionaryInitialized()) {
				this.setContentView(R.layout.activity_main);
			} else {
				this.initializeDatabase();
				this.setContentView(R.layout.download_view);
			}
		}
	}

	private void initializeDatabase() {
		if (this.dictionaryInitService.isInitializationInProgress()) {
			LOGGER.debug("dictionary download already in progress");
		} else {
			AsyncTask.execute(new RunnableDictionaryInitializer());
		}
		this.dictionaryInitService.addListener(this.downloadListener);
	}

	private class RunnableDictionaryInitializer implements Runnable {
		@Override
		public void run() {
			MainActivity.this.startDownload();
		}
	}

	private void startDownload() {
		try {
			final DictionaryResource dictionaryResource = this.getDictionaryResource();
			this.dictionaryInitService.downloadDictionary(dictionaryResource);
		} catch (final DictonaryResourceRegistryServiceException e) {
			LOGGER.error("Download failed", e);
			this.onDownloadFailure();
		}
	}

	private DictionaryResource getDictionaryResource() throws DictonaryResourceRegistryServiceException {
		final DictionaryResourceRegistry registry = this.registryService.getDictionaryResourceRegistry(DbDictionaryDataStore.DATA_FORMAT_VERSION);
		final DictionaryResource dictionaryResource = registry.getDictionaryResources().get(0);
		return dictionaryResource;
	}

	public void onDownloadServiceDisconnected() {
		LOGGER.debug("download service disconnected");
		this.dictionaryInitService = null;
	}

	@Override
	protected void onStart() {
		LOGGER.debug("starting main activity");
		super.onStart();
		this.started = true;
	}

	@Override
	protected void onStop() {
		LOGGER.debug("stopping main activity");
		super.onStop();
		this.started = false;
	}

	@Override
	protected void onDestroy() {
		LOGGER.debug("destroying main activity");
		super.onDestroy();
		if (this.dictionaryInitService != null) {
			this.unbindService(this.downloadServiceConnection);
			this.dictionaryInitService.removeListener(this.downloadListener);
		}
	}

	private void onDownloadSuccess() {
		this.finish();
		if (this.started) {
			/* if activity was stopped then don't unobtrusively restart */
			this.startActivity(this.getIntent());
		}
	}

	private void onDownloadFailure() {
		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		this.getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private class DownloadListener implements DictionaryInitListener {

		@Override
		public void dictionaryInitSuccess() {
			MainActivity.this.onDownloadSuccess();
		}

		@Override
		public void dictionaryInitFailure() {
			MainActivity.this.onDownloadFailure();
		}

	}

	private class DownloadServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(final ComponentName name, final IBinder binder) {
			final DictionaryInitService dictionaryInitService = ((DictionaryInitService.Binder) binder).getService();
			MainActivity.this.onDownloadServiceConnected(dictionaryInitService);
		}

		@Override
		public void onServiceDisconnected(final ComponentName name) {
			MainActivity.this.onDownloadServiceDisconnected();
		}

	}

}
