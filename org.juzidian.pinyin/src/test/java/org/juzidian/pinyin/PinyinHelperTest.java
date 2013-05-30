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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class PinyinHelperTest {

	private final String[] partialSyllables = {
			"b",
			"be",
			"c",
			"ch",
			"cho",
			"chon",
			"co",
			"con",
			"d",
			"den",
			"din",
			"do",
			"don",
			"f",
			"fe",
			"fi",
			"fia",
			"g",
			"go",
			"gon",
			"h",
			"ho",
			"hon",
			"j",
			"jion",
			"k",
			"ko",
			"kon",
			"l",
			"len",
			"lon",
			"m",
			"n",
			"no",
			"non",
			"p",
			"pe",
			"q",
			"qion",
			"r",
			"ra",
			"ro",
			"ron",
			"s",
			"sh",
			"sho",
			"so",
			"son",
			"t",
			"ten",
			"tin",
			"to",
			"ton",
			"w",
			"we",
			"x",
			"xion",
			"y",
			"yon",
			"z",
			"zh",
			"zho",
			"zhon",
			"zo",
			"zon",
	};

	@Test
	public void getPartialSyllablesShouldReturnCollectionContainingAllKnownPartialSyllables() {
		assertThat(PinyinHelper.getPartialSyllables(), containsInAnyOrder(this.partialSyllables));
	}

}
