package io.github.remmerw.thor.dom

import org.w3c.dom.Node
import org.w3c.dom.html.HTMLCollection
import org.w3c.dom.html.HTMLFormElement
import java.util.Locale

class HTMLFormElementModel(name: String) : HTMLElementModel(name), HTMLFormElement {


    override fun getElements(): HTMLCollection {
        return DescendantHTMLCollection(
            this,
            InputFilter(),
            this.treeLock,
            false
        )
    }

    override fun getLength(): Int {
        return this.elements.length
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
        TODO("Not yet implemented")
    }

    open inner class InputFilter : NodeFilter {
        override fun accept(node: Node): Boolean {
            return isInput(node)
        }

        fun isInput(node: Node): Boolean {
            val name = node.nodeName.lowercase(Locale.getDefault())
            return name == "input" || name == "textarea" || name == "select"
        }
    }

}
