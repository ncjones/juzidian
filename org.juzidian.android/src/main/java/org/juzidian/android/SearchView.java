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

import org.juzidian.core.Dictionary;
import org.juzidian.core.SearchQuery;
import org.juzidian.core.SearchResults;
import org.juzidian.core.SearchType;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

/**
 * Dictionary search view which contains a {@link SearchBar} and a ListView.
 */
public class SearchView extends RelativeLayout implements DictionarySearchTaskListener, SearchTriggerListener, PageRequestListener {

	private static final int PAGE_SIZE = 25;

	private Dictionary dictionary;

	private SearchResults currentSearchResults;

	public SearchView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.search_view, this, true);
		this.getSearchBar().setSearchTriggerListener(this);
		this.getSearchResultsView().setPageRequestListener(this);
	}

	public void setDictionary(final Dictionary dictionary) {
		this.dictionary = dictionary;
		this.getSearchBar().setDictionary(dictionary);
	}

	private void doSearch(final SearchQuery searchQuery) {
		this.getSearchResultsView().showLoadingIndicator(true);
		final DictionarySearchTask dictionarySearchTask = new DictionarySearchTask(this.dictionary, this);
		dictionarySearchTask.execute(searchQuery);
	}

	private SearchBar getSearchBar() {
		return (SearchBar) this.findViewById(R.id.searchBar);
	}

	private SearchResultsView getSearchResultsView() {
		return (SearchResultsView) this.findViewById(R.id.searchResultsView);
	}

	@Override
	public void searchTriggered(final SearchType searchType, final String searchText) {
		final SearchQuery searchQuery = new SearchQuery(searchType, searchText, PAGE_SIZE, 0);
		this.currentSearchResults = null;
		this.getSearchResultsView().clearSearchResults();
		this.doSearch(searchQuery);
	}

	@Override
	public void pageRequested() {
		final SearchBar searchBar = this.getSearchBar();
		if (this.currentSearchResults != null) {
			final SearchQuery searchQuery = new SearchQuery(searchBar.getSearchType(), searchBar.getSearchText(), PAGE_SIZE,
					this.currentSearchResults.getPageIndex() + 1);
			this.doSearch(searchQuery);
		}
	}

	@Override
	public void searchComplete(final SearchResults searchResults) {
		this.currentSearchResults = searchResults;
		final SearchResultsView searchResultsView = this.getSearchResultsView();
		if (searchResults.isLastPage()) {
			searchResultsView.setAllowMoreResults(false);
		}
		searchResultsView.addSearchResults(searchResults.getEntries());
		searchResultsView.showLoadingIndicator(false);
	}

}
