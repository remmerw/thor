/*    GNU LESSER GENERAL PUBLIC LICENSE
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
 * Created on Sep 3, 2005
 */
package io.github.remmerw.thor.dom

import io.github.remmerw.thor.style.CSS2PropertiesContext
import io.github.remmerw.thor.style.ComputedCssProperties
import io.github.remmerw.thor.style.CssProperties
import io.github.remmerw.thor.style.LocalCssProperties
import org.w3c.dom.DOMException
import org.w3c.dom.html.HTMLElement
import org.w3c.dom.html.HTMLFormElement
import java.util.Arrays
import java.util.Locale
import java.util.StringTokenizer
import java.util.logging.Level
import kotlin.concurrent.Volatile

open class HTMLElementModel(name: String) : ElementImpl(name), HTMLElement, CSS2PropertiesContext {
    @Volatile
    private var currentStyle: CssProperties? = null


    protected fun forgetLocalStyle() {
        synchronized(this) {
            //TODO to be reconsidered in issue #41
            this.currentStyle = null

        }
    }

    fun forgetStyle(deep: Boolean) {
        // TODO: OPTIMIZATION: If we had a ComputedStyle map in
        // window (Mozilla model) the map could be cleared in one shot.
        synchronized(treeLock) {

            this.currentStyle = null

            if (deep) {

                this.nodes().forEach { nodeModel ->
                    val node: Any? = nodeModel
                    if (node is HTMLElementModel) {
                        node.forgetStyle(deep)
                    }
                }

            }
        }
    }


    fun style(): Any? {
        return LocalCssProperties(this)
    }


    // TODO hide from JS
    // Chromium(v37) and firefox(v32) do not expose this function
    // couldn't find anything in the standards.
    fun getComputedStyle(pseudoElement: String?): CssProperties {
        return ComputedCssProperties(
            this,
            getNodeData(HtmlStyles.getPseudoDeclaration(pseudoElement)),
            false
        )
    }


    override fun getId(): String? {
        // TODO: Check if a cache is useful for this attribute. Original gngr code had a cache here.
        val id = this.getAttribute("id")
        return if (id == null) "" else id
    }

    override fun setId(p0: String?) {
        this.setAttribute("id", id)
    }


    override fun getTitle(): String? {
        return this.getAttribute("title")
    }

    override fun setTitle(p0: String?) {
        this.setAttribute("title", title)
    }

    override fun getLang(): String? {
        return this.getAttribute("lang")
    }

    override fun setLang(p0: String?) {
        this.setAttribute("lang", lang)
    }

    override fun getDir(): String? {
        return this.getAttribute("dir")
    }

    override fun setDir(p0: String?) {
        this.setAttribute("dir", dir)
    }

    override fun getClassName(): String {
        val className = this.getAttribute("class")
        // Blank required instead of null.
        return if (className == null) "" else className
    }

    override fun setClassName(className: String?) {
        this.setAttribute("class", className)
    }


    fun getCharset(): String? {
        return this.getAttribute("charset")
    }

    fun setCharset(charset: String?) {
        this.setAttribute("charset", charset)
    }

    override fun warn(message: String?, err: Throwable?) {
        logger.log(Level.WARNING, message, err)
    }

    override fun warn(message: String?) {
        logger.log(Level.WARNING, message)
    }

    protected fun getAttributeAsInt(name: String, defaultValue: Int): Int {
        val value = this.getAttribute(name)
        try {
            return value!!.toInt()
        } catch (err: Exception) {
            this.warn("Bad integer", err)
            return defaultValue
        }
    }

    fun getAttributeAsBoolean(name: String): Boolean {
        return this.getAttribute(name) != null
    }

    override fun handleAttributeChanged(name: String, oldValue: String?, newValue: String?) {
        super.handleAttributeChanged(name, oldValue, newValue)
        forgetStyle(true)
        this.informInvalidRecursive()
    }


    override fun informInvalid() {
        // This is called when an attribute or child changes.
        // TODO: forgetStyle can call informInvalid() since informInvalid() seems to always follow forgetStyle()
        this.forgetStyle(false)
        super.informInvalid()
    }


    private fun informInvalidRecursive() {
        super.informInvalid()
        this.nodes().forEach { nodeModel ->
            if (nodeModel is HTMLElementModel) {
                nodeModel.informInvalidRecursive()
            }
        }

    }

