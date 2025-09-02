package io.github.remmerw.thor.dom

import org.w3c.dom.Document
import org.w3c.dom.html.HTMLFormElement
import org.w3c.dom.html.HTMLObjectElement

class HTMLObjectElementImpl(name: String) : HTMLAbstractUIElement(name), HTMLObjectElement {
    override fun getAlign(): String? {
        return this.getAttribute("align")
    }

    override fun setAlign(align: String?) {
        this.setAttribute("align", align)
    }

    var alt: String?
        get() = this.getAttribute("alt")
        set(alt) {
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

    override fun getName(): String? {
        return this.getAttribute("name")
    }

    override fun setName(name: String?) {
        this.setAttribute("name", name)
    }

    var `object`: String?
        get() = this.getAttribute("object")
        set(`object`) {
            this.setAttribute("object", `object`)
        }

    override fun getWidth(): String? {
        return this.getAttribute("width")
    }

    override fun setWidth(width: String?) {
        this.setAttribute("width", width)
    }

    override fun getBorder(): String? {
        return this.getAttribute("border")
    }

    override fun setBorder(border: String?) {
        this.setAttribute("border", border)
    }

    override fun getCodeType(): String? {
        return this.getAttribute("codetype")
    }

    override fun setCodeType(codeType: String?) {
        this.setAttribute("codetype", codeType)
    }

    fun getContentDocument(): Document? {
        return this.ownerDocument
    }

    override fun getData(): String? {
        return this.getAttribute("data")
    }

    /* public int getHspace() {
    try {
      return Integer.parseInt(this.getAttribute("hspace"));
    } catch (final Exception err) {
      return 0;
    }
  }*/
    override fun setData(data: String?) {
        this.setAttribute("data", data)
    }

    override fun getDeclare(): Boolean {
        return "declare".equals(this.getAttribute("declare"), ignoreCase = true)
    }

    override fun setDeclare(declare: Boolean) {
        this.setAttribute("declare", if (declare) "declare" else null)
    }

    override fun getForm(): HTMLFormElement? {
        return this.getAncestorForJavaClass(HTMLFormElement::class.java) as HTMLFormElement?
    }

    override fun getHspace(): String? {
        return this.getAttribute("hspace")
    }

    /* public int getVspace() {
    try {
      return Integer.parseInt(this.getAttribute("vspace"));
    } catch (final Exception err) {
      return 0;
    }
  }*/
    override fun setHspace(hspace: String?) {
        this.setAttribute("hspace", hspace)
    }

    override fun getStandby(): String? {
        return this.getAttribute("standby")
    }

    override fun setStandby(standby: String?) {
        this.setAttribute("standby", standby)
    }

    override fun getTabIndex(): Int {
        try {
            return this.getAttribute("tabindex")!!.toInt()
        } catch (err: Exception) {
            return 0
        }
    }

    override fun setTabIndex(tabIndex: Int) {
        this.setAttribute("tabindex", tabIndex.toString())
    }

    /* public void setHspace(final int hspace) {
    this.setAttribute("hspace", String.valueOf(hspace));
  }*/
    override fun getType(): String? {
        return this.getAttribute("type")
    }

    override fun setType(type: String?) {
        this.setAttribute("type", type)
    }

    override fun getUseMap(): String? {
        return this.getAttribute("usemap")
    }

    override fun setUseMap(useMap: String?) {
        this.setAttribute("usemap", useMap)
    }

    override fun getVspace(): String? {
        return this.getAttribute("vspace")
    }

    /* public void setVspace(final int vspace) {
    this.setAttribute("vspace", String.valueOf(vspace));
  }*/
    override fun setVspace(vspace: String?) {
        this.setAttribute("vspace", vspace)
    }
}
