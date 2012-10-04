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
 * A {@link Dictionary} using a {@link DictionaryDataStore} for queries.
 */
public class DataStoreDictionary implements Dictionary {

	private final DictionaryDataStore dataStore;

	public DataStoreDictionary(final DictionaryDataStore dataStore) {
		this.dataStore = dataStore;
	}

	@Override
	public List<DictionaryEntry> find(final String queryString, final SearchType searchType) {
		return searchType.doSearch(this, queryString);
	}

	@Override
	public List<DictionaryEntry> findChinese(final String queryString) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DictionaryEntry> findPinyin(final String queryString) {
		final List<PinyinSyllable> pinyinSyllables = new PinyinParser(queryString).parse();
		return this.dataStore.findPinyin(pinyinSyllables);
	}

	@Override
	public List<DictionaryEntry> findDefinitions(final String queryString) {
		// TODO Auto-generated method stub
		return null;
	}


}
