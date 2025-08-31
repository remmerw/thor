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
 * Created on Jan 15, 2006
 */
package io.github.remmerw.thor.cobra.html.domimpl

import io.github.remmerw.thor.cobra.html.FormInput
import io.github.remmerw.thor.cobra.html.js.Event
import io.github.remmerw.thor.cobra.html.js.NotGetterSetter
import io.github.remmerw.thor.cobra.ua.ImageResponse
import org.mozilla.javascript.Function
import org.w3c.dom.html.HTMLFormElement
import java.io.File

abstract class HTMLBaseInputElement(name: String) : HTMLAbstractUIElement(name) {
    private val imageListeners = ArrayList<ImageListener>()
    private var inputContext: InputContext? = null
    protected var deferredValue: String? = null
    protected var deferredChecked: Boolean? = null
    protected var deferredReadonly: Boolean? = null
    protected var deferredDisabled: Boolean? = null
    var onload: Function? = null
        get() = this.getEventFunction(field, "onload")
    private var imageResponse: ImageResponse? = null
    private var imageSrc: String? = null


    fun  getInputContext() : InputContext?{
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
            ic.disabled = (defDisabled)
        }
        if (defReadonly != null) {
            ic.disabled = (defReadonly)
        }
        if (defChecked != null) {
            ic.disabled = (defChecked)
        }
    }


    fun getDefaultValue() :String? {
        return this.getAttribute("defaultValue")
    }
    fun setDefaultValue(defaultValue :String?){
        this.setAttribute("defaultValue", defaultValue)
    }



    fun getForm(): HTMLFormElement? {
            var parent = this.nodeParent
            while ((parent != null) && parent !is HTMLFormElement) {
                parent = parent.parentNode
            }
            return parent
        }

    fun submitForm(extraFormInputs: Array<FormInput>?) {
        val form = this.getForm() as HTMLFormElementImpl?
        if (form != null) {
            form.submit(extraFormInputs)
        }
    }

    fun resetForm() {
        val form = this.getForm()
        if (form != null) {
            form.reset()
        }
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
            return ic.disabled
        }
    }

    fun setDisabled(disabled: Boolean) {
        val ic = this.inputContext
        if (ic != null) {
            ic.disabled = (disabled)
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
                 val `val` = this.getAttribute("value")
                 return if (`val` == null) "" else `val`
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


    protected val fileValue: File?
        /*
             * (non-Javadoc)
             *
             * @see
             * org.xamjwg.html.domimpl.HTMLElementImpl#assignAttributeField(java.lang.
             * String, java.lang.String)
             */
        get() {
            val ic = this.inputContext
            if (ic != null) {
                return ic.fileValue
            } else {
                return null
            }
        }

    override fun blur() {
        val ic = this.inputContext
        if (ic != null) {
            ic.blur()
        }
    }

    override fun focus() {
        val ic = this.inputContext
        if (ic != null) {
            ic.focus()
        }
    }

    fun select() {
        val ic = this.inputContext
        if (ic != null) {
            ic.select()
        }
    }

    override fun handleAttributeChanged(name: String, oldValue: String?, newValue: String?) {
        super.handleAttributeChanged(name, oldValue, newValue)
        if ("value" == name) {
            this.setValue( newValue)
        } else if ("checked" == name) {
            this.setChecked(newValue != null)
        } else if ("disabled" == name) {
            this.setDisabled( newValue != null)
        } else if ("readonly" == name) {
            this.setReadOnly(newValue != null)
        } else if ("src" == name) {
            // TODO: Should check whether "type" == "image"
            this.loadImage(newValue)
        }
    }

    private fun loadImage(src: String?) {
        val document = this.document as HTMLDocumentImpl?
        if (document != null) {
            synchronized(this.imageListeners) {
                this.imageSrc = src
                this.imageResponse = null
            }
            if (src != null) {
                //document.loadImage(src, HTMLBaseInputElement.LocalImageListener(src))
            }
        }
    }

    /**
     * Adds a listener of image loading events. The listener gets called right
     * away if there's already an image.
     *
     * @param listener
     */
    fun addImageListener(listener: ImageListener) {
        val l = this.imageListeners
        val currentImageResponse: ImageResponse?
        synchronized(l) {
            currentImageResponse = this.imageResponse
            l.add(listener)
        }
        if (currentImageResponse!!.state != ImageResponse.State.loading) {
            // Call listener right away if there's already an
            // image; holding no locks.
            listener.imageLoaded(ImageEvent(this, currentImageResponse))
            // Should not call onload handler here. That's taken
            // care of otherwise.
        }
    }

    fun removeImageListener(listener: ImageListener) {
        val l = this.imageListeners
        synchronized(l) {
            l.remove(listener)
        }
    }

    open fun resetInput() {
        val ic = this.inputContext
        if (ic != null) {
            ic.resetInput()
        }
    }

    private fun dispatchEvent(expectedImgSrc: String, event: ImageEvent) {
        val l = this.imageListeners
        val listenerArray: Array<ImageListener?>?
        synchronized(l) {
            if (expectedImgSrc != this.imageSrc) {
                return
            }
            this.imageResponse = event.imageResponse
            // Get array of listeners while holding lock.
            listenerArray = l.toArray<ImageListener?>(ImageListener.Companion.EMPTY_ARRAY)
        }
        val llength = listenerArray!!.size
        for (i in 0..<llength) {
            // Inform listener, holding no lock.
            listenerArray[i]!!.imageLoaded(event)
        }

        // TODO: With this change, setOnLoad method should add a listener with dispatch mechanism. Best implemented in a parent class.
        dispatchEvent(Event("load", this))

        /*
    final Function onload = this.getOnload();
    if (onload != null) {
      // TODO: onload event object?
      Executor.executeFunction(HTMLBaseInputElement.this, onload, null);
    }*/
    }

    @NotGetterSetter
    fun setCustomValidity(message: String?) {
        // TODO Implement
        println("TODO: HTMLBaseInputElement.setCustomValidity() " + message)
    }

    inner class LocalImageListener(private val expectedImgSrc: String) : ImageListener {
        override fun imageLoaded(event: ImageEvent) {
            dispatchEvent(this.expectedImgSrc, event)
        }

        override fun imageAborted() {
            // Do nothing
        }
    }
}
