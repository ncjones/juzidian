package org.juzidian.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.juzidian.core.Dictionary;
import org.juzidian.core.DictionaryEntry;
import org.juzidian.core.PinyinSyllable;
import org.juzidian.core.SearchType;
import org.juzidian.core.inject.DictionaryModule;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class MainActivity extends Activity implements TextWatcher {

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
		final TextView searchInput = this.getSearchInput();
		searchInput.addTextChangedListener(this);
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
		final EditText searchInput = this.getSearchInput();
		final SearchType selectedSearchType = this.getSelectedSearchType();
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

	private SearchType getSelectedSearchType() {
		final int checkedRadioButtonId = this.getSearchTypeRadioGroup().getCheckedRadioButtonId();
		if (checkedRadioButtonId == -1) {
			return null;
		}
		return this.getSearchType(checkedRadioButtonId);
	}

	private RadioGroup getSearchTypeRadioGroup() {
		return (RadioGroup) this.findViewById(R.id.searchTypeRadioGroup);
	}

	private EditText getSearchInput() {
		return (EditText) this.findViewById(R.id.searchInput);
	}

	private Button getSearchButton() {
		return (Button) this.findViewById(R.id.searchButton);
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

	@Override
	public void afterTextChanged(final Editable searchInputText) {
		final String searchText = this.getSearchInput().getEditableText().toString();
		final Set<SearchType> applicableSearchTypes = SearchType.allApplicableFor(searchText);
		final RadioGroup searchTypeRadioGroup = this.getSearchTypeRadioGroup();
		for (int i = 0; i < searchTypeRadioGroup.getChildCount(); i++) {
			final RadioButton searchTypeRadioButton = (RadioButton) searchTypeRadioGroup.getChildAt(i);
			final SearchType searchType = this.getSearchType(searchTypeRadioButton.getId());
			final boolean isSearchTypeApplicable = applicableSearchTypes.contains(searchType);
			searchTypeRadioButton.setEnabled(isSearchTypeApplicable);
			searchTypeRadioButton.setChecked(isSearchTypeApplicable);
		}
		this.getSearchButton().setEnabled(!applicableSearchTypes.isEmpty());
	}

	@Override
	public void beforeTextChanged(final CharSequence arg0, final int arg1, final int arg2, final int arg3) {
	}

	@Override
	public void onTextChanged(final CharSequence arg0, final int arg1, final int arg2, final int arg3) {
	}

}
