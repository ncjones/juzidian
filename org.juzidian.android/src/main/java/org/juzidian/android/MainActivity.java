package org.juzidian.android;

import javax.inject.Inject;

import org.juzidian.core.SearchQuery;
import org.juzidian.core.SearchType;
import org.juzidian.core.dataload.DictionaryResource;
import org.juzidian.core.dataload.DictionaryResourceRegistry;
import org.juzidian.core.dataload.DictionaryResourceRegistryService;
import org.juzidian.core.dataload.DictonaryResourceRegistryServiceException;
import org.juzidian.core.datastore.DbDictionaryDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends RoboActivity {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainActivity.class);

	private static final String BUNDLE_KEY_SEARCH_TYPE = "search-type";

	private static final String BUNDLE_KEY_SEARCH_TEXT = "search-text";

	@Inject
	private DictionaryResourceRegistryService registryService;

	@InjectView(R.id.searchView)
	@Nullable
	private SearchView searchView;

	@InjectView(R.id.searchBar)
	@Nullable
	private SearchBar searchBar;

	private DictionaryInitService dictionaryInitService;

	private final ServiceConnection downloadServiceConnection = new DownloadServiceConnection();

	private final DictionaryInitListener downloadListener = new DownloadListener();

	private boolean started;

	private SearchType savedSearchType;

	private String savedSearchText;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		LOGGER.debug("creating main activity");
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			this.savedSearchType = (SearchType) savedInstanceState.getSerializable(BUNDLE_KEY_SEARCH_TYPE);
			this.savedSearchText = savedInstanceState.getString(BUNDLE_KEY_SEARCH_TEXT);
			LOGGER.debug("Loaded saved instance state: {} / {}", this.savedSearchType, this.savedSearchText);
		}
		LOGGER.debug("Binding to download service");
		final Intent intent = new Intent(this, DictionaryInitServiceComponent.class);
		this.bindService(intent, this.downloadServiceConnection, Context.BIND_AUTO_CREATE);
	}

	private void onDownloadServiceConnected(final DictionaryInitService dictionaryInitService) {
		LOGGER.debug("download service connected");
		this.dictionaryInitService = dictionaryInitService;
		synchronized (dictionaryInitService) {
			if (dictionaryInitService.isDictionaryInitialized()) {
				this.setContentView(R.layout.activity_main);
				if (this.savedSearchType != null) {
					this.searchBar.setPreferredSearchType(this.savedSearchType);
					this.searchBar.setSearchText(this.savedSearchText);
					this.savedSearchType = null;
					this.savedSearchText = null;
				}
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

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		final SearchQuery currentQuery = this.searchView.getCurrentQuery();
		if (currentQuery != null) {
			outState.putSerializable(BUNDLE_KEY_SEARCH_TYPE, currentQuery.getSearchType());
			outState.putString(BUNDLE_KEY_SEARCH_TEXT, currentQuery.getSearchText());
			LOGGER.debug("Saving current search input", currentQuery);
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

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			this.showAbout();
		}
		return super.onOptionsItemSelected(item);
	}

	private void showAbout() {
		this.startActivity(new Intent(this, AboutActivity.class));
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
			final DictionaryInitService dictionaryInitService = ((DictionaryInitServiceComponent.Binder) binder).getService();
			MainActivity.this.onDownloadServiceConnected(dictionaryInitService);
		}

		@Override
		public void onServiceDisconnected(final ComponentName name) {
			MainActivity.this.onDownloadServiceDisconnected();
		}

	}

}
