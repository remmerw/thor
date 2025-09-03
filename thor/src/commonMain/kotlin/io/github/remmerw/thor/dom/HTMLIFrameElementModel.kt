package io.github.remmerw.thor.dom

import org.w3c.dom.Document
import org.w3c.dom.html.HTMLIFrameElement

class HTMLIFrameElementModel(name: String) : HTMLElementModel(name), HTMLIFrameElement {


    override fun getAlign(): String? {
        return this.getAttribute("align")
    }

    override fun setAlign(align: String?) {
        this.setAttribute("align", align)
    }


    override fun getFrameBorder(): String? {
        return this.getAttribute("frameborder")
    }

    override fun setFrameBorder(frameBorder: String?) {
        this.setAttribute("frameborder", frameBorder)
    }

    override fun getHeight(): String? {
        return this.getAttribute("height")
    }

    override fun setHeight(height: String?) {
        this.setAttribute("height", height)
    }

    override fun getLongDesc(): String? {
        return this.getAttribute("longdesc")
    }

    override fun setLongDesc(longDesc: String?) {
        this.setAttribute("longdesc", longDesc)
    }

    override fun getMarginHeight(): String? {
        return this.getAttribute("marginheight")
    }

    override fun setMarginHeight(marginHeight: String?) {
        this.setAttribute("marginHeight", marginHeight)
    }

    override fun getMarginWidth(): String? {
        return this.getAttribute("marginwidth")
    }

    override fun setMarginWidth(marginWidth: String?) {
        this.setAttribute("marginWidth", marginWidth)
    }

    override fun getName(): String? {
        return this.getAttribute("name")
    }

    override fun setName(name: String?) {
        this.setAttribute("name", name)
    }

    override fun getScrolling(): String? {
        return this.getAttribute("scrolling")
    }

    override fun setScrolling(scrolling: String?) {
        this.setAttribute("scrolling", scrolling)
    }

    override fun getSrc(): String? {
        return this.getAttribute("src")
    }

    override fun setSrc(src: String?) {
        this.setAttribute("src", src)
    }

    override fun getWidth(): String? {
        return this.getAttribute("width")
    }

    override fun setWidth(width: String?) {
        this.setAttribute("width", width)
    }

    override fun getContentDocument(): Document? {
        TODO("Not yet implemented")
    }


}
