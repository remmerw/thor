package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLElement
import org.w3c.dom.html.HTMLFormElement
import java.util.Locale
import java.util.logging.Level

open class HTMLElementModel(name: String) : ElementImpl(name), HTMLElement {


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

    override fun setTitle(title: String?) {
        this.setAttribute("title", title)
    }

    override fun getLang(): String? {
        return this.getAttribute("lang")
    }

    override fun setLang(lang: String?) {
        this.setAttribute("lang", lang)
    }

    override fun getDir(): String? {
        return this.getAttribute("dir")
    }

    override fun setDir(dir: String?) {
        this.setAttribute("dir", dir)
    }

    override fun getClassName(): String? {
        return this.getAttribute("class")
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

    fun getAttributeAsBoolean(name: String): Boolean {
        return this.getAttribute(name) != null
    }

    open fun getFormInputs(): Array<FormInput>? {
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


}
