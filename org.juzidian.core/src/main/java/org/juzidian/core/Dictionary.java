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
 * A searchable Chinese-English dictionary.
 */
public interface Dictionary {

	/**
	 * Find all Chinese words that match the search criteria.
	 * 
	 * @param queryString Chinese characters, Pinyin syllables or English words.
	 * @param searchType the {@link SearchType} indicating how to interpret the
	 *        query string.
	 * @return a list of {@link DictionaryEntry}.
	 */
	List<DictionaryEntry> find(String queryString, SearchType searchType);

	/**
	 * Find all Chinese words that begin with the given Chinese character query
	 * string.
	 * 
	 * @param queryString Chinese characters (simplified or traditional) to
	 *        search for.
	 * @return a list of {@link DictionaryEntry}.
	 */
	List<DictionaryEntry> findChinese(String queryString);

	/**
	 * Find all Chinese words whose sound begins with the given Pinyin
	 * romanisation query string.
	 * 
	 * @param queryString Pinyin syllables to search for.
	 * @return a list of {@link DictionaryEntry}.
	 */
	List<DictionaryEntry> findPinyin(String queryString);

	/**
	 * Find all Chinese words with definitions that contain the given English
	 * word query string.
	 * 
	 * @param queryString English words or partial words.
	 * @return a list of {@link DictionaryEntry}.
	 */
	List<DictionaryEntry> findDefinitions(String queryString);

}
