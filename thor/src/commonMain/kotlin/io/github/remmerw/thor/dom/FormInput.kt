package io.github.remmerw.thor.dom

import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

/**
 * The `FormInput` class contains the state of an HTML form input
 * item.
 */
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

    /**
     * Constructs a `FormInput` with a file value.
     *
     * @param name  The name of the input.
     * @param value The value of the input.
     */
    constructor(name: String, value: File?) {
        this.name = name
        this.textValue = null
        this.fileValue = value
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

    @get:Deprecated(
        """The method is implemented only to provide some backward
      compatibility."""
    )
    val charset: String
        /**
         * Always returns UTF-8.
         *
         */
        get() = "UTF-8"

    @get:Throws(IOException::class)
    @get:Deprecated(
        """Call either {@link #getTextValue()} or {@link #getFileValue()}
      instead."""
    )
    val inputStream: InputStream?
        /**
         * Gets data as an input stream. The caller is responsible for closing the
         * stream.
         *
         */
        get() {
            if (this.isText) {
                return ByteArrayInputStream(this.textValue!!.toByteArray(StandardCharsets.UTF_8))
            } else if (this.isFile) {
                return FileInputStream(this.fileValue)
            } else {
                return null
            }
        }

    /**
     * Shows a string representation of the `FormInput` that may be
     * useful in debugging.
     *
     * @see .getTextValue
     */
    override fun toString(): String {
        return "FormInput[name=" + this.name + ",textValue=" + this.textValue + "]"
    }

    companion object {
        // private final InputStream inputStream;
        // private final String charset;
        val EMPTY_ARRAY: Array<FormInput?> = arrayOfNulls<FormInput>(0)
    }
}