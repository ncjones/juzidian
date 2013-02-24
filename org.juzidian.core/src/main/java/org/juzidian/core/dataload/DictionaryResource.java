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

/**
 * A description of remotely hosted dictionary database resource.
 */
public interface DictionaryResource {

	/**
	 * @return the URL for downloading the remote dictionary resource.
	 */
	String getUrl();

	/**
	 * @return the size of the dictionary resource in bytes.
	 */
	int getSize();

	/**
	 * @return the SHA-1 hash of the dictionary resource.
	 */
	String getSha1();

}