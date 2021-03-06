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
package org.juzidian.cli;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.juzidian.core.AsyncQueryExecutor;
import org.juzidian.core.inject.DictionaryModule;
import org.juzidian.core.inject.ModuleConfigurationException;
import org.juzidian.dataload.inject.DataloadModule;

import com.google.inject.AbstractModule;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class JuzidianCliModule extends AbstractModule {

	static final File DICTIONARY_DB_FILE = new File(System.getProperty("user.home") + "/.juzidian/juzidian-dictionary.db");

	@Override
	protected void configure() {
		install(new DictionaryModule());
		install(new DataloadModule());
		this.bind(ConnectionSource.class).toInstance(this.createConnectionSource());
		this.bind(File.class).annotatedWith(DictionaryDbPath.class).toInstance(DICTIONARY_DB_FILE);
		this.bind(ExecutorService.class).annotatedWith(AsyncQueryExecutor.class).toInstance(Executors.newSingleThreadExecutor());
	}

	private JdbcConnectionSource createConnectionSource() {
		final JdbcConnectionSource jdbcConnectionSource;
		try {
			jdbcConnectionSource = new JdbcConnectionSource("jdbc:sqlite:" + DICTIONARY_DB_FILE.getAbsolutePath());
		} catch (final SQLException e) {
			throw new ModuleConfigurationException(e);
		}
		return jdbcConnectionSource;
	}

}
