package io.github.remmerw.thor.dom

import io.github.remmerw.thor.style.IFrameRenderState
import io.github.remmerw.thor.style.RenderState
import io.github.remmerw.thor.ua.UserAgentContext
import io.github.remmerw.thor.ua.UserAgentContext.RequestKind
import org.w3c.dom.Document
import org.w3c.dom.html.HTMLIFrameElement
import java.net.MalformedURLException

class HTMLIFrameElementModel(name: String) : HTMLAbstractUIElement(name), HTMLIFrameElement {


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

    fun getContentDocument(): Document? {
        TODO("Not yet implemented")
    }

    override fun handleAttributeChanged(name: String, oldValue: String?, newValue: String?) {
        super.handleAttributeChanged(name, oldValue, newValue)
        if ("src" == name) {
            // todo
        }
    }

    override fun handleDocumentAttachmentChanged() {
        super.handleDocumentAttachmentChanged()
        if (isAttachedToDocument) {
            if (hasAttribute("onload")) {

            }
        }
    }


    private fun loadURLIntoFrame(value: String?) {

        try {
            val fullURL = if (value == null) null else this.getFullURL(value)
            if (fullURL != null) {
                if (userAgentContext!!.isRequestPermitted(
                        UserAgentContext.Request(
                            fullURL,
                            RequestKind.Frame
                        )
                    )
                ) {

                    // frame.loadURL(fullURL);
                    // ^^ Using window.open is better because it fires the various events correctly.
                    //this.contentWindow!!.open(fullURL.toExternalForm(), "iframe", "", true)
                } else {
                    println("Request not permitted: " + fullURL)

                }
            } else {
                this.warn("Can't load URL: " + value)
                // TODO: Plug: marking as load=true because we are not handling javascript URIs currently.
                //       javascript URI is being used in some of the web-platform-tests.

            }
        } catch (mfu: MalformedURLException) {
            this.warn("loadURLIntoFrame(): Unable to navigate to src.", mfu)

        }

    }

    override fun createRenderState(prevRenderState: RenderState?): RenderState {
        return IFrameRenderState(prevRenderState, this)
    }

}
