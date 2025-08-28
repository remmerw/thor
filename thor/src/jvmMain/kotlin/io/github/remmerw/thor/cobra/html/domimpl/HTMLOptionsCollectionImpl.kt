package io.github.remmerw.thor.cobra.html.domimpl

import org.w3c.dom.DOMException
import org.w3c.dom.Node
import org.w3c.dom.html.HTMLCollection
import org.w3c.dom.html.HTMLOptionElement

class HTMLOptionsCollectionImpl(selectElement: HTMLElementImpl) :
    DescendentHTMLCollection(selectElement, OPTION_FILTER, selectElement.treeLock, false),
    HTMLCollection {
    @Throws(DOMException::class)
    fun setLength(length: Int) {
        throw UnsupportedOperationException()
    }

    private class OptionFilter : NodeFilter {
        override fun accept(node: Node?): Boolean {
            return node is HTMLOptionElement
        }
    }

    companion object {
        val OPTION_FILTER: NodeFilter = OptionFilter()
    }
}
