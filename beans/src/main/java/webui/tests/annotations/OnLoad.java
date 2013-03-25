package webui.tests.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: guym
 * Date: 2/3/13
 * Time: 9:13 AM
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.FIELD,ElementType.METHOD} )
public @interface OnLoad {
    // applies to list of web elements - whether to wait for only first visible or not
    boolean first() default false;
}
