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
package io.github.remmerw.thor.style

import cz.vutbr.web.css.CSSException
import cz.vutbr.web.css.CSSFactory
import cz.vutbr.web.css.MediaSpec
import cz.vutbr.web.css.NetworkProcessor
import cz.vutbr.web.css.RuleFactory
import cz.vutbr.web.css.StyleSheet
import cz.vutbr.web.csskit.RuleFactoryImpl
import cz.vutbr.web.csskit.antlr4.CSSParserFactory
import io.github.remmerw.thor.Strings
import io.github.remmerw.thor.Urls
import io.github.remmerw.thor.dom.HTMLDocumentImpl
import io.github.remmerw.thor.dom.HTMLElementImpl
import io.github.remmerw.thor.ua.UserAgentContext
import io.github.remmerw.thor.ua.UserAgentContext.RequestKind
import org.w3c.css.sac.InputSource
import org.w3c.dom.Node
import org.w3c.dom.stylesheets.MediaList
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.Reader
import java.io.StringReader
import java.net.MalformedURLException
import java.net.URL
import java.util.StringTokenizer
import java.util.logging.Level
import java.util.logging.Logger

object CSSUtilities {
    private val logger: Logger = Logger.getLogger(CSSUtilities::class.java.name)
    private val rf: RuleFactory = RuleFactoryImpl.getInstance()

    fun preProcessCss(text: String): String {
        try {
            val reader = BufferedReader(StringReader(text))
            var line: String?
            val sb = StringBuffer()
            var pendingLine: String? = null
            // Only last line should be trimmed.
            while ((reader.readLine().also { line = it }) != null) {
                val tline = line!!.trim { it <= ' ' }
                if (tline.length != 0) {
                    if (pendingLine != null) {
                        sb.append(pendingLine)
                        sb.append("\r\n")
                        pendingLine = null
                    }
                    if (tline.startsWith("//")) {
                        pendingLine = line
                        continue
                    }
                    sb.append(line)
                    sb.append("\r\n")
                }
            }
            return sb.toString()
        } catch (ioe: IOException) {
            // not possible
            throw IllegalStateException(ioe.message)
        }
    }

    fun getCssInputSourceForStyleSheet(text: String, scriptURI: String?): InputSource {
        val reader: Reader = StringReader(text)
        val `is` = InputSource(reader)
        `is`.uri = scriptURI
        return `is`
    }

    fun jParseStyleSheet(
        ownerNode: Node?,
        baseURI: String,
        stylesheetStr: String?,
        bcontext: UserAgentContext
    ): StyleSheet? {
        return jParseCSS2(ownerNode, baseURI, stylesheetStr, bcontext)
    }

    @Throws(MalformedURLException::class)
    fun jParse(
        ownerNode: Node?, href: String, doc: HTMLDocumentImpl, baseUri: String,
        considerDoubleSlashComments: Boolean
    ): StyleSheet? {
        val bcontext = doc.userAgentContext()
        val request = bcontext.createHttpRequest()
        val baseURL = URL(baseUri)
        val cssURL = Urls.createURL(baseURL, href)
        val cssURI = cssURL.toExternalForm()
        // Perform a synchronous request

        try {
            request?.open("GET", cssURI, false)
            request?.send(null, UserAgentContext.Request(cssURL, RequestKind.CSS))
        } catch (thrown: IOException) {
            logger.log(Level.WARNING, "parse()", thrown)
        }
        emptyStyleSheet

        val status = request?.status
        if ((status != 200) && (status != 0)) {
            logger.warning("Unable to parse CSS. URI=[" + cssURI + "]. Response status was " + status + ".")
            return emptyStyleSheet
        }

        val text = request.responseText
        if ((text != null) && "" != text) {
            val processedText = if (considerDoubleSlashComments) preProcessCss(text) else text
            return jParseCSS2(ownerNode, cssURI, processedText, bcontext)
        } else {
            return emptyStyleSheet
        }
    }

    val emptyStyleSheet: StyleSheet
        get() {
            val css = rf.createStyleSheet()
            css.unlock()
            return css
        }

    private fun jParseCSS2(
        ownerNode: Node?, cssURI: String, processedText: String?,
        bcontext: UserAgentContext
    ): StyleSheet? {
        try {
            val base = URL(cssURI)
            CSSFactory.setAutoImportMedia(MediaSpec("screen"))
            return CSSParserFactory.getInstance().parse(
                processedText,
                SafeNetworkProcessor(bcontext),
                "utf-8",
                CSSParserFactory.SourceType.EMBEDDED,
                base
            )
        } catch (e: IOException) {
            logger.log(Level.SEVERE, "Unable to parse CSS. URI=[" + cssURI + "].", e)
            return emptyStyleSheet
        } catch (e: CSSException) {
            logger.log(Level.SEVERE, "Unable to parse CSS. URI=[" + cssURI + "].", e)
            return emptyStyleSheet
        }
    }

    fun jParseInlineStyle(
        style: String?, encoding: String?,
        element: HTMLElementImpl, inlinePriority: Boolean
    ): StyleSheet? {
        try {
            return CSSParserFactory.getInstance().parse(
                style,
                SafeNetworkProcessor(null),
                null,
                CSSParserFactory.SourceType.INLINE,
                element,
                inlinePriority,
                element.getDocumentURL()
            )
        } catch (e: IOException) {
            logger.log(Level.SEVERE, "Unable to parse CSS. CSS=[" + style + "].", e)
            return emptyStyleSheet
        } catch (e: CSSException) {
            logger.log(Level.SEVERE, "Unable to parse CSS. CSS=[" + style + "].", e)
            return emptyStyleSheet
        }
    }

    fun matchesMedia(mediaValues: String?, rcontext: UserAgentContext?): Boolean {
        if ((mediaValues == null) || (mediaValues.length == 0)) {
            return true
        }
        if (rcontext == null) {
            return false
        }
        val tok = StringTokenizer(mediaValues, ",")
        while (tok.hasMoreTokens()) {
            val token = tok.nextToken().trim { it <= ' ' }
            val mediaName = Strings.trimForAlphaNumDash(token)
            if (rcontext.isMedia(mediaName)) {
                return true
            }
        }
        return false
    }

    fun matchesMedia(mediaList: MediaList?, rcontext: UserAgentContext?): Boolean {
        if (mediaList == null) {
            return true
        }
        val length = mediaList.length
        if (length == 0) {
            return true
        }
        if (rcontext == null) {
            return false
        }
        for (i in 0..<length) {
            val mediaName = mediaList.item(i)
            if (rcontext.isMedia(mediaName)) {
                return true
            }
        }
        return false
    }

    class SafeNetworkProcessor(val bcontext: UserAgentContext?) : NetworkProcessor {
        @Throws(IOException::class)
        override fun fetch(url: URL): InputStream {
            //return AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>) () -> {

            val request = bcontext?.createHttpRequest()
            request?.open("GET", url, false)
            request?.send(null, UserAgentContext.Request(url, RequestKind.CSS))
            val responseBytes = request?.responseBytes
            if (responseBytes == null) {
                // This can happen when a request is denied by the request manager.
                throw IOException("Empty response")
            } else {
                return ByteArrayInputStream(responseBytes)
            }

            // });
        }
    }
}
