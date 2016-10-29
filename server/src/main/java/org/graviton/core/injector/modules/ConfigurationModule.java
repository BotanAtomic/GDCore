package org.graviton.core.injector.modules;

import com.google.inject.AbstractModule;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import lombok.extern.slf4j.Slf4j;
import org.graviton.api.InjectSetting;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Botan on 29/10/2016 : 03:12
 */

@Slf4j
public class ConfigurationModule extends AbstractModule {

    @Override
    protected void configure() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));

            binder().bindListener(Matchers.any(), listener(((type, encounter) -> {
                for (Field field : type.getRawType().getDeclaredFields()) {
                    if (field.isAnnotationPresent(InjectSetting.class)) {
                        field.setAccessible(true);

                        encounter.register(injector(instance -> {
                            try {
                                field.set(instance, parse(properties.get(field.getAnnotation(InjectSetting.class).value()), field));
                            } catch (IllegalAccessException e) {
                                super.addError(e);
                            }
                        }));
                    }
                }
            })));

            log.debug("Configuration file successfully loaded");
        } catch (IOException e) {
            super.addError(e);
        }
    }

    private TypeListener listener(BiConsumer<TypeLiteral<?>, TypeEncounter<?>> consumer) {
        return consumer::accept;
    }

    private MembersInjector<Object> injector(Consumer<Object> consumer) {
        return consumer::accept;
    }

    private Object parse(Object value, Field field) {
        Type type = field.getType();

        if (type == boolean.class)
            value = Boolean.parseBoolean(value.toString());
        else if (type == int.class)
            value = Integer.parseInt(value.toString());

        return value;
    }
}
