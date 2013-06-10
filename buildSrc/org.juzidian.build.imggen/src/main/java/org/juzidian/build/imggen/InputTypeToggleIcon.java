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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Dimension2D;

/**
 * A toggle state icon for an input search type button.
 */
public class InputTypeToggleIcon extends Icon {

	private final String text;

	public InputTypeToggleIcon(final String text) {
		this.text = text;
	}

	@Override
	protected Dimension2D getBaseDimensions() {
		return new Dimension(54, 34);
	}

	@Override
	protected void draw(final Graphics2D graphics, final double scale) {
		final int width = (int) (this.getBaseDimensions().getWidth() * scale);
		final int height = (int) (this.getBaseDimensions().getHeight() * scale);
		graphics.setComposite(AlphaComposite.Xor);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		final Font font = new Font("WenQuanYi Zen Hei", Font.PLAIN, 8 * height / 9);
		graphics.setFont(font);
		final FontMetrics fontMetrics = graphics.getFontMetrics();
		final int stringWidth = fontMetrics.stringWidth(this.text);
		graphics.setPaint(Color.GRAY);
		graphics.fillRoundRect(0, 0, width, height, width / 5, width / 5);
		final float baseline = height / 2f - (fontMetrics.getAscent() + fontMetrics.getDescent()) / 2f + fontMetrics.getAscent();
		graphics.drawString(this.text, (width - stringWidth) / 2f, baseline);
	}

}
