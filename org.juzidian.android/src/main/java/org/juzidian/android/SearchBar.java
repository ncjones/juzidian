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

import javax.inject.Inject;

import org.juzidian.core.Dictionary;
import org.juzidian.core.SearchType;

import roboguice.RoboGuice;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Search bar UI component.
 */
public class SearchBar extends RelativeLayout {

	private SearchTriggerListener searchTriggerListener;

	@Inject
	private Dictionary dictionary;

	private SearchTypeSelection searchTypeSelection;

	public SearchBar(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.search_bar, this, true);
		RoboGuice.injectMembers(context, this);
		this.getSearchInput().addTextChangedListener(new SearchTextChangeListener());
		this.getSearchTypeButton().setOnClickListener(new SearchTypeButtonClickListener());
		this.getClearSearchButton().setOnClickListener(new ClearSearchButtonClickListener());
		this.setSearchTypeSelection(SearchTypeSelection.PINYIN);
	}

	public void setSearchTriggerListener(final SearchTriggerListener searchTriggerListener) {
		this.searchTriggerListener = searchTriggerListener;
	}

	public String getSearchText() {
		final EditText searchInput = this.getSearchInput();
		return searchInput.getText().toString();
	}

	private EditText getSearchInput() {
		return (EditText) this.findViewById(R.id.searchInput);
	}

	private ImageView getSearchTypeButton() {
		return (ImageView) this.findViewById(R.id.searchTypeButton);
	}

	private ImageView getClearSearchButton() {
		return (ImageView) this.findViewById(R.id.clearSearchButton);
	}

	private void searchTriggered(final SearchType searchType, final String searchText) {
		this.searchTriggerListener.searchTriggered(searchType, searchText);
	}

	private void setSearchTypeSelection(final SearchTypeSelection searchTypeSelection) {
		this.searchTypeSelection = searchTypeSelection;
		final ImageView searchTypeButton = this.getSearchTypeButton();
		searchTypeButton.setImageResource(searchTypeSelection.getDrawableId());
	}

	private void toggleSearchTypeSelection() {
		final SearchTypeSelection nextSelection = this.searchTypeSelection.nextSelection();
		this.setSearchTypeSelection(nextSelection);
		this.searchTriggered(nextSelection.getSearchType(), this.getSearchText());
	}

	public void clearInput() {
		this.getSearchInput().setText("");
	}

	private void showSearchTypeButton() {
		this.showView(this.getSearchTypeButton());
	}

	private void hideSearchTypeButton() {
		this.hideView(this.getSearchTypeButton());
	}

	private void showClearSearchButton() {
		this.showView(this.getClearSearchButton());
	}

	private void hideClearSearchButton() {
		this.hideView(this.getClearSearchButton());
	}

	private void showView(final View view) {
		view.setVisibility(View.VISIBLE);
	}

	private void hideView(final View view) {
		view.setVisibility(View.GONE);
	}

	private Set<SearchType> getApplicableSearchTypes(final String searchText) {
		return SearchBar.this.dictionary.getApplicableSearchTypes(searchText);
	}

	private void textChanged() {
		final String searchText = this.getSearchText();
		if (searchText.isEmpty()) {
			this.hideClearSearchButton();
		} else {
			this.showClearSearchButton();
		}
		final Set<SearchType> applicableSearchTypes = this.getApplicableSearchTypes(searchText);
		SearchType searchType = null;
		switch (applicableSearchTypes.size()) {
		case 0:
			this.hideSearchTypeButton();
			break;
		case 1:
			this.hideSearchTypeButton();
			searchType = applicableSearchTypes.iterator().next();
			break;
		case 2:
			this.showSearchTypeButton();
			searchType = this.searchTypeSelection.getSearchType();
			break;
		default:
			throw new IllegalStateException("Unexpected applicable search types size: " + applicableSearchTypes);
		}
		this.searchTriggered(searchType, searchText);
	}

	private class SearchTextChangeListener implements TextWatcher {

		@Override
		public void afterTextChanged(final Editable searchInputText) {
			SearchBar.this.textChanged();
		}

		@Override
		public void beforeTextChanged(final CharSequence arg0, final int arg1, final int arg2, final int arg3) {
		}

		@Override
		public void onTextChanged(final CharSequence arg0, final int arg1, final int arg2, final int arg3) {
		}

	}

	private class SearchTypeButtonClickListener implements OnClickListener {

		@Override
		public void onClick(final View arg0) {
			SearchBar.this.toggleSearchTypeSelection();
		}

	}

	private class ClearSearchButtonClickListener implements OnClickListener {

		@Override
		public void onClick(final View arg0) {
			SearchBar.this.clearInput();
		}

	}

	private static enum SearchTypeSelection {

		PINYIN(SearchType.PINYIN, R.drawable.search_type_icon_pinyin),

		REVERSE(SearchType.REVERSE, R.drawable.search_type_icon_reverse);

		private final SearchType searchType;

		private final int drawableId;

		private SearchTypeSelection(final SearchType searchType, final int drawableId) {
			this.searchType = searchType;
			this.drawableId = drawableId;
		}

		public SearchType getSearchType() {
			return this.searchType;
		}

		public int getDrawableId() {
			return this.drawableId;
		}

		public SearchTypeSelection nextSelection() {
			return values()[(this.ordinal() + 1) % values().length];
		}

	}

}
