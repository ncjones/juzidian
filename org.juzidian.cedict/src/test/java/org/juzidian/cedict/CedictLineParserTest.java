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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CedictLineParserTest {

	private CedictLineParser parser;

	@Before
	public void setUp() {
		this.parser = new CedictLineParser("學生 学生 [xue2 sheng5] /student/schoolchild/");
	}

	@Test
	public void parseShouldExtractTraditionalCharacters() {
		assertEquals("學生", this.parser.parse().getTraditionalCharacters());
	}

	@Test
	public void parseShouldExtractSimplifiedCharacters() {
		Assert.assertEquals("学生", this.parser.parse().getSimplifiedCharacters());
	}

	@Test
	public void parseShouldExtractPinyinCharacters() {
		final List<CedictPinyinSyllable> pinyinSyllables = this.parser.parse().getPinyinSyllables();
		assertEquals("xue", pinyinSyllables.get(0).getLetters());
		assertEquals(2, pinyinSyllables.get(0).getToneNumber());
		assertEquals("sheng", pinyinSyllables.get(1).getLetters());
		assertEquals(5, pinyinSyllables.get(1).getToneNumber());
	}

	@Test
	public void parseShouldExtractEnglishMeanings() {
		assertEquals(Arrays.asList("student", "schoolchild"), this.parser.parse().getDefinitions());
	}

	@Test
	public void parseShouldReturnSameValueForSubsequentInvocations() {
		assertEquals(this.parser.parse(), this.parser.parse());
	}

}
