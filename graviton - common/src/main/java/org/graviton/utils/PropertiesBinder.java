package org.graviton.utils;

import com.google.inject.Binder;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.graviton.api.InjectSetting;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Botan on 02/11/2016 : 13:48
 */
public class PropertiesBinder {

    public static void bind(Binder binder, Properties properties) {
        binder.bindListener(Matchers.any(), listener(((type, encounter) -> {
            for (Field field : type.getRawType().getDeclaredFields()) {
                if (field.isAnnotationPresent(InjectSetting.class)) {
                    field.setAccessible(true);

                    encounter.register(injector(instance -> {
                        try {
                            field.set(instance, parse(properties.get(field.getAnnotation(InjectSetting.class).value()), field));
                        } catch (IllegalAccessException e) {
                            binder.addError(e);
                        }
                    }));
                }
            }
        })));
    }

    private static TypeListener listener(BiConsumer<TypeLiteral<?>, TypeEncounter<?>> consumer) {
        return consumer::accept;
    }

    private static MembersInjector<Object> injector(Consumer<Object> consumer) {
        return consumer::accept;
    }

    private static Object parse(Object value, Field field) {
        Type type = field.getType();

        if (type == boolean.class)
            value = Boolean.parseBoolean(value.toString());
        else if (type == int.class)
            value = Integer.parseInt(value.toString());

        return value;
    }

}
