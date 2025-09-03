package io.github.remmerw.thor.dom

import cz.vutbr.web.css.NodeData
import cz.vutbr.web.css.TermFunction
import cz.vutbr.web.css.TermIdent
import cz.vutbr.web.css.TermList
import cz.vutbr.web.css.TermString
import cz.vutbr.web.css.TermURI
import io.github.remmerw.thor.style.ComputedCssProperties
import io.github.remmerw.thor.style.CssProperties

// TODO: Extend a common interface or a minimal class instead of HTMLElementImpl
class GeneratedElement(parent: HTMLElementModel, val nodeData: NodeData?, val content: TermList) :
    HTMLElementModel("") {

    private var currentStyle: CssProperties? = null

    init {
        setParentImpl(parent)
        setOwnerDocument(parent.ownerDocument)
    }

    override fun evalCssProperties(): CssProperties {
        synchronized(this) {
            if (currentStyle != null) {
                return currentStyle!!
            }
            currentStyle = ComputedCssProperties(this, nodeData, true)
            return currentStyle!!
        }
    }

    // todo where was it invoked
    fun getChildrenArray(): Array<NodeImpl?> {
        val nodeList = ArrayList<NodeImpl?>()
        for (c in content) {
            if (c is TermIdent) {
                // TODO
            } else if (c is TermString) {
                val value = c.value
                val txt = ownerDocument!!.createTextNode(value)
                nodeList.add(txt as NodeImpl?)
            } else if (c is TermURI) {
                // TODO
            } else if (c is TermFunction) {
                if (c.functionName == "attr") {
                    val params = c.value
                    if (params.size > 0) {
                        val `val` =
                            (nodeParent as ElementImpl).getAttribute(params.get(0).toString())
                        if (`val` != null) {
                            val txt = ownerDocument!!.createTextNode(`val`)
                            nodeList.add(txt as NodeImpl?)
                        }
                    }
                } else {
                    // TODO
                }
            }
        }
        return nodeList.toTypedArray<NodeImpl?>()
    }
}
