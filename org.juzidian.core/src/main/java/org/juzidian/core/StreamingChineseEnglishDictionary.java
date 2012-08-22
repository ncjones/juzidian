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

import java.io.IOException;
import java.util.List;

import org.juzidian.cedict.CedictLoader;

/**
 * A basic sequential search dictionary that does not load all words into
 * memory.
 */
public class StreamingChineseEnglishDictionary implements ChineseEnglishDictionary {

	private final CedictLoader cedictLoader;

	private final InputStreamProvider inputStreamProvider;

	public StreamingChineseEnglishDictionary(final CedictLoader cedictLoader, final InputStreamProvider inputStreamProvider) {
		this.cedictLoader = cedictLoader;
		this.inputStreamProvider = inputStreamProvider;
	}

	@Override
	public List<ChineseWord> find(final String queryString, final SearchType searchType) {
		return searchType.doSearch(this, queryString);
	}

	@Override
	public List<ChineseWord> findChinese(final String queryString) {
		return this.findWords(new HanziSearchWordCollector(queryString));
	}

	@Override
	public List<ChineseWord> findPinyin(final String queryString) {
		final List<PinyinSyllable> pinyinSyllables = new PinyinParser(queryString).parse();
		return this.findWords(new PinyinSearchWordCollector(pinyinSyllables));
	}

	@Override
	public List<ChineseWord> findDefinitions(final String queryString) {
		return this.findWords(new DefinitionSearchWordCollector(queryString));
	}

	private List<ChineseWord> findWords(final SearchWordCollector wordCollector) {
		try {
			this.cedictLoader.loadEntries(this.inputStreamProvider.getInputStream(), wordCollector);
		} catch (final IOException e) {
			throw new RuntimeException("Failed to load cedict entries", e);
		}
		return wordCollector.getWords();
	}

}
