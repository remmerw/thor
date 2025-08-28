/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Contact info: lobochief@users.sourceforge.net
 */
/*
 * Created on Nov 19, 2005
 */
package io.github.remmerw.thor.cobra.html.domimpl

import io.github.remmerw.thor.cobra.html.js.Executor
import io.github.remmerw.thor.cobra.html.parser.HtmlParser
import io.github.remmerw.thor.cobra.html.style.ImageRenderState
import io.github.remmerw.thor.cobra.html.style.RenderState
import io.github.remmerw.thor.cobra.ua.ImageResponse
import org.mozilla.javascript.Function
import org.w3c.dom.UserDataHandler
import org.w3c.dom.html.HTMLImageElement
import java.lang.Boolean
import kotlin.Any
import kotlin.Array
import kotlin.String
import kotlin.synchronized

class HTMLImageElementImpl : HTMLAbstractUIElement, HTMLImageElement {
    private val listeners = ArrayList<ImageListener?>(1)
    var onload: Function? = null
        get() = this.getEventFunction(field, "onload")
    private var imageResponse = ImageResponse()
    private var imageSrc: String? = null

    constructor() : super("IMG")

    constructor(name: String?) : super(name)

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

    /*
  public int getHeight() {
    final UINode r = this.uiNode;
    return r == null ? 0 : r.getBounds().height;
  }

  public void setHeight(final int height) {
    this.setAttribute("height", String.valueOf(height));
  }

  public int getHspace() {
    return this.getAttributeAsInt("hspace", 0);
  }

  public void setHspace(final int hspace) {
    this.setAttribute("hspace", String.valueOf("hspace"));
  } */
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

    override fun getHeight(): String {
        val r = this.uiNode
        val height = if (r == null) 0 else r.getBounds().height
        return height.toString()
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

    /* public int getVspace() {
    return this.getAttributeAsInt("vspace", 0);
  }

  public void setVspace(final int vspace) {
    this.setAttribute("vspace", String.valueOf(vspace));
  } */
    override fun getSrc(): String? {
        return this.getAttribute("src")
    }

    /**
     * Sets the image URI and starts to load the image. Note that an
     * HtmlRendererContext should be available to the HTML document for images to
     * be loaded.
     */
    override fun setSrc(src: String?) {
        this.setAttribute("src", src)
    }

    /* public int getWidth() {
    final UINode r = this.uiNode;
    return r == null ? 0 : r.getBounds().width;
  }

  public void setWidth(final int width) {
    this.setAttribute("width", String.valueOf(width));
  }*/
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

    override fun getWidth(): String {
        val r = this.uiNode
        val width = if (r == null) 0 else r.getBounds().width
        return width.toString()
    }

    override fun setWidth(width: String?) {
        this.setAttribute("width", width)
    }

    override fun handleAttributeChanged(name: String?, oldValue: String?, newValue: String?) {
        super.handleAttributeChanged(name, oldValue, newValue)
        if ("src" == name) {
            (document as HTMLDocumentImpl).addJob(Runnable { loadImage(src) }, false)
        }
    }

    private fun loadImage(src: String?) {
        val document = this.document as HTMLDocumentImpl?
        if (document != null) {
            synchronized(this.listeners) {
                this.imageSrc = src
                this.imageResponse = ImageResponse()
            }
            if (src != null) {
                document.loadImage(src, HTMLImageElementImpl.LocalImageListener(src))
            } else {
                document.markJobsFinished(1, false)
            }
        }
    }

    override fun setUserData(key: String?, data: Any?, handler: UserDataHandler?): Any? {
        if (HtmlParser.MODIFYING_KEY == key && (data !== Boolean.TRUE)) {
            (document as HTMLDocumentImpl).addJob(Runnable { loadImage(src) }, false)
            // this.loadImage(getSrc());
        }
        return super.setUserData(key, data, handler)
    }

    /**
     * Adds a listener of image loading events. The listener gets called right
     * away if there's already an image.
     *
     * @param listener
     */
    fun addImageListener(listener: ImageListener) {
        val l = this.listeners
        val currentImageResponse: ImageResponse?
        synchronized(l) {
            currentImageResponse = this.imageResponse
            l.add(listener)
        }
        if (currentImageResponse!!.state != ImageResponse.State.loading) {
            // Call listener right away if there's already an
            // image; holding no locks.
            listener.imageLoaded(ImageEvent(this, currentImageResponse))
            // Should not call onload handler here. That's taken
            // care of otherwise.
        }
    }

    fun removeImageListener(listener: ImageListener?) {
        val l = this.listeners
        synchronized(l) {
            l.remove(listener)
        }
    }

    private fun dispatchEvent(expectedImgSrc: String, event: ImageEvent) {
        val l = this.listeners
        val listenerArray: Array<ImageListener?>?
        synchronized(l) {
            if (expectedImgSrc != this.imageSrc) {
                return
            }
            this.imageResponse = event.imageResponse
            // Get array of listeners while holding lock.
            listenerArray = l.toArray<ImageListener?>(ImageListener.Companion.EMPTY_ARRAY)
        }
        val llength = listenerArray!!.size
        for (i in 0..<llength) {
            // Inform listener, holding no lock.
            listenerArray[i]!!.imageLoaded(event)
        }
        val onload = this.onload
        if (onload != null) {
            // TODO: onload event object?
            val window = (document as HTMLDocumentImpl).getWindow()
            Executor.executeFunction(
                this@HTMLImageElementImpl,
                onload,
                null,
                window.getContextFactory()
            )
        }
    }

    override fun createRenderState(prevRenderState: RenderState?): RenderState {
        return ImageRenderState(prevRenderState, this)
    }

    override fun getLowSrc(): String? {
        // TODO
        return null
    }

    override fun setLowSrc(lowSrc: String?) {
        // TODO
    }

    private inner class LocalImageListener(private val expectedImgSrc: String) : ImageListener {
        override fun imageLoaded(event: ImageEvent) {
            dispatchEvent(this.expectedImgSrc, event)
            if (document is HTMLDocumentImpl) {
                document.markJobsFinished(1, false)
            }
        }

        override fun imageAborted() {
            if (document is HTMLDocumentImpl) {
                document.markJobsFinished(1, false)
            }
        }
    }
}
