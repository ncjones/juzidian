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
package org.juzidian.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Common I/O utilities.
 */
public class IoUtil {

	/**
	 * Copy bytes from an input stream to an output stream and close both
	 * streams.
	 * 
	 * @param in an input stream to copy bytes from.
	 * @param out an output stream to copy bytes to.
	 * @throws IOException if reading or writing fails.
	 */
	public static void copy(final InputStream in, final OutputStream out) throws IOException {
		try {
			final byte[] buffer = new byte[10000];
			int bufferedBytes;
			while ((bufferedBytes = in.read(buffer)) > 0) {
				out.write(buffer, 0, bufferedBytes);
			}
		} finally {
			try {
				in.close();
			} finally {
				out.close();
			}
		}
	}

}
