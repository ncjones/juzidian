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
package org.juzidian.build.imggen;

import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;

/**
 * A scalable icon which can render itself as a buffered image.
 */
public abstract class Icon {

	/**
	 * Get the dimensions of the icon, in pixels, when rendered at a scale
	 * factor of 1.0.
	 *
	 * @return the icon dimensions.
	 */
	protected abstract Dimension2D getBaseDimensions();

	/**
	 * Draw the icon onto the graphics at the given scale.
	 *
	 * @param graphics the graphics to draw the icon on to.
	 * @param scale the scale factor at which to draw the image.
	 */
	protected abstract void draw(final Graphics2D graphics, final double scale);

	/**
	 * Create a buffered image of the icon at the given scale.
	 *
	 * @param scale the scale factor at which to draw the image.
	 * @return a new buffered image with the icon drawn on it.
	 */
	public BufferedImage render(final double scale) {
		final int width = (int) (this.getBaseDimensions().getWidth() * scale);
		final int height = (int) (this.getBaseDimensions().getHeight() * scale);
		final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.draw(bufferedImage.createGraphics(), scale);
		return bufferedImage;
	}

}
