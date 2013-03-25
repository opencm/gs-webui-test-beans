package webui.tests.cloudify.commands;

import org.apache.commons.collections.Closure;
import org.apache.commons.exec.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webui.tests.utils.CollectionUtils;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * User: guym
 * Date: 3/21/13
 * Time: 11:55 AM
 *
 * This is a factory class for commands
 *
 */
public class CloudifyCliManager {

    String cliHomedir = System.getProperty( "JSHOMEDIR", System.getenv( "JSHOMEDIR" ) ) + File.separator + "tools" + File.separator + "cli";

    long defaultTimeoutMillis = 120000; // 2 minutes

    private static Logger logger = LoggerFactory.getLogger( CloudifyCliManager.class );

    public Bootstrap bootstrap(){
        return init( new Bootstrap() );
    }

    public Connect connect(){
        return init( new Connect() );
    }

    public ListApplications listApplications(){
        return init( new ListApplications() );
    }


    public ListInstances listInstances(){
        return init( new ListInstances() );
    }

    public ListServiceInstanceAttributes listServiceInstanceAttributes(){
        return init( new ListServiceInstanceAttributes() );
    }

    public ListServices listServices(){
        return init( new ListServices() );
    }

    public Login login(){
        return init( new Login() );
    }

    public ShutdownManagers shutdownManagers(){
        return init( new ShutdownManagers() );
    }

    public Teardown teardown(){
        return init( new Teardown() );
    }


    protected <V extends CloudifyCommand<V>> V init(V command){
        return command.setManager( this )
                .setCliHomedir( cliHomedir );
    }

    public Execution execute ( CloudifyCommand command, long timeout ){
        return execute( command.getCommandAsString(), timeout );
    }

    public Execution execute( CloudifyCommand command ){
        return execute( command, defaultTimeoutMillis );
    }
    public Execution execute( String command ){
        return execute( command, defaultTimeoutMillis );
    }

    public CloudifyCliManager andThen( CloudifyCommand command ){
        return new Accumulator().setCloudifyCliManager( this ).andThen( command );
    }

    public Execution execute( String command , long timeout ) {

        logger.info( "running command [{}]", command );
        CommandLine cmdLine = new CommandLine( cliHomedir );
        cmdLine.addArguments( command );

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        ExecuteWatchdog watchdog = new ExecuteWatchdog( timeout );
        executor.setExitValue( 1 );
        executor.setWatchdog( watchdog );
        MyStreamHandler streamHandler = new MyStreamHandler();

        executor.setStreamHandler( streamHandler );

        try
        {
            executor.execute( cmdLine, null, resultHandler );
        } catch ( ExecuteException e )
        {
            logger.error( "Failed to execute process. Exit value: " + e.getExitValue(), e );

            throw new RuntimeException( "Failed to execute process. Exit value: " + e.getExitValue(), e );
        } catch ( IOException e )
        {
            logger.error( "Failed to execute process", e );

            throw new RuntimeException( "Failed to execute process.", e );
        }
        return new Execution().setStreamHandler( streamHandler );
    }


    public static class Execution {
           private MyStreamHandler streamHandler;

           public String getOutput() {
               return streamHandler.getOutput();
           }

           protected Execution setStreamHandler( MyStreamHandler streamHandler ) {
               this.streamHandler = streamHandler;
               return this;
           }
       }


       protected static class MyStreamHandler extends PumpStreamHandler {

           final ByteArrayOutputStream baos = new ByteArrayOutputStream();

           @Override
           protected void createProcessOutputPump( InputStream is, OutputStream os ) {
               super.createProcessOutputPump( is, baos );
           }

           @Override
           protected void createProcessErrorPump( InputStream is, OutputStream os ) {
               super.createProcessErrorPump( is, baos );
           }

           public String getOutput() {
               return baos.toString();
           }
       }

    public static class Accumulator extends CloudifyCliManager{

        private CloudifyCliManager cloudifyCliManager;
        private List<CloudifyCommand> commands = new LinkedList<CloudifyCommand>(  );

        public Accumulator setCloudifyCliManager( CloudifyCliManager cloudifyCliManager ) {
            this.cloudifyCliManager = cloudifyCliManager;
            return this;
        }

