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

import java.util.Collection;
import java.util.List;

/**
 * A dictionary definition of a Chinese word.
 */
public abstract class DictionaryEntry {

	/**
	 * @return the traditional Chinese representation of the word.
	 */
	public abstract String getTraditional();

	/**
	 * @return the simplified Chinese representation of the word.
	 */
	public abstract String getSimplified();

	/**
	 * @return the Pinyin phonetic representation of the word.
	 */
	public abstract List<PinyinSyllable> getPinyin();

	/**
	 * @return a list of English definitions for the word.
	 */
	public abstract List<String> getDefinitions();

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
		return this.getClass().getSimpleName() + " [" + this.getTraditional() + ", " + this.getSimplified() + ", " + this.getPinyinString()
				+ ", " + this.getDefinitions() + "]";
	}

	private String getPinyinString() {
		final StringBuilder stringBuilder = new StringBuilder();
		for (final PinyinSyllable pinyinSyllable : this.getPinyin()) {
			stringBuilder.append(pinyinSyllable.getDisplayValue()).append(" ");
		}
		return stringBuilder.toString().trim();
	}

}
