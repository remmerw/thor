
package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLDivElement

class HTMLDivElementModel(name: String) : HTMLAbstractUIElement(name), HTMLDivElement {
    override fun getAlign(): String? {
        return this.getAttribute("align")
    }

    override fun setAlign(align: String?) {
        this.setAttribute("align", align)
    }


}
