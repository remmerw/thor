package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLHeadingElement

class HTMLHeadingElementModel(name: String) : HTMLElementModel(name), HTMLHeadingElement {
    override fun getAlign(): String? {
        return this.getAttribute("align")
    }

    override fun setAlign(align: String?) {
        this.setAttribute("align", align)
    }
}
