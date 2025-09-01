package io.github.remmerw.thor

import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.StringTokenizer
import java.util.TimeZone
import java.util.logging.Logger

object Urls {
    val PATTERN_RFC1123: DateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US)
    private val logger: Logger = Logger.getLogger(Urls::class.java.name)

    init {
        val df = PATTERN_RFC1123
        df.timeZone = TimeZone.getTimeZone("GMT")
    }

    /**
     * Whether the URL refers to a resource in the local file system.
     */
    fun isLocal(url: URL): Boolean {
        if (isLocalFile(url)) {
            return true
        }
        val protocol = url.protocol
        if ("jar".equals(protocol, ignoreCase = true)) {
            val path = url.path
            val emIdx = path.lastIndexOf('!')
            val subUrlString = if (emIdx == -1) path else path.substring(0, emIdx)
            try {
                val subUrl = URL(subUrlString)
                return isLocal(subUrl)
            } catch (mfu: MalformedURLException) {
                return false
            }
        } else {
            return false
        }
    }

    /**
     * Whether the URL is a file in the local file system.
     */
    fun isLocalFile(url: URL): Boolean {
        val scheme = url.protocol
        return "file".equals(scheme, ignoreCase = true) && !hasHost(url)
    }

    fun hasHost(url: URL): Boolean {
        val host = url.host
        return (host != null) && "" != host
    }

    /**
     * Creates an absolute URL in a manner equivalent to major browsers.
     */
    @Throws(MalformedURLException::class)
    fun createURL(baseUrl: URL?, relativeUrl: String): URL {
        return URL(baseUrl, relativeUrl)
    }

    /**
     * Returns the time when the document should be considered expired. The time
     * will be zero if the document always needs to be revalidated. It will be
     * `null` if no expiration time is specified.
     */
    fun getExpiration(connection: URLConnection, baseTime: Long): Long {
        val cacheControl = connection.getHeaderField("Cache-Control")
        if (cacheControl != null) {
            val tok = StringTokenizer(cacheControl, ",")
            while (tok.hasMoreTokens()) {
                val token = tok.nextToken().trim { it <= ' ' }.lowercase(Locale.getDefault())
                if ("must-revalidate" == token) {
                    return 0L
                } else if (token.startsWith("max-age")) {
                    val eqIdx = token.indexOf('=')
                    if (eqIdx != -1) {
                        val value = token.substring(eqIdx + 1).trim { it <= ' ' }

                        try {
                            val seconds = value.toLong()
                            return (baseTime + (seconds * 1000L))
                        } catch (nfe: NumberFormatException) {
                            logger.warning("getExpiration(): Bad Cache-Control max-age value: " + value)
                            // ignore
                        }
                    }
                }
            }
        }
        val expires = connection.getHeaderField("Expires")
        if (expires != null) {
            try {
                synchronized(PATTERN_RFC1123) {
                    val expDate = PATTERN_RFC1123.parse(expires)
                    return expDate.time
                }
            } catch (pe: ParseException) {
                val seconds: Int
                try {
                    seconds = expires.toInt()
                    return (baseTime + (seconds * 1000L))
                } catch (nfe: NumberFormatException) {
                    logger.warning("getExpiration(): Bad Expires header value: " + expires)
                }
            }
        }

        // For issue #99
        // When there is no cache setting; assume a 60 second cache expiry time, for now.
        // return baseTime + (60 * 1000);
        // ^^ Update: Assume expiry time only if ETag header is present.
        //            We have not implemented the ETag header yet, but the presence of it is a good indicator that the response could be cached.
        val etag = connection.getHeaderField("Etag")
        return if (etag == null) 0 else baseTime + (60 * 1000)
    }

    fun getHeaders(connection: URLConnection): MutableList<NameValuePair?> {
        // Random access index recommended.
        val headers: MutableList<NameValuePair?> = ArrayList<NameValuePair?>()
        var n = 0
        while (true) {
            val value = connection.getHeaderField(n)
            if (value == null) {
                break
            }
            // Key may be null for n == 0.
            val key = connection.getHeaderFieldKey(n)
            if (key != null) {
                headers.add(NameValuePair(key, value))
            }
            n++
        }
        return headers
    }

    fun getCharset(connection: URLConnection): String {
        val contentType = connection.contentType
        if (contentType == null) {
            return getDefaultCharset(connection)
        }
        val tok = StringTokenizer(contentType, ";")
        if (tok.hasMoreTokens()) {
            tok.nextToken()
            while (tok.hasMoreTokens()) {
                val assignment = tok.nextToken().trim { it <= ' ' }
                val eqIdx = assignment.indexOf('=')
                if (eqIdx != -1) {
                    val varName = assignment.substring(0, eqIdx).trim { it <= ' ' }
                    if ("charset".equals(varName, ignoreCase = true)) {
                        val varValue = assignment.substring(eqIdx + 1)
                        return Strings.unquote(varValue.trim { it <= ' ' })
                    }
                }
            }
        }
        return getDefaultCharset(connection)
    }

    private fun getDefaultCharset(connection: URLConnection): String {
        val url = connection.getURL()
        if (isLocalFile(url)) {
            val charset = System.getProperty("file.encoding")
            return if (charset == null) "ISO-8859-1" else charset
        } else {
            return "ISO-8859-1"
        }
    }

    fun getNoRefForm(url: URL): String {
        val host = url.host
        val port = url.port
        val portText = if (port == -1) "" else ":" + port
        val userInfo = url.userInfo
        val userInfoText = if ((userInfo == null) || (userInfo.length == 0)) "" else userInfo + "@"
        val hostPort =
            if ((host == null) || (host.length == 0)) "" else "//" + userInfoText + host + portText
        return url.protocol + ":" + hostPort + url.file
    }

    /**
     * Comparison that does not consider Ref.
     *
     * @param url1
     * @param url2
     */
    fun sameNoRefURL(url1: URL, url2: URL): Boolean {
        return url1.host == url2.host && url1.protocol == url2.protocol
                && (url1.port == url2.port) && url1.file == url2.file
                && url1.userInfo == url2.userInfo
    }

    /**
     * Returns the port of a URL always. When the port is not explicitly set, it
     * returns the default port
     */
    fun getPort(url: URL): Int {
        val setPort = url.port
        return if (setPort == -1) url.defaultPort else setPort
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
            val c = url.get(i)
            if (c.code >= 32) {
                sb.append(c)
            }
        }
        return sb.toString()
    }
}