package io.github.remmerw.thor.cobra.html.domimpl

import org.w3c.dom.html.HTMLFormElement
import org.w3c.dom.html.HTMLOptionElement
import org.w3c.dom.html.HTMLSelectElement

class HTMLOptionElementImpl(name: String?) : HTMLElementImpl(name, true), HTMLOptionElement {
    private var selected = false

    override fun getDefaultSelected(): Boolean {
        return this.getAttributeAsBoolean("selected")
    }

    override fun setDefaultSelected(defaultSelected: Boolean) {
        this.setAttribute("selected", if (defaultSelected) "selected" else null)
    }

    override fun getDisabled(): Boolean {
        return false
    }

    override fun setDisabled(disabled: Boolean) {
        // TODO Unsupported
    }

    override fun getForm(): HTMLFormElement? {
        return this.form
    }

    override fun getIndex(): Int {
        val parent: Any? = this.parentNode
        if (parent is HTMLSelectElement) {
            val options = parent.options as HTMLOptionsCollectionImpl
            return options.indexOf(this)
        } else {
            return -1
        }
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

    override fun setSelected(selected: Boolean) {
        val changed = selected != this.selected
        this.selected = selected
        // Changing the option state changes the selected index.
        val parent: Any? = this.parentNode
        if (parent is HTMLSelectElementImpl) {
            if (changed || (parent.getSelectedIndex() == -1)) {
                if (selected) {
                    parent.setSelectedIndexImpl(this.getIndex())
                } else {
                    val currentIndex = parent.getSelectedIndex()
                    if ((currentIndex != -1) && (currentIndex == this.getIndex())) {
                        parent.setSelectedIndexImpl(-1)
                    }
                }
            }
        }
    }

    override fun getText(): String {
        return this.getRawInnerText(false)
    }

    fun setText(value: String?) {
        this.setTextContent(value)
    }

    override fun getValue(): String? {
        return this.getAttribute("value")
    }

    override fun setValue(value: String?) {
        this.setAttribute("value", value)
    }

    fun setSelectedImpl(selected: Boolean) {
        this.selected = selected
    }

    override fun toString(): String {
        return "HTMLOptionElementImpl[text=" + this.text + ",selected=" + this.selected + "]"
    }
}
