package org.graviton.database.api;

/**
 * Created by Botan on 19/03/2016 : 02:57
 */

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface GameDatabase {
}
