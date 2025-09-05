package io.github.remmerw.thor.core

import java.net.URL

object Urls {

    fun createURL(baseUrl: URL?, relativeUrl: String): URL {
        return URL(baseUrl, relativeUrl)
    }

    /**
     * Converts the given URL into a valid URL by encoding illegal characters.
     * Right now it is implemented like in IE7: only spaces are replaced with
     * "%20". (Firefox 3 also encodes other non-ASCII and some ASCII characters).
     *
     * @param url URL to convert
     * @return the encoded URL
     */
    fun encodeIllegalCharacters(url: String): String {
        return url.replace(" ", "%20")
    }

    /**
     * Converts the given URL into a valid URL by removing control characters
     * (ASCII code < 32).
     *
     * @param url URL to convert
     * @return the encoded URL
     */
    fun removeControlCharacters(url: String): String {
        val sb = StringBuilder(url.length)
        for (i in 0..<url.length) {
            val c = url[i]
            if (c.code >= 32) {
                sb.append(c)
            }
        }
        return sb.toString()
    }
}