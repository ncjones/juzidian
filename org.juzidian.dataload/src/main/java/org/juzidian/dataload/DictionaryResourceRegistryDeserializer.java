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

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.xml.parsers.SAXParser;

import org.xml.sax.SAXException;

/**
 * Deserializes {@link DictionaryResourceRegistry} objects from XML.
 */
public class DictionaryResourceRegistryDeserializer {

	private final SAXParser saxParser;

	@Inject
	public DictionaryResourceRegistryDeserializer(final SAXParser saxParser) {
		this.saxParser = saxParser;
	}

	/**
	 * Parse XML from the input stream to produce a
	 * {@link DictionaryResourceRegistry}.
	 *
	 * @param inputStream an XML input stream.
	 * @return a {@link DictionaryResourceRegistry}.
	 * @throws DictionaryResourceRegistryDeserializerException if
	 *         deserialization fails.
	 */
	public DictionaryResourceRegistry deserialize(final InputStream inputStream) throws DictionaryResourceRegistryDeserializerException {
		final DictionaryRegistrySaxHandler handler = new DictionaryRegistrySaxHandler();
		try {
			this.saxParser.parse(inputStream, handler);
		} catch (final SAXException e) {
			throw new DictionaryResourceRegistryDeserializerException(e);
		} catch (final IOException e) {
			throw new DictionaryResourceRegistryDeserializerException(e);
		}
		return handler.getDictionaryResourceRegistry();
	}

}
