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

	FIRST(1, "ĀāĒēĪīŌōŪūǕǖ"),

	SECOND(2, "ÁáÉéÍíÓóÚúǗǘ"),

	THIRD(3, "ǍǎĚĕǏǐǑǒǓǔǙǚ"),

	FOURTH(4, "ÀàÈèÌìÒòÙùǛǜ"),

	NEUTRAL(5, "AaEeIiOoUuÜü"),

	ANY(null, "AaEeIiOoUuÜü");

	private static final String NON_DIACRITIC_VOWELS = "AaEeIiOoUuÜü";

	private final Integer number;

	/**
	 * Stores the mapping between non-diacritical Pinyin vowels and the
	 * diacritical vowels for this tone.
	 * <p>
	 * We use an array for slightly faster lookups than a hash map and use the
	 * integer value of the non-diacritical vowels as indices. Despite being
	 * initialised for capacity of 253 the array will only contain entries for
	 * each of the non-diacritical vowels &ndash; the entries are spread
	 * throughout the array with the last at index 252 (the int value of 'ü').
	 */
	private final char[] diacritics = new char[253];

	private Tone(final Integer number, final String diacriticVowelsForTone) {
		this.number = number;
		for (int i = 0; i < diacriticVowelsForTone.length(); i++) {
			final char nonDiacriticVowel = NON_DIACRITIC_VOWELS.charAt(i);
			final char diacritic = diacriticVowelsForTone.charAt(i);
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
		if (NON_DIACRITIC_VOWELS.indexOf(vowel) == -1) {
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
