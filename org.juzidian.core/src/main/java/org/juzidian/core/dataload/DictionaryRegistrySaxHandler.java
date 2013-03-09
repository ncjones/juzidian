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
package org.juzidian.core.dataload;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

final class DictionaryRegistrySaxHandler extends DefaultHandler {

	private static interface DictionaryElementTextHandler {
		void handleText(DictionaryResourceImpl resource, String text);
	}

	private static final DictionaryElementTextHandler DICTIONARY_URL_HANDLER = new DictionaryElementTextHandler() {
		@Override
		public void handleText(final DictionaryResourceImpl resource, final String text) {
			resource.setUrl(text);
		}
	};

	private static DictionaryElementTextHandler DICTIONARY_SHA1_HANDLER = new DictionaryElementTextHandler() {
		@Override
		public void handleText(final DictionaryResourceImpl resource, final String text) {
			resource.setSha1(text);
		}
	};

	private static DictionaryElementTextHandler DICTIONARY_SIZE_HANDLER = new DictionaryElementTextHandler() {
		@Override
		public void handleText(final DictionaryResourceImpl resource, final String text) {
			resource.setSize(Integer.parseInt(text));
		}
	};

	private final List<DictionaryResource> dictionaryResources = new ArrayList<DictionaryResource>();

	private DictionaryResourceImpl currentDictionaryResource;

	private DictionaryElementTextHandler currentTextHandler;

	/**
	 * @return the {@link DictionaryResourceRegistry} that has been parsed.
	 */
	public DictionaryResourceRegistry getDictionaryResourceRegistry() {
		return new DictionaryResourceRegistry(this.dictionaryResources);
	}

	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
		if ("dictionary".equals(qName)) {
			this.currentDictionaryResource = new DictionaryResourceImpl();
			this.dictionaryResources.add(this.currentDictionaryResource);
		} else if ("url".equals(qName)) {
			this.currentTextHandler = DICTIONARY_URL_HANDLER;
		}else if ("size".equals(qName)) {
			this.currentTextHandler = DICTIONARY_SIZE_HANDLER;
		}else if ("sha1".equals(qName)) {
			this.currentTextHandler = DICTIONARY_SHA1_HANDLER;
		}
	}

	@Override
	public void endElement(final String uri, final String localName, final String qName) throws SAXException {
		this.currentTextHandler = null;
	}

	@Override
	public void characters(final char[] ch, final int start, final int length) throws SAXException {
		if (this.currentTextHandler != null) {
			this.currentTextHandler.handleText(this.currentDictionaryResource, new String(ch, start, length));
		}
	}

}