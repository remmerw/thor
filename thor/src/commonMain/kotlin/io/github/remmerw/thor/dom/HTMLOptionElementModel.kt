package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLFormElement
import org.w3c.dom.html.HTMLOptionElement

class HTMLOptionElementModel(name: String) : HTMLElementModel(name), HTMLOptionElement {


    override fun getDefaultSelected(): Boolean {
        return this.getAttributeAsBoolean("selected")
    }

    override fun setDefaultSelected(defaultSelected: Boolean) {
        this.setAttribute("selected", defaultSelected.toString())
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
        return null
    }


    override fun getLabel(): String? {
        return this.getAttribute("label")
    }

    override fun setLabel(label: String?) {
        this.setAttribute("label", label)
    }

    override fun getSelected(): Boolean {
        return this.getAttributeAsBoolean("selected")
    }

    override fun setSelected(selected: Boolean) {
        this.setAttribute("selected", selected.toString())
    }


    override fun getValue(): String? {
        return this.getAttribute("value")
    }

    override fun setValue(value: String?) {
        this.setAttribute("value", value)
    }


}
