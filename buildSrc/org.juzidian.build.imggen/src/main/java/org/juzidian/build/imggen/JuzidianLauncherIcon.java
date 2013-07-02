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
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Path2D;

/**
 * The icon for the Juzidian application launcher.
 */
public class JuzidianLauncherIcon extends Icon {

	private final int iconWidth = 72;

	private final int iconHeight = iconWidth;

	private final int bookWidth = (int) (iconWidth * 0.83);

	private final int bookHeight = (int) (iconHeight * 0.9);

	private final int bookRadius = iconWidth / 6;

	private final int bookRearWidth = (int) (bookHeight * 0.02);

	private final int bookPagesWidth = (int) (bookHeight * 0.03);

	private final float bookHorizontalOffset = (iconWidth - bookWidth) / 2f;

	private final float bookVerticalOffset = (iconHeight - (bookHeight + bookPagesWidth + bookRearWidth)) / 2f;

	private final int textLeftPadding = iconWidth / 16;

	final Font font = new Font("WenQuanYi Zen Hei", Font.BOLD, iconHeight / 4);

	@Override
	protected Dimension2D getBaseDimensions() {
		return new Dimension(iconWidth, iconHeight);
	}

	@Override
	protected void draw(final Graphics2D graphics, final double scale) {
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.scale(scale, scale);
		graphics.translate(bookHorizontalOffset, bookVerticalOffset);
		final Color lightRed = new Color(0xe0, 0x52, 0x52);
		final Color darkRed = new Color(0x93, 0x17, 0x16);
		graphics.setPaint(lightRed);
		graphics.fillRoundRect(0, 0, bookWidth, bookHeight, bookRadius, bookRadius);
		graphics.setPaint(Color.WHITE);
		graphics.fillRoundRect(0, bookRearWidth, bookWidth, bookHeight, bookRadius, bookRadius);
		graphics.setPaint(new GradientPaint(0, 0, lightRed, 0, iconHeight, darkRed));
		graphics.fillRoundRect(0, bookRearWidth + bookPagesWidth, bookWidth, bookHeight, bookRadius, bookRadius);
		graphics.setPaint(Color.WHITE);
		graphics.setFont(font);
		final int charHeight = graphics.getFontMetrics().getAscent();
		final int charSpacing = charHeight / 9;
		final int textVerticalOffset = (int) ((bookHeight - (charHeight * 3 + charSpacing * 2)) / 2f);
		graphics.drawChars("橘".toCharArray(), 0, 1, textLeftPadding, textVerticalOffset + charHeight);
		graphics.drawChars("字".toCharArray(), 0, 1, textLeftPadding, textVerticalOffset + charHeight * 2 + charSpacing);
		graphics.drawChars("典".toCharArray(), 0, 1, textLeftPadding, textVerticalOffset + charHeight * 3 + charSpacing * 2);
		graphics.translate(bookWidth * 0.367, bookHeight * 0.26);
		graphics.scale(iconWidth / 400f, iconHeight / 440f);
		graphics.setComposite(AlphaComposite.SrcAtop);
		graphics.fill(getMandarinShape());
	}

	private Shape getMandarinShape() {
		final Path2D path = new Path2D.Float();
		path.moveTo(178, 24);
		path.curveTo(177, 27, 176, 30, 174, 32);
		path.curveTo(172, 33, 169, 32, 166, 34);
		path.curveTo(166, 34, 157, 42, 157, 42);
		path.curveTo(138, 56, 102, 70, 79, 71);
		path.curveTo(76, 71, 70, 70, 68, 71);
		path.curveTo(63, 73, 54, 85, 51, 89);
		path.curveTo(39, 105, 30, 127, 30, 147);
		path.curveTo(30, 147, 30, 155, 30, 155);
		path.curveTo(30, 163, 32, 169, 34, 177);
		path.curveTo(46, 225, 92, 256, 136, 265);
		path.curveTo(144, 267, 156, 269, 164, 269);
		path.curveTo(177, 270, 190, 269, 203, 267);
		path.curveTo(243, 260, 283, 239, 304, 201);
		path.curveTo(312, 187, 317, 170, 317, 153);
		path.curveTo(317, 153, 317, 147, 317, 147);
		path.curveTo(317, 140, 316, 134, 314, 127);
		path.curveTo(304, 84, 267, 54, 228, 41);
		path.curveTo(216, 37, 201, 34, 188, 34);
		path.curveTo(188, 34, 196, 4, 196, 4);
		path.curveTo(196, 4, 192, 3, 192, 3);
		path.curveTo(182, 0, 185, 10, 180, 14);
		path.curveTo(177, 16, 167, 17, 163, 17);
		path.curveTo(163, 17, 146, 12, 146, 12);
		path.curveTo(130, 9, 110, 5, 94, 5);
		path.curveTo(94, 5, 85, 6, 85, 6);
		path.curveTo(70, 7, 57, 10, 43, 16);
		path.curveTo(30, 22, 19, 28, 8, 37);
		path.curveTo(5, 39, -1, 45, 0, 49);
		path.curveTo(1, 51, 8, 54, 10, 55);
		path.curveTo(19, 58, 28, 60, 37, 62);
		path.curveTo(47, 65, 54, 65, 65, 65);
		path.curveTo(88, 65, 92, 64, 114, 56);
		path.curveTo(114, 56, 131, 49, 131, 49);
		path.curveTo(137, 47, 148, 40, 153, 36);
		path.curveTo(164, 29, 163, 24, 178, 24);
		path.closePath();
		path.moveTo(137, 29);
		path.curveTo(130, 33, 123, 31, 115, 31);
		path.curveTo(115, 31, 98, 32, 98, 32);
		path.curveTo(83, 32, 68, 35, 54, 38);
		path.curveTo(46, 40, 35, 43, 28, 43);
		path.curveTo(34, 38, 52, 34, 60, 32);
		path.curveTo(84, 28, 113, 24, 137, 29);
		path.closePath();
		path.moveTo(189, 36);
		path.curveTo(191, 39, 192, 44, 192, 47);
		path.curveTo(190, 59, 165, 53, 167, 40);
		path.curveTo(172, 43, 182, 51, 187, 45);
		path.curveTo(188, 42, 188, 39, 189, 36);
		path.closePath();
		return path;
	}

}
