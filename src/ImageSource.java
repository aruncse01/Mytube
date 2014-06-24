import java.awt.image.BufferedImage;
	import java.io.IOException;

	/**
	 * An ImageSource represents a source for a BufferedImage. 
	 * This interface provides function to get the image from 
	 * different sources.
	 */

	
	public interface ImageSource
	{
	        /**
	         * Get a BufferedImage from image source
	         * @return a source-based instance of {@link BufferedImage} 
	         * @throws IOException
	         */
	        public BufferedImage getImage() throws IOException;
	}

