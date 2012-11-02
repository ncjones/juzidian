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
	 * Find all Chinese words that match the search criteria.
	 * 
	 * @param queryString Chinese characters, Pinyin syllables or English words.
	 * @param searchType the {@link SearchType} indicating how to interpret the
	 *        query string.
	 * @return a list of {@link DictionaryEntry}.
	 */
	public List<DictionaryEntry> find(final String queryString, final SearchType searchType) {
		LOGGER.debug("Find entries: " + searchType + ", " + queryString);
		final long start = System.nanoTime();
		final List<DictionaryEntry> searchResults = searchType.doSearch(this, queryString);
		final long end = System.nanoTime();
		LOGGER.info("Found {} words matching '{}' in {} seconds.", new Object[] { searchResults.size(), queryString,
				((end - start) / 1000 / 1000 / 1000f) });
		return searchResults;
	}

	/**
	 * Find all Chinese words that begin with the given Chinese character query
	 * string.
	 * 
	 * @param queryString Chinese characters (simplified or traditional) to
	 *        search for.
	 * @return a list of {@link DictionaryEntry}.
	 */
	List<DictionaryEntry> findChinese(final String queryString) {
		LOGGER.debug("Find chinese: " + queryString);
		return this.dataStore.findChinese(queryString);
	}

	/**
	 * Find all Chinese words whose sound begins with the given Pinyin
	 * romanisation query string.
	 * 
	 * @param queryString Pinyin syllables to search for.
	 * @return a list of {@link DictionaryEntry}.
	 */
	List<DictionaryEntry> findPinyin(final String queryString) {
		LOGGER.debug("Find pinyin: " + queryString);
		final List<PinyinSyllable> pinyinSyllables = this.pinyinParser.parse(queryString);
		return this.dataStore.findPinyin(pinyinSyllables);
	}

	/**
	 * Find all Chinese words with definitions that contain the given English
	 * word query string.
	 * 
	 * @param queryString English words or partial words.
	 * @return a list of {@link DictionaryEntry}.
	 */
	List<DictionaryEntry> findDefinitions(final String queryString) {
		LOGGER.debug("Find definitions: " + queryString);
		return this.dataStore.findDefinitions(queryString);
	}

}
