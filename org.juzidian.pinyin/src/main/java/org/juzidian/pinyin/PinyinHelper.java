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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

class PinyinHelper {

	private static final Collection<String> ALL_PINYIN_SYLLABLES;
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
	}

	private PinyinHelper() {
		/* no instantiation */
	}

	public static Collection<String> getValidSyllables() {
		return ALL_PINYIN_SYLLABLES;
	}

}
