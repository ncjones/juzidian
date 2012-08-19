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
import org.junit.Test;

public class PinyinParserTest {

	@Test(expected = PinyinParseException.class)
	public void parseShouldRejectDigit() {
		new PinyinParser("2").parse();
	}

	@Test(expected = PinyinParseException.class)
	public void parseShouldRejectPunctuation() {
		new PinyinParser("!").parse();
	}

	@Test(expected = PinyinParseException.class)
	public void parseShouldRejectLeadingPunctuation() {
		new PinyinParser("!hao").parse();
	}

	@Test(expected = PinyinParseException.class)
	public void parseShouldRejectTrailingPunctuation() {
		new PinyinParser("hao!").parse();
	}

	@Test(expected = PinyinParseException.class)
	public void parseShouldRejectNonPinyinWord() {
		new PinyinParser("hi").parse();
	}

	@Test(expected = PinyinParseException.class)
	public void parseShouldRejectIncompleteWord() {
		new PinyinParser("guangzh").parse();
	}

	@Test
	public void parseShouldNotContainAdditionalSyllables() {
		Assert.assertEquals(1, new PinyinParser("hao").parse().size());
	}

	@Test
	public void parseShouldHandleLetters() {
		final List<PinyinSyllable> parsedSyllables = new PinyinParser("hao").parse();
		Assert.assertEquals("hao", parsedSyllables.get(0).getLetters());
	}

	@Test
	public void parseShouldIgnoreLeadingSpace() {
		final List<PinyinSyllable> parsedSyllables = new PinyinParser("  hao").parse();
		Assert.assertEquals("hao", parsedSyllables.get(0).getLetters());
	}

	@Test
	public void parseShouldIgnoreTrailingSpace() {
		final List<PinyinSyllable> parsedSyllables = new PinyinParser("hao  ").parse();
		Assert.assertEquals("hao", parsedSyllables.get(0).getLetters());
	}

	@Test
	public void parseShouldHandleTone() {
		final List<PinyinSyllable> parsedSyllables = new PinyinParser("hao3").parse();
		final PinyinSyllable expectedSyllable = new PinyinSyllable("hao", Tone.THIRD);
		Assert.assertEquals(Arrays.asList(expectedSyllable), parsedSyllables);
	}

	@Test(expected = PinyinParseException.class)
	public void parseShouldRejectInvalidTone() {
		new PinyinParser("hao6").parse();
	}

	@Test
	public void parseShouldPreferLessSyllablesForAmbiguousInput() {
		final List<PinyinSyllable> parsedSyllables = new PinyinParser("xian").parse();
		Assert.assertEquals("xian", parsedSyllables.get(0).getLetters());
	}

	@Test
	public void parseShouldSplitSyllablesOnSpace() {
		final List<PinyinSyllable> parsedSyllables = new PinyinParser("xi an").parse();
		final PinyinSyllable xi = new PinyinSyllable("xi");
		final PinyinSyllable an = new PinyinSyllable("an");
		Assert.assertEquals(Arrays.asList(xi, an), parsedSyllables);
	}

	@Test
	public void parseShouldSplitSyllablesOnMultipleSpaces() {
		final List<PinyinSyllable> parsedSyllables = new PinyinParser("xi  an").parse();
		final PinyinSyllable xi = new PinyinSyllable("xi");
		final PinyinSyllable an = new PinyinSyllable("an");
		Assert.assertEquals(Arrays.asList(xi, an), parsedSyllables);
	}

	@Test
	public void parseShouldSplitSyllablesWithTonesOnSpaces() {
		final List<PinyinSyllable> parsedSyllables = new PinyinParser("xi1 an1").parse();
		final PinyinSyllable xi = new PinyinSyllable("xi", Tone.FIRST);
		final PinyinSyllable an = new PinyinSyllable("an", Tone.FIRST);
		Assert.assertEquals(Arrays.asList(xi, an), parsedSyllables);
	}

	@Test
	public void parseShouldSplitSyllablesOnApostrophe() {
		final List<PinyinSyllable> parsedSyllables = new PinyinParser("xi'an").parse();
		final PinyinSyllable xi = new PinyinSyllable("xi");
		final PinyinSyllable an = new PinyinSyllable("an");
		Assert.assertEquals(Arrays.asList(xi, an), parsedSyllables);
	}

	@Test
	public void parseShouldHandleMultipleSyllables() {
		final List<PinyinSyllable> parsedSyllables = new PinyinParser("nihao").parse();
		final PinyinSyllable ni = new PinyinSyllable("ni");
		final PinyinSyllable hao = new PinyinSyllable("hao");
		Assert.assertEquals(Arrays.asList(ni, hao), parsedSyllables);
	}

	@Test
	public void parseShouldHandleMultipleSyllablesWithTones() {
		final List<PinyinSyllable> parsedSyllables = new PinyinParser("ni3hao3").parse();
		final PinyinSyllable ni = new PinyinSyllable("ni", Tone.THIRD);
		final PinyinSyllable hao = new PinyinSyllable("hao", Tone.THIRD);
		Assert.assertEquals(Arrays.asList(ni, hao), parsedSyllables);
	}

}
