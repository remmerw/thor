package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLElement
import java.util.logging.Level

open class HTMLElementModel(type: ElementType) : ElementImpl(type), HTMLElement {

    override fun getId(): String? {
        return this.getAttribute("id")
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

}
