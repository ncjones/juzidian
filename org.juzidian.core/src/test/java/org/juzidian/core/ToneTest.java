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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ToneTest {

	@Test(expected=IllegalArgumentException.class)
	public void valueOfShouldRejectNegative() {
		Tone.valueOf(-1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void valueOfShouldRejectGreaterThanFive() {
		Tone.valueOf(6);
	}

	@Test(expected = IllegalArgumentException.class)
	public void valueOfShouldRejectZero() {
		Tone.valueOf(0);
	}

	@Test
	public void valueOfNumberOneShouldBeFirstTone() {
		assertEquals(Tone.FIRST, Tone.valueOf(1));
	}

	@Test
	public void valueOfNumberTwoShouldBeSecondTone() {
		assertEquals(Tone.SECOND, Tone.valueOf(2));
	}

	@Test
	public void valueOfNumberThreeShouldBeThirdTone() {
		assertEquals(Tone.THIRD, Tone.valueOf(3));
	}

	@Test
	public void valueOfNumberFourShouldBeFourthTone() {
		assertEquals(Tone.FOURTH, Tone.valueOf(4));
	}

	@Test
	public void valueOfNumberFiveShouldBeNeutralTone() {
		assertEquals(Tone.NEUTRAL, Tone.valueOf(5));
	}

	@Test
	public void valueOfNullShouldBeAnyTone() {
		assertEquals(Tone.ANY, Tone.valueOf((Integer) null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getDiacriticCharacterShouldRejectDiacriticalCharacter() {
		Tone.FIRST.getDiacriticCharacter('ā');
	}

	@Test(expected = IllegalArgumentException.class)
	public void getDiacriticCharacterShouldRejectUpperCaseCharacter() {
		Tone.FIRST.getDiacriticCharacter('A');
	}

	@Test(expected = IllegalArgumentException.class)
	public void getDiacriticCharacterShouldRejectConsonant() {
		Tone.FIRST.getDiacriticCharacter('n');
	}

}
