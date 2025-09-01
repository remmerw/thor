package io.github.remmerw.thor.ua

import java.net.URL

/**
 * Provides information about the user agent (browser) driving the parser and/or
 * renderer.
 *
 *
 * A simple implementation of this interface is provided in
 * [org.cobraparser.html.test.SimpleUserAgentContext].
 *
 * @see HtmlRendererContext.getUserAgentContext
 * @see DocumentBuilderImpl.DocumentBuilderImpl
 */
interface UserAgentContext {
    fun isRequestPermitted(request: Request?): Boolean

    /**
     * Creates an instance of [org.cobraparser.html.HttpRequest] which can
     * be used by the renderer to load images, scripts, external style sheets, and
     * implement the Javascript XMLHttpRequest class (AJAX).
     */
    fun createHttpRequest(): NetworkRequest?

    /**
     * Gets browser "code" name.
     */
    fun getAppCodeName(): String?

    /**
     * Gets browser application name.
     */
    fun getAppName(): String?

    /**
     * Gets browser application version.
     */
    fun getAppVersion(): String?

    /**
     * Gets browser application minor version.
     */
    fun getAppMinorVersion(): String?

    /**
     * Gets browser language code. See [ISO 639-1
 * codes](http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes).
     */
    fun getBrowserLanguage(): String?

    /**
     * Returns a boolean value indicating whether cookies are enabled in the user
     * agent. This value is used for reporting purposes only. TODO: Remove
     */
    fun isCookieEnabled(): Boolean

    /**
     * Returns a boolean value indicating whether scripting is enabled in the user
     * agent. If this value is `false`, the parser will not process
     * scripts and Javascript element attributes will have no effect. TODO: Remove
     */
    fun isScriptingEnabled(): Boolean

    /**
     * Returns a boolean value indicating whether remote (non-inline) CSS
     * documents should be loaded. TODO: Remove
     */
    fun isExternalCSSEnabled(): Boolean

    /**
     * Returns a boolean value indicating whether STYLE tags should be processed.
     * TODO: Remove
     */
    fun isInternalCSSEnabled(): Boolean

    /**
     * Gets the name of the user's operating system.
     */
    fun getPlatform(): String?

    /**
     * Should return the string used in the User-Agent header.
     */
    fun getUserAgent(): String?

    /**
     * Method used to implement Javascript `document.cookie` property.
     */
    fun getCookie(url: URL?): String?

    /**
     * Method used to implement `document.cookie` property.
     *
     * @param cookieSpec Specification of cookies, as they would appear in the Set-Cookie
     * header value of HTTP.
     */
    fun setCookie(url: URL?, cookieSpec: String?)


    /**
     * Gets the scripting optimization level, which is a value equivalent to
     * Rhino's optimization level.
     */
    fun getScriptingOptimizationLevel(): Int

    /**
     * Returns true if the current media matches the name provided.
     *
     * @param mediaName Media name, which may be `screen`, `tty`,
     * etc. (See [HTML Specification](http://www.w3.org/TR/REC-html40/types.html#type-media-descriptors)).
     */
    fun isMedia(mediaName: String?): Boolean

    fun getVendor(): String?

    fun getProduct(): String?

    enum class RequestKind(shortName: String) {
        Image("Img"), CSS("CSS"), Cookie("Cookie"), JavaScript("JS"), Frame("Frame"), XHR("XHR"), Referrer(
            "Referrer"
        );

        val shortName: String?

        init {
            this.shortName = shortName
        }

        companion object {
            private val VALUES: Array<RequestKind?> = entries.toTypedArray()
            fun forOrdinal(o: Int): RequestKind? {
                return VALUES[o]
            }

            fun numKinds(): Int {
                return entries.toTypedArray().size
            }
        }
    }

    class Request(url: URL?, kind: RequestKind) {
        val kind: RequestKind
        val url: URL?

        init {
            this.kind = kind
            this.url = url
        }

        override fun toString(): String {
            return kind.toString() + ": " + url
        }
    }
}