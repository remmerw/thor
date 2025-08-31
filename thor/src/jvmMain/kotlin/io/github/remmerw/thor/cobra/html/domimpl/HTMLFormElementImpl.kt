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
 * Created on Jan 14, 2006
 */
package io.github.remmerw.thor.cobra.html.domimpl

import io.github.remmerw.thor.cobra.html.FormInput
import io.github.remmerw.thor.cobra.html.js.Executor
import io.github.remmerw.thor.cobra.html.js.Window.JSSupplierTask
import org.mozilla.javascript.Function
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.html.HTMLCollection
import org.w3c.dom.html.HTMLFormElement
import java.net.MalformedURLException
import java.util.Collections
import java.util.Locale

class HTMLFormElementImpl : HTMLAbstractUIElement, HTMLFormElement {
    private var elements: HTMLCollection? = null
    var onsubmit: Function? = null
        get() = this.getEventFunction(field, "onsubmit")

    constructor(name: String) : super(name)

    constructor() : super("FORM")

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
            elements = DescendentHTMLCollection(
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
        this.submit(null)
    }

    /**
     * This method should be called when form submission is done by a submit
     * button.
     *
     * @param extraFormInputs Any additional form inputs that need to be submitted, e.g. the
     * submit button parameter.
     */
    fun submit(extraFormInputs: Array<FormInput>?) {
        val onsubmit = this.onsubmit
        if (onsubmit != null) {
            // TODO: onsubmit event object?
            // dispatchEvent(new Event("submit", this));
            val window = (document as HTMLDocumentImpl).window
            window.addJSTask(
                JSSupplierTask<Boolean?>(
                    0,
                    {
                        Executor.executeFunction(
                            this,
                            onsubmit,
                            null,
                            window.contextFactory
                        )
                    },
                    { result: Boolean? ->
                        if (result == true) {
                            submitFormImpl(extraFormInputs)
                        }
                    })
            )
        } else {
            submitFormImpl(extraFormInputs)
        }
    }

    private fun submitFormImpl(extraFormInputs: Array<FormInput>?) {
        val context = this.htmlRendererContext
        if (context != null) {
            val formInputs = ArrayList<FormInput?>()
            if (extraFormInputs != null) {
                Collections.addAll(formInputs, *extraFormInputs)
            }
            this.visit(object : NodeVisitor {
                override fun visit(node: Node) {
                    if (node is HTMLElementImpl) {
                        val fis = node.getFormInputs()
                        if (fis != null) {
                            for (fi in fis) {
                                checkNotNull(fi.name) { "Form input does not have a name: " + node }
                                formInputs.add(fi)
                            }
                        }
                    }
                }
            })
            val fia = formInputs.toArray<FormInput?>(FormInput.EMPTY_ARRAY)
            var href = this.action
            if (href == null) {
                href = this.getBaseURI()
            }
            try {
                val url = this.getFullURL(href!!)
                context.submitForm(this.getMethod(), url, this.target, this.enctype, fia)
            } catch (mfu: MalformedURLException) {
                this.warn("submit()", mfu)
            }
        }
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
