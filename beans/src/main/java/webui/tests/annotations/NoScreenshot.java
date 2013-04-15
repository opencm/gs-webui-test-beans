package webui.tests.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: guym
 * Date: 4/15/13
 * Time: 10:43 AM
 */
@Target( ElementType.METHOD)
@Retention( RetentionPolicy.RUNTIME )
public @interface NoScreenshot {
}
