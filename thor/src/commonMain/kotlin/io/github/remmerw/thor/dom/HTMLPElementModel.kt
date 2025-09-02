package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLParagraphElement

class HTMLPElementModel(name: String) : HTMLAbstractUIElement(name), HTMLParagraphElement {
    override fun getAlign(): String? {
        return this.getAttribute("align")
    }

    override fun setAlign(align: String?) {
        this.setAttribute("align", align)
    }

}
