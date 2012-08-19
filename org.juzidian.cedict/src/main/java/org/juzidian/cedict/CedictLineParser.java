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

/**
 * Parses a CEDict dictionary line.
 * <p>
 * This class is not thread safe.
 */
class CedictLineParser {

	private final String line;

	private int index;

	/**
	 * Create a new {@link CedictLineParser} for the given line.
	 * 
	 * @param line a raw, valid CEDict line.
	 */
	public CedictLineParser(final String line) {
		this.line = line;
	}

	/**
	 * Get the {@link CedictEntry} from the input line.
	 * <p>
	 * No validation is performed on the input; behaviour is undefined for
	 * invalid input.
	 * <p>
	 * This method should <em>not</em> be called by multiple threads.
	 * 
	 * @return the {@link CedictEntry} represented by the input line.
	 */
	public CedictEntry parse() {
		this.index = 0;
		final String traditional = readCharsUntil(' ');
		final String simplified = readCharsUntil(' ');
		/* skip left bracket */
		this.index += 1;
		final String pinyin = readCharsUntil(']');
		/* skip space and slash */
		this.index += 2;
		final String english = this.line.substring(this.index, this.line.length() - 1);
		return new LazyParsingCedictEntry(traditional, simplified, pinyin, english);
	}

	private String readCharsUntil(final char deliminator) {
		final StringBuilder chars = new StringBuilder();
		for (;;) {
			final char c = this.line.charAt(this.index);
			this.index++;
			if (c == deliminator) {
				break;
			}
			chars.append(c);
		}
		return chars.toString();
	}
}
