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

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.juzidian.util.HexUtil;

public class HexUtilTest {

	@Test(expected = NullPointerException.class)
	public void bytesToHexShouldRejectNullByteArray() {
		HexUtil.bytesToHex(null);
	}

	@Test
	public void bytesToHexShouldReturnEmptyStringForEmptyByteArray() {
		final String hex = HexUtil.bytesToHex(new byte[] {});
		Assert.assertThat(hex, Matchers.equalTo(""));
	}

	@Test
	public void bytesToHexShouldReturnHexForSingleByteArray() {
		final String hex = HexUtil.bytesToHex(new byte[] { 0x4f });
		Assert.assertThat(hex, Matchers.equalTo("4f"));
	}

	@Test
	public void bytesToHexShouldReturnHexForMultiByteArray() {
		final String hex = HexUtil.bytesToHex(new byte[] { 0x36, 0x48, 0x5a });
		Assert.assertThat(hex, Matchers.equalTo("36485a"));
	}

	@Test
	public void bytesToHexShouldReturnHexWithLeadingZeroForByteUnder16() {
		final String hex = HexUtil.bytesToHex(new byte[] { 0x0a });
		Assert.assertThat(hex, Matchers.equalTo("0a"));
	}

	@Test
	public void bytesToHexShouldReturnHexWithLeadingZerosForByteZero() {
		final String hex = HexUtil.bytesToHex(new byte[] { 0x00 });
		Assert.assertThat(hex, Matchers.equalTo("00"));
	}

	@Test
	public void bytesToHexShouldReturnHexForBytesAbove128() {
		final String hex = HexUtil.bytesToHex(new byte[] { (byte) 0x9a });
		Assert.assertThat(hex, Matchers.equalTo("9a"));
	}

}
