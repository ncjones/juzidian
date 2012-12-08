package org.juzidian.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
		this.initializeDbFile();
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

	private void initializeDbFile() {
		final InputStream inputStream = this.getDictionaryDbInputStream();
		final File dbFile = new File(DICTIONARY_DB_PATH);
		dbFile.delete();
		try {
			dbFile.createNewFile();
			this.copy(inputStream, new FileOutputStream(dbFile));
		} catch (final IOException e) {
			throw new RuntimeException("Failed to initialize DB file.");
		}
	}

	private InputStream getDictionaryDbInputStream() {
		return this.getResources().openRawResource(R.raw.juzidian_dictionary);
	}

	public void copy(final InputStream in, final OutputStream out) throws IOException {
		final byte[] buf = new byte[10000];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
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
