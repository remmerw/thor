
package io.github.remmerw.thor.dom

import io.github.remmerw.thor.style.CssProperties
import io.github.remmerw.thor.style.LocalCssProperties
import org.w3c.dom.html.HTMLElement
import org.w3c.dom.html.HTMLFormElement
import java.util.Locale
import java.util.StringTokenizer
import java.util.logging.Level
import kotlin.concurrent.Volatile

open class HTMLElementModel(name: String) : ElementImpl(name), HTMLElement {
    @Volatile
    private var currentStyle: CssProperties? = null




    fun style(): Any? {
        return LocalCssProperties(this)
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


    fun documentBaseURI(): String? {
        val doc = this.document as HTMLDocumentImpl?
        if (doc != null) {
            return doc.getBaseURI()
        } else {
            return null
        }
    }


}
