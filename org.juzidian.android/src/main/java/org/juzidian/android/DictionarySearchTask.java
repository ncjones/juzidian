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

import org.juzidian.core.Dictionary;
import org.juzidian.core.DictionaryEntry;
import org.juzidian.core.SearchQuery;

import android.os.AsyncTask;

/**
 * Task to perform asynchronous searches on a dictionary.
 */
public class DictionarySearchTask extends AsyncTask<SearchQuery, Void, List<DictionaryEntry>> {

	private final Dictionary dictionary;

	private final DictionarySearchTaskListener listener;

	public DictionarySearchTask(final Dictionary dictionary, final DictionarySearchTaskListener listener) {
		this.dictionary = dictionary;
		this.listener = listener;
	}

	@Override
	protected List<DictionaryEntry> doInBackground(final SearchQuery... searchQueries) {
		final SearchQuery searchQuery = searchQueries[0];
		return this.dictionary.find(searchQuery).getEntries();
	}

	@Override
	protected void onPostExecute(final List<DictionaryEntry> result) {
		this.listener.searchComplete(result);
	}

}
