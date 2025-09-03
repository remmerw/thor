package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLFormElement
import org.w3c.dom.html.HTMLOptionElement

class HTMLOptionElementModel(name: String) : HTMLElementModel(name), HTMLOptionElement {


    override fun getDefaultSelected(): Boolean {
        return this.getAttributeAsBoolean("selected")
    }

    override fun setDefaultSelected(defaultSelected: Boolean) {
        this.setAttribute("selected", if (defaultSelected) "selected" else null)
    }

    override fun getText(): String? {
        TODO("Not yet implemented")
    }

    override fun getIndex(): Int {
        TODO("Not yet implemented")
    }

    override fun getDisabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setDisabled(p0: Boolean) {
        TODO("Not yet implemented")
    }


    override fun getForm(): HTMLFormElement? {
        return this.form
    }


    override fun getLabel(): String? {
        return this.getAttribute("label")
    }

    override fun setLabel(label: String?) {
        this.setAttribute("label", label)
    }

    override fun getSelected(): Boolean {
        return this.selected
    }

    override fun setSelected(p0: Boolean) {
        TODO("Not yet implemented")
    }


    override fun getValue(): String? {
        return this.getAttribute("value")
    }

    override fun setValue(value: String?) {
        this.setAttribute("value", value)
    }


    override fun toString(): String {
        return "HTMLOptionElementImpl[text=" + this.text + ",selected=" + this.selected + "]"
    }
}
