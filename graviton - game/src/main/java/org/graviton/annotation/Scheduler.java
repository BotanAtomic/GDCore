package org.graviton.annotation;

/**
 * Created by Botan on 18/04/2017. 14:10
 */

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduler {
}

