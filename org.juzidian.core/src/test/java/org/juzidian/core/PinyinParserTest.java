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

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PinyinParserTest {

	private PinyinParser pinyinParser;

	@Before
	public void setUp() {
		this.pinyinParser = new PinyinParser();
	}

	@Test(expected = PinyinParseException.class)
	public void parseShouldRejectDigit() {
		this.pinyinParser.parse("2");
	}

	@Test(expected = PinyinParseException.class)
	public void parseShouldRejectPunctuation() {
		this.pinyinParser.parse("!");
	}

	@Test(expected = PinyinParseException.class)
	public void parseShouldRejectLeadingPunctuation() {
		this.pinyinParser.parse("!hao");
	}

	@Test(expected = PinyinParseException.class)
	public void parseShouldRejectTrailingPunctuation() {
		this.pinyinParser.parse("hao!");
	}

	@Test(expected = PinyinParseException.class)
	public void parseShouldRejectNonPinyinWord() {
		this.pinyinParser.parse("hi");
	}

	@Test(expected = PinyinParseException.class)
	public void parseShouldRejectIncompleteWord() {
		this.pinyinParser.parse("guangzh");
	}

	@Test
	public void parseShouldNotContainAdditionalSyllables() {
		Assert.assertEquals(1, this.pinyinParser.parse("hao").size());
	}

	@Test
	public void parseShouldHandleLetters() {
		final List<PinyinSyllable> parsedSyllables = this.pinyinParser.parse("hao");
		Assert.assertEquals("hao", parsedSyllables.get(0).getLetters());
	}

	@Test
	public void parseShouldIgnoreLeadingSpace() {
		final List<PinyinSyllable> parsedSyllables = this.pinyinParser.parse("  hao");
		Assert.assertEquals("hao", parsedSyllables.get(0).getLetters());
	}

	@Test
	public void parseShouldIgnoreTrailingSpace() {
		final List<PinyinSyllable> parsedSyllables = this.pinyinParser.parse("hao  ");
		Assert.assertEquals("hao", parsedSyllables.get(0).getLetters());
	}

	@Test
	public void parseShouldHandleTone() {
		final List<PinyinSyllable> parsedSyllables = this.pinyinParser.parse("hao3");
		final PinyinSyllable expectedSyllable = new PinyinSyllable("hao", Tone.THIRD);
		Assert.assertEquals(Arrays.asList(expectedSyllable), parsedSyllables);
	}

	@Test(expected = PinyinParseException.class)
	public void parseShouldRejectInvalidTone() {
		this.pinyinParser.parse("hao6");
	}

	@Test
	public void parseShouldPreferLessSyllablesForAmbiguousInput() {
		final List<PinyinSyllable> parsedSyllables = this.pinyinParser.parse("xian");
		Assert.assertEquals("xian", parsedSyllables.get(0).getLetters());
	}

	@Test
	public void parseShouldSplitSyllablesOnSpace() {
		final List<PinyinSyllable> parsedSyllables = this.pinyinParser.parse("xi an");
		final PinyinSyllable xi = new PinyinSyllable("xi");
		final PinyinSyllable an = new PinyinSyllable("an");
		Assert.assertEquals(Arrays.asList(xi, an), parsedSyllables);
	}

	@Test
	public void parseShouldSplitSyllablesOnMultipleSpaces() {
		final List<PinyinSyllable> parsedSyllables = this.pinyinParser.parse("xi  an");
		final PinyinSyllable xi = new PinyinSyllable("xi");
		final PinyinSyllable an = new PinyinSyllable("an");
		Assert.assertEquals(Arrays.asList(xi, an), parsedSyllables);
	}

	@Test
	public void parseShouldSplitSyllablesWithTonesOnSpaces() {
		final List<PinyinSyllable> parsedSyllables = this.pinyinParser.parse("xi1 an1");
		final PinyinSyllable xi = new PinyinSyllable("xi", Tone.FIRST);
		final PinyinSyllable an = new PinyinSyllable("an", Tone.FIRST);
		Assert.assertEquals(Arrays.asList(xi, an), parsedSyllables);
	}

	@Test
	public void parseShouldSplitSyllablesOnApostrophe() {
		final List<PinyinSyllable> parsedSyllables = this.pinyinParser.parse("xi'an");
		final PinyinSyllable xi = new PinyinSyllable("xi");
		final PinyinSyllable an = new PinyinSyllable("an");
		Assert.assertEquals(Arrays.asList(xi, an), parsedSyllables);
	}

	@Test
	public void parseShouldHandleMultipleSyllables() {
		final List<PinyinSyllable> parsedSyllables = this.pinyinParser.parse("nihao");
		final PinyinSyllable ni = new PinyinSyllable("ni");
		final PinyinSyllable hao = new PinyinSyllable("hao");
		Assert.assertEquals(Arrays.asList(ni, hao), parsedSyllables);
	}

	@Test
	public void parseShouldHandleMultipleSyllablesWithTones() {
		final List<PinyinSyllable> parsedSyllables = this.pinyinParser.parse("ni3hao3");
		final PinyinSyllable ni = new PinyinSyllable("ni", Tone.THIRD);
		final PinyinSyllable hao = new PinyinSyllable("hao", Tone.THIRD);
		Assert.assertEquals(Arrays.asList(ni, hao), parsedSyllables);
	}

	@Test
	public void parseShouldConvertLettersToLowerCase() {
		final List<PinyinSyllable> parsedSyllables = this.pinyinParser.parse("HAO3");
		final PinyinSyllable expectedSyllable = new PinyinSyllable("hao", Tone.THIRD);
		Assert.assertEquals(Arrays.asList(expectedSyllable), parsedSyllables);
	}

	@Test
	public void isValidShouldBeFalseForInvalidInput() {
		Assert.assertFalse(this.pinyinParser.isValid("hello"));
	}

	@Test
	public void isValidShouldBeTrueForValidInput() {
		Assert.assertTrue(this.pinyinParser.isValid("ni3hao3"));
	}

	@Test
	public void isValidShouldBeTrueForValidUpperCaseInput() {
		Assert.assertTrue(this.pinyinParser.isValid("NI3HAO3"));
	}

}
