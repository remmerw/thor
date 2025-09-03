package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLOListElement

class HTMLOListElementModel(name: String) : HTMLElementModel(name), HTMLOListElement {
    override fun getCompact(): Boolean {
        val compactText = this.getAttribute("compact")
        return "compact".equals(compactText, ignoreCase = true)
    }

    override fun setCompact(compact: Boolean) {
        this.setAttribute("compact", if (compact) "compact" else null)
    }

    override fun getStart(): Int {
        val startText = this.getAttribute("start")
        if (startText == null) {
            return 1
        }
        return try {
            startText.toInt()
        } catch (_: Throwable) {
            1
        }
    }

    override fun setStart(start: Int) {
        this.setAttribute("start", start.toString())
    }

    override fun getType(): String? {
        return this.getAttribute("type")
    }

    override fun setType(type: String?) {
        this.setAttribute("type", type)
    }
}
