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
import java.util.Collection;
import java.util.List;

import org.juzidian.cedict.CedictEntry;
import org.juzidian.cedict.CedictPinyinSyllable;

/**
 * A dictionary definition of a Chinese word.
 */
public class ChineseWord {

	private final CedictEntry cedictEntry;

	public ChineseWord(final CedictEntry cedictEntry) {
		this.cedictEntry = cedictEntry;
	}

	/**
	 * @return the traditional Chinese representation of the word.
	 */
	public String getTraditional() {
		return this.cedictEntry.getTraditionalCharacters();
	}

	/**
	 * @return the simplified Chinese representation of the word.
	 */
	public String getSimplified() {
		return this.cedictEntry.getSimplifiedCharacters();
	}

	/**
	 * @return the Pinyin phonetic representation of the word.
	 */
	public List<PinyinSyllable> getPinyin() {
		return this.createPinyinSyllables(this.cedictEntry.getPinyinSyllables());
	}

	/**
	 * @return a list of English definitions for the word.
	 */
	public List<String> getDefinitions() {
		return this.cedictEntry.getDefinitions();
	}

	private List<PinyinSyllable> createPinyinSyllables(final List<CedictPinyinSyllable> pinyinSyllables) {
		final List<PinyinSyllable> syllables = new ArrayList<PinyinSyllable>();
		for (final CedictPinyinSyllable cedictSyllable : pinyinSyllables) {
			final PinyinSyllable syllable = new PinyinSyllable(cedictSyllable.getLetters(), Tone.valueOf(cedictSyllable.getToneNumber()));
			syllables.add(syllable);
		}
		return syllables;
	}

	/**
	 * @param pinyinSyllables a list of {@link PinyinSyllable}.
	 * @return <code>true</code> if this word starts with the given syllables.
	 */
	public boolean pinyinStartsWith(final Collection<PinyinSyllable> pinyinSyllables) {
		final List<PinyinSyllable> actualPinyinSyllables = this.getPinyin();
		int index = 0;
		for (final PinyinSyllable pinyinSyllable : pinyinSyllables) {
			if (index >= actualPinyinSyllables.size()) {
				return false;
			}
			final PinyinSyllable actualPinyinSyllable = actualPinyinSyllables.get(index);
			if (!pinyinSyllable.matches(actualPinyinSyllable)) {
				return false;
			}
			index += 1;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ChineseWord [" + this.getTraditional() + ", " + this.getSimplified() + ", " + this.getPinyinString() + ", " + this.getDefinitions() + "]";
	}

	private String getPinyinString() {
		final StringBuilder stringBuilder = new StringBuilder();
		for (final PinyinSyllable pinyinSyllable : this.getPinyin()) {
			stringBuilder.append(pinyinSyllable.getDisplayValue()).append(" ");
		}
		return stringBuilder.toString().trim();
	}

}
