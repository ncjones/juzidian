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

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * A dictionary entry persisted to a database.
 */
@DatabaseTable(tableName = "entry")
public class DbDictionaryEntry {

	static final String COLUMN_HANZI_TRADITIONAL = "hanzi_traditional";

	static final String COLUMN_HANZI_SIMPLIFIED = "hanzi_simplified";

	static final String COLUMN_PINYIN = "pinyin";

	static final String COLUMN_ENGLISH = "english";

	@DatabaseField(id = true)
	private Long id;

	@DatabaseField(columnName = COLUMN_HANZI_TRADITIONAL, canBeNull = false)
	private String traditional;

	@DatabaseField(columnName = COLUMN_HANZI_SIMPLIFIED, canBeNull = false)
	private String simplified;

	@DatabaseField(columnName = COLUMN_PINYIN, canBeNull = false)
	private String pinyin;

	@DatabaseField(columnName = COLUMN_ENGLISH, canBeNull = false)
	private String english;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getTraditional() {
		return this.traditional;
	}

	public void setTraditional(final String traditional) {
		this.traditional = traditional;
	}

	public String getSimplified() {
		return this.simplified;
	}

	public void setSimplified(final String simplified) {
		this.simplified = simplified;
	}

	public String getPinyin() {
		return this.pinyin;
	}

	public void setPinyin(final String pinyin) {
		this.pinyin = pinyin;
	}

	public String getEnglish() {
		return this.english;
	}

	public void setEnglish(final String english) {
		this.english = english;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final DbDictionaryEntry other = (DbDictionaryEntry) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
