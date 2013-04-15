package webui.tests.listeners;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webui.tests.annotations.NoScreenshot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * User: guym
 * Date: 4/10/13
 * Time: 6:07 PM
 */
public class SnapshotListener extends RunListener {

    private static Logger logger = LoggerFactory.getLogger( SnapshotListener.class );

    @Override
    public void testFailure( Failure failure ) throws Exception {
        logger.info( "testFailure : [{}].", failure );
        if ( failure.getDescription().getAnnotation( NoScreenshot.class ) != null ){
            logger.info( "skipping screenshot due to annotation" );
        }else{
            String filename = failure.getDescription().getDisplayName().replaceAll( "\\(", "_" ).replaceAll( "\\)","_" ) + "_" + System.currentTimeMillis() + ".jpg";
            logger.info( "saving screenshot to file [{}]", filename );
            Robot robot = new Robot();
            BufferedImage screenShot = robot.createScreenCapture( new Rectangle( Toolkit.getDefaultToolkit().getScreenSize() ) );
            ImageIO.write( screenShot, "JPG", new File( filename ) );
            super.testFailure( failure );
        }
    }
}
