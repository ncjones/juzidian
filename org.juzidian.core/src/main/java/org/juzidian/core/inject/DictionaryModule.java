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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;

import org.juzidian.cedict.CedictInputStreamProvider;
import org.juzidian.cedict.CedictLoader;
import org.juzidian.core.DictionaryDataStore;
import org.juzidian.core.datastore.DbDictionaryDataStore;
import org.juzidian.core.datastore.DbDictionaryEntry;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

public abstract class DictionaryModule extends AbstractModule {

	private final String cedictDataPath;

	/**
	 * Create a dictionary module which will not use any {@link CedictLoader}
	 * instances.
	 */
	public DictionaryModule() {
		this(null);
	}

	/**
	 * Create a dictionary module which will use the given cedict data file for
	 * providing input streams to {@link CedictLoader} instances.
	 * 
	 * @param cedictDataPath the path to a cedict data file.
	 */
	public DictionaryModule(final String cedictDataPath) {
		this.cedictDataPath = cedictDataPath;
	}

	String getCedictDataPath() {
		return this.cedictDataPath;
	}

	@Override
	protected void configure() {
		this.bind(CedictLoader.class).toInstance(new CedictLoader(new CedictInputStreamProvider() {
			@Override
			public InputStream getInputStream() {
				final String cedictDataPath = DictionaryModule.this.getCedictDataPath();
				if (cedictDataPath == null) {
					throw new IllegalStateException("Dictionary Guice module was not provided with a cedict data path.");
				}
				try {
					return new FileInputStream(cedictDataPath);
				} catch (final FileNotFoundException e) {
					throw new IllegalStateException("Cedict data file does not exist: " + cedictDataPath, e);
				}
			}
		}));
		this.bind(DictionaryDataStore.class).to(DbDictionaryDataStore.class);
		final ConnectionSource connectionSource;
		try {
			connectionSource = this.createConnectionSource("jdbc:sqlite:juzidian.db");
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
		final Dao<DbDictionaryEntry, Long> entryDao = this.createEntryDao(connectionSource);
		this.bind(new TypeLiteral<Dao<DbDictionaryEntry, Long>>() {
		}).toInstance(entryDao);
	}

	private Dao<DbDictionaryEntry, Long> createEntryDao(final ConnectionSource connectionSource) {
		try {
			/*
			 * Explicit type parameters are necessary to prevent compilation
			 * error on open jdk 6.
			 */
			return DaoManager.<Dao<DbDictionaryEntry, Long>, DbDictionaryEntry> createDao(connectionSource, DbDictionaryEntry.class);
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract ConnectionSource createConnectionSource(String jdbcUrl) throws SQLException;

}
