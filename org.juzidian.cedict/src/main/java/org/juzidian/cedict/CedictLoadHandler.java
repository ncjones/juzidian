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
package org.juzidian.cedict;

/**
 * Handles CEDict entry loading events.
 */
public interface CedictLoadHandler {

	/**
	 * A CEDict input stream is about to be read.
	 */
	void loadingStarted();

	/**
	 * An entry has been read from a CEDict input stream.
	 * 
	 * @param cedictEntry the {@link CedictEntry} that has been loaded.
	 */
	void entryLoaded(CedictEntry cedictEntry);

	/**
	 * A CEDict input stream has finished being read.
	 */
	void loadingFinished();
}
