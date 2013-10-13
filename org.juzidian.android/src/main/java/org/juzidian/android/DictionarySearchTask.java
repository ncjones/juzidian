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

import org.juzidian.core.SearchCancelledException;
import org.juzidian.core.SearchResults;
import org.juzidian.core.SearchResultsFuture;

import android.os.AsyncTask;

/**
 * Task to asynchronously get results from a {@link SearchResultsFuture}.
 */
public class DictionarySearchTask extends AsyncTask<SearchResultsFuture, Void, SearchResults> {

	private final DictionarySearchTaskListener listener;

	public DictionarySearchTask(final DictionarySearchTaskListener listener) {
		this.listener = listener;
	}

	@Override
	protected SearchResults doInBackground(final SearchResultsFuture... searchResultsFutures) {
		final SearchResultsFuture searchResultsFuture = searchResultsFutures[0];
		try {
			return searchResultsFuture.getResults();
		} catch (SearchCancelledException e) {
			return null;
		}
	}

	@Override
	protected void onPostExecute(final SearchResults result) {
		this.listener.searchComplete(result);
	}

}
