package org.juzidian.build.imggen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.imageio.ImageIO;

/**
 * Generator of Android image resources from scalable icon descriptors.
 */
public class AndroidImageResourceGenerator {

	private static RenderingScale[] RENDER_SCALES = {
		new RenderingScale("ldpi", 0.5),
		new RenderingScale("mdpi", 0.667),
		new RenderingScale("hdpi", 1.0),
		new RenderingScale("xhdpi", 1.33),
	};

	private final File resDir;

	/**
	 * Create a new {@link AndroidImageResourceGenerator}.
	 * 
	 * @param resDir the target Android "res" directory where image resources
	 *        will be created.
	 */
	public AndroidImageResourceGenerator(final File resDir) {
		this.resDir = resDir;
	}

	/**
	 * Generate scaled image resources for the given icon descriptors.
	 * <p>
	 * Image resources will be generated with appropriate scales for each
	 * Android screen densities in "drawable" child directories of the
	 * configured "res" directory.
	 * <p>
	 * The HDPI density will use the given icons without scaling. Other
	 * densities will scale the images up or down appropriately.
	 * 
	 * @param icons a collection of {@link IconDescriptor}.
	 */
	public void generateIconImages(final Collection<IconDescriptor> icons) {
		for (final IconDescriptor icon : icons) {
			this.generateIconImages(icon);
		}
	}

	private void generateIconImages(final IconDescriptor icon) {
		for (final RenderingScale scale : RENDER_SCALES) {
			this.generateIconImage(icon, scale);
		}
	}

	private void generateIconImage(final IconDescriptor descriptor, final RenderingScale renderScale) {
		final BufferedImage bufferedImage = descriptor.getIcon().render(renderScale.getFactor());
		this.imageGenerated(descriptor, renderScale, bufferedImage);
	}

	private void imageGenerated(final IconDescriptor iconDescriptor, final RenderingScale renderScale, final BufferedImage image) {
		final File file = new File(this.resDir, "drawable-" + renderScale.getName() + File.separator + iconDescriptor.getName() + ".png");
		file.mkdirs();
		try {
			ImageIO.write(image, "png", file);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}
