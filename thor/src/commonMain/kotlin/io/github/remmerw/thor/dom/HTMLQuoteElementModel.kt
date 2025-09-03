package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLQuoteElement

class HTMLQuoteElementModel(name: String) : HTMLElementModel(name), HTMLQuoteElement {
    override fun getCite(): String? {
        return getAttribute("cite")
    }

    override fun setCite(cite: String?) {
        setAttribute("cite", cite)
    }
}
