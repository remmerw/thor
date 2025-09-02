package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLAppletElement

class HTMLAppletElementModel(name: String) : HTMLAbstractUIElement(name), HTMLAppletElement {
    override fun getAlign(): String? {
        return this.getAttribute("align")
    }

    override fun setAlign(align: String?) {
        this.setAttribute("align", align)
    }

    override fun getAlt(): String? {
        return this.getAttribute("alt")
    }

    override fun setAlt(alt: String?) {
        this.setAttribute("alt", alt)
    }

    override fun getArchive(): String? {
        return this.getAttribute("archive")
    }

    override fun setArchive(archive: String?) {
        this.setAttribute("archive", archive)
    }

    override fun getCode(): String? {
        return this.getAttribute("code")
    }

    override fun setCode(code: String?) {
        this.setAttribute("code", code)
    }

    override fun getCodeBase(): String? {
        return this.getAttribute("codebase")
    }

    override fun setCodeBase(codeBase: String?) {
        this.setAttribute("codebase", codeBase)
    }

    override fun getHeight(): String? {
        return this.getAttribute("height")
    }

    override fun setHeight(height: String?) {
        this.setAttribute("height", height)
    }

    override fun getHspace(): String? {
        return this.getAttribute("hspace")
    }

    override fun setHspace(hspace: String?) {
        this.setAttribute("hspace", hspace)
    }

    override fun getName(): String? {
        return this.getAttribute("name")
    }

    override fun setName(name: String?) {
        this.setAttribute("name", name)
    }

    override fun getObject(): String? {
        return this.getAttribute("object")
    }

    override fun setObject(`object`: String?) {
        this.setAttribute("object", `object`)
    }

    override fun getVspace(): String? {
        return this.getAttribute("vspace")
    }

    override fun setVspace(vspace: String?) {
        this.setAttribute("vspace", vspace)
    }

    override fun getWidth(): String? {
        return this.getAttribute("width")
    }

    override fun setWidth(width: String?) {
        this.setAttribute("width", width)
    }
}
