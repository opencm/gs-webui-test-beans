package webui.tests.cloudify.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import webui.tests.CloudDetails;

import java.io.*;

/**
 * User: guym
 * Date: 3/21/13
 * Time: 11:57 AM
 */
public class Bootstrap extends CloudifyCommand<Bootstrap> {

    private CloudDetails cloudDetails;

    public Bootstrap setCloudDetails( CloudDetails cloudDetails ) {
        this.cloudDetails = cloudDetails;
        return arg( "bootstrap-" + cloudDetails.getType() );
    }

    @Override
    public CloudifyCliManager.Execution execute() {
        arg( cloudDetails.getProvider() );
        return super.execute();
    }

    public Bootstrap noWebServices(){
        return arg("-no-web-services");
    }

    public Bootstrap cloudifyOverrides( File cloudOverrideFile ){
        return arg("-cloud-override", toCliPath( cloudOverrideFile ) );
    }

    public String toCliPath ( File file ){
        return file == null ? "" : file.getAbsolutePath().replace("\\","/");
    }

    public Bootstrap useExistingFromFile( File file ){
        return arg( "-use-existing-from-file", toCliPath( file ) );
    }

    public Bootstrap verbose(){
        return arg( "--verbose" );
    }

    public Bootstrap timeout( Long timeoutInMillis ){
        return timeoutInMillis == null ? this : arg("-timeout", Long.toString( timeoutInMillis / 60000 ));
    }

    public Bootstrap secured( ){
        return arg( "-secured" );
    }

    public Bootstrap user( String user ){
        return arg( "-user",user );
    }

    public Bootstrap password( String password ){
        return arg( "-password", password );
    }

    public Bootstrap securityFile( File securityFile ){
        return arg( "-security-file", toCliPath(securityFile ) );
    }

    public Bootstrap keystore( File keyStoreFile ){
        return arg( "-keystore",toCliPath( keyStoreFile ) );
    }

    public Bootstrap keyStorePassword( String keyStorePassword ){
        return arg( "-keystore-password", keyStorePassword );
    }


    public static class Details extends CloudifyCommand.Details<Bootstrap>{
        public Boolean noWebServices;
        public File cloudOverride;
        public File useExitistingFromFile;
        public Boolean verbose;
        public Long timeout;
        public Boolean secured;
        public String user;
        public String password;
        public File securityFile;
        public File keyStore;
        public String keyStorePassword;
        @Autowired
        public CloudDetails cloudDetails;

        public Bootstrap populate ( Bootstrap bootstrap ){
            return bootstrap
                    ._if( cloudDetails ).setCloudDetails( cloudDetails )

                    ._if( noWebServices ).noWebServices()

                    ._if( cloudOverride ).cloudifyOverrides( cloudOverride )

                    ._if( useExitistingFromFile ).useExistingFromFile( useExitistingFromFile )

                    ._if( verbose ).verbose()

                    ._if( timeout ).timeout( timeout )

                    ._if( secured ).secured()

                    ._if( user ).user( user )

                    ._if( password ).password( password )

                    ._if( securityFile ).securityFile( securityFile )

                    ._if( keyStore ).keystore( keyStore )

                    ._if( keyStorePassword ).keyStorePassword( keyStorePassword );
               }

        public Details setNoWebServices( Boolean noWebServices ) {
            this.noWebServices = noWebServices;
            return this;
        }

        public Details setCloudOverride( File cloudOverride ) {
            this.cloudOverride = cloudOverride;
            return this;
        }

        public Details setUseExitistingFromFile( File useExitistingFromFile ) {
            this.useExitistingFromFile = useExitistingFromFile;
            return this;
        }

        public Details setVerbose( Boolean verbose ) {
            this.verbose = verbose;
            return this;
        }

        public Details setTimeout( Long timeout ) {
            this.timeout = timeout;
            return this;
        }

        public Details setSecured( Boolean secured ) {
            this.secured = secured;
            return this;
        }

        public Details setUser( String user ) {
            this.user = user;
            return this;
        }

        public Details setPassword( String password ) {
            this.password = password;
            return this;
        }

        public Details setSecurityFile( File securityFile ) {
            this.securityFile = securityFile;
            return this;
        }

        public Details setKeyStore( File keyStore ) {
            this.keyStore = keyStore;
            return this;
        }

        public Details setKeyStorePassword( String keyStorePassword ) {
            this.keyStorePassword = keyStorePassword;
            return this;
        }


    }

}
