package io.github.remmerw.thor.dom

import org.w3c.dom.DOMException
import org.w3c.dom.Element

internal class ElementFactory private constructor() {


    @Throws(DOMException::class)
    fun createElement(document: DocumentImpl, name: String): Element {

        try {
            val type = ElementType.valueOf(name.uppercase())
            val element = ElementImpl(document, type)
            return element
        } catch (_: Throwable) {
            println("Not yet supported node $name")
            val element = ElementImpl(document, ElementType.UNKNOWN)
            return element
        }
    }

    companion object {
        val instance: ElementFactory = ElementFactory()
    }
}
