package io.github.remmerw.thor.dom

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.html.HTMLCollection
import org.w3c.dom.html.HTMLFormElement
import java.util.Locale

class HTMLFormElementModel : HTMLAbstractUIElement, HTMLFormElement {
    private var elements: HTMLCollection? = null


    constructor(name: String) : super(name)


    fun namedItem(name: String): Any? {
        try {
            // TODO: This could use document.namedItem.
            this.visit(object : NodeVisitor {
                override fun visit(node: Node) {
                    if (isInput(node)) {
                        if (name == (node as Element).getAttribute("name")) {
                            throw StopVisitorException(node)
                        }
                    }
                }
            })
        } catch (sve: StopVisitorException) {
            return sve.tag
        }
        return null
    }

    fun item(index: Int): Any? {
        try {
            this.visit(object : NodeVisitor {
                private var current = 0

                override fun visit(node: Node) {
                    if (isInput(node)) {
                        if (this.current == index) {
                            throw StopVisitorException(node)
                        }
                        this.current++
                    }
                }
            })
        } catch (sve: StopVisitorException) {
            return sve.tag
        }
        return null
    }

    override fun getElements(): HTMLCollection {
        var elements = this.elements
        if (elements == null) {
            elements = DescendantHTMLCollection(
                this,
                InputFilter(),
                this.treeLock,
                false
            )
            this.elements = elements
        }
        return elements
    }

    override fun getLength(): Int {
        return this.getElements().length
    }

    override fun getName(): String? {
        return this.getAttribute("name")
    }

    override fun setName(name: String?) {
        this.setAttribute("name", name)
    }

    override fun getAcceptCharset(): String? {
        return this.getAttribute("acceptCharset")
    }

    override fun setAcceptCharset(acceptCharset: String?) {
        this.setAttribute("acceptCharset", acceptCharset)
    }

    override fun getAction(): String? {
        return this.getAttribute("action")
    }

    override fun setAction(action: String?) {
        this.setAttribute("action", action)
    }

    override fun getEnctype(): String? {
        return this.getAttribute("enctype")
    }

    override fun setEnctype(enctype: String?) {
        this.setAttribute("enctype", enctype)
    }

    override fun getMethod(): String {
        var method = this.getAttribute("method")
        if (method == null) {
            method = "GET"
        }
        return method
    }

    override fun setMethod(method: String?) {
        this.setAttribute("method", method)
    }

    override fun getTarget(): String? {
        return this.getAttribute("target")
    }

    override fun setTarget(target: String?) {
        this.setAttribute("target", target)
    }

    override fun submit() {
        TODO("Not yet implemented")
    }


    override fun reset() {
        this.visit(object : NodeVisitor {
            override fun visit(node: Node) {
                if (node is HTMLBaseInputElement) {
                    node.resetInput()
                }
            }
        })
    }

    open inner class InputFilter : NodeFilter {
        /*
         * (non-Javadoc)
         *
         * @see org.xamjwg.html.domimpl.NodeFilter#accept(org.w3c.dom.Node)
         */
        override fun accept(node: Node): Boolean {
            return isInput(node)
        }
    }

    companion object {
        fun isInput(node: Node): Boolean {
            val name = node.nodeName.lowercase(Locale.getDefault())
            return name == "input" || name == "textarea" || name == "select"
        }
    }
}
