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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Dimension2D;
import java.awt.geom.RoundRectangle2D;

/**
 * A cross ("X") icon.
 */
public class CrossIcon extends Icon {

	private final int width = 32;

	private final int height = this.width;

	private final double lineLength = this.width * 0.85f;

	private final double lineWidth = this.lineLength * 0.1f;

	private final double lineVerticalOffset = (this.height - this.lineLength) / 2d;

	private final double lineHorizontalOffset = (this.width - this.lineWidth) / 2d;

	private final double cornerRadius = this.lineWidth;

	private final int centreX = this.width / 2;

	private final int centreY = this.height / 2;

	@Override
	protected Dimension2D getBaseDimensions() {
		return new Dimension(this.width, this.height);
	}

	@Override
	protected void draw(final Graphics2D graphics, final double scale) {
		graphics.scale(scale, scale);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setPaint(Color.GRAY);
		final RoundRectangle2D rectangle = new RoundRectangle2D.Double(this.lineHorizontalOffset, this.lineVerticalOffset, this.lineWidth, this.lineLength,
				this.cornerRadius, this.cornerRadius);
		graphics.rotate(Math.PI / 4, this.centreX, this.centreY);
		graphics.fill(rectangle);
		graphics.rotate(Math.PI / 2, this.centreX, this.centreY);
		graphics.fill(rectangle);
	}

}
