package io.github.remmerw.thor.cobra.html.js

import io.github.remmerw.thor.cobra.html.domimpl.HTMLDocumentImpl
import io.github.remmerw.thor.cobra.js.AbstractScriptableDelegate
import java.net.MalformedURLException
import java.net.URL
import java.util.logging.Level
import java.util.logging.Logger

class Location internal constructor(private val window: Window) : AbstractScriptableDelegate() {
    var target: String? = null

    private val uRL: URL?
        get() {
            var url: URL?
            try {
                val document = this.window.documentNode
                url = if (document == null) null else URL(document.documentURI)
            } catch (mfu: MalformedURLException) {
                url = null
            }
            return url
        }

    val hash: String?
        get() {
            val url: URL? = this.uRL
            return if (url == null) null else url.ref
        }

    val host: String?
        get() {
            val url: URL? = this.uRL
            if (url == null) {
                return null
            }
            return url.host + (if (url.port == -1) "" else ":" + url.port)
        }

    val hostname: String?
        get() {
            val url: URL? = this.uRL
            if (url == null) {
                return null
            }
            return url.host
        }

    val pathname: String?
        get() {
            val url: URL? = this.uRL
            return if (url == null) null else url.path
        }

    val port: String?
        get() {
            val url: URL? = this.uRL
            if (url == null) {
                return null
            }
            val port = url.port
            return if (port == -1) null else port.toString()
        }

    val protocol: String?
        get() {
            val url: URL? = this.uRL
            if (url == null) {
                return null
            }
            return url.protocol + ":"
        }

    val search: String
        get() {
            val url: URL? = this.uRL
            val query = if (url == null) null else url.query
            // Javascript requires "?" in its search string.
            return if (query == null) "" else "?" + query
        }

    var href: String?
        get() {
            val document = this.window.documentNode
            return if (document == null) null else document.documentURI
        }
        set(uri) {
            val rcontext = this.window.htmlRendererContext
            if (rcontext != null) {
                try {
                    val url: URL?
                    val document = this.window.documentNode
                    if (document is HTMLDocumentImpl) {
                        url = document.getFullURL(uri!!)
                    } else {
                        url = URL(uri)
                    }
                    rcontext.navigate(url!!, this.target)
                } catch (mfu: MalformedURLException) {
                    logger.log(
                        Level.WARNING,
                        "setHref(): Malformed location: [" + uri + "].",
                        mfu
                    )
                }
            }
        }

    fun reload() {
        // TODO: This is not really reload.
        val document = this.window.documentNode
        if (document is HTMLDocumentImpl) {
            val rcontext = document.getHtmlRendererContext()
            if (rcontext != null) {
                rcontext.reload()
            } else {
                document.warn("reload(): No renderer context in Location's document.")
            }
        }
    }

    fun replace(href: String) {
        this.href = href
    }

    override fun toString(): String {
        // This needs to be href. Callers
        // rely on that.
        return this.href!!
    }

    companion object {
        private val logger: Logger = Logger.getLogger(Location::class.java.name)
    }
}
