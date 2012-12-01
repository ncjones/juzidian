/*
 * Copyright Nathan Jones 2012
 *
 * This file is part of Juzidian.
 *
 * Juzidian is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Juzidian is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Juzidian.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.juzidian.android;

import java.util.Set;

import org.juzidian.core.SearchType;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Search bar UI component.
 */
public class SearchBar extends RelativeLayout {

	public SearchBar(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.search_bar, this, true);
		final TextView searchInput = this.getSearchInput();
		searchInput.addTextChangedListener(new SearchTextChangeListener());
	}

	public String getSearchText() {
		final EditText searchInput = this.getSearchInput();
		return searchInput.getText().toString();
	}

	public SearchType getSearchType() {
		final int checkedRadioButtonId = this.getSearchTypeRadioGroup().getCheckedRadioButtonId();
		if (checkedRadioButtonId == -1) {
			return null;
		}
		return this.getSearchTypeForRadioId(checkedRadioButtonId);
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

	private SearchType getSearchTypeForRadioId(final int searchTypeRadioButtonId) {
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

	private class SearchTextChangeListener implements TextWatcher {

		@Override
		public void afterTextChanged(final Editable searchInputText) {
			final String searchText = SearchBar.this.getSearchInput().getEditableText().toString();
			final Set<SearchType> applicableSearchTypes = SearchType.allApplicableFor(searchText);
			final RadioGroup searchTypeRadioGroup = SearchBar.this.getSearchTypeRadioGroup();
			for (int i = 0; i < searchTypeRadioGroup.getChildCount(); i++) {
				final RadioButton searchTypeRadioButton = (RadioButton) searchTypeRadioGroup.getChildAt(i);
				final SearchType searchType = SearchBar.this.getSearchTypeForRadioId(searchTypeRadioButton.getId());
				final boolean isSearchTypeApplicable = applicableSearchTypes.contains(searchType);
				searchTypeRadioButton.setEnabled(isSearchTypeApplicable);
				searchTypeRadioButton.setChecked(isSearchTypeApplicable);
			}
			SearchBar.this.getSearchButton().setEnabled(!applicableSearchTypes.isEmpty());
		}

		@Override
		public void beforeTextChanged(final CharSequence arg0, final int arg1, final int arg2, final int arg3) {
		}

		@Override
		public void onTextChanged(final CharSequence arg0, final int arg1, final int arg2, final int arg3) {
		}

	}

}
