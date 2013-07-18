package webui.tests.utils;

import java.util.Collection;
import java.util.List;

/**
 * User: guym
 * Date: 3/19/13
 * Time: 4:09 PM
 */
public class CollectionUtils extends org.apache.commons.collections.CollectionUtils {

    public static <T> T first( Collection<T> c ){
        return (T) c.iterator().next();
    }

    public static interface Predicate<T> {
        boolean apply(T t);
    }

    public static <T> T find(List<T> list, Predicate<T> predicate) {
        for (T item : list) {
            if (predicate.apply(item)) {
                return item;
            }
        }
        return null;
    }

    public static interface Action<T> {
        void apply(T item);
    }

    public static <T> void each(List<T> list, Action<T> action) {
        for (T item : list) {
            action.apply(item);
        }
    }

}
