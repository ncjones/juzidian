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

import java.util.ArrayList;
import java.util.List;

import org.juzidian.cedict.CedictEntry;
import org.juzidian.cedict.CedictPinyinSyllable;
import org.juzidian.core.DictionaryEntry;
import org.juzidian.pinyin.PinyinSyllable;
import org.juzidian.pinyin.Tone;

/**
 * A {@link DictionaryEntry} which delegates to a {@link CedictEntry}.
 */
class CedictDictionaryEntryAdaptor extends DictionaryEntry {

	private final CedictEntry cedictEntry;

	public CedictDictionaryEntryAdaptor(final CedictEntry cedictEntry) {
		this.cedictEntry = cedictEntry;
	}

	@Override
	public String getTraditional() {
		return this.cedictEntry.getTraditionalCharacters();
	}

	@Override
	public String getSimplified() {
		return this.cedictEntry.getSimplifiedCharacters();
	}

	@Override
	public List<PinyinSyllable> getPinyin() {
		return this.createPinyinSyllables(this.cedictEntry.getPinyinSyllables());
	}

	@Override
	public List<String> getDefinitions() {
		return this.cedictEntry.getDefinitions();
	}

	private List<PinyinSyllable> createPinyinSyllables(final List<CedictPinyinSyllable> pinyinSyllables) {
		final List<PinyinSyllable> syllables = new ArrayList<PinyinSyllable>();
		for (final CedictPinyinSyllable cedictSyllable : pinyinSyllables) {
			final String syllableLetters = cedictSyllable.getLetters().replace("u:", "ü");
			final Tone syllableTone = Tone.valueOf(cedictSyllable.getToneNumber());
			final PinyinSyllable syllable = new PinyinSyllable(syllableLetters, syllableTone);
			syllables.add(syllable);
		}
		return syllables;
	}

}
