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
package org.juzidian.dataload;

import static org.mockito.Matchers.eq;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.juzidian.dataload.DownloadProgressHandler;
import org.juzidian.dataload.ProgressMonitoringInputStream;
import org.mockito.Mockito;

public class ProgressMonitoringInputStreamTest {

	@Test
	public void downloadProgressHandlerShouldRecieveNotifications() throws Exception {
		final DownloadProgressHandler handler = Mockito.mock(DownloadProgressHandler.class);
		final ByteArrayInputStream bais = new ByteArrayInputStream(new byte[18000]);
		final ProgressMonitoringInputStream inputStream = new ProgressMonitoringInputStream(bais, 18000, handler);
		consumeInputStream(inputStream);
		Mockito.verify(handler).handleProgress(eq(18000), eq(8192));
		Mockito.verify(handler).handleProgress(eq(18000), eq(16384));
		Mockito.verify(handler).handleProgress(eq(18000), eq(18000));
	}

	private static void consumeInputStream(final InputStream inputStream) throws IOException {
		while (inputStream.read() != -1) {
			/* discard bytes */
		}
	}

}
