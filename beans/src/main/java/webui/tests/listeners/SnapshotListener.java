package webui.tests.listeners;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public void testRunStarted( Description description ) throws Exception {
        logger.info("testRunStarted : [{}]" , description );
        super.testRunStarted( description );
    }

    @Override
    public void testRunFinished( Result result ) throws Exception {
        logger.info("testRunFinished : [{}]" , result );
        super.testRunFinished( result );
    }

    @Override
    public void testStarted( Description description ) throws Exception {
        logger.info("testStarted : [{}]" , description );
        super.testStarted( description );
    }

    @Override
    public void testFinished( Description description ) throws Exception {
        logger.info("testFinished : [{}]" , description );
        super.testFinished( description );
    }

    @Override
    public void testFailure( Failure failure ) throws Exception {
        logger.info( "testFailure : [{}]", failure );
        Robot robot = new Robot();
        BufferedImage screenShot = robot.createScreenCapture( new Rectangle( Toolkit.getDefaultToolkit().getScreenSize() ) );
        ImageIO.write( screenShot, "JPG", new File( "screenShot.jpg" ) );
        super.testFailure( failure );
    }

    @Override
    public void testAssumptionFailure( Failure failure ) {
        logger.info("testAssumptionFailure : [{}]" , failure );
        super.testAssumptionFailure( failure );
    }

    @Override
    public void testIgnored( Description description ) throws Exception {
        logger.info("testIgnored : [{}]" , description );
        super.testIgnored( description );
    }
}
