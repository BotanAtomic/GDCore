package org.graviton.api;

/**
 * Created by Botan on 19/03/2016.
 */

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface InjectSetting {
    String value();
}
