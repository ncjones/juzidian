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

import java.io.IOException;
import java.io.InputStream;

/**
 * Provides input streams containing UTF8-encoded CEDict entries.
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
public interface CedictInputStreamProvider {

	InputStream getInputStream() throws IOException;
}
