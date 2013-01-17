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

import java.util.HashMap;
import java.util.Map;

import org.juzidian.pinyin.PinyinHelper;

/**
 * A phonetic representation of a Chinese syllable using the Pinyin romanisation
 * system.
 */
public class PinyinSyllable {

	private static final PinyinHelper PINYIN_HELPER = new PinyinHelper();

	private static Map<String, Integer> DIACRITIC_INDICES = new HashMap<String, Integer>();
	static {
		for (final String pinyin : PINYIN_HELPER.getValidSyllables()) {
			DIACRITIC_INDICES.put(pinyin, getToneDiacriticIndex(pinyin));
		}
	}

	private final String letters;

	private final Tone tone;

	public PinyinSyllable(final String letters, final Tone tone) {
		if (letters == null) {
			throw new IllegalArgumentException("letters is null");
		}
		if (tone == null) {
			throw new IllegalArgumentException("tone is null");
		}
		this.letters = letters;
		this.tone = tone;
	}

	public PinyinSyllable(final String letters) {
		this(letters, Tone.ANY);
	}

	public String getLetters() {
		return this.letters;
	}

	public Tone getTone() {
		return this.tone;
	}

	/**
	 * Check if this syllable has the same letters and a matching tone of the
	 * given syllable.
	 * 
	 * @param pinyinSyllable another syllable.
	 * @return <code>true</code> if this syllable matches the given syllable.
	 */
	public boolean matches(final PinyinSyllable pinyinSyllable) {
		return this.letters.equals(pinyinSyllable.letters) && this.tone.matches(pinyinSyllable.tone);
	}

	/**
	 * @return the display value of the syllable using diacritical tone marks.
	 */
	public String getDisplayValue() {
		if (!PINYIN_HELPER.getValidSyllables().contains(this.letters)) {
			return this.letters;
		}
		switch (this.tone) {
		case NEUTRAL:
			return "·" + this.letters;
		default:
			return this.getLettersWithDiacritic();
		}
	}

	private String getLettersWithDiacritic() {
		final int diacriticIndex = DIACRITIC_INDICES.get(this.letters);
		final char charToReplace = this.letters.charAt(diacriticIndex);
		final char diacriticChar = this.tone.getDiacriticCharacter(charToReplace);
		return this.letters.replace(charToReplace, diacriticChar);
	}

	private static int getToneDiacriticIndex(final String letters) {
		/*
		 * From http://www.pinyin.info/rules/where.html: 1) A and e trump all
		 * other vowels and always take the tone mark. There are no Mandarin
		 * syllables in Hanyu Pinyin that contain both a and e. 2) In the
		 * combination ou, o takes the mark. 3) In all other cases, the final
		 * vowel takes the mark.
		 */
		if (letters.contains("a")) {
			return letters.indexOf('a');
		}
		if (letters.contains("e")) {
			return letters.indexOf('e');
		}
		if (letters.contains("ou")) {
			return letters.indexOf('o');
		}
		for (int i = letters.length() - 1; i >= 0; i--) {
			if ("iouü".indexOf(letters.charAt(i)) != -1) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public String toString() {
		return "PinyinSyllable [letters=" + this.letters + ", tone=" + this.tone + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.letters == null) ? 0 : this.letters.hashCode());
		result = prime * result + ((this.tone == null) ? 0 : this.tone.hashCode());
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
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final PinyinSyllable other = (PinyinSyllable) obj;
		if (this.letters == null) {
			if (other.letters != null) {
				return false;
			}
		} else if (!this.letters.equals(other.letters)) {
			return false;
		}
		if (this.tone != other.tone) {
			return false;
		}
		return true;
	}

}
