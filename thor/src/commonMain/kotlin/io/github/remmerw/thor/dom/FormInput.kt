package io.github.remmerw.thor.dom

import java.io.File

class FormInput {
    /**
     * Gets the name of the input.
     */
    val name: String

    /**
     * Gets the text value of the form input. If the form input does not hold a
     * text value, this method should not be called.
     *
     * @see .isText
     */
    val textValue: String?

    /**
     * Gets the file value of the form input. If the form input does not hold a
     * file value, this method should not be called.
     *
     * @see .isFile
     */
    val fileValue: File?

    /**
     * Constructs a `FormInput` with a text value.
     *
     * @param name  The name of the input.
     * @param value The value of the input.
     */
    constructor(name: String, value: String?) : super() {
        this.name = name
        this.textValue = value
        this.fileValue = null
    }

    val isText: Boolean
        /**
         * Returns true if the form input holds a text value.
         */
        get() = this.textValue != null

    val isFile: Boolean
        /**
         * Returns true if the form input holds a file value.
         */
        get() = this.fileValue != null


    override fun toString(): String {
        return "FormInput[name=" + this.name + ",textValue=" + this.textValue + "]"
    }
}