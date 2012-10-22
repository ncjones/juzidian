package org.juzidian.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

import org.juzidian.core.Dictionary;
import org.juzidian.core.DictionaryEntry;
import org.juzidian.core.PinyinSyllable;
import org.juzidian.core.SearchType;
import org.juzidian.core.inject.DictionaryModule;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class MainActivity extends Activity {

	private static final String DICTIONARY_DB_PATH = "/data/data/org.juzidian.android/juzidian-dictionary.db";

	private Dictionary dictionary;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		this.initializeDbFile();
		final Injector injector = Guice.createInjector(new DictionaryModule() {
			@Override
			protected ConnectionSource createConnectionSource(final String jdbcUrl) throws SQLException {
				final SQLiteDatabase sqliteDb = SQLiteDatabase.openDatabase(DICTIONARY_DB_PATH, null,
						SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
				return new AndroidConnectionSource(sqliteDb);
			}
		});
		this.dictionary = injector.getInstance(Dictionary.class);
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

	public void doSearch(@SuppressWarnings("unused") final View view) {
		final EditText searchInput = (EditText) this.findViewById(R.id.searchInput);
		final RadioGroup searchTypeRadioGroup = (RadioGroup) this.findViewById(R.id.searchTypeRadioGroup);
		final SearchType selectedSearchType = this.getSearchType(searchTypeRadioGroup.getCheckedRadioButtonId());
		final LinearLayout searchResultLayout = (LinearLayout) this.findViewById(R.id.searchResultLayout);
		searchResultLayout.removeAllViews();
		final ProgressBar progressBar = new ProgressBar(searchResultLayout.getContext());
		searchResultLayout.addView(progressBar);
		final List<DictionaryEntry> words = this.dictionary.find(searchInput.getText().toString(), selectedSearchType);
		searchResultLayout.removeAllViews();
		for (final DictionaryEntry chineseWord : words) {
			final TextView textView = new TextView(searchResultLayout.getContext());
			final String pinyinDisplay = this.createPinyinDisplay(chineseWord);
			final String englishDefinitionDisplay = this.createEnglishDefinitionDisplay(chineseWord);
			textView.setText(chineseWord.getTraditional() + " (" + pinyinDisplay + "): " + englishDefinitionDisplay);
			searchResultLayout.addView(textView);
		}
	}

	private SearchType getSearchType(final int searchTypeRadioButtonId) {
		switch (searchTypeRadioButtonId) {
		case R.id.pinyinSearchTypeRadio:
			return SearchType.PINYIN;
		case R.id.reverseSearchTypeRadio:
			return SearchType.REVERSE;
		case R.id.hanziSearchTypeRadio:
			return SearchType.HANZI;
		}
		throw new IllegalArgumentException("Invalid search type radio button id: " + searchTypeRadioButtonId);
	}

	private String createEnglishDefinitionDisplay(final DictionaryEntry chineseWord) {
		final List<String> definitions = chineseWord.getDefinitions();
		final StringBuilder stringBuilder = new StringBuilder();
		for (final String definition : definitions) {
			stringBuilder.append(definition).append("; ");
		}
		return stringBuilder.substring(0, stringBuilder.length() - 2);
	}

	private String createPinyinDisplay(final DictionaryEntry chineseWord) {
		final StringBuilder stringBuilder = new StringBuilder();
		for (final PinyinSyllable syllable : chineseWord.getPinyin()) {
			stringBuilder.append(syllable.getDisplayValue()).append(" ");
		}
		return stringBuilder.toString().trim();
	}

}