        public CloudifyCliManager andThen( CloudifyCommand command ){
            commands.add( command );
            return this;
        }

        @Override
        protected <V extends CloudifyCommand<V>> V init( V command ) {
            // let the super initialize and then override manager with this.
            return cloudifyCliManager.init( command ).setManager( this );
        }

        @Override
        public Execution execute( CloudifyCommand command, long timeout ) {
            final List<String> commands = new LinkedList<String>(  );

            CollectionUtils.forAllDo( this.commands, new Closure() {
                @Override
                public void execute( Object input ) {
                    commands.add( ((CloudifyCommand) input).getCommandAsString() );
                }
            } );

            return super.execute( StringUtils.join(commands,";"), timeout );
        }
    }
//
//    public String listApplications( boolean expectedToFail ) throws IOException, InterruptedException {
//        String command = connectCommand() + "list-applications";
//        if ( expectedToFail )
//        {
//            lastActionOutput = CommandTestUtils.runCommandExpectedFail( command );
//            return lastActionOutput;
//        }
//        lastActionOutput = CommandTestUtils.runCommandAndWait( command );
//        return lastActionOutput;
//    }
//
//    public String listServices( final String applicationName, boolean expectedToFail ) throws IOException, InterruptedException {
//        String command = connectCommand() + "use-application " + applicationName + ";list-services";
//        if ( expectedToFail )
//        {
//            lastActionOutput = CommandTestUtils.runCommandExpectedFail( command );
//            return lastActionOutput;
//        }
//        lastActionOutput = CommandTestUtils.runCommandAndWait( command );
//        return lastActionOutput;
//
//    }
//
//    public String listInstances( final String applicationName, final String serviceName, boolean expectedToFail ) throws IOException, InterruptedException {
//        String command = connectCommand() + "use-application " + applicationName + ";list-instances " + serviceName;
//        if ( expectedToFail )
//        {
//            lastActionOutput = CommandTestUtils.runCommandExpectedFail( command );
//            return lastActionOutput;
//        }
//        lastActionOutput = CommandTestUtils.runCommandAndWait( command );
//        return lastActionOutput;
//    }
//
//    public String listServiceInstanceAttributes( final String applicationName, final String serviceName, final int instanceNumber, boolean expectedToFail ) throws IOException, InterruptedException {
//        String command = connectCommand() + "use-application " + applicationName + ";list-attributes -scope service:" + serviceName + ":" + instanceNumber;
//        if ( expectedToFail )
//        {
//            lastActionOutput = CommandTestUtils.runCommandExpectedFail( command );
//            return lastActionOutput;
//        }
//        lastActionOutput = CommandTestUtils.runCommandAndWait( command );
//        return lastActionOutput;
//    }
//
//    public String shutdownManagers( final String applicationName, final String backupFilePath, boolean expectedToFail ) throws IOException, InterruptedException {
//        String command = connectCommand() + "use-application " + applicationName + ";shutdown-managers -file " + backupFilePath;
//        if ( expectedToFail )
//        {
//            lastActionOutput = CommandTestUtils.runCommandExpectedFail( command );
//            return lastActionOutput;
//        }
//        lastActionOutput = CommandTestUtils.runCommandAndWait( command );
//        return lastActionOutput;
//    }
//
//    public String connect( boolean expectedToFail ) throws IOException, InterruptedException {
//        String command = connectCommand();
//        if ( expectedToFail )
//        {
//            lastActionOutput = CommandTestUtils.runCommandExpectedFail( command );
//            return lastActionOutput;
//        }
//        lastActionOutput = CommandTestUtils.runCommandAndWait( command );
//        return lastActionOutput;
//    }
//
//    public String login( boolean expectedToFail ) throws IOException, InterruptedException {
//        lastActionOutput = CommandTestUtils.runCommand( connectCommand() + "login " + user + " " + password, true, expectedToFail );
//        return lastActionOutput;
//    }
//
//    public String login( final String user, final String password, boolean expectedToFail ) throws IOException, InterruptedException {
//        lastActionOutput = CommandTestUtils.runCommand( connectCommand() + "login " + user + " " + password, true, expectedToFail );
//        return lastActionOutput;
//    }
//
//

}
