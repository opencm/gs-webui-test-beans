package webui.tests.selenium;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.FieldDecorator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import webui.tests.components.Absolute;
import webui.tests.components.FirstDisplayed;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

/**
 * User: guym
 * Date: 4/8/13
 * Time: 5:18 PM
 */
public class GsFieldDecorator implements FieldDecorator, ApplicationContextAware {

    final DefaultFieldDecorator defaultFieldDecorator;

    final SearchContext searchContext;
    private final WebDriver webDriver;
    private ApplicationContext applicationContext = null;


    public GsFieldDecorator( SearchContext searchContext, WebDriver webDriver ) {
        this.searchContext = searchContext;
        this.webDriver = webDriver;
        defaultFieldDecorator = new DefaultFieldDecorator( new DefaultElementLocatorFactory( searchContext ) );
    }


    private boolean isComponentCollection( Field field ){
        return Collection.class.isAssignableFrom( field.getType()  )
                && field.getGenericType() instanceof ParameterizedType
                && (GsSeleniumComponent.class.isAssignableFrom( getActualType( field )));

    }

    private Class getActualType( Field field ){
            return (Class)((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }


    public Object getEnhancedObject( Class clzz, MethodInterceptor methodInterceptor  ){
        Enhancer e = new Enhancer();
        e.setSuperclass(clzz);
        e.setCallback( methodInterceptor );
        return e.create();
    }


    @Override
    public Object decorate( ClassLoader loader, Field field ) {
        if ( ( GsSeleniumComponent.class.isAssignableFrom( field.getType() ) || field.isAnnotationPresent( FirstDisplayed.class  ) ) && field.isAnnotationPresent( FindBy.class )){
            return getEnhancedObject( field.getType(), getElementHandler( field ) );
        }else if ( isComponentCollection( field ) ){ // handle multiple components on same page
            return getEnhancedObject( field.getType(), new GsLocators.ListHandler( getLocator( field ), getActualType( field ) ) );
        }else{
            return defaultFieldDecorator.decorate( loader, field );
        }
    }

    private GsLocators.ElementHandler getElementHandler( Field field ) {
        return new GsLocators.ElementHandler( field, getLocator( field ), webDriver )
                .setFirstDisplayed( field.isAnnotationPresent( FirstDisplayed.class ) );
    }

    private ElementLocator getLocator( Field field ) {
        if ( field.isAnnotationPresent(Absolute.class)){
            return new DefaultElementLocatorFactory( webDriver ).createLocator( field );
        }else{
            return new DefaultElementLocatorFactory( searchContext ).createLocator( field );
        }
    }

    @Override
    public void setApplicationContext( ApplicationContext applicationContext ) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
