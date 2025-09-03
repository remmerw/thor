package io.github.remmerw.thor.dom

import org.w3c.dom.DOMException
import org.w3c.dom.html.HTMLCollection
import org.w3c.dom.html.HTMLElement
import org.w3c.dom.html.HTMLFormElement
import org.w3c.dom.html.HTMLSelectElement

open class HTMLSelectElementModel(name: String) : HTMLElementModel(name), HTMLSelectElement {

    override fun blur() {
        TODO("Not yet implemented")
    }

    override fun focus() {
        TODO("Not yet implemented")
    }

    @Throws(DOMException::class)
    override fun add(element: HTMLElement, before: HTMLElement?) {
        this.insertBefore(element, before)
    }

    override fun getLength(): Int {
        return this.options.length
    }

    override fun getForm(): HTMLFormElement? {
        TODO("Not yet implemented")
    }


    override fun getMultiple(): Boolean {
        return this.getAttributeAsBoolean("multiple")
    }

    override fun setMultiple(p0: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getSize(): Int {
        TODO("Not yet implemented")
    }

    override fun setSize(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun getTabIndex(): Int {
        TODO("Not yet implemented")
    }

    override fun setTabIndex(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun getOptions(): HTMLCollection {
        synchronized(this) {
            return this.options
        }
    }

    override fun getDisabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setDisabled(p0: Boolean) {
        TODO("Not yet implemented")
    }


    override fun getType(): String {
        return if (this.multiple) "select-multiple" else "select-one"
    }

    override fun getSelectedIndex(): Int {
        TODO("Not yet implemented")
    }

    override fun setSelectedIndex(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun getValue(): String? {
        TODO("Not yet implemented")
    }

    override fun setValue(p0: String?) {
        TODO("Not yet implemented")
    }


    override fun getName(): String? {
        return this.getAttribute("name") // TODO: Should this return value of "id"?
    }

    override fun setName(name: String?) {
        this.setAttribute("name", name) // TODO: Should this return value of "id"?
    }

    override fun remove(index: Int) {
        try {
            this.removeChild(this.options.item(index))
        } catch (de: DOMException) {
            this.warn("remove(): Unable to remove option at index " + index + ".", de)
        }
    }

}
