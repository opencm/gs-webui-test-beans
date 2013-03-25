package webui.tests.cloudify.commands;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * User: guym
 * Date: 3/21/13
 * Time: 11:55 AM
 */
public abstract class CloudifyCommand<T extends CloudifyCommand> {

    protected CloudifyCliManager manager;

    private T orig; // for nice if/set syntax

    private static Logger logger = LoggerFactory.getLogger( CloudifyCommand.class );

    protected Long timeout = null;

    private List<String> args = new LinkedList<String>(  );
    private String cliHomedir;

    public T setManager( CloudifyCliManager manager ) {
        this.manager = manager;
        return ( T ) this;
    }

    public T arg( String ... str ){
        Collections.addAll( args, str );
        return ( T ) this;
    }

    public String[] argsArray(){
        return args.toArray( new String[args.size()] );
    }

    public void setTimeout( long timeout ) {
        this.timeout = timeout;
    }

    public T setCliHomedir( String cliHomedir ) {
        this.cliHomedir = cliHomedir;
        return ( T ) this;
    }

    public CloudifyCliManager.Execution execute( ){
        if ( this.timeout != null ){
            return manager.execute( this, timeout );
        }else{
            return manager.execute( this );
        }
    }


    public CloudifyCliManager andThen(){
        return manager.andThen( this );
    }


    public T _if( Object o ){
        boolean result = o != null ; // takes care of Boolean scenario.

        if ( o instanceof Boolean )
        {
            result = Boolean.TRUE.equals( o );
        }
        if ( o instanceof File && !((File)o).exists())
        {
            logger.warn( "settings command details, a file that does not exist [{}]", ((File)o).getAbsolutePath() );
        }

        return result ? getOrig() : getFake();
    }

    private T getOrig(){
        return orig == null ? ( T ) this : orig;
    }

    // get a fake instance of this command. either I am a fake instance, and then I return myself.
    // or I am an original command and I need to create a fake that points to me.
    private T getFake(){
        if ( orig != null ) {
            return ( T ) this;
        }else{
            try
            {
                CloudifyCommand cmd = this.getClass().newInstance();
                cmd.orig = this;
                return ( T ) cmd;
            } catch ( Exception e )
            {
                throw new RuntimeException( String.format( "unable to create new instance of command [%s]", getClass() ), e );
            }
        }
    }

    public String getCommandAsString(){
        return StringUtils.join(args, " ");
    }

    public abstract static class Details<T extends CloudifyCommand> {
        public abstract T populate( T t );
    }

    public T details( CloudifyCommand.Details<T> d ) {
        return ( T ) d.populate( ( T ) this ).getOrig();
    }



}
