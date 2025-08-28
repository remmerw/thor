package io.github.remmerw.thor.cobra.html.js

import io.github.remmerw.thor.cobra.js.JavaClassWrapper

/**
 * Specifies the property name directly instead of being inferred. In the longer
 * run, it might be better to add [java.beans.BeanInfo] awareness in
 * [JavaClassWrapper]
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class PropertyName(val value: String)
