

import org.apache.log4j.Logger;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


import javax.media.Buffer;
import javax.media.CannotRealizeException;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.Time;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;

import org.datanucleus.util.Log4JLogger;



/**
 * This class gets an image at a specific media time from 
 * a Video as a image source
 */
public class VideoImageSource 
{
   // Log4j instance.
   private static final Logger m_logger = Logger.getLogger(VideoImageSource.class);
   
        private URL m_url = null;
        private double m_media_time = 1.0d;
        
        /**
         * Class constructor specifying the source codestream
         * @param _stream the source codestream of the video file
         * @throws IOException
         */
   public VideoImageSource(InputStream _stream) throws IOException
   {
           if(_stream == null)
           {
                   throw new IllegalArgumentException("InputStream cannot be null in VideoImageSource.");
           }

       File file = File.createTempFile("vtls-video-", "");
       file.deleteOnExit();

           BufferedOutputStream output_stream = new BufferedOutputStream(new FileOutputStream(file));
           BufferedInputStream input_stream = new BufferedInputStream(_stream);

                int stream_byte;
                while ((stream_byte = input_stream.read()) != -1)
                {
                        output_stream.write(stream_byte);
                }

                output_stream.flush();
                output_stream.close();
                input_stream.close();
                
                try
                {
                   m_url = file.toURL();
                }
                catch(MalformedURLException e)
                {
                }
   }
   
   /**
    * Class constructor specifying the {@link URL} of the Video file
    * @param _url The {@link URL} of the video file
    */
        public VideoImageSource(URL _url)
        {
           m_url = _url;
        }

        /**
         * Class constructor specifying the Vedio {@link File} 
         * @param _file The vedio {@link File} 
         */ 
        public VideoImageSource(File _file) throws MalformedURLException
        {
           if(_file == null)
              throw new IllegalArgumentException("File cannot be null in VideoImageSource.");
           m_url = _file.toURL();
        }
        
        /**
         * Get a {@link BufferedImage} from the video file at a specific
         * media time
         * @return a {@link BufferedImage}
         * @throws IOException
         */
        public BufferedImage getImage() throws IOException
        {
           if(m_url == null)
           {
                   return null;
           }
              
           try
           {
                   Image image = getImageFromVideo(m_url, m_media_time);
                   if(image instanceof BufferedImage)
                   {
                           return (BufferedImage)image;
                   }
                }
                catch(NoPlayerException e)
                {
                   return null;
                }
                catch(CannotRealizeException e)
                {
                   return null;
                }
            return null;
        }
        
        ///////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////
        /**
         * Set a specific media time to the vedio file
         * @param _time a double of the media time
         */
        public void setMediaTime(double _time)
        {
                m_media_time = _time;
        }

        /**
         * Get the pre-set media time for this video
         * @return a double representing the media time
         */
        public double getMediaTime()
        {
                return m_media_time;
        }

        ///////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////
        
        /**
         * Get an image at a specific media time from a video file.
         * @param _media_time  a specific media time of the image in the video file
         * @param _url the {@link URL} of the source video file
         * @throws IOException if an input or output error occurs
         * @throws NoPlayerException if a Player can't be found for this URL 
         * @throws CannotRealizeException  if a Player or Processor for this URL can 
         *                                 not be realized  

         */
        private Image getImageFromVideo(URL _url, double _media_time) throws IOException, NoPlayerException, CannotRealizeException
        {
            Player m_player = null;
           
                // Create a realized player.
                m_player = Manager.createRealizedPlayer(_url);

                if(m_player != null)
                        m_player.deallocate();

                // Check that we have something to work with.
                if (m_player == null || m_player.getVisualComponent() == null)
                        return null;
                        
                // Set the frame we want.
                m_player.setMediaTime(new Time(_media_time));

                // Grab a handle to a FrameGrabbingControl by full class name.
                FrameGrabbingControl grabber = (FrameGrabbingControl)m_player.getControl("javax.media.control.FrameGrabbingControl");

                // Get a Buffer from the current Frame and convert that into an Image.
                Buffer buffer = grabber.grabFrame();

                Image _return = new BufferToImage((VideoFormat)buffer.getFormat()).createImage(buffer);
                
                if(m_player != null)
                {
                        m_player.deallocate();
                        m_player.close();
                        m_player = null;
                        buffer = null;
                        grabber = null;
                }
                
                return _return;
        }
}