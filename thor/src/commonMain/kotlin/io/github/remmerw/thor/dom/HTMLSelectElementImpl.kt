package io.github.remmerw.thor.dom

import org.w3c.dom.DOMException
import org.w3c.dom.html.HTMLCollection
import org.w3c.dom.html.HTMLElement
import org.w3c.dom.html.HTMLSelectElement

open class HTMLSelectElementImpl(name: String) : HTMLBaseInputElement(name), HTMLSelectElement {
    private var multipleState: Boolean? = null

    private var options: HTMLCollection? = null
    private var deferredSelectedIndex = -1


    @Throws(DOMException::class)
    override fun add(element: HTMLElement, before: HTMLElement?) {
        this.insertBefore(element, before)
    }

    override fun getLength(): Int {
        return this.options!!.length
    }


    override fun getMultiple(): Boolean {
        val m = this.multipleState
        if (m != null) {
            return m
        }
        return this.getAttributeAsBoolean("multiple")
    }

    override fun setMultiple(multiple: Boolean) {
        val prevMultiple = this.getMultiple()
        this.multipleState = multiple
        if (prevMultiple != multiple) {
            this.informLayoutInvalid()
        }
    }

    override fun getOptions(): HTMLCollection {
        synchronized(this) {
            if (this.options == null) {
                this.options = HTMLOptionsCollectionImpl(this)
            }
            return this.options!!
        }
    }


    override fun getSelectedIndex(): Int {
        val ic = this.getInputContext()
        if (ic != null) {
            return ic.selectedIndex
        } else {
            return this.deferredSelectedIndex
        }
    }

    override fun setSelectedIndex(selectedIndex: Int) {
        this.setSelectedIndexImpl(selectedIndex)
        val options = this.options
        val length = options!!.length
        for (i in 0..<length) {
            val option = options.item(i) as HTMLOptionElementImpl
            option.setSelectedImpl(i == selectedIndex)
        }
    }


    override fun getSize(): Int {
        val ic = this.getInputContext()
        if (ic != null) {
            return ic.visibleSize
        } else {
            return 0
        }
    }

    override fun setSize(size: Int) {
        val ic = this.getInputContext()
        if (ic != null) {
            ic.visibleSize = (size)
        }
    }


    override fun getType(): String {
        return if (this.getMultiple()) "select-multiple" else "select-one"
    }

    override fun remove(index: Int) {
        try {
            this.removeChild(this.options?.item(index))
        } catch (de: DOMException) {
            this.warn("remove(): Unable to remove option at index " + index + ".", de)
        }
    }

    fun setSelectedIndexImpl(selectedIndex: Int) {
        val ic = this.getInputContext()
        if (ic != null) {
            ic.selectedIndex = (selectedIndex)
        } else {
            this.deferredSelectedIndex = selectedIndex
        }
    }

    override fun getFormInputs(): Array<FormInput>? {
        // Needs to be overriden for forms to submit.
        val ic = this.getInputContext()
        var values = if (ic == null) null else ic.values
        if (values == null) {
            val value = this.value
            values = if (value == null) null else arrayOf<String?>(value)
            if (values == null) {
                return null
            }
        }
        val name = this.name
        if (name == null) {
            return null
        }
        val formInputs = ArrayList<FormInput?>()
        for (value in values) {
            formInputs.add(FormInput(name, value))
        }
        return formInputs.toArray<FormInput>(FormInput.EMPTY_ARRAY)
    }

    override fun resetInput() {
        val ic = this.getInputContext()
        if (ic != null) {
            ic.resetInput()
        }
    }

    override fun setInputContext(ic: InputContext) {
        super.setInputContext(ic)
        ic.selectedIndex = (this.deferredSelectedIndex)
    }
}
