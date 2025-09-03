package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLPreElement

class HTMLPreElementModel(name: String) : HTMLElementModel(name), HTMLPreElement {
    override fun getWidth(): Int {
        val widthText = this.getAttribute("width")
        if (widthText == null) {
            return 0
        }
        return try {
            widthText.toInt()
        } catch (_: Throwable) {
            0
        }
    }

    override fun setWidth(width: Int) {
        this.setAttribute("width", width.toString())
    }
}
