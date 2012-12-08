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

import java.util.List;

import org.juzidian.core.DictionaryEntry;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 * Displays dictionary entry search results.
 */
public class SearchResultsView extends RelativeLayout {

	private final SearchResultsListAdapter searchResultsListAdapter;

	public SearchResultsView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.search_results, this, true);
		final ListView listView = this.getSearchResultListView();
		this.searchResultsListAdapter = new SearchResultsListAdapter(listView.getContext());
		listView.setAdapter(this.searchResultsListAdapter);
	}

	private ProgressBar getSearchLoadingIndicator() {
		return (ProgressBar) this.findViewById(R.id.searchLoadingIndicator);
	}

	private ListView getSearchResultListView() {
		return (ListView) this.findViewById(R.id.searchResultsListView);
	}

	public void showLoadingIndicator(final boolean show) {
		if (show) {
			this.getSearchResultListView().setVisibility(View.GONE);
			this.getSearchLoadingIndicator().setVisibility(View.VISIBLE);
		} else {
			this.getSearchLoadingIndicator().setVisibility(View.GONE);
			this.getSearchResultListView().setVisibility(View.VISIBLE);
		}
	}

	public void clearSearchResults() {
		this.searchResultsListAdapter.clear();
	}

	public void addSearchResults(final List<DictionaryEntry> searchResults) {
		final SearchResultsListAdapter adapter = this.searchResultsListAdapter;
		adapter.addAll(searchResults);
	}

}
