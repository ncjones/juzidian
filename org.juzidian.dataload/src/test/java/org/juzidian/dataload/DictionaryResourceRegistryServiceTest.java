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
import static org.mockito.Mockito.when;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Before;
import org.junit.Test;
import org.juzidian.dataload.DictionaryResource;
import org.juzidian.dataload.DictionaryResourceRegistry;
import org.juzidian.dataload.DictionaryResourceRegistryDeserializer;
import org.juzidian.dataload.DictionaryResourceRegistryService;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class DictionaryResourceRegistryServiceTest {

	private DictionaryResourceRegistryService dictionaryResourceRegistryService;

	@Before
	public void setUp() throws Exception {
		final SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
		final DictionaryResourceRegistryDeserializer deserializer = new DictionaryResourceRegistryDeserializer(saxParser);
		MockUrlHandler.delegate = Mockito.mock(MockUrlHandler.class);
		this.dictionaryResourceRegistryService = new DictionaryResourceRegistryService(new URL("mock://localhost/dictionaries/"), deserializer);
	}

	@Test
	public void getDictionaryResourceRegistryShouldRequestUrlForGivenVersion() throws Exception {
		final String xml = "<dictionaries></dictionaries>";
		when(MockUrlHandler.delegate.openConnection(Matchers.any(URL.class))).thenReturn(new MockUrlConnection(xml));
		this.dictionaryResourceRegistryService.getDictionaryResourceRegistry(3);
		Mockito.verify(MockUrlHandler.delegate).openConnection(Matchers.eq(new URL("mock://localhost/dictionaries/v3/registry.xml")));
	}

	@Test
	public void getDictionaryResourceRegistryShouldReturnDeserializedRegistry() throws Exception {
		final String xml = "<dictionaries><dictionary><url>http://foo/bar</url></dictionary></dictionaries>";
		when(MockUrlHandler.delegate.openConnection(Matchers.any(URL.class))).thenReturn(new MockUrlConnection(xml));
		final DictionaryResourceRegistry dictionaryResourceRegistry = this.dictionaryResourceRegistryService.getDictionaryResourceRegistry(3);
		assertThat(dictionaryResourceRegistry.getDictionaryResources(), hasSize(1));
		final DictionaryResource dictionaryResource = dictionaryResourceRegistry.getDictionaryResources().get(0);
		assertThat(dictionaryResource.getUrl(), equalTo("http://foo/bar"));
	}

}

