package org.graviton.utils;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Created by Botan on 15/01/2017. 16:44
 */

@Slf4j
public class FastClassLoader {

    public static <V> List<Class<? extends V>> getClasses(String packageName, Class<V> type) {
        return new ArrayList<>(new Reflections(packageName).getSubTypesOf(type));
    }

}
