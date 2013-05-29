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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.juzidian.pinyin.Tone.FIRST;
import static org.juzidian.pinyin.Tone.THIRD;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
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
	public void parseShouldRejectEmptyString() {
		this.pinyinParser.parse("");
	}

	@Test(expected = PinyinParseException.class)
	public void parseShouldRejectWhitespaceString() {
		this.pinyinParser.parse(" ");
	}

	@Test
	public void parseShouldHandleIncompleteSyllable() {
		assertThat(this.pinyinParser.parse("zh"), contains(pinyin("zh")));
	}

	@Test
	public void parseShouldHandleIncompleteSyllableWithTrailingWhitespace() {
		assertThat(this.pinyinParser.parse("zh "), contains(pinyin("zh")));
	}

	@Test
	public void parseShouldHandleIncompleteSyllableWithLeadingWhitespace() {
		assertThat(this.pinyinParser.parse(" zh"), contains(pinyin("zh")));
	}

	@Test
	public void parseShouldHandleIncompleteSyllableAtPhraseEndAndPhraseStart() {
		assertThat(this.pinyinParser.parse("zhouzh"), contains(pinyin("zhou"), pinyin("zh")));
	}

	@Test(expected = PinyinParseException.class)
	public void parseShouldRejectIncompleteSyllableTwiceAtPhraseEnd() {
		this.pinyinParser.parse("guangzhzh");
	}

	@Test
	public void parseShouldHandleIncompleteSyllableAtPhraseEnd() {
		assertThat(this.pinyinParser.parse("guangzh"), contains(pinyin("guang"), pinyin("zh")));
	}

	@Test
	public void parseShouldHandleIncompleteSyllableAtPhraseEndWithTrailingWhitespace() {
		assertThat(this.pinyinParser.parse("guangzh "), contains(pinyin("guang"), pinyin("zh")));
	}

	@Test(expected = PinyinParseException.class)
	public void parseShouldRejectIncompleteSyllableMidPhrase() {
		this.pinyinParser.parse("guangzhshi");
	}

	@Test
	public void parseShouldHandleIncompleteSyllableContainingStandalone() {
		assertThat(this.pinyinParser.parse("hon"), contains(pinyin("hon")));
	}

	@Test
	public void parseShouldHandleIncompleteSyllableAtPhraseEndContainingStandalone() {
		assertThat(this.pinyinParser.parse("cai"), contains(pinyin("cai"), pinyin("hon")));
	}

	@Test
	public void parseShouldHandleLetters() {
		assertThat(this.pinyinParser.parse("hao"), contains(pinyin("hao")));
	}

	@Test
	public void parseShouldIgnoreLeadingSpace() {
		assertThat(this.pinyinParser.parse("  hao"), contains(pinyin("hao")));
	}

	@Test
	public void parseShouldIgnoreTrailingSpace() {
		assertThat(this.pinyinParser.parse("hao  "), contains(pinyin("hao")));
	}

	@Test
	public void parseShouldHandleTone() {
		assertThat(this.pinyinParser.parse("hao3"), contains(pinyin("hao", THIRD)));
	}

	@Test(expected = PinyinParseException.class)
	public void parseShouldRejectInvalidTone() {
		this.pinyinParser.parse("hao6");
	}

	@Test
	public void parseShouldPreferLessSyllablesForAmbiguousInput() {
		assertThat(this.pinyinParser.parse("xian"), contains(pinyin("xian")));
	}

	@Test
	public void parseShouldSplitSyllablesOnSpace() {
		assertThat(this.pinyinParser.parse("xi an"), contains(pinyin("xi"), pinyin("an")));
	}

	@Test
	public void parseShouldSplitSyllablesOnMultipleSpaces() {
		assertThat(this.pinyinParser.parse("xi  an"), contains(pinyin("xi"), pinyin("an")));
	}

	@Test
	public void parseShouldSplitSyllablesWithTonesOnSpaces() {
		assertThat(this.pinyinParser.parse("xi1 an1"), contains(pinyin("xi", FIRST), pinyin("an", FIRST)));
	}

	@Test
	public void parseShouldSplitSyllablesOnApostrophe() {
		assertThat(this.pinyinParser.parse("xi'an"), contains(pinyin("xi"), pinyin("an")));
	}

	@Test
	public void parseShouldHandleMultipleSyllables() {
		assertThat(this.pinyinParser.parse("nihao"), contains(pinyin("ni"), pinyin("hao")));
	}

	@Test
	public void parseShouldHandleMultipleSyllablesWithTones() {
		assertThat(this.pinyinParser.parse("ni3hao3"), contains(pinyin("ni", THIRD), pinyin("hao", THIRD)));
	}

	@Test
	public void parseShouldConvertLettersToLowerCase() {
		assertThat(this.pinyinParser.parse("HAO3"), contains(pinyin("hao", THIRD)));
	}

	@Test
	public void isValidShouldBeFalseForInvalidInput() {
		assertThat(this.pinyinParser.isValid("hello"), is(false));
	}

	@Test
	public void isValidShouldBeTrueForValidInput() {
		assertThat(this.pinyinParser.isValid("ni3hao3"), is(true));
	}

	@Test
	public void isValidShouldBeTrueForValidUpperCaseInput() {
		assertThat(this.pinyinParser.isValid("NI3HAO3"), is(true));
	}

	private static Matcher<PinyinSyllable> pinyin(final String letters, final Tone tone) {
		return new PinyinSyllableMatcher(new PinyinSyllable(letters, tone));
	}

	private static Matcher<PinyinSyllable> pinyin(final String letters) {
		return new PinyinSyllableMatcher(new PinyinSyllable(letters));
	}

}
