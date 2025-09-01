package io.github.remmerw.thor.style

import java.io.Serializable

/**
 * @author J. H. S.
 */
class NameValuePair
/**
 * @param name
 * @param value
 */(
    /**
     * @return Returns the name.
     */
    val name: String?,
    /**
     * @return Returns the value.
     */
    val value: String?
) : Serializable {
    companion object {
        private const val serialVersionUID = 22574500600001010L
    }
}