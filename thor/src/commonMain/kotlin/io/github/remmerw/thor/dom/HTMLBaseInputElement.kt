package io.github.remmerw.thor.dom


abstract class HTMLBaseInputElement(name: String) : HTMLElementModel(name) {

    fun getDefaultValue(): String? {
        return this.getAttribute("defaultValue")
    }

    fun setDefaultValue(defaultValue: String?) {
        this.setAttribute("defaultValue", defaultValue)
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
}
