package io.github.remmerw.thor.dom

import org.w3c.dom.Element
import java.util.logging.Level

open class HTMLElementModel(type: ElementType) : ElementImpl(type), Element {

    override fun getId(): String? {
        return this.getAttribute("id")
    }

    fun setId(id: String?) {
        this.setAttribute("id", id)
    }

    fun getTitle(): String? {
        return this.getAttribute("title")
    }

    fun setTitle(title: String?) {
        this.setAttribute("title", title)
    }

    fun getLang(): String? {
        return this.getAttribute("lang")
    }

    fun setLang(lang: String?) {
        this.setAttribute("lang", lang)
    }

    fun getDir(): String? {
        return this.getAttribute("dir")
    }

    fun setDir(dir: String?) {
        this.setAttribute("dir", dir)
    }

    fun getClassName(): String? {
        return this.getAttribute("class")
    }

    fun setClassName(className: String?) {
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

}
