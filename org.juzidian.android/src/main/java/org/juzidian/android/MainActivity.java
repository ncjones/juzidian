package org.juzidian.android;

import org.juzidian.core.Dictionary;
import org.juzidian.core.dataload.DictionaryResourceRegistryService;
import org.juzidian.core.inject.DictionaryModule;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class MainActivity extends Activity implements DictionaryInitializationListener {

	final Injector injector = Guice.createInjector(new DictionaryModule(), new JuzidianAndroidModule());

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		this.initializeDatabase();
	}

	private void initializeDatabase() {
		final DownloadManager downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
		final DictionaryResourceRegistryService registryService = this.injector.getInstance(DictionaryResourceRegistryService.class);
		final DictionaryInitializer dictionaryInitializer = new DictionaryInitializer(this, downloadManager, registryService, this);
		final DictionaryInitializerTask dictionaryDownloadTask = new DictionaryInitializerTask(dictionaryInitializer);
		dictionaryDownloadTask.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		this.getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private SearchView getSearchView() {
		return (SearchView) this.findViewById(R.id.searchView);
	}

	@Override
	public void initializationCompleted() {
		this.getSearchView().setDictionary(this.injector.getInstance(Dictionary.class));
	}

}
