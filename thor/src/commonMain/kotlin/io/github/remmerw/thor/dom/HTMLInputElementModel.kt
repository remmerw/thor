package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLFormElement
import org.w3c.dom.html.HTMLInputElement

class HTMLInputElementModel(name: String) : HTMLElementModel(name), HTMLInputElement {
    private var defaultChecked = false


    override fun getDefaultChecked(): Boolean {
        return this.defaultChecked
    }

    override fun setDefaultChecked(defaultChecked: Boolean) {
        this.defaultChecked = defaultChecked
    }

    override fun getForm(): HTMLFormElement? {
        TODO("Not yet implemented")
    }

    override fun blur() {
        TODO("Not yet implemented")
    }

    override fun focus() {
        TODO("Not yet implemented")
    }

    override fun select() {
        TODO("Not yet implemented")
    }

    override fun getChecked(): Boolean {
        return this.getAttributeAsBoolean("checked")
    }

    override fun setChecked(checked: Boolean) {
        this.setAttribute("checked", checked.toString())
    }

    override fun getDisabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setDisabled(p0: Boolean) {
        TODO("Not yet implemented")
    }


    override fun getMaxLength(): Int {
        TODO("Not yet implemented")
    }

    override fun setMaxLength(maxLength: Int) {
        TODO("Not yet implemented")
    }

    override fun getReadOnly(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setReadOnly(p0: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getSize(): String {
        TODO("Not yet implemented")
    }

    override fun setSize(size: String) {
        TODO("Not yet implemented")
    }

    override fun getSrc(): String? {
        return this.getAttribute("src")
    }

    override fun setSrc(src: String?) {
        this.setAttribute("src", src)
    }

    override fun getTabIndex(): Int {
        TODO("Not yet implemented")
    }

    override fun setTabIndex(p0: Int) {
        TODO("Not yet implemented")
    }


    override fun getType(): String? {
        return this.getAttribute("type")
    }


    override fun getUseMap(): String? {
        return this.getAttribute("usemap")
    }

    override fun setUseMap(useMap: String?) {
        this.setAttribute("usemap", useMap)
    }

    override fun getValue(): String? {
        TODO("Not yet implemented")
    }

    override fun setValue(p0: String?) {
        TODO("Not yet implemented")
    }


    override fun click() {
        TODO("Not yet implemented")
    }

    override fun getDefaultValue(): String? {
        return this.getAttribute("defaultValue")
    }

    override fun setDefaultValue(defaultValue: String?) {
        this.setAttribute("defaultValue", defaultValue)
    }

    override fun getAccept(): String? {
        return this.getAttribute("accept")
    }

    override fun setAccept(accept: String?) {
        this.setAttribute("accept", accept)
    }

    override fun getAccessKey(): String? {
        return this.getAttribute("accessKey")
    }

    override fun setAccessKey(accept: String?) {
        this.setAttribute("accessKey", accept)
    }

    override fun getAlign(): String? {
        return this.getAttribute("align")
    }

    override fun setAlign(align: String?) {
        this.setAttribute("align", align)
    }

    override fun getAlt(): String? {
        return this.getAttribute("alt")
    }

    override fun setAlt(alt: String?) {
        this.setAttribute("alt", alt)
    }

    override fun getName(): String? {
        return this.getAttribute("name") // TODO: Should this return value of "id"?
    }

    override fun setName(name: String?) {
        this.setAttribute("name", name) // TODO: Should this return value of "id"?
    }
}
