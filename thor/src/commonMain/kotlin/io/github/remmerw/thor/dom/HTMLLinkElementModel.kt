package io.github.remmerw.thor.dom

import io.github.remmerw.thor.core.Urls
import io.github.remmerw.thor.css.StyleSheetWrapper
import org.w3c.dom.css.CSSStyleSheet
import org.w3c.dom.html.HTMLLinkElement
import org.w3c.dom.stylesheets.LinkStyle

class HTMLLinkElementModel(name: String) : HTMLElementModel(name), HTMLLinkElement, LinkStyle {
    private var styleSheet: StyleSheetWrapper? = null
    private var disabled = false

    override fun getDisabled(): Boolean {
        return this.disabled
    }

    override fun setDisabled(disabled: Boolean) {
        this.disabled = disabled
        val sheet: CSSStyleSheet? = this.styleSheet
        if (sheet != null) {
            sheet.disabled = disabled
        }
    }


    fun setDisabledInternal(disabled: Boolean) {
        this.disabled = disabled
    }

    override fun getHref(): String {
        val href = this.getAttribute("href")
        return if (href == null) "" else Urls.removeControlCharacters(href)
    }

    override fun setHref(href: String?) {
        this.setAttribute("href", href)
    }

    override fun getHreflang(): String? {
        return this.getAttribute("hreflang")
    }

    override fun setHreflang(hreflang: String?) {
        this.setAttribute("hreflang", hreflang)
    }

    override fun getMedia(): String? {
        return this.getAttribute("media")
    }

    override fun setMedia(media: String?) {
        this.setAttribute("media", media)
    }

    override fun getRel(): String? {
        return this.getAttribute("rel")
    }

    override fun setRel(rel: String?) {
        this.setAttribute("rel", rel)
    }

    override fun getRev(): String? {
        return this.getAttribute("rev")
    }

    override fun setRev(rev: String?) {
        this.setAttribute("rev", rev)
    }

    override fun getTarget(): String? {
        return this.getAttribute("target")
    }

    override fun setTarget(target: String?) {
        this.setAttribute("target", target)
    }

    override fun getType(): String? {
        return this.getAttribute("type")
    }

    override fun setType(type: String?) {
        this.setAttribute("type", type)
    }


    override fun getSheet(): CSSStyleSheet? {
        return this.styleSheet
    }


}
