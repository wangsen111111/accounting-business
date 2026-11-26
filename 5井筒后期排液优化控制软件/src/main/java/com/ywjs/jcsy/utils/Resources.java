package com.ywjs.jcsy.utils;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author ZLC
 * @create 2020-08-04 09:58
 */
public class Resources {

    public static <T> T findItem(List<T> container, Predicate<T> p) {
        T result = null;
        if (container != null) {
            for (T item : container) {
                if (p.test(item)) {
                    result = item;
                    break;
                }
            }
        }
        return result;
    }
}
