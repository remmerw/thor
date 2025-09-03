package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLFormElement
import java.io.File

abstract class HTMLBaseInputElement(name: String) : HTMLAbstractUIElement(name) {

    private var inputContext: InputContext? = null
    protected var deferredValue: String? = null
    protected var deferredChecked: Boolean? = null
    protected var deferredReadonly: Boolean? = null
    protected var deferredDisabled: Boolean? = null


    fun getInputContext(): InputContext? {
        return inputContext
    }

    open fun setInputContext(ic: InputContext) {
        var dv: String? = null
        var defDisabled: Boolean? = null
        var defReadonly: Boolean? = null
        var defChecked: Boolean? = null
        synchronized(this) {
            this.inputContext = ic
            dv = this.deferredValue
            defDisabled = this.deferredDisabled
            defReadonly = this.deferredReadonly
            defChecked = this.deferredChecked
        }
        if (dv != null) {
            ic.value = (dv)
        }
        if (defDisabled != null) {
            ic.setDisabled(defDisabled)
        }
        if (defReadonly != null) {
            ic.setDisabled(defReadonly)
        }
        if (defChecked != null) {
            ic.setDisabled(defChecked)
        }
    }

    fun getDefaultValue(): String? {
        return this.getAttribute("defaultValue")
    }

    fun setDefaultValue(defaultValue: String?) {
        this.setAttribute("defaultValue", defaultValue)
    }


    fun getForm(): HTMLFormElement? {
        var parent = this.nodeParent
        while ((parent != null) && parent !is HTMLFormElement) {
            parent = parent.parentNode
        }
        return parent
    }


    fun getAccept(): String? {
        return this.getAttribute("accept")
    }

    fun setAccept(accept: String?) {
        this.setAttribute("accept", accept)
    }

    fun getAccessKey(): String? {
        return this.getAttribute("accessKey")
    }

    fun setAccessKey(accept: String?) {
        this.setAttribute("accessKey", accept)
    }

    fun getAlign(): String? {
        return this.getAttribute("align")
    }

    fun setAlign(align: String?) {
        this.setAttribute("align", align)
    }

    fun getAlt(): String? {
        return this.getAttribute("alt")
    }

    fun setAlt(alt: String?) {
        this.setAttribute("alt", alt)
    }

    fun getName(): String? {
        return this.getAttribute("name") // TODO: Should this return value of "id"?
    }

    fun setName(name: String?) {
        this.setAttribute("name", name) // TODO: Should this return value of "id"?
    }

    fun getDisabled(): Boolean {
        val ic = this.inputContext
        if (ic == null) {
            val db = this.deferredDisabled
            return db != null && db
        } else {
            return ic.isDisabled()
        }
    }

    fun setDisabled(disabled: Boolean) {
        val ic = this.inputContext
        if (ic != null) {
            ic.setDisabled(disabled)
        } else {
            this.deferredDisabled = disabled
        }
    }


    fun getReadOnly(): Boolean {
        val ic = this.inputContext
        if (ic == null) {
            val db = this.deferredReadonly
            return db != null && db
        } else {
            return ic.readOnly
        }
    }

    fun setReadOnly(readOnly: Boolean) {
        val ic = this.inputContext
        if (ic != null) {
            ic.readOnly = (readOnly)
        } else {
            this.deferredReadonly = readOnly
        }
    }

    open fun getChecked(): Boolean {
        val ic = this.inputContext
        if (ic == null) {
            val db = this.deferredChecked
            return db != null && db
        } else {
            return ic.checked
        }
    }

    open fun setChecked(checked: Boolean) {
        val ic = this.inputContext
        if (ic != null) {
            ic.checked = (checked)
        } else {
            this.deferredChecked = checked
        }
    }


    fun getTabIndex(): Int {
        val ic = this.inputContext
        return if (ic == null) 0 else ic.tabIndex
    }

    fun setTabIndex(tabIndex: Int) {
        val ic = this.inputContext
        if (ic != null) {
            ic.tabIndex = (tabIndex)
        }
    }

    fun getValue(): String? {
        val ic = this.inputContext
        if (ic != null) {
            // Note: Per HTML Spec, setValue does not set attribute.
            return ic.value
        } else {
            val dv = this.deferredValue
            if (dv != null) {
                return dv
            } else {
                val value = this.getAttribute("value")
                return if (value == null) "" else value
            }
        }
    }

    fun setValue(value: String?) {
        var ic: InputContext? = null
        synchronized(this) {
            ic = this.inputContext
            if (ic == null) {
                this.deferredValue = value
            }
        }
        if (ic != null) {
            ic.value = (value)
        }
    }


    fun fileValue(): File? {
        val ic = this.inputContext
        if (ic != null) {
            return ic.fileValue
        } else {
            return null
        }
    }

    fun select() {
        val ic = this.inputContext
        ic?.select()
    }


    open fun resetInput() {
        val ic = this.inputContext
        if (ic != null) {
            ic.resetInput()
        }
    }


    fun setCustomValidity(message: String?) {
        // TODO Implement
        println("TODO: HTMLBaseInputElement.setCustomValidity() " + message)
    }

}
