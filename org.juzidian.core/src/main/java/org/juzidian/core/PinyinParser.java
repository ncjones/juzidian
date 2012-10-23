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
import java.util.List;

/**
 * Parses an input string to create a sequence of {@link PinyinSyllable}s.
 * <p>
 * The input string should contain a list of complete Pinyin syllables. Tones
 * are optional and can be indicated using tone numbers (1, 2, 3, 4 or 5)
 * immediately after the syllable to which they apply.
 * <p>
 * Syllables may be separated by spaces but they can usually be parsed
 * unambiguously without spaces. In the case of ambiguous syllables the longer
 * syllable is preferred, eg. "xian" is interpreted as a single syllable. Either
 * whitespace or an apostrophe can be used to disambiguate such syllables, eg.
 * "xi'an".
 */
public class PinyinParser {

	/**
	 * Check if the input text is parseable as Pinyin.
	 * 
	 * @return <code>true</code> if the input text can be parsed.
	 */
	public boolean isValid(final String text) {
		return new ParseInstance(text).getSyllables() != null;
	}

	/**
	 * Parse the input text as Pinyin.
	 * 
	 * @return a list of {@link PinyinSyllable}.
	 * @throws PinyinParseException if the input is not valid Pinyin.
	 * @see #isValid(String)
	 */
	public List<PinyinSyllable> parse(final String text) {
		final List<PinyinSyllable> syllables = new ParseInstance(text).getSyllables();
		if (syllables == null) {
			throw new PinyinParseException("Invalid pinyin input: " + text);
		}
		return syllables;
	}

	private static class ParseInstance {

		private static final PinyinHelper PINYIN_HELPER = new PinyinHelper();

		private final String input;

		private int index;

		public ParseInstance(final String input) {
			this.input = input.trim();
		}

		public List<PinyinSyllable> getSyllables() {
			final List<PinyinSyllable> syllables = new ArrayList<PinyinSyllable>();
			for (this.index = 0; this.index < this.input.length();) {
				final PinyinSyllable syllable = this.findSyllable();
				if (syllable == null) {
					return null;
				}
				syllables.add(syllable);
			}
			return syllables;
		}

		private PinyinSyllable findSyllable() {
			/*
			 * Consume characters until non-syllable character found. If
			 * non-syllable character is digit then use as tone.
			 */
			String currentSyllablePart = "";
			Tone tone = Tone.ANY;
			this.consumeWhitespace();
			for (; this.index < this.input.length(); this.index++) {
				final char currentChar = this.input.charAt(this.index);
				final String potentialPinyinSyllablePart = currentSyllablePart + currentChar;
				if (!this.isValidPinyinSyllablePart(potentialPinyinSyllablePart)) {
					if (Character.isDigit(currentChar)) {
						final int toneNumber = Integer.parseInt("" + currentChar);
						if (toneNumber >= 0 && toneNumber <= 5) {
							tone = Tone.valueOf(toneNumber);
							this.index++;
						}
					}
					if (currentChar == '\'') {
						this.index++;
					}
					break;
				} else {
					currentSyllablePart = potentialPinyinSyllablePart;
				}
			}
			return this.isValidPinyinSyllable(currentSyllablePart) ? new PinyinSyllable(currentSyllablePart, tone) : null;
		}

		private void consumeWhitespace() {
			for (; this.index < this.input.length(); this.index++) {
				final char currentChar = this.input.charAt(this.index);
				if (!Character.isWhitespace(currentChar)) {
					break;
				}
			}
		}

		private boolean isValidPinyinSyllable(final String string) {
			return PINYIN_HELPER.getValidSyllables().contains(string);
		}

		private boolean isValidPinyinSyllablePart(final String pinyinSyllablePart) {
			final List<String> validPinyinSyllables = PINYIN_HELPER.getValidSyllables();
			for (final String validPinyinSyllable : validPinyinSyllables) {
				if (validPinyinSyllable.startsWith(pinyinSyllablePart)) {
					return true;
				}
			}
			return false;
		}

	}

}
