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

import java.util.List;

import org.juzidian.core.DictionaryEntry;
import org.juzidian.core.PinyinSyllable;

/**
 * A persisted, searchable Chinese {@link DictionaryEntry} data store.
 */
public interface DictionaryDataStore {

	/**
	 * Find dictionary entries in the data store that begin with the given
	 * pinyin syllables.
	 * 
	 * @param pinyin a sequence of {@link PinyinSyllable}.
	 * @return a list of {@link DictionaryEntry}.
	 */
	List<DictionaryEntry> findPinyin(List<PinyinSyllable> pinyin);

	/**
	 * Find dictionary entries in the data store that begin with the given
	 * Chinese characters.
	 * 
	 * @param chineseCharacters Chinese character text to find.
	 * @return a list of {@link DictionaryEntry}.
	 */
	List<DictionaryEntry> findChinese(String chineseCharacters);

	/**
	 * Find dictionary entries in the data store that contain the given English
	 * text.
	 * 
	 * @param englishWords English definition text to find.
	 * @return a list of {@link DictionaryEntry}.
	 */
	List<DictionaryEntry> findDefinitions(String englishWords);

}
