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
package org.juzidian.build.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;

import org.juzidian.cedict.CedictInputStreamProvider;
import org.juzidian.cedict.CedictLoader;
import org.juzidian.core.datastore.DbDictionaryDataStore;
import org.juzidian.core.inject.DictionaryModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class DictionaryDataBaseCreator {

	public static void main(final String[] args) {
		if (args.length < 1) {
			System.out.println("Path to CEDict data file must be specified.");
			return;
		}
		if (args.length < 2) {
			System.out.println("Path to database file must be specified.");
			return;
		}
		final String cedictDataFile = args[0];
		final String dbFileName = args[1];
		final String jdbcUrl = "jdbc:sqlite:" + dbFileName;
		final Injector injector = Guice.createInjector(new DictionaryModule() {
			@Override
			protected ConnectionSource createConnectionSource() throws SQLException {
				return new JdbcConnectionSource(jdbcUrl);
			}
		});
		final DbDictionaryDataStoreDbInitializer dbInitializer = new DbDictionaryDataStoreDbInitializer(
				new DbDictionaryDataStoreEntryPopulator(new CedictLoader(new CedictInputStreamProvider() {
					@Override
					public InputStream getInputStream() {
						try {
							return new FileInputStream(cedictDataFile);
						} catch (final FileNotFoundException e) {
							throw new IllegalStateException("Cedict data file does not exist: " + cedictDataFile, e);
						}
					}
				})));
		final DbDictionaryDataStore dictionaryDataStore = injector.getInstance(DbDictionaryDataStore.class);
		dbInitializer.initializeDb(dictionaryDataStore);
	}

}
