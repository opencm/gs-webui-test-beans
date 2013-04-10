package webui.tests.components;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: guym
 * Date: 4/9/13
 * Time: 11:55 AM
 *
 * This annotation enables support for same page in different versions.<br/>
 * For example, lets say I have page X with an input. <br/>
 * In XAP version, the input's name is "service-name" and in Cloudify it is "processing-unit-name".<br/>
 * The test will require only one WebElement, but the CSS selector for the page can be<br/>
 * "[name=service-name],[name=processing-unit-name]"<br/>
 * The locator should find the first displayed and inject that one in the field.<br/>
 *
 */
@Target( ElementType.FIELD)
@Retention( RetentionPolicy.RUNTIME )
public @interface FirstDisplayed {
}
