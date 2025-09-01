package io.github.remmerw.thor.cobra.html.js

import io.github.remmerw.thor.cobra.html.js.Window.JSRunnableTask
import io.github.remmerw.thor.cobra.ua.NetworkRequest
import io.github.remmerw.thor.cobra.ua.NetworkRequestEvent
import io.github.remmerw.thor.cobra.ua.NetworkRequestListener
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import io.github.remmerw.thor.cobra.ua.UserAgentContext.RequestKind
import io.github.remmerw.thor.cobra.util.DOMExceptions
import io.github.remmerw.thor.cobra.util.Urls
import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
import org.mozilla.javascript.ScriptRuntime
import org.mozilla.javascript.Scriptable
import org.w3c.dom.DOMException
import org.w3c.dom.Document
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.Locale
import java.util.logging.Level
import java.util.logging.Logger

class XMLHttpRequest(
    private val pcontext: UserAgentContext,
    private val codeSource: URL,
    private val scope: Scriptable?, // TODO: This is a quick hack
    private val window: Window
)  {
    var scriptable: Scriptable? = null

     fun scriptable(): Scriptable? {
        return scriptable
    }
    private val request: NetworkRequest = pcontext.createHttpRequest()!!
    var onreadystatechange: Function? = null
        get() {
            synchronized(this) {
                return field
            }
        }
        set(value) {
            synchronized(this) {
                field = value
                if ((value != null) && !this.listenerAdded) {
                    this.request.addNetworkRequestListener(NetworkRequestListener { netEvent: NetworkRequestEvent? -> executeReadyStateChange() })
                    this.listenerAdded = true
                }
            }
        }
    private var listenerAdded = false
    private var onLoad: Function? = null

    fun abort() {
        request.abort()
    }

    @get:NotGetterSetter
    val allResponseHeaders: String?
        get() =// TODO: Need to also filter out based on CORS
            request.getAllResponseHeaders(excludedResponseHeadersLowerCase)

    val readyState: Int
        get() = request.readyState

    val responseBytes: ByteArray?
        get() = request.responseBytes

    fun getResponseHeader(headerName: String): String? {
        // TODO: Need to also filter out based on CORS
        if (excludedResponseHeadersLowerCase.contains(headerName.lowercase(Locale.getDefault()))) {
            return request.getResponseHeader(headerName)
        } else {
            return null
        }
    }

    val responseText: String?
        get() = request.responseText

    val responseXML: Document?
        get() = request.responseXML

    val status: Int
        get() = request.status

    val statusText: String?
        get() = request.statusText

    @Throws(MalformedURLException::class)
    private fun getFullURL(relativeUrl: String): URL {
        return Urls.createURL(this.codeSource, relativeUrl)
    }

    @Throws(IOException::class)
    fun open(
        method: String?,
        url: String,
        asyncFlag: Boolean,
        userName: String?,
        password: String?
    ) {
        val adjustedMethod: String? = checkAndAdjustMethod(method)
        try {
            request.open(adjustedMethod, this.getFullURL(url), asyncFlag, userName, password)
        } catch (mfe: MalformedURLException) {
            throw ScriptRuntime.typeError("url malformed")
        }
    }

    @Throws(IOException::class)
    fun open(method: String?, url: String, asyncFlag: Boolean, userName: String?) {
        val adjustedMethod: String? = checkAndAdjustMethod(method)
        request.open(adjustedMethod, this.getFullURL(url), asyncFlag, userName)
    }

    @Throws(IOException::class)
    fun open(method: String?, url: String, asyncFlag: Boolean) {
        val adjustedMethod: String? = checkAndAdjustMethod(method)
        request.open(adjustedMethod, this.getFullURL(url), asyncFlag)
    }

    @Throws(IOException::class)
    fun open(method: String?, url: String) {
        val adjustedMethod: String? = checkAndAdjustMethod(method)
        request.open(adjustedMethod, this.getFullURL(url))
    }

    // private boolean listenerAddedLoad;
    @Throws(IOException::class)
    fun send(content: String?) {
        val urlOpt = request.uRL!!
        if (urlOpt.isPresent) {
            val url = urlOpt.get()
            if (isSameOrigin(url, codeSource)) {
                // final URLPermission urlPermission = new URLPermission(url.toExternalForm());
                // final SocketPermission socketPermission = new SocketPermission(url.getHost() + ":" + Urls.getPort(url), "connect,resolve");
                // final StoreHostPermission storeHostPermission = StoreHostPermission.forURL(url);

                request.send(content, UserAgentContext.Request(url, RequestKind.XHR))

                // }
            } else {
                val msg = String.format(
                    "Failed to execute 'send' on 'XMLHttpRequest': Failed to load '%s'",
                    url.toExternalForm()
                )
                throw DOMExceptions.ExtendedError.NetworkError.createException(msg)
            }
        }
    }

    fun setOnload(value: Function?) {
        synchronized(this) {
            this.onLoad = value
            if ((value != null) && !this.listenerAdded) {
                this.request.addNetworkRequestListener(NetworkRequestListener { netEvent: NetworkRequestEvent? -> executeReadyStateChange() })
                this.listenerAdded = true
            }
        }
    }

    private fun executeReadyStateChange() {

    }

    // As per: http://www.w3.org/TR/XMLHttpRequest2/#the-setrequestheader-method
    fun setRequestHeader(header: String, value: String?) {
        val readyState = request.readyState
        if (readyState == NetworkRequest.STATE_LOADING) {
            if (isWellFormattedHeaderValue(header, value)) {
                if (!isProhibited(header)) {
                    request.addRequestedHeader(header, value)
                } else {
                    // TODO: Throw exception?
                    println("Prohibited header: " + header)
                }
            } else {
                throw DOMException(DOMException.SYNTAX_ERR, "header or value not well formatted")
            }
        } else {
            throw DOMException(
                DOMException.INVALID_STATE_ERR,
                "Can't set header when request state is: " + readyState
            )
        }
    }

    companion object {
        // TODO: See reference:
        // http://www.xulplanet.com/references/objref/XMLHttpRequest.html
        private val logger: Logger = Logger.getLogger(XMLHttpRequest::class.java.name)

        // excluded as per https://dvcs.w3.org/hg/xhr/raw-file/default/xhr-1/Overview.html
        private val excludedResponseHeadersLowerCase = mutableListOf<String?>(
            "set-cookie",
            "set-cookie2"
        )
        private val prohibitedMethods = arrayOf<String?>(
            "CONNECT", "TRACE", "TRACK"
        )
        private val upperCaseMethods = arrayOf<String?>(
            "DELETE", "GET", "HEAD", "OPTIONS", "POST", "PUT"
        )

        // This list comes from https://dvcs.w3.org/hg/xhr/raw-file/tip/Overview.html#the-setrequestheader()-method
        // It has been lower-cased for faster comparison
        private val prohibitedHeaders = arrayOf<String?>(
            "accept-charset",
            "accept-encoding",
            "access-control-request-headers",
            "access-control-request-method",
            "connection",
            "content-length",
            "cookie",
            "cookie2",
            "date",
            "dnt",
            "expect",
            "host",
            "keep-alive",
            "origin",
            "referer",
            "te",
            "trailer",
            "transfer-encoding",
            "upgrade",
            "user-agent",
            "via"
        )

        private fun checkAndAdjustMethod(method: String?): String? {
            for (p in prohibitedMethods) {
                if (p.equals(method, ignoreCase = true)) {
                    throw DOMExceptions.ExtendedError.SecurityError.createException()
                }
            }

            for (u in upperCaseMethods) {
                if (u.equals(method, ignoreCase = true)) {
                    return u
                }
            }

            return method
        }

        private fun isSameOrigin(url1: URL, url2: URL): Boolean {
            return url1.host == url2.host &&
                    (url1.port == (url2.port)) &&
                    url1.protocol == url2.protocol
        }

        private fun isProhibited(header: String): Boolean {
            val headerTL = header.lowercase(Locale.getDefault())
            for (prohibitedHeader in prohibitedHeaders) {
                if (prohibitedHeader == headerTL) {
                    return true
                }
            }
            val prohibitedPrefixMatch = headerTL.startsWith("proxy-") || headerTL.startsWith("sec-")
            return prohibitedPrefixMatch
        }

        private fun isWellFormattedHeaderValue(header: String?, value: String?): Boolean {
            // TODO Needs implementation as per https://dvcs.w3.org/hg/xhr/raw-file/tip/Overview.html#the-setrequestheader()-method
            return true
        }
    }
}