    open fun getFormInputs(): Array<FormInput>? {
        return null
    }

    private fun classMatch(classTL: String?): Boolean {
        val classNames = this.getClassName()
        if ((classNames == null) || (classNames.length == 0)) {
            return classTL == null
        }
        val tok = StringTokenizer(classNames, " \t\r\n")
        while (tok.hasMoreTokens()) {
            val token = tok.nextToken()
            if (token.lowercase(Locale.getDefault()) == classTL) {
                return true
            }
        }
        return false
    }

    /**
     * Get an ancestor that matches the element tag name given and the style class
     * given.
     *
     * @param elementTL An tag name in lowercase or an asterisk (*).
     * @param classTL   A class name in lowercase.
     */
    fun getAncestorWithClass(elementTL: String, classTL: String?): HTMLElementModel? {
        val nodeObj: Any? = this.nodeParent
        if (nodeObj is HTMLElementModel) {
            val pelementTL = nodeObj.tagName.lowercase(Locale.getDefault())
            if (("*" == elementTL || elementTL == pelementTL) && nodeObj.classMatch(classTL)) {
                return nodeObj
            }
            return nodeObj.getAncestorWithClass(elementTL, classTL)
        } else {
            return null
        }
    }

    fun getParentWithClass(elementTL: String, classTL: String?): HTMLElementModel? {
        val nodeObj: Any? = this.nodeParent
        if (nodeObj is HTMLElementModel) {
            val pelementTL = nodeObj.tagName.lowercase(Locale.getDefault())
            if (("*" == elementTL || elementTL == pelementTL) && nodeObj.classMatch(classTL)) {
                return nodeObj
            }
        }
        return null
    }

    fun preceedingSiblingElement(): HTMLElementModel? {
        val parentNode = this.nodeParent
        if (parentNode == null) {
            return null
        }
        val childNodes = parentNode.childNodes
        if (childNodes == null) {
            return null
        }
        val length = childNodes.length
        var priorElement: HTMLElementModel? = null
        for (i in 0..<length) {
            val child = childNodes.item(i)
            if (child === this) {
                return priorElement
            }
            if (child is HTMLElementModel) {
                priorElement = child
            }
        }
        return null
    }

    fun getPreceedingSiblingWithClass(elementTL: String, classTL: String?): HTMLElementModel? {
        val psibling = this.preceedingSiblingElement()
        if (psibling != null) {
            val pelementTL = psibling.tagName.lowercase(Locale.getDefault())
            if (("*" == elementTL || elementTL == pelementTL) && psibling.classMatch(classTL)) {
                return psibling
            }
        }
        return null
    }

    fun getAncestorWithId(elementTL: String, idTL: String): HTMLElementModel? {
        val nodeObj: Any? = this.nodeParent
        if (nodeObj is HTMLElementModel) {
            val pelementTL = nodeObj.tagName.lowercase(Locale.getDefault())
            val pid = nodeObj.id
            val pidTL = if (pid == null) null else pid.lowercase(Locale.getDefault())
            if (("*" == elementTL || elementTL == pelementTL) && idTL == pidTL) {
                return nodeObj
            }
            return nodeObj.getAncestorWithId(elementTL, idTL)
        } else {
            return null
        }
    }

    fun getParentWithId(elementTL: String, idTL: String): HTMLElementModel? {
        val nodeObj: Any? = this.nodeParent
        if (nodeObj is HTMLElementModel) {
            val pelementTL = nodeObj.tagName.lowercase(Locale.getDefault())
            val pid = nodeObj.id
            val pidTL = if (pid == null) null else pid.lowercase(Locale.getDefault())
            if (("*" == elementTL || elementTL == pelementTL) && idTL == pidTL) {
                return nodeObj
            }
        }
        return null
    }

    fun getPreceedingSiblingWithId(elementTL: String, idTL: String): HTMLElementModel? {
        val psibling = this.preceedingSiblingElement()
        if (psibling != null) {
            val pelementTL = psibling.tagName.lowercase(Locale.getDefault())
            val pid = psibling.id
            val pidTL = if (pid == null) null else pid.lowercase(Locale.getDefault())
            if (("*" == elementTL || elementTL == pelementTL) && idTL == pidTL) {
                return psibling
            }
        }
        return null
    }

