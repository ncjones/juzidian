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

import static org.juzidian.core.SearchType.HANZI;
import static org.juzidian.core.SearchType.PINYIN;
import static org.juzidian.core.SearchType.REVERSE;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SearchTypeForTextTest {

	private final String inputText;

	private final SearchType expectedType;

	public SearchTypeForTextTest(final String inputText, final SearchType expectedType) {
		this.inputText = inputText;
		this.expectedType = expectedType;
	}

	@Parameters(name = "''{0}'' should have search type {1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ null, null },
				{ "", null },
				{ " ", null },
				{ "a", PINYIN },
				{ " a ", PINYIN },
				{ "ni", PINYIN },
				// { "nih", PINYIN },
				{ "niha", PINYIN },
				{ "nihao", PINYIN },
				// { "ni-hao", PINYIN },
				// { "ni.hao", PINYIN },
				// { "ni*hao", PINYIN },
				{ "ni'hao", PINYIN },
				{ "ni3", PINYIN },
				{ "nihao3", PINYIN },
				{ "ni2hao3", PINYIN },
				{ "ni2 hao3", PINYIN },
				// { "ni h", PINYIN },
				{ "ni hao", PINYIN },
				// { "nǐhǎo", PINYIN },
				// { "lv", PINYIN },
				{ "nihi", REVERSE },
				{ "v", REVERSE },
				{ "un", REVERSE },
				{ "xa", REVERSE },
				{ "app", REVERSE },
				{ "2", REVERSE },
				{ "你", HANZI },
				{ "你好", HANZI },
				{ " 你好 ", HANZI },
				{ "你 好", HANZI },
				{ "你好。", HANZI },
				{ "你，好", HANZI },
				{ "+你", HANZI },
				{ "hello你好", HANZI },
				{ "你好(hello)", HANZI },
				{ "酒dian", HANZI },
				{ "jiu店", HANZI },
				{ "200米", HANZI }
		});
	}

	@Test
	public void test() {
		final SearchType searchType = SearchType.forText(this.inputText);
		Assert.assertEquals("Search type for " + this.inputText, this.expectedType, searchType);
	}

}
