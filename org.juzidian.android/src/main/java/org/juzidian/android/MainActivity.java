package org.juzidian.android;

import java.io.File;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.activity.RoboActivity;
import android.app.DownloadManager;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends RoboActivity {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainActivity.class);

	@Inject
	private JuzidianDownloadManager downloadManager;

	private DownloadHandler downloadHandler;

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
		if (!this.downloadManager.isDownloadInProgress()) {
			final DictionaryInitializerTask dictionaryDownloadTask = new DictionaryInitializerTask(this);
			dictionaryDownloadTask.execute();
		} else {
			LOGGER.debug("dictionary download already in progress");
		}
		this.downloadHandler = new DownloadHandler();
		this.registerReceiver(this.downloadHandler, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (this.downloadHandler != null) {
			this.unregisterReceiver(this.downloadHandler);
		}
	}

	private void onDownloadSuccess() {
		this.restartActivity();
	}

	private void onDownloadFailure() {
		this.restartActivity();
	}

	private void restartActivity() {
		this.finish();
		this.startActivity(this.getIntent());
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		this.getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private class DownloadHandler extends JuzidianDownloadManagerBroadcastReceiver {

		@Override
		protected void handleDownloadSuccess() {
			MainActivity.this.onDownloadSuccess();
		}

		@Override
		protected void handleDownloadFailure() {
			MainActivity.this.onDownloadFailure();
		}

	}

}
