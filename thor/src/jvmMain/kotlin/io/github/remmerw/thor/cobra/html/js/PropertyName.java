package io.github.remmerw.thor.cobra.html.js;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.github.remmerw.thor.cobra.js.JavaClassWrapper;

/**
 * Specifies the property name directly instead of being inferred. In the longer
 * run, it might be better to add {@link java.beans.BeanInfo} awareness in
 * {@link JavaClassWrapper}
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyName {

    String value();

}
