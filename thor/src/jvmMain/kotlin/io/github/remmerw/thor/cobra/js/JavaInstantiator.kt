package io.github.remmerw.thor.cobra.js

interface JavaInstantiator {
    @Throws(InstantiationException::class, IllegalAccessException::class)
    fun newInstance(args: Array<Any?>?): Any?
}
