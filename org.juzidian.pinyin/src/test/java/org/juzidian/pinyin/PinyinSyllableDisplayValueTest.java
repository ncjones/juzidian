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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PinyinSyllableDisplayValueTest {


	@Parameter(0)
	public String pinyinLetters;

	@Parameter(1)
	public Integer pinyinToneNumber;

	@Parameter(2)
	public String expectedFormattedPinyin;

	@Parameters(name = "Pinyin syllable ''{0}'' should have display value: {2}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				// verify tone marks for each vowel
				{ "ma", 1, "mā" },
				{ "ma", 2, "má" },
				{ "ma", 3, "mǎ" },
				{ "ma", 4, "mà" },
				{ "he", 1, "hē" },
				{ "he", 2, "hé" },
				{ "he", 3, "hĕ" },
				{ "he", 4, "hè" },
				{ "yi", 1, "yī" },
				{ "yi", 2, "yí" },
				{ "yi", 3, "yǐ" },
				{ "yi", 4, "yì" },
				{ "po", 1, "pō" },
				{ "po", 2, "pó" },
				{ "po", 3, "pǒ" },
				{ "po", 4, "pò" },
				{ "bu", 1, "bū" },
				{ "bu", 2, "bú" },
				{ "bu", 3, "bǔ" },
				{ "bu", 4, "bù" },
				{ "lü", 1, "lǖ" },
				{ "lü", 2, "lǘ" },
				{ "lü", 3, "lǚ" },
				{ "lü", 4, "lǜ" },
				/* verify position of tone mark for each vowel combination */
				{ "lai", 1, "lāi" },
				{ "lao", 3, "lǎo" },
				{ "wei", 4, "wèi" },
				{ "xia", 1, "xiā" },
				{ "xiao", 3, "xiǎo" },
				{ "xie", 4, "xiè" },
				{ "xiong", 1, "xiōng" },
				{ "xiu", 4, "xiù" },
				{ "kou", 3, "kǒu" },
				{ "hua", 1, "huā" },
				{ "huai", 4, "huài" },
				{ "yue", 4, "yuè" },
				{ "hui", 2, "huí" },
				{ "huo", 3, "huǒ" },
				{ "lüe", 4, "lüè" },
				/* verify capitalised vowels */
				{ "A", 1, "Ā" },
				{ "E", 4, "È" },
				{ "Ou", 1, "Ōu" },
				/* verify neutral tone */
				{ "ma", 5, "·ma" },
				/* verify no tone */
				{ "ma", null, "ma" },
		});
	}

	@Test
	public void test() {
		final PinyinSyllable pinyinSyllable = new PinyinSyllable(this.pinyinLetters, Tone.valueOf(this.pinyinToneNumber));
		assertThat(pinyinSyllable.getDisplayValue(), equalTo(this.expectedFormattedPinyin));
	}

}
