package io.github.remmerw.thor.dom

import org.w3c.dom.Element

internal class ElementFactory private constructor() {


    @Throws(DOMException::class)
    fun createElement(document: DocumentImpl, name: String): Element {
        val uid = document.nextUid()
        return ElementImpl(document, uid, name.uppercase())
    }

    companion object {
        val instance: ElementFactory = ElementFactory()
    }
}
