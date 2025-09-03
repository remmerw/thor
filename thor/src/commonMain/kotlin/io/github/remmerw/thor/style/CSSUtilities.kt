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
import io.github.remmerw.thor.dom.ElementImpl
import org.w3c.css.sac.InputSource
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.Reader
import java.io.StringReader
import java.net.URL
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

    fun parseStyleSheet(
        baseURI: String,
        stylesheetStr: String?
    ): StyleSheet? {
        return jParseCSS2(baseURI, stylesheetStr)
    }


    val emptyStyleSheet: StyleSheet
        get() {
            val css = rf.createStyleSheet()
            css.unlock()
            return css
        }

    private fun jParseCSS2(cssURI: String, processedText: String?): StyleSheet? {
        try {
            val base = URL(cssURI)
            CSSFactory.setAutoImportMedia(MediaSpec("screen"))
            return CSSParserFactory.getInstance().parse(
                processedText,
                SafeNetworkProcessor(),
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

    fun parseInlineStyle(
        style: String?, encoding: String?,
        element: ElementImpl, inlinePriority: Boolean
    ): StyleSheet? {
        try {
            return CSSParserFactory.getInstance().parse(
                style,
                SafeNetworkProcessor(),
                encoding,
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

    class SafeNetworkProcessor() : NetworkProcessor {
        @Throws(IOException::class)
        override fun fetch(url: URL): InputStream {
            println("TODO SafeNetworkProcessor fetch " + url)
            return ByteArrayInputStream(byteArrayOf())
        }
    }
}
