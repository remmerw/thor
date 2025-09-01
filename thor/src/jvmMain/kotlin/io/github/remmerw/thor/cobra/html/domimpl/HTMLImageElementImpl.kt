
package io.github.remmerw.thor.cobra.html.domimpl

import io.github.remmerw.thor.cobra.html.js.Executor
import io.github.remmerw.thor.cobra.html.parser.HtmlParser
import io.github.remmerw.thor.cobra.html.style.ImageRenderState
import io.github.remmerw.thor.cobra.html.style.RenderState
import org.mozilla.javascript.Function
import org.w3c.dom.UserDataHandler
import org.w3c.dom.html.HTMLImageElement
import java.lang.Boolean
import kotlin.Any
import kotlin.Array
import kotlin.String
import kotlin.synchronized

class HTMLImageElementImpl : HTMLAbstractUIElement, HTMLImageElement {

    private var imageSrc: String? = null

    constructor(name: String) : super(name)

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
        val r = this.uINode
        val height = if (r == null) 0 else r.bounds()!!.height
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

    override fun getIsMap(): kotlin.Boolean {
        return this.getAttributeAsBoolean("isMap")
    }

    override fun setIsMap(isMap: kotlin.Boolean) {
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
        val r = this.uINode
        val width = if (r == null) 0 else r.bounds()!!.width
        return width.toString()
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

    private fun loadImage(src: String?) {
        val document = this.document as HTMLDocumentImpl?
        if (document != null) {

            if (src != null) {
                document.loadImage(src)
            } else {
                document.markJobsFinished(1, false)
            }
        }
    }

    override fun setUserData(key: String, data: Any?, handler: UserDataHandler?): Any? {
        if (HtmlParser.MODIFYING_KEY == key && (data != Boolean.TRUE)) {
            // todo
            // this.loadImage(getSrc());
        }
        return super.setUserData(key, data, handler)
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
}
