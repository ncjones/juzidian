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

import java.io.StringReader;
import java.util.List;

import org.juzidian.pinyin.PinyinSyllable;

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
		try {
			this.parse(text);
		} catch (final PinyinParseException e) {
			return false;
		}
		return true;
	}

	/**
	 * Parse the input text as Pinyin.
	 * 
	 * @return a list of {@link PinyinSyllable}.
	 * @throws PinyinParseException if the input is not valid Pinyin.
	 * @see #isValid(String)
	 */
	public List<PinyinSyllable> parse(final String text) {
		try {
			return new PinyinParseInstance(new StringReader(text.toLowerCase())).parseInput();
		} catch (final Throwable e) {
			throw new PinyinParseException("Invalid pinyin input: " + text, e);
		}
	}

}
