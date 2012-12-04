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
		public List<DictionaryEntry> doSearch(final Dictionary dictionary, final String query, final long limit, final long offset) {
			return dictionary.findChinese(query, limit, offset);
		}
	},

	/**
	 * A search for Pinyin sounds.
	 */
	PINYIN {
		@Override
		public List<DictionaryEntry> doSearch(final Dictionary dictionary, final String query, final long limit, final long offset) {
			return dictionary.findPinyin(query, limit, offset);
		}
	},

	/**
	 * A search for definitions.
	 */
	REVERSE {
		@Override
		public List<DictionaryEntry> doSearch(final Dictionary dictionary, final String query, final long limit, final long offset) {
			return dictionary.findDefinitions(query, limit, offset);
		}
	};

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

	private static final PinyinParser PINYIN_PARSER = new PinyinParser();

	private static Set<SearchType> PINYIN_AND_REVERSE = new HashSet<SearchType>(Arrays.asList(PINYIN, REVERSE));

	/**
	 * Perform the appropriate search on the dictionary for this search type.
	 * 
	 * @param dictionary a dictionary to search.
	 * @param query the query string to search for.
	 * @param limit the maximum number of results to find.
	 * @param offset the result index to start searching from.
	 * @return the dictionary's search result.
	 */
	abstract List<DictionaryEntry> doSearch(Dictionary dictionary, String query, long limit, long offset);

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
	public static Set<SearchType> allApplicableFor(final String searchText) {
		if (searchText == null || "".equals(searchText.trim())) {
			return Collections.emptySet();
		}
		for (final char c : searchText.toCharArray()) {
			if (isChineseCharacter(c)) {
				return Collections.singleton(HANZI);
			}
		}
		if (PINYIN_PARSER.isValid(searchText)) {
			return Collections.unmodifiableSet(PINYIN_AND_REVERSE);
		}
		return Collections.singleton(REVERSE);
	}

	private static boolean isChineseCharacter(final char c) {
		/*
		 * http://stackoverflow.com/questions/1675739/to-split-only-chinese-characters-in-java
		 */
		return CHINESE_UNICODE_BLOCKS.contains(UnicodeBlock.of(c));
	}

}