    fun getAncestor(elementTL: String): HTMLElementModel? {
        val nodeObj: Any? = this.nodeParent
        if (nodeObj is HTMLElementModel) {
            if ("*" == elementTL) {
                return nodeObj
            }
            val pelementTL = nodeObj.tagName.lowercase(Locale.getDefault())
            if (elementTL == pelementTL) {
                return nodeObj
            }
            return nodeObj.getAncestor(elementTL)
        } else {
            return null
        }
    }

    fun getParent(elementTL: String): HTMLElementModel? {
        val nodeObj: Any? = this.nodeParent
        if (nodeObj is HTMLElementModel) {
            if ("*" == elementTL) {
                return nodeObj
            }
            val pelementTL = nodeObj.tagName.lowercase(Locale.getDefault())
            if (elementTL == pelementTL) {
                return nodeObj
            }
        }
        return null
    }

    fun getPreceedingSibling(elementTL: String): HTMLElementModel? {
        val psibling = this.preceedingSiblingElement()
        if (psibling != null) {
            if ("*" == elementTL) {
                return psibling
            }
            val pelementTL = psibling.tagName.lowercase(Locale.getDefault())
            if (elementTL == pelementTL) {
                return psibling
            }
        }
        return null
    }

    protected fun getAncestorForJavaClass(javaClass: Class<HTMLFormElement>): Any? {
        val nodeObj: Any? = this.nodeParent
        if ((nodeObj == null) || javaClass.isInstance(nodeObj)) {
            return nodeObj
        } else if (nodeObj is HTMLElementModel) {
            return nodeObj.getAncestorForJavaClass(javaClass)
        } else {
            return null
        }
    }


    override fun documentBaseURI(): String? {
        val doc = this.document as HTMLDocumentImpl?
        if (doc != null) {
            return doc.getBaseURI()
        } else {
            return null
        }
    }

    override fun handleDocumentAttachmentChanged() {
        if (isAttachedToDocument) {
            forgetLocalStyle()
            forgetStyle(false)
            informInvalid()
        }
        super.handleDocumentAttachmentChanged()
    }


    // Based on http://www.w3.org/TR/dom/#domtokenlist
    inner class DOMTokenList {
        private val classes: Array<String>
            get() = getAttribute("class")?.split(" ".toRegex())?.dropLastWhile { it.isEmpty() }
                ?.toTypedArray() ?: emptyArray()

        private fun getClasses(max: Int): Array<String> {
            return getAttribute("class")?.split(" ".toRegex(), max.coerceAtLeast(0))?.toTypedArray()
                ?: emptyArray()
        }

        val length: Long
            get() = this.classes.size.toLong()

        fun item(index: Long): String? {
            val indexInt = index.toInt()
            return getClasses(indexInt + 1)[0]
        }

        fun contains(token: String?): Boolean {
            return Arrays.stream<String?>(this.classes).anyMatch { t: String? -> t == token }
        }

        fun add(token: String?) {
            add(arrayOf<String>(token!!))
        }

        fun add(tokens: Array<String>) {
            val sb = StringBuilder()
            for (token in tokens) {
                if (token.length == 0) {
                    throw DOMException(DOMException.SYNTAX_ERR, "empty token")
                }

                // TODO: Check for whitespace and throw IllegalCharacterError
                sb.append(' ')
                sb.append(token)
            }
            setAttribute("class", getAttribute("class") + sb)
        }

        fun remove(tokenToRemove: String?) {
            remove(arrayOf<String?>(tokenToRemove))
        }

        fun remove(tokensToRemove: Array<String?>) {
            val existingClasses = this.classes
            val sb = StringBuilder()
            for (clazz in existingClasses) {
                if (!Arrays.stream<String?>(tokensToRemove)
                        .anyMatch { tr: String? -> tr == clazz }
                ) {
                    sb.append(' ')
                    sb.append(clazz)
                }
            }
            setAttribute("class", sb.toString())
        }

        fun toggle(tokenToToggle: String): Boolean {
            val existingClasses = this.classes
            for (clazz in existingClasses) {
                if (tokenToToggle == clazz) {
                    remove(tokenToToggle)
                    return false
                }
            }

            // Not found, hence add
            add(tokenToToggle)
            return true
        }

        fun toggle(token: String?, force: Boolean): Boolean {
            if (force) {
                add(token)
            } else {
                remove(token)
            }
            return force
        } /* TODO: stringifier; */
    }


}
