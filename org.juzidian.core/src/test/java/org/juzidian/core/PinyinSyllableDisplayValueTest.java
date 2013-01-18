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

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.juzidian.pinyin.PinyinSyllable;

@RunWith(Parameterized.class)
public class PinyinSyllableDisplayValueTest {

	private static final PinyinParser PINYIN_PARSER = new PinyinParser();

	private final PinyinSyllable pinyinSyllable;

	private final String expectedFormattedPinyin;

	public PinyinSyllableDisplayValueTest(final String pinyinString, final String expectedFormattedPinyin) {
		this.pinyinSyllable = PINYIN_PARSER.parse(pinyinString).get(0);
		this.expectedFormattedPinyin = expectedFormattedPinyin;
	}

	@Parameters(name = "Pinyin syllable ''{0}'' should have display value: {1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				// verify tone marks for each vowel
				{ "ma1", "mā" },
				{ "ma2", "má" },
				{ "ma3", "mǎ" },
				{ "ma4", "mà" },
				{ "he1", "hē" },
				{ "he2", "hé" },
				{ "he3", "hĕ" },
				{ "he4", "hè" },
				{ "yi1", "yī" },
				{ "yi2", "yí" },
				{ "yi3", "yǐ" },
				{ "yi4", "yì" },
				{ "po1", "pō" },
				{ "po2", "pó" },
				{ "po3", "pǒ" },
				{ "po4", "pò" },
				{ "bu1", "bū" },
				{ "bu2", "bú" },
				{ "bu3", "bǔ" },
				{ "bu4", "bù" },
				{ "lü1", "lǖ" },
				{ "lü2", "lǘ" },
				{ "lü3", "lǚ" },
				{ "lü4", "lǜ" },
				/* verify position of tone mark for each vowel combination */
				{ "lai1", "lāi" },
				{ "lao3", "lǎo" },
				{ "wei4", "wèi" },
				{ "xia1", "xiā" },
				{ "xiao3", "xiǎo" },
				{ "xie4", "xiè" },
				{ "xiong1", "xiōng" },
				{ "xiu4", "xiù" },
				{ "kou3", "kǒu" },
				{ "hua1", "huā" },
				{ "huai4", "huài" },
				{ "yue4", "yuè" },
				{ "hui2", "huí" },
				{ "huo3", "huǒ" },
				{ "lüe4", "lüè" },
				/* verify neutral tone */
				{ "ma5", "·ma" },
				/* verify no tone */
				{ "ma", "ma" },
		});
	}

	@Test
	public void test() {
		assertThat(this.pinyinSyllable.getDisplayValue(), equalTo(this.expectedFormattedPinyin));
	}

}
