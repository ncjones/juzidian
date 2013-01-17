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
package org.juzidian.build.data;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.juzidian.cedict.CedictEntry;
import org.juzidian.cedict.CedictLoadHandler;
import org.juzidian.cedict.CedictPinyinSyllable;
import org.juzidian.core.DictionaryEntry;
import org.juzidian.pinyin.PinyinHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EntryCollector implements CedictLoadHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(EntryCollector.class);

	private static final PinyinHelper PINYIN_HELPER = new PinyinHelper();

	private final List<DictionaryEntry> entries = new LinkedList<DictionaryEntry>();

	private final List<DictionaryEntry> excludedEntries = new LinkedList<DictionaryEntry>();

	public Collection<DictionaryEntry> getEntries() {
		return this.entries;
	}

	@Override
	public void loadingStarted() {

	}

	@Override
	public void entryLoaded(final CedictEntry cedictEntry) {
		final DictionaryEntry entry = new CedictDictionaryEntryAdaptor(cedictEntry);
		if (this.allPinyinValid(cedictEntry)) {
			this.entries.add(entry);
		} else {
			this.excludedEntries.add(entry);
			LOGGER.debug("Excluding entry with invalid Pinyin: " + cedictEntry);
		}
	}

	private boolean allPinyinValid(final CedictEntry cedictEntry) {
		for (final CedictPinyinSyllable syllable : cedictEntry.getPinyinSyllables()) {
			final String letters = syllable.getLettersNormalized();
			if (!PINYIN_HELPER.getValidSyllables().contains(letters)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void loadingFinished() {
		LOGGER.warn("Excluded {} entries with invalid Pinyin.", this.excludedEntries.size());
	}

}