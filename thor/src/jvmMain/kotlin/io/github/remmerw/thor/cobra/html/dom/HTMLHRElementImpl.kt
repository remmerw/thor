package io.github.remmerw.thor.cobra.html.dom

import org.w3c.dom.html.HTMLHRElement

class HTMLHRElementImpl(name: String) : HTMLAbstractUIElement(name), HTMLHRElement {
    override fun getAlign(): String? {
        return this.getAttribute("align")
    }

    override fun setAlign(align: String?) {
        this.setAttribute("align", align)
    }

    override fun getNoShade(): Boolean {
        return "noshade".equals(this.getAttribute("noshade"), ignoreCase = true)
    }

    override fun setNoShade(noShade: Boolean) {
        this.setAttribute("noshade", if (noShade) "noshade" else null)
    }

    override fun getSize(): String? {
        return this.getAttribute("size")
    }

    override fun setSize(size: String?) {
        this.setAttribute("size", size)
    }

    override fun getWidth(): String? {
        return this.getAttribute("width")
    }

    override fun setWidth(width: String?) {
        this.setAttribute("width", width)
    }
}
