package org.juzidian.android;

import java.io.File;

import org.juzidian.core.Dictionary;
import org.juzidian.core.inject.DictionaryModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class MainActivity extends RoboActivity {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainActivity.class);

	private final Injector injector = Guice.createInjector(new DictionaryModule(), new JuzidianAndroidModule());

	@InjectView(R.id.searchView)
	private SearchView searchView;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		this.initializeDatabase();
	}

	@Override
	public void onStart() {
		super.onStart();
		this.searchView.setDictionary(this.injector.getInstance(Dictionary.class));
	}

	private void initializeDatabase() {
		if (new File(DictionaryInstaller.DICTIONARY_DB_PATH).exists()) {
			return;
		}
		final SharedPreferences sharedPreferences = this.getSharedPreferences(DictionaryDownloader.DOWNLOAD_PREFS, Context.MODE_PRIVATE);
		final long juzidianDownloadId = sharedPreferences.getLong(DictionaryDownloader.CURRENT_DOWNLOAD_ID, 0);
		if (juzidianDownloadId == 0) {
			final DictionaryInitializerTask dictionaryDownloadTask = new DictionaryInitializerTask(this);
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

}
