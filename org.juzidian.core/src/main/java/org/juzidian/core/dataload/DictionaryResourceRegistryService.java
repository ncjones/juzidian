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

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A service client for retrieving a remote {@link DictionaryResourceRegistry}.
 */
public class DictionaryResourceRegistryService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryResourceRegistryService.class);

	private final URL serviceEndPointUrl;

	private final DictionaryResourceRegistryDeserializer deserializer;

	@Inject
	public DictionaryResourceRegistryService(@DictionaryServiceUrl final URL serviceEndPointUrl,
			final DictionaryResourceRegistryDeserializer deserializer) {
		this.serviceEndPointUrl = serviceEndPointUrl;
		this.deserializer = deserializer;
	}

	/**
	 * Get the {@link DictionaryResourceRegistry} for the given data format
	 * version.
	 *
	 * @param dataFormatVersion
	 * @return a {@link DictionaryResourceRegistry}.
	 * @throws DictonaryResourceRegistryServiceException if retrieving the
	 *         registry fails.
	 */
	public DictionaryResourceRegistry getDictionaryResourceRegistry(final int dataFormatVersion) throws DictonaryResourceRegistryServiceException {
		try {
			final URL url = new URL(this.serviceEndPointUrl, "v" + dataFormatVersion + "/registry.xml");
			LOGGER.debug("Getting resource registry: {}", url);
			return this.deserializer.deserialize(url.openStream());
		} catch (final DictionaryResourceRegistryDeserializerException e) {
			throw new DictonaryResourceRegistryServiceException(e);
		} catch (final IOException e) {
			throw new DictonaryResourceRegistryServiceException(e);
		}
	}

}
