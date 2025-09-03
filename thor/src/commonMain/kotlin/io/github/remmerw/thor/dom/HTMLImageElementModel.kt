package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLImageElement

class HTMLImageElementModel(name: String) : HTMLElementModel(name), HTMLImageElement {


    override fun getName(): String? {
        return this.getAttribute("name")
    }

    override fun setName(name: String?) {
        this.setAttribute("name", name)
    }

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

    override fun getBorder(): String? {
        return this.getAttribute("border")
    }

    override fun setBorder(border: String?) {
        this.setAttribute("border", border)
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

    override fun getIsMap(): Boolean {
        return this.getAttributeAsBoolean("isMap")
    }

    override fun setIsMap(isMap: Boolean) {
        this.setAttribute("isMap", if (isMap) "isMap" else null)
    }

    override fun getLongDesc(): String? {
        return this.getAttribute("longDesc")
    }

    override fun setLongDesc(longDesc: String?) {
        this.setAttribute("longDesc", longDesc)
    }

    override fun getSrc(): String? {
        return this.getAttribute("src")
    }

    override fun setSrc(src: String?) {
        this.setAttribute("src", src)
    }

    override fun getUseMap(): String? {
        return this.getAttribute("useMap")
    }

    override fun setUseMap(useMap: String?) {
        this.setAttribute("useMap", useMap)
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


    override fun getLowSrc(): String? {
        TODO()
    }

    override fun setLowSrc(lowSrc: String?) {
        TODO()
    }
}
