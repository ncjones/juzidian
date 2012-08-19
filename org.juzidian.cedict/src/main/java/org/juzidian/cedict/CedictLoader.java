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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Reads CEDict entries from an input stream.
 * <p>
 * A valid CEDict entry is expected to have the following format:
 * 
 * <pre>
 * {traditional characters} {space} {simplified characters} {space}
 *    {left bracket} {pinyin word} {right bracket} {space} ({slash}
 *    {english meaning})+ {slash}
 * </pre>
 * 
 * See <a href="http://cc-cedict.org/wiki/format:syntax">CC-CEDict Wiki</a> for
 * more information.
 */
public class CedictLoader {

	/**
	 * Read entries from a valid, UTF8-encoded CEDict input stream and notify a
	 * {@link CedictLoadHandler} of each entry.
	 * <p>
	 * Lines read from the input stream beginning with "#" are ignored.
	 * <p>
	 * No validation is performed on the input; behaviour is undefined for
	 * invalid input.
	 * 
	 * @param inputStream an input stream to read lines from.
	 * @param loadHandler an {@link CedictLoadHandler} to notify of entry
	 *        loading events.
	 * @throws IOException if an entry fails to be read.
	 */
	public void loadEntries(final InputStream inputStream, final CedictLoadHandler loadHandler) throws IOException {
		final Reader fileReader = new InputStreamReader(inputStream, "UTF-8");
		final BufferedReader bufferedReader = new BufferedReader(fileReader);
		loadHandler.loadingStarted();
		String line = bufferedReader.readLine();
		while (line != null) {
			if (!line.startsWith("#")) {
				loadHandler.entryLoaded(new CedictLineParser(line).parse());
			}
			line = bufferedReader.readLine();
		}
		loadHandler.loadingFinished();
	}

}