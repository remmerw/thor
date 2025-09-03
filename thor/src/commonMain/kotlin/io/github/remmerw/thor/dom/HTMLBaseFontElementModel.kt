package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLBaseFontElement

class HTMLBaseFontElementModel(name: String) : HTMLElementModel(name), HTMLBaseFontElement {
    override fun getColor(): String? {
        return this.getAttribute("color")
    }

    override fun setColor(color: String?) {
        this.setAttribute("color", color)
    }

    override fun getFace(): String? {
        return this.getAttribute("face")
    }

    override fun setFace(face: String?) {
        this.setAttribute("face", face)
    }

    override fun getSize(): String? {
        return this.getAttribute("size")
    }

    override fun setSize(size: String?) {
        this.setAttribute("size", size)
    }

}
