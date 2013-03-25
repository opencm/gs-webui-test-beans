package webui.tests.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

/**
 * User: guym
 * Date: 3/21/13
 * Time: 11:42 AM
 */
public class WebUtils {

    private static Logger logger = LoggerFactory.getLogger( WebUtils.class );

    public static boolean ping( String host, int ip, String errorMessage ) {
        Socket socket = null;
        try
        {
            socket = new Socket( host, ip );
            return true;
        } catch ( Exception e )
        {
            if ( !StringUtils.isEmpty(errorMessage) ){
                logger.error(errorMessage, e);
            }
            return false;
        } finally
        {
            if ( socket != null ) try
            {
                socket.close();
            } catch ( IOException e )
            {
            }
        }
    }

}
