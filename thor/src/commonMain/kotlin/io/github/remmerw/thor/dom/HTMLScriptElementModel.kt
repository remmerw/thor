package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLScriptElement

class HTMLScriptElementModel(name: String) : HTMLElementModel(name), HTMLScriptElement {
    private var text: String? = null // todo
    private var defer = false // todo


    override fun getText(): String? {
        return this.text
    }

    override fun setText(text: String?) {
        this.text = text
    }

    override fun getHtmlFor(): String? {
        return this.getAttribute("htmlFor")
    }

    override fun setHtmlFor(htmlFor: String?) {
        this.setAttribute("htmlFor", htmlFor)
    }

    override fun getEvent(): String? {
        return this.getAttribute("event")
    }

    override fun setEvent(event: String?) {
        this.setAttribute("event", event)
    }

    override fun getDefer(): Boolean {
        return this.defer
    }

    override fun setDefer(defer: Boolean) {
        this.defer = defer
    }

    override fun getSrc(): String? {
        return this.getAttribute("src")
    }

    override fun setSrc(src: String?) {
        this.setAttribute("src", src)
    }

    override fun getType(): String? {
        return this.getAttribute("type")
    }

    override fun setType(type: String?) {
        this.setAttribute("type", type)
    }

}
