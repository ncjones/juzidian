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
package org.juzidian.core;

/**
 * A tone associated with a Mandarin Chinese syllable.
 */
public enum Tone {

	FIRST(1),

	SECOND(2),

	THIRD(3),

	FOURTH(4),

	NEUTRAL(5),

	ANY(null);

	private final Integer number;

	private Tone(final Integer number) {
		this.number = number;
	}

	/**
	 * @return the numeric representation of the tone.
	 */
	public Integer getNumber() {
		return this.number;
	}

	/**
	 * @param toneNumber the number (1-5) that represents a tone.
	 * @return the tone with the given tone number.
	 */
	public static Tone valueOf(final Integer toneNumber) {
		if (toneNumber == null) {
			return Tone.ANY;
		}
		for (final Tone tone : Tone.values()) {
			if (toneNumber.equals(tone.number)) {
				return tone;
			}
		}
		throw new IllegalArgumentException("Invalid tone number: " + toneNumber);
	}

	/**
	 * @param tone another tone.
	 * @return <code>true</code> if the given tone is this tone or this tone is
	 *         ANY.
	 * @throws IllegalArgumentException if tone is <code>null</code>.
	 */
	public boolean matches(final Tone tone) {
		if (tone == null) {
			throw new IllegalArgumentException("tone is null");
		}
		return tone.equals(this) || this.equals(ANY);
	}

}
