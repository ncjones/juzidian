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
 * An individual Pinyin syllable from a CEDict entry.
 */
public class CedictPinyinSyllable {

	private final String letters;

	private final int toneNumber;

	public CedictPinyinSyllable(final String letters, final int toneNumber) {
		this.letters = letters;
		this.toneNumber = toneNumber;
	}

	public String getLetters() {
		return this.letters;
	}

	public int getToneNumber() {
		return this.toneNumber;
	}

	/**
	 * Check if the letters of the syllable are an invalid phoneme that is known
	 * to be used by some entries in the CEDict dictionary such as "xx" (no
	 * pronunciation), "r" (Erhua suffix) and "m" (the interjection "呣").
	 */
	public boolean isKnownInvalidSyllable() {
		return "xx".equals(this.letters) || "r".equals(this.letters) || "m".equals(this.letters);
	}

}
