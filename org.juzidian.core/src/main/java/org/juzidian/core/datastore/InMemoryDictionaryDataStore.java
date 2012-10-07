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
package org.juzidian.core.datastore;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.juzidian.cedict.CedictLoader;
import org.juzidian.core.Dictionary;
import org.juzidian.core.DictionaryEntry;
import org.juzidian.core.PinyinSyllable;

/**
 * A simple {@link Dictionary} which keeps all data in-memory and
 * finds words using sequential search.
 */
public class InMemoryDictionaryDataStore implements DictionaryDataStore {

	private final List<DictionaryEntry> words = new ArrayList<DictionaryEntry>();

	private final CedictLoader cedictLoader;

	@Inject
	public InMemoryDictionaryDataStore(final CedictLoader cedictLoader) {
		this.cedictLoader = cedictLoader;
		final InMemoryDictionaryDataStoreLoadHandler handler = new InMemoryDictionaryDataStoreLoadHandler(this);
		try {
			this.cedictLoader.loadEntries(handler);
		} catch (final IOException e) {
			throw new RuntimeException("Failed to load cedict entries.", e);
		}
		System.out.println(MessageFormat.format("Loaded {0} entries in {1, number, #.###} seconds", handler.getEntryCount(),
				handler.getDuration() / 1000 / 1000 / 1000d));
	}

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
	public List<DictionaryEntry> findPinyin(final List<PinyinSyllable> pinyinSyllables) {
		final List<DictionaryEntry> matchingWords = new ArrayList<DictionaryEntry>();
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

}
