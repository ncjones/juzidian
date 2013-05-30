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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses an input string to create a sequence of {@link PinyinSyllable}s.
 * <p>
 * The input string should contain a sequence of Pinyin syllables. The last
 * syllable in the sequence may be a partial syllable. Tones are optional and
 * can be indicated using tone numbers (1, 2, 3, 4 or 5) immediately after the
 * syllable to which they apply.
 * <p>
 * Adjacent syllables without tone numbers can sometimes have multiple
 * interpretations. In such cases the longer syllable is preferred, eg. "xian"
 * is interpreted as a single syllable. Either whitespace or an apostrophe can
 * be used to explicitly delineate adjacent syllables, eg. "xi'an".
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
		final String cleanText = text.toLowerCase().trim();
		try {
			return new PinyinParseInstance(new StringReader(cleanText)).parseInput();
		} catch (final ParseException e) {
			try {
				return this.parsePartial(cleanText, e.currentToken);
			} catch (final PartialPinyinParseException e2) {
				throw new PinyinParseException("Invalid pinyin input: " + text, e);
			}
		} catch (final TokenMgrError e) {
			throw new PinyinParseException("Invalid pinyin input: " + text, e);
		}
	}

	private List<PinyinSyllable> parsePartial(final String text, final Token currentToken) throws PartialPinyinParseException {
		if (currentToken.kind == PinyinParseInstanceConstants.EOF) {
			throw new PartialPinyinParseException(text);
		}
		if (!this.isLastToken(text, currentToken)) {
			throw new PartialPinyinParseException(text);
		}
		if (!PinyinHelper.getPartialSyllables().contains(currentToken.image)) {
			throw new PartialPinyinParseException(text);
		}
		final List<PinyinSyllable> precedingSyllables;
		if (text.equals(currentToken.image)) {
			precedingSyllables = new ArrayList<PinyinSyllable>();
		} else {
			precedingSyllables = this.parse(text.substring(0, currentToken.beginColumn - 1));
		}
		precedingSyllables.add(new PinyinSyllable(currentToken.image));
		return precedingSyllables;
	}

	private boolean isLastToken(final String text, final Token currentToken) {
		return text.length() == currentToken.beginColumn + currentToken.image.length() - 1;
	}

	private static class PartialPinyinParseException extends Exception {

		private static final long serialVersionUID = 1L;

		private PartialPinyinParseException(final String message) {
			super("Invalid partial pinyin input: " + message);
		}

	}

}
