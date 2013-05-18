package org.juzidian.android;

import java.io.File;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.activity.RoboActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends RoboActivity {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainActivity.class);

	@Inject
	@DownloadSharedPrefs
	private SharedPreferences downloadPrefs;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.isDbInitialized()) {
			this.setContentView(R.layout.activity_main);
		} else {
			this.initializeDatabase();
			this.setContentView(R.layout.download_view);
		}
	}

	private boolean isDbInitialized() {
		return new File(DictionaryInstaller.DICTIONARY_DB_PATH).exists();
	}

	private void initializeDatabase() {
		final long juzidianDownloadId = this.downloadPrefs.getLong(DictionaryDownloader.CURRENT_DOWNLOAD_ID, 0);
		if (juzidianDownloadId == 0) {
			final DictionaryInitializerTask dictionaryDownloadTask = new DictionaryInitializerTask(this);
			dictionaryDownloadTask.execute();
		} else {
			LOGGER.debug("dictionary download already in progress");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		this.getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
