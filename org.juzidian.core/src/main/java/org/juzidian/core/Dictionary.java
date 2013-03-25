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

import java.lang.Character.UnicodeBlock;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.juzidian.pinyin.PinyinParser;
import org.juzidian.pinyin.PinyinSyllable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A searchable Chinese-English dictionary.
 */
@Singleton
public class Dictionary {

	private static final Logger LOGGER = LoggerFactory.getLogger(Dictionary.class);

	private static final Collection<UnicodeBlock> CHINESE_UNICODE_BLOCKS = Arrays.asList(
			UnicodeBlock.CJK_COMPATIBILITY,
			UnicodeBlock.CJK_COMPATIBILITY_FORMS,
			UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS,
			UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT,
			UnicodeBlock.CJK_RADICALS_SUPPLEMENT,
			UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION,
			UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS,
			UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A,
			UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B,
			UnicodeBlock.KANGXI_RADICALS,
			UnicodeBlock.IDEOGRAPHIC_DESCRIPTION_CHARACTERS
			);

	private static Set<SearchType> PINYIN_AND_REVERSE = new HashSet<SearchType>(Arrays.asList(SearchType.PINYIN, SearchType.REVERSE));

	private final DictionaryDataStore dataStore;

	private final PinyinParser pinyinParser;

	@Inject
	public Dictionary(final DictionaryDataStore dataStore, final PinyinParser pinyinParser) {
		this.dataStore = dataStore;
		this.pinyinParser = pinyinParser;
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
		final String filteredQueryString = this.filterPinyinQuery(queryString);
		final List<PinyinSyllable> pinyinSyllables = this.pinyinParser.parse(filteredQueryString);
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

	private String filterPinyinQuery(final String queryString) {
		return queryString.replace('v', 'ü');
	}

	/**
	 * Get the search types that are applicable for the given text.
	 * <p>
	 * If the input is <code>null</code> or empty or contains only whitespace
	 * then no search types are considered applicable and an empty set is
	 * returned.
	 * 
	 * @param searchText a search query input.
	 * @return a Set of {@link SearchType}.
	 */
	public Set<SearchType> getApplicableSearchTypes(final String searchText) {
		if (searchText == null || "".equals(searchText.trim())) {
			return Collections.emptySet();
		}
		for (final char c : searchText.toCharArray()) {
			if (isChineseCharacter(c)) {
				return Collections.singleton(SearchType.HANZI);
			}
		}
		if (this.pinyinParser.isValid(this.filterPinyinQuery(searchText))) {
			return Collections.unmodifiableSet(PINYIN_AND_REVERSE);
		}
		return Collections.singleton(SearchType.REVERSE);
	}

	private static boolean isChineseCharacter(final char c) {
		return CHINESE_UNICODE_BLOCKS.contains(UnicodeBlock.of(c));
	}

}
