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
package org.juzidian.pinyin;

/**
 * A tone associated with a Mandarin Chinese syllable.
 */
public enum Tone {

	FIRST(1, new char[] { 'ā', 'ē', 'ī', 'ō', 'ū', 'ǖ' }),

	SECOND(2, new char[] { 'á', 'é', 'í', 'ó', 'ú', 'ǘ' }),

	THIRD(3, new char[] { 'ǎ', 'ĕ', 'ǐ', 'ǒ', 'ǔ', 'ǚ' }),

	FOURTH(4, new char[] { 'à', 'è', 'ì', 'ò', 'ù', 'ǜ' }),

	NEUTRAL(5, new char[] { 'a', 'e', 'i', 'o', 'u', 'ü' }),

	ANY(null, new char[] { 'a', 'e', 'i', 'o', 'u', 'ü' });

	private static final String AEIOUÜ = "aeiouü";

	private final Integer number;

	/*
	 * Store the mapping between non-diacritical Pinyin vowels and the
	 * diacritical vowels for this tone using an array for slightly faster
	 * lookups than a hash map.
	 */
	private final char[] diacritics = new char[253];

	private Tone(final Integer number, final char[] diacritics) {
		this.number = number;
		for (int i = 0; i < diacritics.length; i++) {
			final char nonDiacriticVowel = AEIOUÜ.charAt(i);
			final char diacritic = diacritics[i];
			this.diacritics[nonDiacriticVowel] = diacritic;
		}
	}

	/**
	 * @return the numeric representation of the tone.
	 */
	public Integer getNumber() {
		return this.number;
	}

	public String getDisplayValue() {
		return ANY.equals(this) ? "" : this.getNumber().toString();
	}

	/**
	 * Get the character that uses a diacritical mark to represent this tone on
	 * the given Pinyin vowel.
	 * 
	 * @param vowel a lower-case Pinyin vowel (a, e, i, o, u, ü) without a
	 *        diacritic tone mark.
	 * @return a character with the diacritic tone mark for this tone.
	 * @throws IllegalArgumentException if the character is not a non-diacritic,
	 *         lower case Pinyin vowel.
	 */
	public char getDiacriticCharacter(final char vowel) {
		if (AEIOUÜ.indexOf(vowel) == -1) {
			throw new IllegalArgumentException("character is not a valid pinyin vowel: " + vowel);
		}
		return this.diacritics[vowel];
	}

	/**
	 * @param toneNumber the number (1-5) that represents a tone.
	 * @return the tone with the given tone number.
	 */
	public static Tone valueOf(final Integer toneNumber) {
		if (toneNumber == null) {
			return Tone.ANY;
		}
		for (final Tone tone : Tone.values()) {
			if (toneNumber.equals(tone.number)) {
				return tone;
			}
		}
		throw new IllegalArgumentException("Invalid tone number: " + toneNumber);
	}

	/**
	 * @param tone another tone.
	 * @return <code>true</code> if the given tone is this tone or this tone is
	 *         ANY.
	 * @throws IllegalArgumentException if tone is <code>null</code>.
	 */
	public boolean matches(final Tone tone) {
		if (tone == null) {
			throw new IllegalArgumentException("tone is null");
		}
		return tone.equals(this) || this.equals(ANY);
	}

}
