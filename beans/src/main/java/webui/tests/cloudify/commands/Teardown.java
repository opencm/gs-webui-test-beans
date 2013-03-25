package webui.tests.cloudify.commands;

import org.springframework.beans.factory.annotation.Autowired;
import webui.tests.CloudDetails;

/**
 * User: guym
 * Date: 3/21/13
 * Time: 11:58 AM
 */
public class Teardown extends CloudifyCommand<Teardown>{

    private CloudDetails cloudDetails;

    public Teardown setCloudDetails( CloudDetails cloudDetails ) {
          this.cloudDetails = cloudDetails;
          return arg( "teardown-" + cloudDetails.getType() );
    }

    public Teardown timeout( Long millis ){
        return millis == null ? this : arg( "-timeout", Long.toString( millis / 60000 ) );
    }

    public Teardown force(){
        return arg( "-force" );
    }

    public Teardown verbose(){
        return arg( "--verbose" );
    }

    @Override
    public CloudifyCliManager.Execution execute() {
        arg( cloudDetails.getProvider() );
        return super.execute();
    }

    public static class Details extends CloudifyCommand.Details<Teardown>{

        private Long timeout;
        private Boolean force;
        private Boolean verbose;

        @Autowired
        private CloudDetails cloudDetails ;

        public void setTimeout( Long timeout ) {
            this.timeout = timeout;
        }

        public void setForce( Boolean force ) {
            this.force = force;
        }

        public Details setCloudDetails( CloudDetails cloudDetails ) {
            this.cloudDetails = cloudDetails;
            return this;
        }

        public Details setVerbose( Boolean verbose ) {
            this.verbose = verbose;
            return this;
        }

        @Override
        public Teardown populate( Teardown teardown ) {
            return teardown._if( cloudDetails ).setCloudDetails( cloudDetails )._if( timeout ).timeout( timeout )._if( verbose).verbose()._if( force ).force();
        }
    }

}
