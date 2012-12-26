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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class PinyinSyllableTest {

	@Test
	public void tonelessSyllableShouldMatchIdenticalSyllable() {
		assertThat(new PinyinSyllable("hao").matches(new PinyinSyllable("hao")), equalTo(true));
	}

	@Test
	public void tonelessSyllableShouldMatchTonedSyllable() {
		assertThat(new PinyinSyllable("hao").matches(new PinyinSyllable("hao", Tone.FIRST)), equalTo(true));
	}

	@Test
	public void tonedSyllableShouldNotMatchTonelessSyllable() {
		assertThat(new PinyinSyllable("hao", Tone.FIRST).matches(new PinyinSyllable("hao")), equalTo(false));
	}

	@Test
	public void syllableShouldNotMatchSyllableWithDifferentLetters() {
		assertThat(new PinyinSyllable("hao", Tone.FIRST).matches(new PinyinSyllable("han")), equalTo(false));
	}

	@Test
	public void syllableShouldNotMatchSyllableWithDifferentTone() {
		assertThat(new PinyinSyllable("hao", Tone.FIRST).matches(new PinyinSyllable("hao", Tone.SECOND)), equalTo(false));
	}

	@Test
	public void getDisplayValueShouldHandleInvalidPinyin() {
		assertThat(new PinyinSyllable("r").getDisplayValue(), equalTo("r"));
	}

	@Test
	public void getDisplayValueShouldExcludeToneForInvalidPinyin() {
		assertThat(new PinyinSyllable("r", Tone.FIRST).getDisplayValue(), equalTo("r"));
	}

	@Test
	public void getDisplayValueShouldExcludeToneForInvalidPinyinWithNeutralTone() {
		assertThat(new PinyinSyllable("r", Tone.NEUTRAL).getDisplayValue(), equalTo("r"));
	}

}