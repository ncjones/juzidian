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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

class PinyinHelper {

	private static final Collection<String> ALL_PINYIN_SYLLABLES;

	private static final Collection<String> PARTIAL_SYLLABLES;

	static {
		final String[] pinyinArray = {
				"a", "ai", "an", "ang", "ao", "e", "ei", "en", "er", "o", "ou",

				"ba", "pa", "ma", "fa", "da", "ta", "na", "la", "ga", "ka", "ha", "zha", "cha", "sha", "za", "ca", "sa", "wa", "ya",
				"bai", "pai", "mai", "dai", "tai", "nai", "lai", "gai", "kai", "hai", "zhai", "chai", "shai", "zai", "cai", "sai", "wai",
				"ban", "pan", "man", "fan", "dan", "tan", "nan", "lan", "gan", "kan", "han", "zhan", "chan", "shan", "ran", "zan", "can", "san", "wan", "yan",
				"bang", "pang", "mang", "fang", "dang", "tang", "nang", "lang", "gang", "kang", "hang", "zhang", "chang", "shang", "rang", "zang", "cang", "sang", "wang", "yang",
				"bao", "pao", "mao", "dao", "tao", "nao", "lao", "gao", "kao", "hao", "zhao", "chao", "shao", "rao", "zao", "cao", "sao", "yao",

				"me", "de", "te", "ne", "le", "ge", "ke", "he", "zhe", "che", "she", "re", "ze", "ce", "se", "ye",
				"bei", "pei", "mei", "fei", "dei", "tei", "nei", "lei", "gei", "kei", "hei", "shei", "zei", "wei",
				"ben", "pen", "men", "fen", "nen", "gen", "ken", "hen", "zhen", "chen", "shen", "ren", "zen", "cen", "sen", "wen",
				"beng", "peng", "meng", "feng", "deng", "teng", "neng", "leng", "geng", "keng", "heng", "zheng", "cheng", "sheng",
				"reng", "zeng", "ceng", "seng", "weng",

				"bi", "pi", "mi", "di", "ti", "ni", "li", "ji", "qi", "xi", "zhi", "chi", "shi", "ri", "zi", "ci", "si", "yi",
				"dia", "lia", "jia", "qia", "xia",
				"bian", "pian", "mian", "dian", "tian", "nian", "lian", "jian", "qian", "xian",
				"niang", "liang", "jiang", "qiang", "xiang",
				"biao", "piao", "miao", "fiao", "diao", "tiao", "niao", "liao", "jiao", "qiao", "xiao",
				"bie", "pie", "mie", "die", "tie", "nie", "lie", "jie", "qie", "xie",
				"bin", "pin", "min", "nin", "lin", "jin", "qin", "xin", "yin",
				"bing", "ping", "ming", "ding", "ting", "ning", "ling", "jing", "qing", "xing", "ying",
				"jiong", "qiong", "xiong",
				"miu", "diu", "niu", "liu", "jiu", "qiu", "xiu",

				"bo", "po", "mo", "fo", "lo", "wo", "yo",
				"dong", "tong", "nong", "long", "gong", "kong", "hong", "zhong", "chong", "rong", "zong", "cong", "song", "yong",
				"pou", "mou", "fou", "dou", "tou", "nou", "lou", "gou", "kou", "hou", "zhou", "chou", "shou", "rou", "zou", "cou",
				"sou", "you",

				"bu", "pu", "mu", "fu", "du", "tu", "nu", "lu", "gu", "ku", "hu", "ju", "qu", "xu", "zhu", "chu", "shu", "ru", "zu", "cu", "su", "wu", "yu",
				"gua", "kua", "hua", "zhua", "chua", "shua",
				"guai", "kuai", "huai", "zhuai", "chuai", "shuai",
				"duan", "tuan", "nuan", "luan", "guan", "kuan", "huan", "juan", "quan", "xuan", "zhuan", "chuan", "shuan", "ruan", "zuan", "cuan", "suan", "yuan",
				"guang", "kuang", "huang", "zhuang", "chuang", "shuang",
				"jue", "que", "xue", "yue",
				"dui", "tui", "gui", "kui", "hui", "zhui", "chui", "shui", "rui", "zui", "cui", "sui",
				"dun", "tun", "nun", "lun", "gun", "kun", "hun", "jun", "qun", "xun", "zhun", "chun", "shun", "run", "zun", "cun", "sun", "yun",
				"duo", "tuo", "nuo", "luo", "guo", "kuo", "huo", "zhuo", "chuo", "shuo", "ruo", "zuo", "cuo", "suo",

				"nü", "lü",
				"nüe", "lüe",
		};
		ALL_PINYIN_SYLLABLES = Collections.unmodifiableSortedSet(new TreeSet<String>(Arrays.asList(pinyinArray)));
		PARTIAL_SYLLABLES = Collections.unmodifiableSortedSet(createPartialSyllables(ALL_PINYIN_SYLLABLES));
	}

	private PinyinHelper() {
		/* no instantiation */
	}

	public static Collection<String> getValidSyllables() {
		return ALL_PINYIN_SYLLABLES;
	}

	/**
	 * Get all partial Pinyin syllables.
	 * <p>
	 * A partial Pinyin syllable is a string which is not a valid Pinyin
	 * syllable, nor a combination of valid Pinyin Syllables, but for which
	 * there is at least one valid Pinyin syllable that it is the start of.
	 * 
	 * @return a Collection of all partial Pinyin syllables.
	 */
	public static Collection<String> getPartialSyllables() {
		return PARTIAL_SYLLABLES;
	}

	private static SortedSet<String> createPartialSyllables(final Collection<String> validSyllables) {
		final SortedSet<String> partialSyllables = new TreeSet<String>();
		for (final String syllable : validSyllables) {
			partialSyllables.addAll(getPartials(syllable));
		}
		return partialSyllables;
	}

	private static Collection<String> getPartials(final String syllable) {
		final List<String> partials = new ArrayList<String>();
		for (int i = syllable.length() - 1; i > 0; i--) {
			final String syllablePart = syllable.substring(0, i);
			if (isPartial(syllablePart)) {
				partials.add(syllablePart);
			}
		}
		return partials;
	}

	private static boolean isPartial(final String syllablePart) {
		if (isValidSyllable(syllablePart)) {
			return false;
		}
		if (syllablePart.length() == 1) {
			return true;
		}
		final String partialEnd = syllablePart.substring(syllablePart.length() - 1);
		final String partialStart = syllablePart.substring(0, syllablePart.length() - 1);
		return !(isValidSyllable(partialEnd) && isValidSyllable(partialStart));
	}

	private static boolean isValidSyllable(final String syllablePart) {
		return PinyinHelper.getValidSyllables().contains(syllablePart);
	}

}
