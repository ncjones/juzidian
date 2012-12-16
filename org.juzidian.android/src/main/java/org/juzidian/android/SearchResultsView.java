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
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Displays dictionary entry search results.
 */
public class SearchResultsView extends RelativeLayout {

	private final SearchResultsListAdapter searchResultsListAdapter;

	private PageRequestListener pageRequestListener;

	private boolean lastItemScrolled;

	private boolean allowMoreResults = true;

	public SearchResultsView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.search_results, this, true);
		final ListView listView = this.getSearchResultListView();
		this.searchResultsListAdapter = new SearchResultsListAdapter(listView.getContext());
		listView.setAdapter(this.searchResultsListAdapter);
		listView.setOnScrollListener(new SearchResultsScrollListener());
	}

	public void setPageRequestListener(final PageRequestListener pageRequestListener) {
		this.pageRequestListener = pageRequestListener;
	}

	private ListView getSearchResultListView() {
		return (ListView) this.findViewById(R.id.searchResultsListView);
	}

	public void showLoadingIndicator(final boolean show) {
		this.searchResultsListAdapter.setLoading(show);
	}

	public void clearSearchResults() {
		this.searchResultsListAdapter.clear();
		this.lastItemScrolled = false;
		this.allowMoreResults = true;
	}

	public void addSearchResults(final List<DictionaryEntry> searchResults) {
		this.searchResultsListAdapter.addAll(searchResults);
		this.lastItemScrolled = false;
	}

	/**
	 * @param allowMoreResults <code>true</code> if more search results can be
	 *        requested and the results view should attempt to load more when
	 *        the list is scrolled to the bottom.
	 */
	public void setAllowMoreResults(final boolean allowMoreResults) {
		this.allowMoreResults = allowMoreResults;
	}

	private void lastItemScrolled() {
		if (!this.allowMoreResults || this.lastItemScrolled) {
			return;
		}
		if (this.pageRequestListener != null) {
			this.lastItemScrolled = true;
			this.pageRequestListener.pageRequested();
		}
	}

	private final class SearchResultsScrollListener implements AbsListView.OnScrollListener {

		@Override
		public void onScrollStateChanged(final AbsListView view, final int scrollState) {
		}

		@Override
		public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
			if (totalItemCount == 0) {
				return;
			}
			final int lastVisibleItem = firstVisibleItem + visibleItemCount;
			if (lastVisibleItem >= totalItemCount - 1) {
				SearchResultsView.this.lastItemScrolled();
			}
		}
	}

}
