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

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A searchable Chinese-English dictionary.
 */
public class Dictionary {

	private static final Logger LOGGER = LoggerFactory.getLogger(Dictionary.class);

	private final DictionaryDataStore dataStore;

	private final PinyinParser pinyinParser = new PinyinParser();

	@Inject
	public Dictionary(final DictionaryDataStore dataStore) {
		this.dataStore = dataStore;
	}

	/**
	 * Find all Chinese words that match the search criteria and pagination
	 * bounds.
	 * <p>
	 * If the number of results returned is less than the given query page size
	 * then queries for subsequent pages will return empty results.
	 * 
	 * @param query the {@link SearchQuery} to find entries for.
	 * @return a {@link SearchResults}.
	 */
	public SearchResults find(final SearchQuery query) {
		LOGGER.debug("Find entries: {}", query);
		final long start = System.nanoTime();
		final List<DictionaryEntry> searchResults = query.getSearchType().doSearch(this, query.getSearchText(), query.getPageSize(),
				query.getPageSize() * query.getPageIndex());
		final long end = System.nanoTime();
		LOGGER.info("Found {} words matching '{}' in {} seconds.", new Object[] { searchResults.size(), query.getSearchText(),
				((end - start) / 1000 / 1000 / 1000f) });
		return new SearchResults(query.getPageSize(), query.getPageIndex(), searchResults);
	}

	/**
	 * Find all Chinese words that begin with the given Chinese character query
	 * string.
	 * 
	 * @param queryString Chinese characters (simplified or traditional) to
	 *        search for.
	 * @param limit the maximum number of results to find.
	 * @param offset the result index to start searching from.
	 * @return a list of {@link DictionaryEntry}.
	 */
	List<DictionaryEntry> findChinese(final String queryString, final long limit, final long offset) {
		LOGGER.debug("Find chinese: " + queryString);
		return this.dataStore.findChinese(queryString, limit, offset);
	}

	/**
	 * Find all Chinese words whose sound begins with the given Pinyin
	 * romanisation query string.
	 * 
	 * @param queryString Pinyin syllables to search for.
	 * @param limit the maximum number of results to find.
	 * @param offset the result index to start searching from.
	 * @return a list of {@link DictionaryEntry}.
	 */
	List<DictionaryEntry> findPinyin(final String queryString, final long limit, final long offset) {
		LOGGER.debug("Find pinyin: " + queryString);
		final List<PinyinSyllable> pinyinSyllables = this.pinyinParser.parse(queryString);
		return this.dataStore.findPinyin(pinyinSyllables, limit, offset);
	}

	/**
	 * Find all Chinese words with definitions that contain the given English
	 * word query string.
	 * 
	 * @param queryString English words or partial words.
	 * @param limit the maximum number of results to find.
	 * @param offset the result index to start searching from.
	 * @return a list of {@link DictionaryEntry}.
	 */
	List<DictionaryEntry> findDefinitions(final String queryString, final long limit, final long offset) {
		LOGGER.debug("Find definitions: " + queryString);
		return this.dataStore.findDefinitions(queryString, limit, offset);
	}

}
