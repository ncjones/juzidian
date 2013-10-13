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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.juzidian.pinyin.PinyinParser;
import org.mockito.Mockito;

@RunWith(Parameterized.class)
public class DictionaryApplicableSearchTypesTest {

	private static Set<SearchType> NONE = Collections.emptySet();

	private static Set<SearchType> HANZI_ONLY = Collections.singleton(HANZI);

	private static Set<SearchType> REVERSE_ONLY = Collections.singleton(REVERSE);

	private static Set<SearchType> PINYIN_AND_REVERSE = new HashSet<SearchType>(Arrays.asList(PINYIN, REVERSE));

	private final String inputText;

	private final Set<SearchType> expectedTypes;

	public DictionaryApplicableSearchTypesTest(final String inputText, final Set<SearchType> expectedType) {
		this.inputText = inputText;
		this.expectedTypes = expectedType;
	}

	@Parameters(name = "''{0}'' should have applicable search types: {1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ null, NONE },
				{ "", NONE },
				{ " ", NONE },
				{ "a", PINYIN_AND_REVERSE },
				{ " a ", PINYIN_AND_REVERSE },
				{ "ni", PINYIN_AND_REVERSE },
				{ "nih", PINYIN_AND_REVERSE },
				{ "niha", PINYIN_AND_REVERSE },
				{ "nihao", PINYIN_AND_REVERSE },
				{ "h", PINYIN_AND_REVERSE },
				{ "ho", PINYIN_AND_REVERSE },
				{ "hon", PINYIN_AND_REVERSE },
				{ "hong", PINYIN_AND_REVERSE },
				{ "honk", REVERSE_ONLY },
				// { "ni-hao", PINYIN_AND_REVERSE },
				// { "ni.hao", PINYIN_AND_REVERSE },
				// { "ni*hao", PINYIN_AND_REVERSE },
				{ "ni'hao", PINYIN_AND_REVERSE },
				{ "ni3", PINYIN_AND_REVERSE },
				{ "nihao3", PINYIN_AND_REVERSE },
				{ "ni2hao3", PINYIN_AND_REVERSE },
				{ "ni2 hao3", PINYIN_AND_REVERSE },
				{ "ni h", PINYIN_AND_REVERSE },
				{ "ni hao", PINYIN_AND_REVERSE },
				// { "nǐhǎo", PINYIN_AND_REVERSE },
				{ "lv", PINYIN_AND_REVERSE },
				{ "nihi", REVERSE_ONLY },
				{ "v", REVERSE_ONLY },
				{ "un", REVERSE_ONLY },
				{ "xa", REVERSE_ONLY },
				{ "app", REVERSE_ONLY },
				{ "2", REVERSE_ONLY },
				{ "你", HANZI_ONLY },
				{ "你好", HANZI_ONLY },
				{ " 你好 ", HANZI_ONLY },
				{ "你 好", HANZI_ONLY },
				{ "你好。", HANZI_ONLY },
				{ "你，好", HANZI_ONLY },
				{ "+你", HANZI_ONLY },
				{ "hello你好", HANZI_ONLY },
				{ "你好(hello)", HANZI_ONLY },
				{ "酒dian", HANZI_ONLY },
				{ "jiu店", HANZI_ONLY },
				{ "200米", HANZI_ONLY }
		});
	}

	@Test
	public void test() {
		final DictionaryDataStore dataStore = Mockito.mock(DictionaryDataStore.class);
		final Dictionary dictionary = new Dictionary(dataStore, new PinyinParser(), new CurrentThreadExecutor());
		final Set<SearchType> searchTypes = dictionary.getApplicableSearchTypes(this.inputText);
		Assert.assertEquals(this.inputText, this.expectedTypes, searchTypes);
	}

}
