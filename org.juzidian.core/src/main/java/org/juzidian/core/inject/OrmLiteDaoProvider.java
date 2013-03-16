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
package org.juzidian.core.inject;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Provider;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

public abstract class OrmLiteDaoProvider<T, U> implements Provider<Dao<T, U>> {

	private final Dao<T, U> dao;

	@Inject
	public OrmLiteDaoProvider(final ConnectionSource connectionSource) {
		try {
			this.dao = DaoManager.<Dao<T, U>, T> createDao(connectionSource, this.getEntityClass());
		} catch (final SQLException e) {
			throw new ModuleConfigurationException(e);
		}
	}

	protected abstract Class<T> getEntityClass();

	@Override
	public Dao<T, U> get() {
		return this.dao;
	}

}
