package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLHtmlElement

class HTMLHtmlElementModel(name: String) : HTMLElementModel(name), HTMLHtmlElement {


    override fun getVersion(): String? {
        return this.getAttribute("version")
    }

    override fun setVersion(version: String?) {
        this.setAttribute("version", version)
    }
}
