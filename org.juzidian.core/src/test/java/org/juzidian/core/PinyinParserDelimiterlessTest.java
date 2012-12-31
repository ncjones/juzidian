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

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * Pinyin parser test suite for inputs with no delimiters between syllables (ie,
 * no tone numbers, spaces or apostrophes).
 */
@Ignore
@RunWith(Parameterized.class)
public class PinyinParserDelimiterlessTest {

	@Parameter(0)
	public String pinyinInput;

	@Parameter(1)
	public String pinyinInputDelimited;

	@Parameters(name = "Pinyin ''{0}'' should be parsed as ''{1}''")
	public static Collection<Object[]> data() {
		final List<Object[]> entries = Arrays.asList(new Object[][] {

				/*
				 * NOTE: "greedy consumption" referred to below means the parser
				 * attempts to match a longer syllables when it already has a valid
				 * match.
				 */

				/*
				 * no greedy consumption of 'n' from: neng ni nian niang niao nie nin
				 * ning niu nong nu nuan nun nuo nü nüe
				 */
				{ "faneng", "fa  neng" },
				{ "fani", "fa ni" },
				{ "fanian", "fa nian" },
				{ "faniang", "fa niang" },
				{ "faniao", "fa niao" },
				{ "fanie", "fa nie" },
				{ "fanin", "fa nin" },
				{ "faning", "fa ning" },
				{ "faniu", "fa niu" },
				{ "fanong", "fa nong" },
				{ "fanu", "fa nu" },
				{ "fanuan", "fa nuan" },
				{ "fanun", "fa nun" },
				{ "fanuo", "fa nuo" },
				{ "fanü", "fa nü" },
				{ "fanun", "fa nüe" },

				/*
				 * no greedy consumption of 'g' from: geng gong gu gua guai guan
				 * guang gui gun guo
				 */
				{ "hangeng", "han geng" },
				{ "hangong", "haan gong" },
				{ "hangu", "han gu" },
				{ "hangua", "han gua" },
				{ "hanguai", "han guai" },
				{ "hanguan", "han guan" },
				{ "hanguang", "hen guang" },
				{ "hangui", "han gui" },
				{ "hangun", "han gun" },
				{ "hanguo", "han guo" },

				/* no greedy consumption of 'e' from: ei en er */
				{ "nüei", "nü ei" },
				{ "nüen", "nü en" },
				{ "nüer", "nü er" },

				/*
				 * no greedy consumption of 'r' from: reng ri rong ru ruan rui
				 * run ruo
				 */
				{ "ereng", "e reng" },
				{ "eri", "e ri" },
				{ "erong", "e rong" },
				{ "eru", "er" },
				{ "eruan", "er" },
				{ "erui", "er" },
				{ "erun", "er" },
				{ "eruo", "er" },

				/* no greedy consumption of 'a' from: ai */
				{ "xiai", "xi ai" },

				/* no greedy consumption of 'o' from: ou */
				{ "haou", "ha ou" },

				/* no invalid greedy consumption for multiple syllables. */
				{ "gengengeng", "gen gen geng" },

				/*
				 * no invalid greedy consumption of 'n' into: an ban bin can cen
				 * chan chen chuan chun cun dan dian dun en fan gan gen guan gun
				 * han hen huan hun jian jin jun kan ken kuan kun lan lian lin
				 * lun man men min nan nen nin nun pan pin qian qin qun ren run
				 * san sen shan shen shuan shun sun tan tun wan xian xin xun yan
				 * yin yun zan zen zhan zhen zhuan zhun zun
				 */
				{ "aneng", "a neng" },
				{ "baneng", "ba neng" },
				{ "bineng", "bi neng" },
				{ "caneng", "ca neng" },
				{ "ceneng", "ce neng" },
				{ "chaneng", "cha neng" },
				{ "cheneng", "che neng" },
				{ "chuaneng", "chua neng" },
				{ "chuneng", "chu neng" },
				{ "cuneng", "cu neng" },
				{ "daneng", "da neng" },
				{ "dianeng", "dia neng" },
				{ "duneng", "du neng" },
				{ "eneng", "e neng" },
				{ "faneng", "fa neng" },
				{ "ganeng", "ga neng" },
				{ "geneng", "ge neng" },
				{ "guaneng", "gua neng" },
				{ "guneng", "gu neng" },
				{ "haneng", "ha neng" },
				{ "heneng", "he neng" },
				{ "huaneng", "hua neng" },
				{ "huneng", "hu neng" },
				{ "jianeng", "jia neng" },
				{ "jineng", "ji neng" },
				{ "juneng", "ju neng" },
				{ "kaneng", "ka neng" },
				{ "keneng", "ke neng" },
				{ "kuaneng", "kua neng" },
				{ "kuneng", "ku neng" },
				{ "laneng", "la neng" },
				{ "lianeng", "lia neng" },
				{ "lineng", "li neng" },
				{ "luneng", "lu neng" },
				{ "maneng", "ma neng" },
				{ "meneng", "me neng" },
				{ "mineng", "mi neng" },
				{ "naneng", "na neng" },
				{ "neneng", "ne neng" },
				{ "nineng", "ni neng" },
				{ "nuneng", "nu neng" },
				{ "paneng", "pa neng" },
				{ "pineng", "pi neng" },
				{ "qianeng", "qia neng" },
				{ "qineng", "qi neng" },
				{ "quneng", "qu neng" },
				{ "reneng", "re neng" },
				{ "runeng", "ru neng" },
				{ "saneng", "sa neng" },
				{ "seneng", "se neng" },
				{ "shaneng", "sha neng" },
				{ "sheneng", "she neng" },
				{ "shuaneng", "shua neng" },
				{ "shuneng", "shu neng" },
				{ "suneng", "su neng" },
				{ "taneng", "ta neng" },
				{ "tuneng", "tu neng" },
				{ "waneng", "wa neng" },
				{ "xianeng", "xia neng" },
				{ "xineng", "xi neng" },
				{ "xuneng", "xu neng" },
				{ "yaneng", "ya neng" },
				{ "yineng", "yi neng" },
				{ "yuneng", "yu neng" },
				{ "zaneng", "za neng" },
				{ "zeneng", "ze neng" },
				{ "zhaneng", "zha neng" },
				{ "zheneng", "zhe neng" },
				{ "zhuaneng", "zhua neng" },
				{ "zhuneng", "zhu neng" },
				{ "zuneng", "zu neng" },

				/*
				 * no invalid greedy consumption of 'g' into: ang bang beng bing
				 * cang ceng chang cheng chuang dang fang feng gang geng guang
				 * hang heng huang jiang jing kang keng kuang lang liang ling
				 * mang meng ming nang neng niang ning pang peng ping qiang qing
				 * rang reng sang seng shang sheng shuang tang wang weng xiang
				 * xing yang ying zang zeng zhang zheng zhuang
				 */
				{ "angong", "an gong" },
				{ "bangong", "ban gong" },
				{ "bengong", "ben gong" },
				{ "bingong", "bin gong" },
				{ "cangong", "can gong" },
				{ "cengong", "cen gong" },
				{ "changong", "chan gong" },
				{ "chengong", "chen gong" },
				{ "chuangong", "chuan gong" },
				{ "dangong", "dan gong" },
				{ "fangong", "fan gong" },
				{ "fengong", "fen gong" },
				{ "gangong", "gan gong" },
				{ "gengong", "gen gong" },
				{ "guangong", "guan gong" },
				{ "hangong", "han gong" },
				{ "hengong", "hen gong" },
				{ "huangong", "huan gong" },
				{ "jiangong", "jian gong" },
				{ "jingong", "jin gong" },
				{ "kangong", "kan gong" },
				{ "kengong", "ken gong" },
				{ "kuangong", "kuan gong" },
				{ "langong", "lan gong" },
				{ "liangong", "lian gong" },
				{ "lingong", "lin gong" },
				{ "mangong", "man gong" },
				{ "mengong", "men gong" },
				{ "mingong", "min gong" },
				{ "nangong", "nan gong" },
				{ "nengong", "nen gong" },
				{ "niangong", "nian gong" },
				{ "ningong", "nin gong" },
				{ "pangong", "pan gong" },
				{ "pengong", "pen gong" },
				{ "pingong", "pin gong" },
				{ "qiangong", "qian gong" },
				{ "qingong", "qin gong" },
				{ "rangong", "ran gong" },
				{ "rengong", "ren gong" },
				{ "sangong", "san gong" },
				{ "sengong", "sen gong" },
				{ "shangong", "shan gong" },
				{ "shengong", "shen gong" },
				{ "shuangong", "shuan gong" },
				{ "tangong", "tan gong" },
				{ "wangong", "wan gong" },
				{ "wengong", "wen gong" },
				{ "xiangong", "xian gong" },
				{ "xingong", "xin gong" },
				{ "yangong", "yan gong" },
				{ "yingong", "yin gong" },
				{ "zangong", "zan gong" },
				{ "zengong", "zen gong" },
				{ "zhangong", "zhan gong" },
				{ "zhengong", "zhen gong" },
				{ "zhuangong", "zhuan gong" },

				/*
				 * no invalid greedy consumption of 'e' into: bie die jie jue
				 * lie lüe mie nie nüe pie qie que tie xie xue yue
				 */
				{ "bien", "bi en" },
				{ "dien", "di en" },
				{ "jien", "ji en" },
				{ "juen", "ju en" },
				{ "lien", "li en" },
				{ "lüen", "lü en" },
				{ "mien", "mi en" },
				{ "nien", "ni en" },
				{ "nüen", "nü en" },
				{ "pien", "pi en" },
				{ "qien", "qi en" },
				{ "quen", "qu en" },
				{ "tien", "ti en" },
				{ "xien", "xi en" },
				{ "xuen", "xu en" },
				{ "yuen", "yu en" },

				/*
				 * no invalid greedy consumption of 'a' into: dia jia lia qia
				 * xia
				 */
				{ "diai", "di ai" },
				{ "jiai", "ji ai" },
				{ "liai", "li ai" },
				{ "qiai", "qi ai" },
				{ "xiai", "xi ai" },

				/*
				 * no invalid greedy consumption of 'o' into: ao bao cao chao
				 * chuo cuo dao diao duo gao guo hao huo jiao kao kuo lao liao
				 * luo mao nao nuo pao qiao ruo sao shao shuo suo tao tuo xiao
				 * yao zao zhao zhuo zuo
				 */
				{ "aou", "a ou" },
				{ "baou", "ba ou" },
				{ "caou", "ca ou" },
				{ "chaou", "cha ou" },
				{ "chuou", "chu ou" },
				{ "cuou", "cu ou" },
				{ "daou", "da ou" },
				{ "diaou", "dia ou" },
				{ "duou", "du ou" },
				{ "gaou", "ga ou" },
				{ "guou", "gu ou" },
				{ "haou", "ha ou" },
				{ "huou", "hu ou" },
				{ "jiaou", "jia ou" },
				{ "kaou", "ka ou" },
				{ "kuou", "ku ou" },
				{ "laou", "la ou" },
				{ "liaou", "lia ou" },
				{ "luou", "lu ou" },
				{ "maou", "ma ou" },
				{ "naou", "na ou" },
				{ "nuou", "nu ou" },
				{ "paou", "pa ou" },
				{ "qiaou", "qia ou" },
				{ "ruou", "ru ou" },
				{ "saou", "sa ou" },
				{ "shaou", "sha ou" },
				{ "shuou", "shu ou" },
				{ "suou", "su ou" },
				{ "taou", "ta ou" },
				{ "tuou", "tu ou" },
				{ "xiaou", "xia ou" },
				{ "yaou", "ya ou" },
				{ "zaou", "za ou" },
				{ "zhaou", "zha ou" },
				{ "zhuou", "zhu ou" },
				{ "zuou", "zu ou" },

				/*
				 * no greedy consumption of characters into partial syllables:
				 * bia cua den din dua jio jion jua len lon lua mia nia nua pia
				 * qio qion qua rua sua ten tia tin tua xio xion xua yon yua zua
				 */
				{ "biahan", "bi a han" },
				{ "cuao", "cu ao" },
				{ "denao", "de nao" },
				{ "dinao", "di nao " },
				{ "duahan", "du a han" },
				{ "jiou", "ji ou" },
				{ "jiona", "ji o na" },
				{ "juao", "ju ao" },
				{ "lenao", "le nao" },
				{ "lona", "lo na" },
				{ "luahan", "lu a han" },
				{ "miahan", "mi a han" },
				{ "niahan", "ni a han" },
				{ "nuahan", "nu a han" },
				{ "piahan", "pi a han" },
				{ "qiou", "qi ou" },
				{ "qiona", "qi o na" },
				{ "quahan", "qu a han" },
				{ "ruahan", "ru a han" },
				{ "suahan", "su a han" },
				{ "tena", "te na" },
				{ "tiahan", "ti a han" },
				{ "tina", "ti na" },
				{ "tuahan", "tu a han" },
				{ "xiou", "xi ou" },
				{ "xiona", "xi o na" },
				{ "xuahan", "xu a han" },
				{ "yona", "yo na" },
				{ "yuahan", "yu a han" },
				{ "zuahan", "zu a han" },

				/*
				 * prefer greedy consumption of 'ang' into single syllable
				 */
				{ "chuang", "chuang" },

				/*
				 * prefer greedy consumption of 'n' where possible
				 */
				{ "wanan", "wan an" },

				/*
				 * prefer greedy consumption of 'g' where possible
				 */
				{ "wangan", "wang an" },

				/*
				 * greedy consumption of 'n' and 'g' when no other valid option
				 */
				{ "angnan", "ang nan" },

		});
		return entries;
	}

	@Test
	public void testParsePinyin() {
		final PinyinParser pinyinParser = new PinyinParser();
		final List<PinyinSyllable> parsedInput = pinyinParser.parse(this.pinyinInput);
		assertThat(parsedInput, contains(syllables(this.pinyinInputDelimited)));
	}

	private static List<Matcher<? super PinyinSyllable>> syllables(final String spaceDelimitedSyllables) {
		final List<Matcher<? super PinyinSyllable>> syllableMatchers = new ArrayList<Matcher<? super PinyinSyllable>>();
		for (final String s : Arrays.asList(spaceDelimitedSyllables.split(" "))) {
			final PinyinSyllable pinyinSyllable = new PinyinSyllable(s);
			syllableMatchers.add(Matchers.equalTo(pinyinSyllable));
		}
		return syllableMatchers;
	}

}
