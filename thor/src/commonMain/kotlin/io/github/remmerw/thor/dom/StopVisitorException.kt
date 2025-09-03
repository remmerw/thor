package io.github.remmerw.thor.dom

internal class StopVisitorException : RuntimeException {
    val tag: Any?

    constructor(tag: Any?) {
        this.tag = tag
    }

}
