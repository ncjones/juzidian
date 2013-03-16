package org.juzidian.android;

import org.juzidian.core.Dictionary;
import org.juzidian.core.inject.DictionaryModule;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class MainActivity extends Activity {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		final Injector injector = Guice.createInjector(new DictionaryModule(), new JuzidianAndroidModule());
		this.getSearchView().setDictionary(injector.getInstance(Dictionary.class));
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
