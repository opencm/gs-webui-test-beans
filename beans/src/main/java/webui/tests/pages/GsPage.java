package webui.tests.pages;

import com.thoughtworks.selenium.Selenium;
import webui.tests.utils.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
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

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * User: guym
 * Date: 3/6/13
 * Time: 10:38 PM
 */
@Component
public abstract class GsPage<T extends GsPage> {

    private static Logger logger = LoggerFactory.getLogger( GsPage.class );

    protected static StopWatch stopWatch = new Slf4JStopWatch( logger );
    private static final String TOTAL_WAIT = "total_wait";
    private static final String ELEMENT_WAIT = "element_wait";
    private static final long SLEEP_DELTA_MILLIS = 100;

    @Autowired(required = true)
    protected WebDriver webDriver;

    @Autowired
    protected Selenium selenium;

    public boolean isTextPresent( String text ) {
        return selenium.isTextPresent( text );
    }

    @Autowired
    public GsPage<T> setSelenium( Selenium selenium ) {
        this.selenium = selenium;
        return this;
    }

    public Collection<WebElement> findDisplayedWindowDialogs() {
        return findDisplayed( By.cssSelector( ".x-window-dlg" ) );
    }

    public Collection<WebElement> findDisplayed( By by ) {
        List<WebElement> elements = webDriver.findElements( by );
        Collection<WebElement> displayElements = CollectionUtils.select( elements, new Predicate() {
            @Override
            public boolean evaluate( Object o ) {
                return ( ( WebElement ) o ).isDisplayed();
            }
        } );
        return displayElements;
    }

    public T load() {
        PageFactory.initElements( webDriver, this );
        waitForAll(); // waits for all fields annotated with onload.
        return ( T ) this;
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
        waitForElement( 30000, TimeUnit.MILLISECONDS, visibilityOf( element ) );
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
        waitForAll( 30000, TimeUnit.MILLISECONDS );
        return ( T ) this;
    }

    protected void waitForAll( final long timeout, final TimeUnit unit ) {


        final GsPage me = this;
        ReflectionUtils.doWithFields( this.getClass(), new ReflectionUtils.FieldCallback() {
                    @Override
                    public void doWith( Field field ) throws IllegalArgumentException, IllegalAccessException {
                        if ( !field.isAccessible() )
                        {
                            field.setAccessible( true );
                        }
                        if ( WebElement.class.isAssignableFrom( field.getType() ) )
                        {
                            waitFor( timeout, unit, ( WebElement ) ReflectionUtils.getField( field, me ) );
                        } else if ( List.class.isAssignableFrom( field.getType() ) )
                        {
                            waitFor( timeout, unit, (List) ReflectionUtils.getField( field, me )  );
                        }
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
        waitFor( 30000, TimeUnit.MILLISECONDS, webElements );
    }

    /**
     * waits for all webElements to be visible.
     *
     * @param timeout     - total timeout
     * @param unit        - timeout unit
     * @param webElements - webElements we are waiting for.
     */
    protected void waitFor( long timeout, TimeUnit unit, final WebElement... webElements ) {
        waitForAllImpl( timeout, unit, webElements );
    }

    protected void waitFor( long timeout, TimeUnit unit, final List<WebElement> ... webElements ){
        waitForAllImpl( timeout, unit, webElements );
    }

    private void waitForAllImpl( long timeout, TimeUnit unit, final Object... obj ) {
        try
        {
            stopWatch.start( TOTAL_WAIT );
            for ( Object o : obj )
            {
                waitForElement( timeout, unit, visibilityOf( o ) );
            }

        } finally
        {
            stopWatch.stop( TOTAL_WAIT );
        }
    }


    public boolean isTextInPopup( String containedText ) {
        Collection<WebElement> popups = findDisplayedWindowDialogs();
        if ( !CollectionUtils.isEmpty( popups ) )
        {
            for ( WebElement popup : popups )
            {
                String text = popup.getText();
                if ( !StringUtils.isEmpty( text ) && text.toLowerCase(  ).contains( containedText.toLowerCase(  ) ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    public T closeDialog( String str ){
        WebElement e = CollectionUtils.first( findDisplayedWindowDialogs() );
        List<WebElement> buttons = e.findElements( By.cssSelector( "button" ) );
        for ( WebElement button  : buttons )
        {
            if ( button.getText().equalsIgnoreCase( str )){
                button.click();
                break;
            }
        }
        return (T) this;
    }





}
