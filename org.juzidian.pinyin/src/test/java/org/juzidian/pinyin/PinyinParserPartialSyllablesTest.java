/*
 * Copyright Nathan Jones 2013
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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PinyinParserPartialSyllablesTest {

	private PinyinParser pinyinParser;

	@Parameter(0)
	public String partialSyllable;

	@Parameters(name = "Parse Pinyin ending with ''{0}''")
	public static Collection<Object[]> parameters() {
		final ArrayList<Object[]> parameters = new ArrayList<Object[]>();
		for (final String partial : PinyinHelper.getPartialSyllables()) {
			parameters.add(new Object[] { partial });
		}
		return parameters;
	}

	@Before
	public void setUp() {
		this.pinyinParser = new PinyinParser();
	}

	@Test
	public void partialSyllableIsValidPinyin() {
		assertThat(this.pinyinParser.isValid(this.partialSyllable), is(true));
	}

	@Test
	public void partialSyllableAfterAnyValidSyllableIsValidPinyin() {
		for (final String syllable : PinyinHelper.getValidSyllables()) {
			assertThat(this.pinyinParser.isValid(syllable + this.partialSyllable), is(true));
		}
	}

}
