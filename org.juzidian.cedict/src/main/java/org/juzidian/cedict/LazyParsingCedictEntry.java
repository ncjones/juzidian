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
package org.juzidian.cedict;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A {@link CedictEntry} which lazily parses the Pinyin syllables and definition
 * list.
 */
class LazyParsingCedictEntry implements CedictEntry {

	private final String traditional;

	private final String simplified;

	private final String pinyin;

	private final String english;

	public LazyParsingCedictEntry(final String traditional, final String simplified, final String pinyin, final String english) {
		this.traditional = traditional;
		this.simplified = simplified;
		this.pinyin = pinyin;
		this.english = english;
	}

	@Override
	public String getTraditionalCharacters() {
		return this.traditional;
	}

	@Override
	public String getSimplifiedCharacters() {
		return this.simplified;
	}

	@Override
	public List<CedictPinyinSyllable> getPinyinSyllables() {
		return createSyllables(this.pinyin.split(" "));
	}

	private List<CedictPinyinSyllable> createSyllables(final String[] rawPinyinSyllables) {
		final List<CedictPinyinSyllable> syllables = new ArrayList<CedictPinyinSyllable>(rawPinyinSyllables.length);
		for (final String rawPinyinSyllable : rawPinyinSyllables) {
			if (rawPinyinSyllable.length() == 1) {
				/*
				 * Some CEDict entries contain punctuation or space-separated
				 * English initialisms in the pinyin component.
				 */
				continue;
			}
			syllables.add(createSyllable(rawPinyinSyllable));
		}
		return syllables;
	}

	private CedictPinyinSyllable createSyllable(final String rawPinyinSyllable) {
		final String letters = rawPinyinSyllable.substring(0, rawPinyinSyllable.length() - 1);
		final char lastChar = rawPinyinSyllable.charAt(rawPinyinSyllable.length() - 1);
		final int toneNumber = Character.isDigit(lastChar) ? lastChar - 48 : 0;
		return new CedictPinyinSyllable(letters, toneNumber);
	}

	@Override
	public List<String> getDefinitions() {
		return Arrays.asList(this.english.split("/"));
	}

	@Override
	public String toString() {
		return "LazyParsingCedictEntry [traditional=" + this.traditional + ", simplified=" + this.simplified + ", pinyin=" + this.pinyin + ", english="
				+ this.english + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.english == null) ? 0 : this.english.hashCode());
		result = prime * result + ((this.pinyin == null) ? 0 : this.pinyin.hashCode());
		result = prime * result + ((this.simplified == null) ? 0 : this.simplified.hashCode());
		result = prime * result + ((this.traditional == null) ? 0 : this.traditional.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final LazyParsingCedictEntry other = (LazyParsingCedictEntry) obj;
		if (this.traditional == null) {
			if (other.traditional != null) {
				return false;
			}
		} else if (!this.traditional.equals(other.traditional)) {
			return false;
		}
		if (this.simplified == null) {
			if (other.simplified != null) {
				return false;
			}
		} else if (!this.simplified.equals(other.simplified)) {
			return false;
		}
		if (this.pinyin == null) {
			if (other.pinyin != null) {
				return false;
			}
		} else if (!this.pinyin.equals(other.pinyin)) {
			return false;
		}
		if (this.english == null) {
			if (other.english != null) {
				return false;
			}
		} else if (!this.english.equals(other.english)) {
			return false;
		}
		return true;
	}

}
