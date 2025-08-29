package io.github.remmerw.thor.cobra.html.domimpl

import cz.vutbr.web.css.NodeData
import cz.vutbr.web.css.TermFunction
import cz.vutbr.web.css.TermIdent
import cz.vutbr.web.css.TermList
import cz.vutbr.web.css.TermString
import cz.vutbr.web.css.TermURI
import io.github.remmerw.thor.cobra.html.style.ComputedJStyleProperties
import io.github.remmerw.thor.cobra.html.style.JStyleProperties

// TODO: Extend a common interface or a minimal class instead of HTMLElementImpl
class GeneratedElement(parent: HTMLElementImpl, nodeData: NodeData?, content: TermList) :
    HTMLElementImpl("") {
    private val nodeData: NodeData?
    private val content: TermList
    private var currentStyle: JStyleProperties? = null

    init {
        setParentImpl(parent)
        setOwnerDocument(parent.ownerDocument)
        this.nodeData = nodeData
        this.content = content
    }

    override fun getCurrentStyle(): JStyleProperties {
        synchronized(this) {
            if (currentStyle != null) {
                return currentStyle!!
            }
            currentStyle = ComputedJStyleProperties(this, nodeData, true)
            return currentStyle!!
        }
    }

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
                            (parentNode as ElementImpl).getAttribute(params.get(0).toString())
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
