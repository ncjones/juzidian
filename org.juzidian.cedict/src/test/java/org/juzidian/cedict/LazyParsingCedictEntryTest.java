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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LazyParsingCedictEntryTest {

	private LazyParsingCedictEntry entry;

	@Before
	public void setUp() {
		this.entry = new LazyParsingCedictEntry("你好", "你好", "ni2 hao3", "hello/hi/g'day");
	}

	@Test
	public void englishWordsShouldBeSplitOnSlash() {
		Assert.assertEquals("hello", this.entry.getDefinitions().get(0));
		Assert.assertEquals("hi", this.entry.getDefinitions().get(1));
		Assert.assertEquals("g'day", this.entry.getDefinitions().get(2));
	}

	@Test
	public void pinyinSyllablesShouldBeSplitOnSpace() {
		Assert.assertEquals("ni", this.entry.getPinyinSyllables().get(0).getLetters());
		Assert.assertEquals("hao", this.entry.getPinyinSyllables().get(1).getLetters());
	}

	@Test
	public void pinyinSyllablesShouldContainTones() {
		Assert.assertEquals(2, this.entry.getPinyinSyllables().get(0).getToneNumber());
		Assert.assertEquals(3, this.entry.getPinyinSyllables().get(1).getToneNumber());
	}

	@Test
	public void pinyinSyllablesIgnoreMiddleDotPunctuation() {
		this.entry = new LazyParsingCedictEntry("泰格・伍茲,", "泰格・伍兹", "Tai4 ge2 · Wu3 zi1", "Tiger Woods");
		Assert.assertEquals(4, this.entry.getPinyinSyllables().size());
	}

	@Test
	public void pinyinSyllablesIgnoreCommaPunctuation() {
		this.entry = new LazyParsingCedictEntry("挨戶，挨家", "挨户，挨家", "ai1 hu4 , ai1 jia1", "to go from house to house");
		Assert.assertEquals(4, this.entry.getPinyinSyllables().size());
	}

	@Test
	public void pinyinSyllablesIgnoreEnglishInitialisms() {
		this.entry = new LazyParsingCedictEntry("Ｕ盤", "Ｕ盘", "U pan2", "USB flash drive");
		Assert.assertEquals("pan", this.entry.getPinyinSyllables().get(0).getLetters());
	}

}
