package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLFrameSetElement

class HTMLFrameSetElementModel(name: String) : HTMLElementModel(name), HTMLFrameSetElement {


    override fun getCols(): String? {
        return this.getAttribute("cols")
    }

    override fun setCols(cols: String?) {
        this.setAttribute("cols", cols)
    }

    override fun getRows(): String? {
        return this.getAttribute("rows")
    }

    override fun setRows(rows: String?) {
        this.setAttribute("rows", rows)
    }
}
