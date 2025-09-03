package io.github.remmerw.thor.parser

object ArrayUtilities {


    fun <T> contains(ts: Array<T?>, t: T?): Boolean {
        for (e in ts) {
            if (e == t) {
                return true
            }
        }
        return false
    }

}