package webui.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import webui.tests.cloudify.commands.*;

/**
 * User: guym
 * Date: 3/6/13
 * Time: 4:09 PM
 */
public class CloudifyTestBeanImpl implements CloudifyTestBean {

    private static Logger logger = LoggerFactory.getLogger( CloudifyTestBean.class );

    @Autowired
    private CloudifyCliManager cloudifyCliManager;

    /************ Command flags ******************/

    @Autowired
    private Bootstrap.Details bootstrapFlags;

    @Autowired
    private Teardown.Details teardownFlags;

    @Autowired
    private Connect.Details connectFlags;

    /************* Connect Flags *****************/

    private int restPort = 8100;

    private String cloudifyHost = "localhost";

    @Override
    public CloudifyCliManager.Execution teardown() {
        logger.info( "cloudify bean teardown" );
        return cloudifyCliManager.connect().details( connectFlags ).andThen().teardown().details( teardownFlags ).execute();
    }

    @Override
    public CloudifyCliManager.Execution bootstrap() {
        logger.info( "cloudify bean bootstrapping" );
        return cloudifyCliManager.bootstrap().details( bootstrapFlags ).execute();
    }


    public void setCloudifyCliManager( CloudifyCliManager cloudifyCliManager ) {
        this.cloudifyCliManager = cloudifyCliManager;
    }

    public void setBootstrapFlags( Bootstrap.Details bootstrapFlags ) {
        this.bootstrapFlags = bootstrapFlags;
    }

    public void setTeardownFlags( Teardown.Details teardownFlags ) {
        this.teardownFlags = teardownFlags;
    }

    public void setConnectFlags( Connect.Details connectFlags ) {
        this.connectFlags = connectFlags;
    }
}
