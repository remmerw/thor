package io.github.remmerw.thor.cobra.html.domimpl

import io.github.remmerw.thor.cobra.html.BrowserFrame
import io.github.remmerw.thor.cobra.html.js.Event
import io.github.remmerw.thor.cobra.html.js.Executor
import io.github.remmerw.thor.cobra.html.js.Window
import io.github.remmerw.thor.cobra.html.js.Window.JSRunnableTask
import io.github.remmerw.thor.cobra.html.style.IFrameRenderState
import io.github.remmerw.thor.cobra.html.style.RenderState
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import io.github.remmerw.thor.cobra.ua.UserAgentContext.RequestKind
import org.mozilla.javascript.Function
import org.w3c.dom.Document
import org.w3c.dom.html.HTMLIFrameElement
import java.net.MalformedURLException

class HTMLIFrameElementImpl(name: String) : HTMLAbstractUIElement(name), HTMLIFrameElement,
    FrameNode {

    private var browserFrame: BrowserFrame? = null
    private var jobCreated = false
    private var onload: Function? = null



    private fun markJobDone(jobs: Int, loaded: Boolean) {
        synchronized(this) {
            (document as HTMLDocumentImpl).markJobsFinished(jobs, false)
            jobCreated = false
            if (loaded) {
                if (onload != null) {
                    // TODO: onload event object?
                    val window = (document as HTMLDocumentImpl).window
                    window.addJSTask(JSRunnableTask(0, "IFrame onload handler", Runnable {
                        Executor.executeFunction(
                            this@HTMLIFrameElementImpl,
                            onload!!,
                            Event("load", this@HTMLIFrameElementImpl),
                            window.contextFactory
                        )
                    }))
                }

                dispatchEvent(Event("load", this))
            }
        }
    }

    override fun getBrowserFrame(): BrowserFrame? {
        return browserFrame
    }


    override fun setBrowserFrame(frame: BrowserFrame?) {
        this.browserFrame = frame

    }

    override fun getAlign(): String? {
        return this.getAttribute("align")
    }

    override fun setAlign(align: String?) {
        this.setAttribute("align", align)
    }

    override fun getContentDocument(): Document? {
        // TODO: Domain-based security
        val frame = this.getBrowserFrame()
        if (frame == null) {
            // Not loaded yet
            return null
        }

        run {
            // TODO: Remove this very ugly hack.
            // This is required because the content document is sometimes not ready, even though the browser frame is.
            // The browser frame is created by the layout thread, but the iframe is loaded in the window's JS Scheduler thread.
            // See GH #140
            var count = 10
            while (count > 0 && frame.contentDocument() == null) {
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    throw RuntimeException("Error while waiting for iframe document")
                }
                count--
            }
        }

        return frame.contentDocument()
    }


    val contentWindow: Window?
        get() {
            val frame = this.getBrowserFrame()
            if (frame == null) {
                // Not loaded yet
                return null
            }
            return Window.getWindow(frame.htmlRendererContext())
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



    fun setOnload(onload: Function?) {
        this.onload = onload
    }

    private fun loadURLIntoFrame(value: String?) {
        val frame = this.getBrowserFrame()
        if (frame != null) {
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
                        frame.htmlRendererContext()!!.setJobFinishedHandler(object : Runnable {
                            override fun run() {
                                println("Iframes window's job over!")
                                markJobDone(1, true)
                            }
                        })
                        // frame.loadURL(fullURL);
                        // ^^ Using window.open is better because it fires the various events correctly.
                        this.contentWindow!!.open(fullURL.toExternalForm(), "iframe", "", true)
                    } else {
                        println("Request not permitted: " + fullURL)
                        markJobDone(1, false)
                    }
                } else {
                    this.warn("Can't load URL: " + value)
                    // TODO: Plug: marking as load=true because we are not handling javascript URIs currently.
                    //       javascript URI is being used in some of the web-platform-tests.
                    markJobDone(1, true)
                }
            } catch (mfu: MalformedURLException) {
                this.warn("loadURLIntoFrame(): Unable to navigate to src.", mfu)
                markJobDone(1, false)
            }
        }
    }

    override fun createRenderState(prevRenderState: RenderState?): RenderState {
        return IFrameRenderState(prevRenderState, this)
    }

}
