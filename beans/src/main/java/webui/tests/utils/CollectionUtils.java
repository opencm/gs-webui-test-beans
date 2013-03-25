package webui.tests.utils;

import java.util.Collection;

/**
 * User: guym
 * Date: 3/19/13
 * Time: 4:09 PM
 */
public class CollectionUtils extends org.apache.commons.collections.CollectionUtils {

    public static <T> T first( Collection<T> c ){
        return (T) c.iterator().next();
    }
}
