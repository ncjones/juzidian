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
package org.juzidian.core.inject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.juzidian.core.DictionaryDataStore;
import org.juzidian.core.dataload.DictionaryServiceUrl;
import org.juzidian.core.datastore.DbDictionaryDataStore;
import org.juzidian.core.datastore.DbDictionaryEntry;
import org.juzidian.core.datastore.DbDictionaryMetadata;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

public abstract class DictionaryModule extends AbstractModule {

	@Override
	protected void configure() {
		this.bind(DictionaryDataStore.class).to(DbDictionaryDataStore.class);
		final ConnectionSource connectionSource;
		try {
			connectionSource = this.createConnectionSource();
		} catch (final SQLException e) {
			throw new ModuleConfigurationException(e);
		}
		this.bind(new TypeLiteral<Dao<DbDictionaryEntry, Long>>() {
		}).toInstance(this.<Dao<DbDictionaryEntry, Long>, DbDictionaryEntry> createDao(connectionSource, DbDictionaryEntry.class));
		this.bind(new TypeLiteral<Dao<DbDictionaryMetadata, Long>>() {
		}).toInstance(this.<Dao<DbDictionaryMetadata, Long>, DbDictionaryMetadata> createDao(connectionSource, DbDictionaryMetadata.class));
		final String dictionaryRegistryServiceUrl = this.getProperty("dictionaryRegistryServiceUrl");
		this.bind(URL.class).annotatedWith(DictionaryServiceUrl.class).toInstance(this.createUrl(dictionaryRegistryServiceUrl));
		this.bind(SAXParser.class).toInstance(this.createSaxParser());
	}

	private String getProperty(final String key) {
		final Properties properties = new Properties();
		final InputStream inputStream = this.getClass().getResourceAsStream("/juzidian-core.properties");
		if (inputStream == null) {
			throw new ModuleConfigurationException("Missing resource: /juzidian-core.properties");
		}
		try {
			properties.load(inputStream);
		} catch (final IOException e) {
			throw new ModuleConfigurationException(e);
		}
		return properties.getProperty(key);
	}

	private URL createUrl(final String spec) {
		try {
			return new URL(spec);
		} catch (final MalformedURLException e) {
			throw new ModuleConfigurationException(e);
		}
	}

	private SAXParser createSaxParser() {
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser;
		try {
			saxParser = factory.newSAXParser();
		} catch (final Exception e) {
			throw new ModuleConfigurationException(e);
		}
		return saxParser;
	}

	private <T extends Dao<U, Long>, U> T createDao(final ConnectionSource connectionSource, final Class<U> entityClass) {
		try {
			return DaoManager.<T, U> createDao(connectionSource, entityClass);
		} catch (final SQLException e) {
			throw new ModuleConfigurationException(e);
		}
	}

	protected abstract ConnectionSource createConnectionSource() throws SQLException;

}
