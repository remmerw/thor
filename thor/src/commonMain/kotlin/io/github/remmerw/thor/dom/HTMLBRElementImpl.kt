package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLBRElement

class HTMLBRElementImpl(name: String) : HTMLElementImpl(name), HTMLBRElement {
    override fun getClear(): String? {
        return this.getAttribute("clear")
    }

    override fun setClear(clear: String?) {
        this.setAttribute("clear", clear)
    }

    override fun appendInnerTextImpl(buffer: StringBuffer) {
        buffer.append("\r\n")
        super.appendInnerTextImpl(buffer)
    }
}
