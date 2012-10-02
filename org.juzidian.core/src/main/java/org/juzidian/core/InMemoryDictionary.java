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
 * A simple {@link Dictionary} which keeps all data in-memory and
 * finds words using sequential search.
 */
public class InMemoryDictionary implements Dictionary {

	private final List<DictionaryEntry> words = new ArrayList<DictionaryEntry>();

	public void addWord(final DictionaryEntry word) {
		this.words.add(word);
	}

	public List<DictionaryEntry> getWords() {
		return this.words;
	}

	@Override
	public List<DictionaryEntry> findChinese(final String queryString) {
		final List<DictionaryEntry> matchingWords = new ArrayList<DictionaryEntry>();
		for (final DictionaryEntry word : this.words) {
			if (word.getSimplified().startsWith(queryString) || word.getTraditional().startsWith(queryString)) {
				matchingWords.add(word);
			}
		}
		return matchingWords;
	}

	@Override
	public List<DictionaryEntry> findPinyin(final String queryString) {
		final List<DictionaryEntry> matchingWords = new ArrayList<DictionaryEntry>();
		final List<PinyinSyllable> pinyinSyllables = new PinyinParser(queryString).parse();
		for (final DictionaryEntry word : this.words) {
			if (word.pinyinStartsWith(pinyinSyllables)) {
				matchingWords.add(word);
			}
		}
		return matchingWords;
	}

	@Override
	public List<DictionaryEntry> findDefinitions(final String queryString) {
		final List<DictionaryEntry> matchingWords = new ArrayList<DictionaryEntry>();
		for (final DictionaryEntry word : this.words) {
			if (word.getDefinitions().contains(queryString)) {
				matchingWords.add(word);
			}
		}
		return matchingWords;
	}

	@Override
	public List<DictionaryEntry> find(final String queryString, final SearchType searchType) {
		return searchType.doSearch(this, queryString);
	}

}
