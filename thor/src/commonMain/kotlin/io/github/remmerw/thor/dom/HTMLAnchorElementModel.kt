package io.github.remmerw.thor.dom

import io.github.remmerw.thor.core.Urls
import io.github.remmerw.thor.css.StyleSheetWrapper
import org.w3c.dom.css.CSSStyleSheet
import org.w3c.dom.html.HTMLAnchorElement
import org.w3c.dom.stylesheets.LinkStyle
import java.net.MalformedURLException
import java.net.URL
import java.util.Optional
import java.util.function.Function

class HTMLAnchorElementModel(name: String) : HTMLElementModel(name), HTMLAnchorElement,
    LinkStyle {
    private var styleSheet: StyleSheetWrapper? = null


    override fun getAccessKey(): String? {
        TODO("Not yet implemented")
    }

    override fun setAccessKey(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun getCoords(): String? {
        TODO("Not yet implemented")
    }

    override fun setCoords(p0: String?) {
        TODO("Not yet implemented")
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

    override fun getName(): String? {
        TODO("Not yet implemented")
    }

    override fun setName(p0: String?) {
        TODO("Not yet implemented")
    }

    fun getMedia(): String? {
        return this.getAttribute("media")
    }

    fun setMedia(media: String?) {
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

    override fun getShape(): String? {
        TODO("Not yet implemented")
    }

    override fun setShape(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun getTabIndex(): Int {
        TODO("Not yet implemented")
    }

    override fun setTabIndex(p0: Int) {
        TODO("Not yet implemented")
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

    override fun blur() {
        TODO("Not yet implemented")
    }

    override fun focus() {
        TODO("Not yet implemented")
    }

    private val absoluteURL: Optional<URL>
        get() {
            val href = this.getHref()
            if (href.startsWith("javascript:")) {
                return Optional.empty<URL>()
            } else {
                try {
                    return Optional.ofNullable<URL>(this.getFullURL(href))
                } catch (mfu: MalformedURLException) {
                    this.warn("Malformed URI: [" + href + "].", mfu)
                }
            }
            return Optional.empty<URL>()
        }


    val absoluteHref: String?
        get() =// TODO: Use Either in getAbsoluteURL and use the branch type for javascript
            this.absoluteURL.map<String?>(Function { u: URL? -> u!!.toExternalForm() })
                .orElse(getHref())


    override fun toString(): String {
        // Javascript code often depends on this being exactly href. See js9.html.
        // To change, perhaps add method to AbstractScriptableDelegate.
        // Chromium 37 and FF 32 both return the full url
        // return this.getHref();
        return this.absoluteHref!!
    }


    override fun getSheet(): CSSStyleSheet? {
        return this.styleSheet
    }

}
