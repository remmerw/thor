package io.github.remmerw.thor.dom


internal class ElementFactory private constructor() {


    @Throws(DOMException::class)
    fun createElement(document: Document, name: String): Element {
        val uid = document.nextUid()
        return Element(document, uid, name.uppercase())
    }

    companion object {
        val instance: ElementFactory = ElementFactory()
    }
}
