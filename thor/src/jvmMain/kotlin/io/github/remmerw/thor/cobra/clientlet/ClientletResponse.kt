/*
Copyright 1994-2006 The Lobo Project. All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer. Redistributions in binary form must
reproduce the above copyright notice, this list of conditions and the following
disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE LOBO PROJECT ``AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
EVENT SHALL THE FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.github.remmerw.thor.cobra.clientlet

import io.github.remmerw.thor.cobra.ua.RequestType
import java.io.IOException
import java.io.InputStream
import java.io.Serializable
import java.net.URL
import java.util.Date

/**
 * Represents a URL response such as an HTTP or file protocol response.
 */
interface ClientletResponse {
    /**
     * Gets the response URL. This may be different to the request URL in the case
     * of a redirect.
     */

    val responseURL: URL

    /**
     * Gets the request method for the response URL. This may be different to the
     * original request method in case of a redirect.
     */

    val lastRequestMethod: String?

    /**
     * Gets a response header.
     *
     * @param name The header name.
     */
    fun getHeader(name: String?): String?

    /**
     * Gets all values for a particular header.
     *
     * @param name The header name.
     */
    fun getHeaders(name: String?): Array<String?>?

    /**
     * Gets an iterator of response header names.
     */
    val headerNames: MutableIterator<String?>?

    @get:Throws(IOException::class)
    val inputStream: InputStream?

    /**
     * Gets the response content type. This can also contain a character encoding,
     * e.g. *text/html; charset=ISO-8859-1*.
     *
     * @see .getMimeType
     */
    val contentType: String?

    /**
     * Gets only the mime-type part of the content type, e.g. *text/html*.
     *
     * @see .getContentType
     */
    val mimeType: String?

    /**
     * A convenience method used to match parameters provided against the response
     * mime-type or the "file extension" of the response URL's file path. The file
     * extension is matched only when the mime type of the response is either
     * `application/octet-stream`, `content/unknown`, or not
     * provided.
     *
     * @param mimeType      A mime type, e.g. *application/x-acme*.
     * @param fileExtension A collection of file extensions, each starting with a dot, e.g.
     * *new String[] { ".acme", ".acm" }*.
     * @return True if the navigator considers there is a match.
     */
    fun matches(mimeType: String?, fileExtension: Array<String?>?): Boolean

    /**
     * Gets the content length of the reponse. This may be -1 if the content
     * length is not known.
     */
    val contentLength: Int

    /**
     * Returns true only if the response comes from a local cache.
     */
    val isFromCache: Boolean

    /**
     * Gets the charset specified with the content type. If no such charset has
     * been provided, the implementation may recommend a default.
     */
    val charset: String?

    /**
     * Determines whether a charset has been provided with the Content-Type
     * header.
     */
    val isCharsetProvided: Boolean

    @get:Throws(IOException::class)
    val responseCode: Int

    @get:Throws(IOException::class)
    val responseMessage: String?

    /**
     * Returns true only if the response is allowed to be cached.
     */
    val isCacheable: Boolean

    /**
     * Returns true only if the response does not result from a reload, forward or
     * back. Generally, this method indicates that a response is not related to an
     * entry already in the navigation history.
     */
    val isNewNavigationAction: Boolean

    /**
     * If available, gets an object previously persisted along with the cached
     * document.
     *
     * @param classLoader A class loader that can load an object of the type expected.
     * @see .setNewPersistentCachedObject
     */
    fun getPersistentCachedObject(classLoader: ClassLoader?): Any?

    /**
     * Caches the object provided in persistent memory and associates it with the
     * reponse URL, if caching is allowed.
     *
     * @param object A `Serializable` object.
     */
    fun setNewPersistentCachedObject(`object`: Serializable?)

    /**
     * If available, gets an object previously cached in main memory associated
     * with the response URL.
     *
     *
     * **Note**: Most callers should only use the persistent cached object if
     * [.isFromCache] returns true.
     *
     * @see .setNewTransientCachedObject
     */
    val transientCachedObject: Any?

    /**
     * Caches an object in main memory, provided caching is allowed and there's
     * enough memory to do so. The object is associated with the current response
     * URL.
     *
     * @param object     An object.
     * @param approxSize The approximate byte size the object occupies in memory. Note that
     * values less than the size of the response in bytes are assumed to
     * be in error.
     */
    fun setNewTransientCachedObject(`object`: Any?, approxSize: Int)

    /**
     * Gets the approximate size in bytes of the transient cached object
     * previously associated with the response.
     *
     *
     * **Note**: Most callers should only use the transient cached object if
     * [.isFromCache] returns true.
     */
    /* Commented because nothing is using it.
  public int getTransientCachedObjectSize();
  */
    /**
     * Gets the value of the "Date" header. This method returns `null`
     * if the header is not available.
     */
    val date: Date?

    /**
     * Gets the type of request.
     */
    val requestType: RequestType?
}
