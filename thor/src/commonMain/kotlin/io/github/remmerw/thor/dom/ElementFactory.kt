package io.github.remmerw.thor.dom

import org.w3c.dom.DOMException
import org.w3c.dom.Element

internal class ElementFactory private constructor() {


    @Throws(DOMException::class)
    fun createElement(document: HTMLDocumentImpl?, name: String): Element {

        try {
            val type = ElementType.valueOf(name.uppercase())
            val element = HTMLElementModel(type)
            element.setOwnerDocument(document) // todo document is parameter
            return element
        } catch (_: Throwable) {
            println("Not yet supported node $name")
            val element = HTMLElementModel(ElementType.UNKNOWN)
            element.setOwnerDocument(document)
            return element
        }
    }

    companion object {
        val instance: ElementFactory = ElementFactory()
    }
}
