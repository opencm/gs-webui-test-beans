package webui.tests.selenium;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: guym
 * Date: 4/8/13
 * Time: 5:35 PM
 */
public class GsLocators {



    public static class ListHandler implements MethodInterceptor {

        private final ElementLocator locator;
        private final Class<? extends GsSeleniumComponent> type;
        private WebDriver webDriver = null;

        public ListHandler( ElementLocator locator, Class<? extends GsSeleniumComponent> type ) {
            this.locator = locator;
            this.type = type;
        }


        @Override
        public Object intercept( Object o, Method method, Object[] objects, MethodProxy methodProxy ) throws Throwable {
            List<WebElement> elements = locator.findElements();
            List<GsSeleniumComponent> components = ( List<GsSeleniumComponent> ) o;
            components.clear();
            for ( WebElement element : elements )
            {
                GsSeleniumComponent e = type.newInstance();
                e.setWebElement( element );
                e.setWebDriver( webDriver );
                components.add( e );
            }

            try
            {
                return methodProxy.invokeSuper( components, objects );
            } catch ( InvocationTargetException e )
            {
                // Unwrap the underlying exception
                throw e.getCause();
            }
        }
    }

    public static class ElementHandler implements MethodInterceptor {

        private static Logger logger = LoggerFactory.getLogger( ElementHandler.class );

        private final ElementLocator locator;
        private boolean firstDisplayed = false;
        private WebDriver webDriver = null;
        private Field field;
        // todo : add cache.

        private static Set<String> ignoredMethods = new HashSet<String>(  ){
            {
                add( "toString" );
                add( "hashCode" );
            }
        };


        public ElementHandler( Field field, ElementLocator locator, WebDriver webDriver ) {
            this.locator = locator;
            this.webDriver = webDriver;
            this.field = field;
            logger.debug( "created handler for [{}]", field );
        }

        public ElementHandler setFirstDisplayed( boolean firstDisplayed ) {
            logger.debug( "setting firstDisplayed [{}] for [{}]", firstDisplayed, field );
            this.firstDisplayed = firstDisplayed;
            return this;
        }

        private WebElement getFirstDisplayed( ){
            List<WebElement> elements = locator.findElements();
            for ( WebElement webElement : elements )
            {
                if ( webElement.isDisplayed() )
                {
                    return webElement;
                }
            }
            return null;
        }

        private WebElement locateElement(){
            if ( firstDisplayed ){
                return getFirstDisplayed();
            }else{
                return locator.findElement();
            }
        }

        @Override
        public Object intercept( Object o, Method method, Object[] objects, MethodProxy methodProxy ) throws Throwable {

            if ( ignoredMethods.contains( method.getName() ) ){
                return methodProxy.invokeSuper( o, objects );
            }
            logger.debug( "[{}] intercepted method [{}] on object [{}]. Will search for first displayed [{}]", new Object[]{field, method, o, firstDisplayed} );
            if ( o instanceof GsSeleniumComponent )
            {
                if ( !method.getName().equals( "setWebElement" ) && !method.getName().equals( "setWebDriver" ) )
                {
                    GsSeleniumComponent comp = ( GsSeleniumComponent ) o;

                    WebElement element = locateElement();

                    comp.setWebElement( element );
                    comp.setWebDriver( webDriver );
                }

                try
                {
                    return methodProxy.invokeSuper( o, objects );
                } catch ( InvocationTargetException e )
                {
                    throw e.getCause();
                }

            }

            else if ( o instanceof WebElement && firstDisplayed ){// only handle first displayed
                WebElement displayedElement = locateElement();

                if ( displayedElement != null ){
                    logger.info( "found first displayed. invoking method" );
                    return method.invoke( displayedElement, objects );
                }else{
                    logger.info("unable to detect first displayed");
                }
            }

            return null;

        }

        @Override
        public String toString() {
            return "ElementHandler{" +
                    "field=" + field +
                    '}';
        }
    }
}
