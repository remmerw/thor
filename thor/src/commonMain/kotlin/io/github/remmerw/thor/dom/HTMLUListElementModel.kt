package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLUListElement

class HTMLUListElementModel(name: String) : HTMLElementModel(name), HTMLUListElement {
    override fun getCompact(): Boolean {
        val compactText = this.getAttribute("compact")
        return "compact".equals(compactText, ignoreCase = true)
    }

    override fun setCompact(compact: Boolean) {
        this.setAttribute("compact", if (compact) "compact" else null)
    }

    override fun getType(): String? {
        return this.getAttribute("type")
    }

    override fun setType(type: String?) {
        this.setAttribute("type", type)
    }
}
