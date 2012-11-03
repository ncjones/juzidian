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
package org.juzidian.core.datastore;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * The metadata for the dictionary data base.
 */
@DatabaseTable(tableName = "dictionary_metadata")
public class DbDictionaryMetadata {

	@DatabaseField(id = true)
	private Long id;

	@DatabaseField(columnName = "build_date", canBeNull = false)
	private Date buildDate;

	@DatabaseField(columnName = "version", canBeNull = false)
	private int version;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public Date getBuildDate() {
		return this.buildDate;
	}

	public void setBuildDate(final Date buildDate) {
		this.buildDate = buildDate;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "DbDictionaryMetadata [buildDate=" + this.buildDate + ", version=" + this.version + "]";
	}

}
