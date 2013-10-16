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
package org.juzidian.dataload;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Before;
import org.junit.Test;
import org.juzidian.dataload.DictionaryResource;
import org.juzidian.dataload.DictionaryResourceRegistry;
import org.juzidian.dataload.DictionaryResourceRegistryDeserializer;

public class DictionaryResourceRegistryDeserializerTest {

	private DictionaryResourceRegistryDeserializer deserializer;

	@Before
	public void setUp() throws Exception {
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		final SAXParser saxParser = factory.newSAXParser();
		this.deserializer = new DictionaryResourceRegistryDeserializer(saxParser);
	}

	@Test
	public void test() throws Exception {
		final String xml = "<juzidianDictionaries>\n" +
				"	  <dictionary formatVersion='0'>\n" +
				"	    <size>12345678</size>\n" +
				"	    <sha1>1234abcd</sha1>\n" +
				"	    <url>http://test/dict1</url>\n" +
				"	  </dictionary>\n" +
				"	</juzidianDictionaries>\n";
		final DictionaryResourceRegistry registry = this.deserializer.deserialize(new ByteArrayInputStream(xml.getBytes()));
		final List<DictionaryResource> dictionaryResources = registry.getDictionaryResources();
		assertThat(dictionaryResources, hasSize(1));
		final DictionaryResource dictionaryResource = dictionaryResources.get(0);
		assertThat(dictionaryResource.getSha1(), equalTo("1234abcd"));
		assertThat(dictionaryResource.getSize(), equalTo(12345678));
		assertThat(dictionaryResource.getUrl(), equalTo("http://test/dict1"));
	}

}
