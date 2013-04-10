package webui.tests.components;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import webui.tests.annotations.OnLoad;
import webui.tests.selenium.GsFieldDecorator;
import webui.tests.selenium.GsSeleniumComponent;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * User: guym
 * Date: 4/9/13
 * Time: 3:24 PM
 */
@Component
public class AbstractComponent<T extends AbstractComponent> implements GsSeleniumComponent {

    @Autowired(required = true)
    protected WebDriver webDriver;

    protected WebElement webElement;

    private static Logger logger = LoggerFactory.getLogger( AbstractComponent.class );


    public static final int DEFAULT_TIMEOUT = 120000;
    protected static StopWatch stopWatch = new Slf4JStopWatch( logger );
    private static final String TOTAL_WAIT = "total_wait";
    private static final String ELEMENT_WAIT = "element_wait";
    private static final long SLEEP_DELTA_MILLIS = 100;


    @Override
    public void setWebElement( WebElement webElement ) {
        this.webElement = webElement;
    }

    @Override
    public void setWebDriver( WebDriver webDriver ) {
        this.webDriver = webDriver;
    }

    public T load ( SearchContext searchContext ){
        PageFactory.initElements( new GsFieldDecorator( searchContext, webDriver  ), this );

        final Object me = this;
        // load components - we only load components that have "FindBy"
        // this in order to avoid default behavior where field name is used as ID locator.
        // default behavior causes GsPage fields to be initialized too - which is unwanted.
        // this is not a limitation, this is a preference, we do not want to encourage default behavior.
        ReflectionUtils.doWithFields( this.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith( Field field ) throws IllegalArgumentException, IllegalAccessException {
                field.setAccessible( true );
                ((AbstractComponent)ReflectionUtils.getField( field, me )).load();
            }
        }, new ReflectionUtils.FieldFilter() {
                    @Override
                    public boolean matches( Field field ) {
                        return AbstractComponent.class.isAssignableFrom( field.getType() ) && field.isAnnotationPresent( FindBy.class );
                    }
                } );

        waitForAll(); // waits for all fields annotated with onload.
        return ( T ) this;
    }

    public T load(){
        return load( webElement );
    }


    public ExpectedCondition<WebElement> waitFor( final WebElement element ) {
        return new ExpectedCondition<WebElement>() {
            public WebElement apply( WebDriver driver ) {
                try
                {
                    return element.isDisplayed() ? element : null;
                } catch ( StaleElementReferenceException e )
                {
                    return null;
                }
            }
        };
    }

    public ExpectedCondition<WebElement> waitFor( final List<WebElement> elements ) {
        return new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply( WebDriver webDriver ) {
                for ( WebElement element : elements )
                {
                    try
                    {
                        if ( element.isDisplayed() )
                        {
                            return element;
                        }
                    } catch ( StaleElementReferenceException e )
                    {

                    }
                }
                return null;
            }
        };
    }


    private ExpectedCondition visibilityOf( Object wd ) {
        if ( WebElement.class.isAssignableFrom( wd.getClass() ) )
        {
            return ExpectedConditions.visibilityOf( ( WebElement ) wd );
        }
        if ( List.class.isAssignableFrom( wd.getClass() ) )
        {
            return waitFor( ( List<WebElement> ) wd );
        }
        return null;
    }

    protected <T extends WebElement> T waitForElement( T element ) {
        waitForElement( DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS, visibilityOf( element ) );
        return element;
    }


    /**
     * waits for a web element to be visible.
     *
     * @param timeout - total timeout.
     * @param unit    - unit of timeout.
     * @return - returns the web element.
     */
    protected void waitForElement( long timeout, TimeUnit unit, com.google.common.base.Function predicate ) {
        try
        {
            stopWatch.start( ELEMENT_WAIT );
            new WebDriverWait( webDriver, unit.toMillis( timeout ), SLEEP_DELTA_MILLIS )
                    .pollingEvery( SLEEP_DELTA_MILLIS, TimeUnit.MILLISECONDS )
                    .withTimeout( timeout, unit )
                    .ignoring( RuntimeException.class, ElementNotVisibleException.class )
                    .until( predicate );
        } catch ( Exception e )
        {
            throw new ElementNotVisibleException( String.format( "waited %s millis and still element is not visible", unit.toMillis( timeout ) ), e );
        } finally
        {
            stopWatch.stop( ELEMENT_WAIT );
        }
    }

    /**
     * <p>
     * A "load page" utility. There are some elements on a page we cannot do without.<br/>
     * This method goes over all fields and waits for fields annotated with "OnLoad".<br/>
     * If a field is not loaded, the test will fail.
     * </p>
     */
    protected T waitForAll() {
        waitForAll( DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS );
        return ( T ) this;
    }

    protected void waitForAll( final long timeout, final TimeUnit unit ) {


        final Object me = this;
        ReflectionUtils.doWithFields( this.getClass(), new ReflectionUtils.FieldCallback() {
                    @Override
                    public void doWith( Field field ) throws IllegalArgumentException, IllegalAccessException {
                        if ( !field.isAccessible() )
                        {
                            field.setAccessible( true );
                        }
                        waitFor( timeout, unit, ReflectionUtils.getField( field, me ) );
                    }

                }, new ReflectionUtils.FieldFilter() {
                    @Override
                    public boolean matches( Field field ) {
                        return field.isAnnotationPresent( OnLoad.class );
                    }
                }
        );
    }

    protected void waitFor( final WebElement... webElements ) {
        waitFor( DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS, webElements );
    }

    /**
     * waits for all webElements to be visible.
     *
     * @param timeout     - total timeout
     * @param unit        - timeout unit
     * @param webElements - webElements we are waiting for.
     *                    can have "List", or "WebElement" or "AbstractComponent"
     */
    protected void waitFor( long timeout, TimeUnit unit, final Object ... webElements ) {
        try
        {
            stopWatch.start( TOTAL_WAIT + "_" + hashCode());
            for ( Object o : webElements )
            {
                if ( o instanceof AbstractComponent ){ // drill down to component

                    ((AbstractComponent)o).waitForAll( timeout, unit );

                }else{

                    waitForElement( timeout, unit, visibilityOf( o ) );

                }
            }

        } finally
        {
            stopWatch.stop( TOTAL_WAIT  + "_" + hashCode() );
        }
    }

}
