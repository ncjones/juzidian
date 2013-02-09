package org.juzidian.android;

import java.sql.SQLException;

import org.juzidian.core.Dictionary;
import org.juzidian.core.inject.DictionaryModule;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class MainActivity extends Activity {

	private static final String DICTIONARY_DB_PATH = "/data/data/org.juzidian.android/juzidian-dictionary.db";

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		final Injector injector = Guice.createInjector(new DictionaryModule() {
			@Override
			protected ConnectionSource createConnectionSource() throws SQLException {
				final SQLiteDatabase sqliteDb = SQLiteDatabase.openDatabase(DICTIONARY_DB_PATH, null,
						SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
				return new AndroidConnectionSource(sqliteDb);
			}
		});
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
