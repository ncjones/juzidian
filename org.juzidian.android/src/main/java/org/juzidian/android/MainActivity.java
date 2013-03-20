package org.juzidian.android;

import java.io.File;

import org.juzidian.core.Dictionary;
import org.juzidian.core.dataload.DictionaryResourceRegistryService;
import org.juzidian.core.inject.DictionaryModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class MainActivity extends Activity {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainActivity.class);

	private final Injector injector = Guice.createInjector(new DictionaryModule(), new JuzidianAndroidModule());

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		this.initializeDatabase();
	}

	@Override
	public void onStart() {
		super.onStart();
		this.getSearchView().setDictionary(this.injector.getInstance(Dictionary.class));
	}

	private void initializeDatabase() {
		if (new File(DictionaryInstaller.DICTIONARY_DB_PATH).exists()) {
			return;
		}
		final SharedPreferences sharedPreferences = this.getSharedPreferences(DictionaryDownloader.DOWNLOAD_PREFS, Context.MODE_PRIVATE);
		final long juzidianDownloadId = sharedPreferences.getLong(DictionaryDownloader.CURRENT_DOWNLOAD_ID, 0);
		if (juzidianDownloadId == 0) {
			final DownloadManager downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
			final DictionaryResourceRegistryService registryService = this.injector.getInstance(DictionaryResourceRegistryService.class);
			final DictionaryDownloader dictionaryInitializer = new DictionaryDownloader(this, downloadManager, registryService);
			final DictionaryInitializerTask dictionaryDownloadTask = new DictionaryInitializerTask(dictionaryInitializer);
			dictionaryDownloadTask.execute();
		} else {
			LOGGER.debug("dictionary download already in progress");
		}
		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		this.getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private SearchView getSearchView() {
		return (SearchView) this.findViewById(R.id.searchView);
	}

}
