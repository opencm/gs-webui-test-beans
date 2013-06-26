package webui.tests.cloudify.commands;

import org.apache.commons.collections.Closure;
import org.apache.commons.exec.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import webui.tests.exec.ExecutorFactory;
import webui.tests.utils.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private ExecutorFactory executorFactory;

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
        CommandLine cmdLine = new CommandLine( new File( cliHomedir, "cloudify" + ( SystemUtils.IS_OS_WINDOWS ? ".bat" : ".sh" ) ) );
        cmdLine.addArguments( command, false );

//        CommandLine cmdLine = new CommandLine( "echo hello" );
        logger.info( "running command [{}]", cmdLine.toString() );

        Executor executor = executorFactory.createNew();
        executor.setExitValue( 0 );
        MyStreamHandler streamHandler = new MyStreamHandler( );
        executor.setStreamHandler( streamHandler );

        try
        {

            Map<String, String> env = new HashMap<String,String>(  );
            env.put( "DEBUG", "true" );
            env.put( "VERBOSE", "true" );
            env.putAll( System.getenv() );
            int i = executor.execute( cmdLine, env );
            logger.info( "executor finished with : " + i );


        } catch ( ExecuteException e )
        {
            logger.error( "Failed to execute process. Exit value: " + e.getExitValue(), e );

            throw new RuntimeException( "Failed to execute process. Exit value: " + e.getExitValue(), e );
        } catch ( IOException e )
        {
            logger.error( "Failed to execute process", e );

            throw new RuntimeException( "Failed to execute process.", e );
        }
        return null; // new Execution().setStreamHandler( streamHandler );
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

//    public static class MyCallable implements Callable<String>{
//
//        private OutputStream os;
//        private InputStream is;
////        private BufferedReader reader;
//
//
//
//        public MyCallable( OutputStream os, InputStream is ) {
//            this.os = os;
//            this.is = is;
//        }
//
//        @Override
//        public String call() throws Exception {
////            String line = reader.readLine();
//            os.write( is.read(  ) );
//            return "line";
//        }
//    }


    // guym - urrrggh. there's an issue with cloudify's CLI when running bootstrap command
    // for some reason, the input stream remains open, blocked on "read" while the process has finished.
    // adding a work around - wrapping the result in Future.
    // I will "kill" the streamers when the process is done manually.
//    public static class MyStreamPumper implements Runnable{
//
//        private final MyStreamHandler myStreamHandler;
//        private static Logger logger = LoggerFactory.getLogger(MyStreamPumper.class);
//        private InputStream is;
//        private OutputStream os;
//
//        public MyStreamPumper( InputStream is, OutputStream os, MyStreamHandler myStreamHandler ) {
//            this.is = is;
//            this.os = os;
//            this.myStreamHandler = myStreamHandler;
//        }
//
//        @Override
//        public void run() {
//            logger.info( "pumper running : " + Thread.currentThread().getName() );
//            try
//            {
//                int i = 0;
//                while ( ( i = is.read() ) > 0 ){
//                    os.write( i );
//                }
//            } catch ( IOException e )
//            {
//                logger.error( "error while reading process output",e );
//            }
//        }
//    }


    protected static class MyStreamOutputHandler extends LogOutputStream {
        private StringBuilder sb;
        private Logger logger;

        public MyStreamOutputHandler( String name ) {
            logger = LoggerFactory.getLogger( name );
        }

        @Override
        protected void processLine( String line, int level ) {
            logger.info( line ); // currently ignoring level. todo: use level;
            sb.append( line );
        }

        public String getOutput(){
            return sb.toString();
        }

        public MyStreamOutputHandler setSb( StringBuilder sb ) {
            this.sb = sb;
            return this;
        }
    }
       protected static class MyStreamHandler extends PumpStreamHandler {

           StringBuilder sb = new StringBuilder(  );


           public MyStreamHandler(  ) {

           }

           @Override
           protected void createProcessOutputPump( InputStream is, OutputStream os ) {
               super.createProcessOutputPump( is, new MyStreamOutputHandler( "cli-info " ).setSb( sb ) );
           }

           @Override
           protected void createProcessErrorPump( InputStream is, OutputStream os ) {
               super.createProcessErrorPump( is, new MyStreamOutputHandler("cli-error").setSb( sb ) );
           }

           public String getOutput() {
               return sb.toString();
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

            commands.add( command.getCommandAsString() ); // add the currently executing command

            return cloudifyCliManager.execute( StringUtils.join( commands, ";" ), timeout );
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


    public void setExecutorFactory( ExecutorFactory executorFactory ) {
        this.executorFactory = executorFactory;
    }
}
