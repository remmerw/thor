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
 * Created on Nov 13, 2005
 */
package io.github.remmerw.thor.ua

import org.w3c.dom.Document
import java.io.IOException
import java.net.URL
import java.util.Optional

/**
 * The `NetworkRequest` interface should be implemented to provide
 * network request capabilities.
 *
 *
 * It is used in a similar manner to `XMLHttpRequest` in Javascript
 * (AJAX). Normally, a listener will be added by calling
 * [.addReadyStateChangeListener], the method
 * [open][.open] will be called, and
 * finally, [.send] will be called to complete the request.
 *
 * @see UserAgentContext.createNetworkRequest
 */
interface NetworkRequest {
    /**
     * Gets the state of the request, a value between 0 and 4.
     *
     * @return A value corresponding to one of the STATE* constants in this class.
     */
    val readyState: Int

    /**
     * Gets the request response as text.
     */
    val responseText: String?

    /**
     * Gets the request response as an XML DOM.
     */
    val responseXML: Document?


    /**
     * Gets the request response bytes.
     */
    val responseBytes: ByteArray?

    /**
     * Gets the status of the response. Note that this can be 0 for file requests
     * in addition to 200 for successful HTTP requests.
     */
    val status: Int

    /**
     * Gets the status text of the request, e.g. "OK" for 200.
     */
    val statusText: String?

    /**
     * Aborts an ongoing request.
     */
    fun abort()

    /**
     * Gets a string with all the response headers.
     */
    fun getAllResponseHeaders(excludedHeadersLowerCase: MutableList<String?>?): String?

    /**
     * Gets a response header value.
     *
     * @param headerName The name of the header.
     */
    fun getResponseHeader(headerName: String?): String?

    /**
     * Starts an asynchronous request.
     *
     * @param method The request method.
     * @param url    The destination URL.
     */
    @Throws(IOException::class)
    fun open(method: String?, url: String?)

    /**
     * Opens an asynchronous request.
     *
     * @param method The request method.
     * @param url    The destination URL.
     */
    @Throws(IOException::class)
    fun open(method: String?, url: URL)

    /**
     * Opens an request.
     *
     * @param method    The request method.
     * @param url       The destination URL.
     * @param asyncFlag Whether the request is asynchronous.
     */
    @Throws(IOException::class)
    fun open(method: String?, url: URL, asyncFlag: Boolean)

    /**
     * Opens a request.
     *
     * @param method    The request method.
     * @param url       The destination URL.
     * @param asyncFlag Whether the request should be asynchronous.
     */
    @Throws(IOException::class)
    fun open(method: String?, url: String?, asyncFlag: Boolean)

    /**
     * Opens a request.
     *
     * @param method    The request method.
     * @param url       The destination URL.
     * @param asyncFlag Whether the request should be asynchronous.
     * @param userName  The HTTP authentication user name.
     */
    @Throws(IOException::class)
    fun open(method: String?, url: URL, asyncFlag: Boolean, userName: String?)

    /**
     * Opens a request.
     *
     * @param method    The request method.
     * @param url       The destination URL.
     * @param asyncFlag Whether the request should be asynchronous.
     * @param userName  The HTTP authentication user name.
     * @param password  The HTTP authentication password.
     */
    @Throws(IOException::class)
    fun open(method: String?, url: URL, asyncFlag: Boolean, userName: String?, password: String?)

    /**
     * Sends POST content if any.
     *
     * @param content POST content or `null` for GET requests.
     * @throws IOException
     */
    @Throws(IOException::class)
    fun send(content: String?, requestType: UserAgentContext.Request?)

    /**
     * Adds a listener of ReadyState changes. The listener should be invoked even
     * in the case of errors.
     *
     * @param listener An instanceof of [NetworkRequestListener].
     */
    fun addNetworkRequestListener(listener: NetworkRequestListener?)

    val uRL: Optional<URL>?

    val isAsnyc: Boolean

    fun addRequestedHeader(header: String?, value: String?)

    companion object {
        /**
         * The uninitialized request state.
         */
        const val STATE_UNINITIALIZED: Int = 0

        /**
         * The loading request state. The `open` method has been called,
         * but a response has not been received yet.
         */
        const val STATE_LOADING: Int = 1

        /**
         * The loaded request state. Headers and status are now available.
         */
        const val STATE_LOADED: Int = 2

        /**
         * The interactive request state. Downloading response.
         */
        const val STATE_INTERACTIVE: Int = 3

        /**
         * The complete request state. All operations are finished.
         */
        const val STATE_COMPLETE: Int = 4

        const val STATE_ABORTED: Int = 5
    }
}
