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
package org.juzidian.core;

import java.util.List;

/**
 * A type of dictionary search: {@link #HANZI}, {@link #PINYIN} or
 * {@link #REVERSE}.
 */
public enum SearchType {

	/**
	 * A search for Chinese characters.
	 */
	HANZI {
		@Override
		public List<DictionaryEntry> doSearch(final Dictionary dictionary, final String query, final long limit, final long offset, final SearchCanceller canceller) {
			return dictionary.findChinese(query, limit, offset, canceller);
		}
	},

	/**
	 * A search for Pinyin sounds.
	 */
	PINYIN {
		@Override
		public List<DictionaryEntry> doSearch(final Dictionary dictionary, final String query, final long limit, final long offset, final SearchCanceller canceller) {
			return dictionary.findPinyin(query, limit, offset, canceller);
		}
	},

	/**
	 * A search for definitions.
	 */
	REVERSE {
		@Override
		public List<DictionaryEntry> doSearch(final Dictionary dictionary, final String query, final long limit, final long offset, final SearchCanceller canceller) {
			return dictionary.findDefinitions(query, limit, offset, canceller);
		}
	};

	/**
	 * Perform the appropriate search on the dictionary for this search type.
	 * 
	 * @param dictionary a dictionary to search.
	 * @param query the query string to search for.
	 * @param limit the maximum number of results to find.
	 * @param offset the result index to start searching from.
	 * @return the dictionary's search result.
	 */
	abstract List<DictionaryEntry> doSearch(Dictionary dictionary, String query, long limit, long offset, final SearchCanceller canceller);

}
