package io.github.remmerw.thor.cobra.util

/**
 * Thrown when something hasn't been implemented yet.
 * Based on an idea discussed in http://stackoverflow.com/questions/2329358/is-there-anything-like-nets-notimplementedexception-in-java
 */
class NotImplementedYetException : RuntimeException {
    constructor() : super("Not Implemented Yet")

    constructor(msg: String?) : super("Not Implemented Yet: " + msg)

    companion object {
        private val serialVersionUID = -1809608435437763522L
    }
}
