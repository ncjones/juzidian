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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link ChineseEnglishDictionary} which keeps all data in-memory and
 * finds words using sequential search.
 */
public class SimpleChineseEnglishDictionary implements ChineseEnglishDictionary {

	private final List<ChineseWord> words = new ArrayList<ChineseWord>();

	public void addWord(final ChineseWord word) {
		this.words.add(word);
	}

	public List<ChineseWord> getWords() {
		return this.words;
	}

	@Override
	public List<ChineseWord> findChinese(final String queryString) {
		final List<ChineseWord> matchingWords = new ArrayList<ChineseWord>();
		for (final ChineseWord word : this.words) {
			if (word.getSimplified().startsWith(queryString) || word.getTraditional().startsWith(queryString)) {
				matchingWords.add(word);
			}
		}
		return matchingWords;
	}

	@Override
	public List<ChineseWord> findPinyin(final String queryString) {
		final List<ChineseWord> matchingWords = new ArrayList<ChineseWord>();
		final List<PinyinSyllable> pinyinSyllables = new PinyinParser(queryString).parse();
		for (final ChineseWord word : this.words) {
			if (word.pinyinStartsWith(pinyinSyllables)) {
				matchingWords.add(word);
			}
		}
		return matchingWords;
	}

	@Override
	public List<ChineseWord> findDefinitions(final String queryString) {
		final List<ChineseWord> matchingWords = new ArrayList<ChineseWord>();
		for (final ChineseWord word : this.words) {
			if (word.getDefinitions().contains(queryString)) {
				matchingWords.add(word);
			}
		}
		return matchingWords;
	}

	@Override
	public List<ChineseWord> find(final String queryString, final SearchType searchType) {
		return searchType.doSearch(this, queryString);
	}

}
