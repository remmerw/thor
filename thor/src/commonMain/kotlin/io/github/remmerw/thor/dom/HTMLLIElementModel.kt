package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLLIElement

class HTMLLIElementModel(name: String) : HTMLElementModel(name), HTMLLIElement {
    override fun getType(): String? {
        return this.getAttribute("type")
    }

    override fun setType(type: String?) {
        this.setAttribute("type", type)
    }

    override fun getValue(): Int {
        val valueText = this.getAttribute("value")
        if (valueText == null) {
            return 0
        }
        return try {
            valueText.toInt()
        } catch (_: Throwable) {
            0
        }
    }

    override fun setValue(value: Int) {
        this.setAttribute("value", value.toString())
    }
}
