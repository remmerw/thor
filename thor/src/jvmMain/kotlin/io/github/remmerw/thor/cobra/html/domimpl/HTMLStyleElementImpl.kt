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
/*
 * Created on Nov 27, 2005
 */
package io.github.remmerw.thor.cobra.html.domimpl

import io.github.remmerw.thor.cobra.html.style.CSSUtilities
import io.github.remmerw.thor.css.JStyleSheetWrapper
import org.w3c.dom.DOMException
import org.w3c.dom.Node.TEXT_NODE
import org.w3c.dom.Text
import org.w3c.dom.css.CSSStyleSheet
import org.w3c.dom.html.HTMLStyleElement
import org.w3c.dom.stylesheets.LinkStyle
import java.util.Locale

class HTMLStyleElementImpl(name: String) : HTMLElementImpl(name), HTMLStyleElement, LinkStyle {
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

    //TODO hide from JS
    fun setDisabledImpl(disabled: Boolean) {
        this.disabled = disabled
    }

    override fun getMedia(): String? {
        return this.getAttribute("media")
    }

    override fun setMedia(media: String?) {
        this.setAttribute("media", media)
    }

    override fun getType(): String? {
        return this.getAttribute("type")
    }

    override fun setType(type: String?) {
        this.setAttribute("type", type)
    }

    // TODO: This should probably not be a nop. We should probably be handling changes to inner text.
    override fun appendInnerTextImpl(buffer: StringBuffer) {
        // nop
    }

    @Throws(DOMException::class)
    override fun setAttribute(name: String, value: String?) {
        super.setAttribute(name, value)
        if (isAttachedToDocument) {
            val nameLowerCase = name.lowercase(Locale.getDefault())
            if ("type" == nameLowerCase || "media" == nameLowerCase || "title" == nameLowerCase) {
                this.disabled = false
                this.processStyle()
            }
        }
    }

    private val onlyText: String
        get() {
            val nl = this.childNodes
            val sb = StringBuilder()
            for (i in 0..<nl.length) {
                val n = nl.item(i)
                if (n.nodeType == TEXT_NODE) {
                    val textNode = n as Text
                    sb.append(textNode.textContent)
                }
            }
            return sb.toString()
        }

    private val isAllowedType: Boolean
        get() {
            val type = this.type
            return ((type == null) || (type.trim { it <= ' ' }.length == 0) || (type.equals(
                "text/css",
                ignoreCase = true
            )))
        }

    // TODO: check if this method can be made private
    protected fun processStyle() {
        if (isAttachedToDocument) {
            /* check if type == "text/css" or no, empty value is also allowed as well.
       if it is something other than empty or "text/css" set the style sheet to null
       we need not check for the media type here, jStyle parser should take care of this.
       */
            if (this.isAllowedType) {
                val uacontext = this.userAgentContext
                if (uacontext!!.isInternalCSSEnabled()) {
                    val doc = this.ownerDocument as HTMLDocumentImpl
                    val newStyleSheet = processStyleHelper()
                    newStyleSheet.setDisabled(this.disabled)
                    this.styleSheet = newStyleSheet
                    doc.styleSheetManager.invalidateStyles()
                }
            } else {
                this.detachStyleSheet()
            }
        }
    }

    private fun processStyleHelper(): JStyleSheetWrapper {
        val doc = this.ownerDocument as HTMLDocumentImpl
        // TODO a sanity check can be done for the media type while setting it to the style sheet
        // as in is it a valid media type or not
        try {
            val text = this.onlyText
            val processedText = CSSUtilities.preProcessCss(text)
            val baseURI = doc.getBaseURI()
            // TODO if the new StyleSheet contains any @import rules, then we should queue them for further processing. GH #137
            val jSheet = CSSUtilities.jParseStyleSheet(
                this,
                baseURI!!,
                processedText,
                doc.getUserAgentContext()
            )
            return JStyleSheetWrapper(
                jSheet,
                this.media,
                null,
                this.type,
                this.title,
                this,
                doc.styleSheetManager.bridge
            )
        } catch (err: Exception) {
            this.warn("Unable to parse style sheet", err)
        }
        return this.emptyStyleSheet
    }

    private val emptyStyleSheet: JStyleSheetWrapper
        get() {
            val doc = this.ownerDocument as HTMLDocumentImpl
            return JStyleSheetWrapper(
                CSSUtilities.emptyStyleSheet,
                this.media,
                null,
                this.type,
                this.title,
                this,
                doc.styleSheetManager.bridge
            )
        }

    private fun detachStyleSheet() {
        if (this.styleSheet != null) {
            this.styleSheet!!.setOwnerNode(null)
            this.styleSheet = null
            val doc = this.ownerDocument as HTMLDocumentImpl
            doc.styleSheetManager.invalidateStyles()
        }
    }

    override fun getSheet(): CSSStyleSheet? {
        return this.styleSheet
    }

    override fun handleChildListChanged() {
        this.processStyle()
    }

    override fun handleDocumentAttachmentChanged() {
        if (isAttachedToDocument) {
            this.processStyle()
        } else {
            this.detachStyleSheet()
        }
    }
}
