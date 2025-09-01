/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Contact info: lobochief@users.sourceforge.net
 */
package io.github.remmerw.thor.dom

import io.github.remmerw.thor.core.Urls
import io.github.remmerw.thor.css.JStyleSheetWrapper
import io.github.remmerw.thor.style.CSSUtilities
import org.w3c.dom.css.CSSStyleSheet
import org.w3c.dom.html.HTMLLinkElement
import org.w3c.dom.stylesheets.LinkStyle
import java.net.MalformedURLException
import java.net.URL
import java.util.Locale
import java.util.Optional
import java.util.function.Function

class HTMLLinkElementImpl(name: String) : HTMLAbstractUIElement(name), HTMLLinkElement, LinkStyle {
    private var styleSheet: JStyleSheetWrapper? = null
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
        val target = this.getAttribute("target")
        if (target != null) {
            return target
        }
        val doc = this.document as HTMLDocumentImpl?
        return if (doc == null) null else doc.defaultTarget
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

    private val isWellFormedURL: Boolean
        // TODO can go in Urls util class.
        get() {
            val doc = this.ownerDocument as HTMLDocumentImpl
            try {
                val baseURL = URL(doc.getBaseURI())
                // we call createURL just to check whether it throws an exception
                // if the URL is not well formed.
                Urls.createURL(baseURL, this.getHref())
                return true
            } catch (mfe: MalformedURLException) {
                // this.warn("Will not parse CSS. URI=[" + this.getHref() + "] with BaseURI=[" + doc.getBaseURI() + "] does not appear to be a valid URI.");
                return false
            }
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

    // TODO: Should HTMLLinkElement actually support navigation? The Link element seems to be conflated with <a> elements

    fun navigate(): Boolean {
        // If there is no href attribute, chromium only dispatches the handlers without starting a navigation

        val hrefAttr = this.getAttribute("href")
        if (hrefAttr == null) {
            return false
        }

        if (this.disabled) {
            return false
        }
        val href = getHref()
        if (href.startsWith("#")) {
            // TODO: Scroll to the element. Issue #101
        } else if (href.startsWith("javascript:")) {
            val script = href.substring(11)
            // evalInScope adds the JS task
            println(script)
        } else {
            val urlOpt = this.absoluteURL
            if (urlOpt.isPresent) {
                val rcontext = this.htmlRendererContext
                val target = this.getTarget()
                rcontext?.linkClicked(this, urlOpt.get(), target)
                return true
            }
        }
        return false
    }

    override fun toString(): String {
        // Javascript code often depends on this being exactly href. See js9.html.
        // To change, perhaps add method to AbstractScriptableDelegate.
        // Chromium 37 and FF 32 both return the full url
        // return this.getHref();
        return this.absoluteHref!!
    }

    /**
     * Sets the owner node to null so as to update the old reference of the
     * stylesheet held by JS
     */
    private fun detachStyleSheet() {
        if (this.styleSheet != null) {
            this.styleSheet!!.setOwnerNode(null)
            this.styleSheet = null
            val doc = this.ownerDocument as HTMLDocumentImpl
            doc.styleSheetManager.invalidateStyles()
        }
    }

    private fun isSameRel(name: String?, oldValue: String?): Boolean {
        if ("rel" == name) {
            return this.isSameAttributeValue("rel", oldValue)
        }
        return false
    }

    private fun isSameHref(name: String?, oldValue: String?): Boolean {
        if ("href" == name) {
            return this.isSameAttributeValue("href", oldValue)
        }
        return false
    }

    private fun isSameAttributeValue(name: String, oldValue: String?): Boolean {
        val newValue = this.getAttribute(name)
        if (oldValue == null) {
            return newValue == null
        } else {
            return oldValue == newValue
        }
    }

    private val cleanRel: String?
        get() {
            val rel = this.rel
            return if (rel == null) null else rel.trim { it <= ' ' }
                .lowercase(Locale.getDefault())
        }

    private fun isStyleSheet(): Boolean {
        val rel = this.cleanRel
        return ((rel != null) && (rel == "stylesheet"))
    }

    private val isAltStyleSheet: Boolean
        get() {
            val rel = this.cleanRel
            return ((rel != null) && (rel == "alternate stylesheet"))
        }

    private val isAllowedRel: Boolean
        get() = ((isStyleSheet()) || (this.isAltStyleSheet))

    private val isAllowedType: Boolean
        get() {
            val type = this.type
            return ((type == null) || (type.trim { it <= ' ' }.length == 0) || (type.equals(
                "text/css",
                ignoreCase = true
            )))
        }

    private fun processLink() {
        val doc = this.ownerDocument as HTMLDocumentImpl

        val uacontext = this.userAgentContext
        if (uacontext!!.isExternalCSSEnabled()) {
            try {
                val href = this.getHref()
                val jSheet = CSSUtilities.jParse(
                    this, href, doc,
                    doc.baseURI!!, false
                )
                if (this.styleSheet != null) {
                    this.styleSheet!!.jStyleSheet = jSheet
                } else {
                    val styleSheet = JStyleSheetWrapper(
                        jSheet, this.media, href, this.type, this.title,
                        this, doc.styleSheetManager.bridge
                    )
                    this.styleSheet = styleSheet
                }
                this.styleSheet!!.setDisabled(this.isAltStyleSheet or this.disabled)
                doc.styleSheetManager.invalidateStyles()
            } catch (mfe: MalformedURLException) {
                this.detachStyleSheet()
                this.warn(
                    ("Will not parse CSS. URI=[" + this.getHref() + "] with BaseURI=[" + doc.getBaseURI()
                            + "] does not appear to be a valid URI.")
                )
            } catch (err: Exception) {
                this.warn("Unable to parse CSS. URI=[" + this.getHref() + "].", err)
            }
        }

    }

    private fun deferredProcess() {
        processLinkHelper(true)
    }

    private fun processLinkHelper(defer: Boolean) {
        // according to firefox, whenever the URL is not well formed, the style sheet has to be null
        // and in all other cases an empty style sheet has to be set till the link resource can be fetched
        // and processed. But however the style sheet is not in ready state till it is processed. This is
        // indicated by setting the jStyleSheet of the JStyleSheetWrapper to null.
        val doc = this.ownerDocument as HTMLDocumentImpl
        if (isAttachedToDocument && this.isWellFormedURL && this.isAllowedRel && this.isAllowedType) {
            if (defer) {
                this.styleSheet = this.emptyStyleSheet
                doc.styleSheetManager.invalidateStyles()
                //TODO need to think how to schedule this. refer issue #69

            } else {
                processLink()
            }
        } else {
            this.detachStyleSheet()

        }
    }

    private val emptyStyleSheet: JStyleSheetWrapper
        get() {
            val doc = this.ownerDocument as HTMLDocumentImpl
            return JStyleSheetWrapper(
                null, this.media, this.getHref(), this.type, this.title, this,
                doc.styleSheetManager.bridge
            )
        }

    override fun getSheet(): CSSStyleSheet? {
        return this.styleSheet
    }

    override fun handleDocumentAttachmentChanged() {
        deferredProcess()
    }

    override fun handleAttributeChanged(name: String, oldValue: String?, newValue: String?) {
        super.handleAttributeChanged(name, oldValue, newValue)

        // TODO according to firefox's behavior whenever a valid attribute is
        // changed on the element the disabled flag is set to false. Need to
        // verify with the specs.
        // TODO check for all the attributes associated with an link element
        // according to firefox if the new value of rel/href is the same as the
        // old one then, the nothing has to be done. In all other cases the link element
        // has to be re-processed.
        if (isSameRel(name, oldValue) || isSameHref(name, oldValue)) {
        } else if ("rel" == name || "href" == name || "type" == name || "media" == name) {
            this.disabled = false
            this.detachStyleSheet()
            this.processLinkHelper(true)
        }
    }
}
