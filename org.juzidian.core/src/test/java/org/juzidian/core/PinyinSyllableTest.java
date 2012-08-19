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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PinyinSyllableTest {

	@Test
	public void tonelessSyllableShouldMatchIdenticalSyllable() {
		assertTrue(new PinyinSyllable("hao").matches(new PinyinSyllable("hao")));
	}

	@Test
	public void tonelessSyllableShouldMatchTonedSyllable() {
		assertTrue(new PinyinSyllable("hao").matches(new PinyinSyllable("hao", Tone.FIRST)));
	}

	@Test
	public void tonedSyllableShouldNotMatchTonelessSyllable() {
		assertFalse(new PinyinSyllable("hao", Tone.FIRST).matches(new PinyinSyllable("hao")));
	}

	@Test
	public void syllableShouldNotMatchSyllableWithDifferentLetters() {
		assertFalse(new PinyinSyllable("hao", Tone.FIRST).matches(new PinyinSyllable("han")));
	}

	@Test
	public void syllableShouldNotMatchSyllableWithDifferentTone() {
		assertFalse(new PinyinSyllable("hao", Tone.FIRST).matches(new PinyinSyllable("hao", Tone.SECOND)));
	}

}