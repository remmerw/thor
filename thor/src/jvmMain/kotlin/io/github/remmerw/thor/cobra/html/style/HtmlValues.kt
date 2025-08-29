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
package io.github.remmerw.thor.cobra.html.style

import io.github.remmerw.thor.cobra.util.Urls
import io.github.remmerw.thor.cobra.util.gui.ColorFactory
import org.w3c.dom.css.CSS2Properties
import java.awt.Color
import java.awt.GraphicsEnvironment
import java.awt.Insets
import java.awt.Toolkit
import java.net.MalformedURLException
import java.net.URL
import java.util.Locale
import java.util.StringTokenizer
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.math.max

object HtmlValues {
    val SYSTEM_FONTS: MutableMap<String, FontInfo> = HashMap<String, FontInfo>()
    const val DEFAULT_FONT_SIZE: Float = 16.0f
    val DEFAULT_FONT_SIZE_INT: Int = DEFAULT_FONT_SIZE.toInt()

    // TODO: Make the minimum font size configurable
    const val MINIMUM_FONT_SIZE_PIXELS: Int = 14
    val DEFAULT_FONT_SIZE_BOX: Float = (DEFAULT_FONT_SIZE)
    const val DEFAULT_BORDER_WIDTH: Int = 2
    const val BORDER_STYLE_NONE: Int = 0
    const val BORDER_STYLE_HIDDEN: Int = 1
    const val BORDER_STYLE_DOTTED: Int = 2
    const val BORDER_STYLE_DASHED: Int = 3
    const val BORDER_STYLE_SOLID: Int = 4
    const val BORDER_STYLE_DOUBLE: Int = 5
    const val BORDER_STYLE_GROOVE: Int = 6
    const val BORDER_STYLE_RIDGE: Int = 7
    const val BORDER_STYLE_INSET: Int = 8
    const val BORDER_STYLE_OUTSET: Int = 9
    const val BORDER_THIN_SIZE: String = "1px"
    const val BORDER_MEDIUM_SIZE: String = "3px"
    const val BORDER_THICK_SIZE: String = "5px"
    private val logger: Logger = Logger.getLogger(HtmlValues::class.java.name)
    private val topUpdater: InsetUpdater = object : InsetUpdater {
        override fun updateValue(insets: HtmlInsets, value: Int) {
            insets.top = value
        }

        override fun updateType(insets: HtmlInsets, type: Int) {
            insets.topType = type
        }
    }
    private val leftUpdater: InsetUpdater = object : InsetUpdater {
        override fun updateValue(insets: HtmlInsets, value: Int) {
            insets.left = value
        }

        override fun updateType(insets: HtmlInsets, type: Int) {
            insets.leftType = type
        }
    }
    private val bottomUpdater: InsetUpdater = object : InsetUpdater {
        override fun updateValue(insets: HtmlInsets, value: Int) {
            insets.bottom = value
        }

        override fun updateType(insets: HtmlInsets, type: Int) {
            insets.bottomType = type
        }
    }
    private val rightUpdater: InsetUpdater = object : InsetUpdater {
        override fun updateValue(insets: HtmlInsets, value: Int) {
            insets.right = value
        }

        override fun updateType(insets: HtmlInsets, type: Int) {
            insets.rightType = type
        }
    }

    init {
        val systemFont = FontInfo()
        SYSTEM_FONTS.put("caption", systemFont)
        SYSTEM_FONTS.put("icon", systemFont)
        SYSTEM_FONTS.put("menu", systemFont)
        SYSTEM_FONTS.put("message-box", systemFont)
        SYSTEM_FONTS.put("small-caption", systemFont)
        SYSTEM_FONTS.put("status-bar", systemFont)
    }

    /* Not used by anyone
  private static int getBorderWidth(final String sizeText, final int borderStyle, final RenderState renderState) {
    if (borderStyle == BORDER_STYLE_NONE) {
      return 0;
    } else {
      if (sizeText == null || sizeText.length() == 0) {
        return DEFAULT_BORDER_WIDTH;
      }
      return HtmlValues.getPixelSize(sizeText, renderState, DEFAULT_BORDER_WIDTH);
    }
  }*/
    fun isBorderStyle(token: String): Boolean {
        val tokenTL = token.lowercase(Locale.getDefault())
        return tokenTL == "solid" || tokenTL == "dashed" || tokenTL == "dotted" || tokenTL == "double"
                || tokenTL == "none" || tokenTL == "hidden" || tokenTL == "groove" || tokenTL == "ridge"
                || tokenTL == "inset" || tokenTL == "outset"
    }

    fun getMarginInsets(cssProperties: CSS2Properties, renderState: RenderState?): HtmlInsets? {
        var insets: HtmlInsets? = null
        val topText = cssProperties.marginTop
        insets = updateInset(insets, topText, renderState, topUpdater)
        val leftText = cssProperties.marginLeft
        insets = updateInset(insets, leftText, renderState, leftUpdater)
        val bottomText = cssProperties.marginBottom
        insets = updateInset(insets, bottomText, renderState, bottomUpdater)
        val rightText = cssProperties.marginRight
        insets = updateInset(insets, rightText, renderState, rightUpdater)
        return insets
    }

    fun getPaddingInsets(cssProperties: CSS2Properties, renderState: RenderState?): HtmlInsets? {
        var insets: HtmlInsets? = null
        val topText = cssProperties.paddingTop
        insets = updateInset(insets, topText, renderState, topUpdater)
        val leftText = cssProperties.paddingLeft
        insets = updateInset(insets, leftText, renderState, leftUpdater)
        val bottomText = cssProperties.paddingBottom
        insets = updateInset(insets, bottomText, renderState, bottomUpdater)
        val rightText = cssProperties.paddingRight
        insets = updateInset(insets, rightText, renderState, rightUpdater)
        return insets
    }

    /**
     * Populates [BorderInfo].
     *
     * @param binfo         A BorderInfo with its styles already populated.
     * @param cssProperties The CSS properties object.
     * @param renderState   The current render state.
     */
    fun populateBorderInsets(
        binfo: BorderInfo,
        cssProperties: CSS2Properties,
        renderState: RenderState?
    ) {
        var insets: HtmlInsets? = null
        if (binfo.topStyle != BORDER_STYLE_NONE && binfo.topStyle != BORDER_STYLE_HIDDEN) {
            val topText = cssProperties.borderTopWidth
            insets = updateBorderInset(insets, topText, renderState, topUpdater, binfo.topStyle)
        }
        if (binfo.leftStyle != BORDER_STYLE_NONE && binfo.leftStyle != BORDER_STYLE_HIDDEN) {
            val leftText = cssProperties.borderLeftWidth
            insets = updateBorderInset(insets, leftText, renderState, leftUpdater, binfo.leftStyle)
        }
        if (binfo.bottomStyle != BORDER_STYLE_NONE && binfo.bottomStyle != BORDER_STYLE_HIDDEN) {
            val bottomText = cssProperties.borderBottomWidth
            insets =
                updateBorderInset(insets, bottomText, renderState, bottomUpdater, binfo.bottomStyle)
        }
        if (binfo.rightStyle != BORDER_STYLE_NONE && binfo.rightStyle != BORDER_STYLE_HIDDEN) {
            val rightText = cssProperties.borderRightWidth
            insets =
                updateBorderInset(insets, rightText, renderState, rightUpdater, binfo.rightStyle)
        }
        binfo.insets = insets
    }

    private fun updateInset(
        insets: HtmlInsets?,
        sizeText: String?,
        renderState: RenderState?,
        updater: InsetUpdater
    ): HtmlInsets? {
        var insets = insets
        var sizeText = sizeText
        if (sizeText == null) {
            return insets
        }
        sizeText = sizeText.trim { it <= ' ' }
        if (sizeText.length == 0) {
            return insets
        }
        if (insets == null) {
            insets = HtmlInsets()
        }
        if ("auto".equals(sizeText, ignoreCase = true)) {
            updater.updateType(insets, HtmlInsets.Companion.TYPE_AUTO)
        } else if (sizeText.endsWith("%")) {
            updater.updateType(insets, HtmlInsets.Companion.TYPE_PERCENT)
            try {
                updater.updateValue(insets, sizeText.substring(0, sizeText.length - 1).toInt())
            } catch (nfe: NumberFormatException) {
                updater.updateValue(insets, 0)
            }
        } else {
            updater.updateType(insets, HtmlInsets.Companion.TYPE_PIXELS)
            updater.updateValue(insets, getPixelSize(sizeText, renderState, 0))
        }
        return insets
    }

    private fun updateBorderInset(
        insets: HtmlInsets?,
        sizeText: String?,
        renderState: RenderState?,
        updater: InsetUpdater,
        borderStyle: Int
    ): HtmlInsets? {
        var sizeText = sizeText
        if (sizeText == null) {
            if (borderStyle != BORDER_STYLE_NONE) {
                sizeText = BORDER_MEDIUM_SIZE
            }
        }
        return updateInset(insets, sizeText, renderState, updater)
    }

    fun getInsets(insetsSpec: String, renderState: RenderState?, negativeOK: Boolean): Insets? {
        val insetsArray = IntArray(4)
        var size = 0
        val tok = StringTokenizer(insetsSpec)
        if (tok.hasMoreTokens()) {
            var token = tok.nextToken()
            insetsArray[0] = getPixelSize(token, renderState, 0)
            if (negativeOK || (insetsArray[0] >= 0)) {
                size = 1
                if (tok.hasMoreTokens()) {
                    token = tok.nextToken()
                    insetsArray[1] = getPixelSize(token, renderState, 0)
                    if (negativeOK || (insetsArray[1] >= 0)) {
                        size = 2
                        if (tok.hasMoreTokens()) {
                            token = tok.nextToken()
                            insetsArray[2] = getPixelSize(token, renderState, 0)
                            if (negativeOK || (insetsArray[2] >= 0)) {
                                size = 3
                                if (tok.hasMoreTokens()) {
                                    token = tok.nextToken()
                                    insetsArray[3] = getPixelSize(token, renderState, 0)
                                    size = 4
                                    if (negativeOK || (insetsArray[3] >= 0)) {
                                        // nop
                                    } else {
                                        insetsArray[3] = 0
                                    }
                                }
                            } else {
                                size = 4
                                insetsArray[2] = 0
                            }
                        }
                    } else {
                        size = 4
                        insetsArray[1] = 0
                    }
                }
            } else {
                size = 1
                insetsArray[0] = 0
            }
        }
        if (size == 4) {
            return Insets(insetsArray[0], insetsArray[3], insetsArray[2], insetsArray[1])
        } else if (size == 1) {
            val `val` = insetsArray[0]
            return Insets(`val`, `val`, `val`, `val`)
        } else if (size == 2) {
            return Insets(insetsArray[0], insetsArray[1], insetsArray[0], insetsArray[1])
        } else if (size == 3) {
            return Insets(insetsArray[0], insetsArray[1], insetsArray[2], insetsArray[1])
        } else {
            return null
        }
    }

    /**
     * Gets a number for 1 to 7.
     *
     * @param oldHtmlSpec A number from 1 to 7 or +1, etc.
     */
    fun getFontNumberOldStyle(oldHtmlSpec: String, renderState: RenderState): Int {
        var oldHtmlSpec = oldHtmlSpec
        oldHtmlSpec = oldHtmlSpec.trim { it <= ' ' }
        var tentative: Int
        try {
            if (oldHtmlSpec.startsWith("+")) {
                tentative = renderState.fontBase + oldHtmlSpec.substring(1).toInt()
            } else if (oldHtmlSpec.startsWith("-")) {
                tentative = renderState.fontBase + oldHtmlSpec.toInt()
            } else {
                tentative = oldHtmlSpec.toInt()
            }
            if (tentative < 1) {
                tentative = 1
            } else if (tentative > 7) {
                tentative = 7
            }
        } catch (nfe: NumberFormatException) {
            // ignore
            tentative = 3
        }
        return tentative
    }

    fun getFontSize(fontNumber: Int): Float {
        when (fontNumber) {
            1 -> return 10.0f
            2 -> return 11.0f
            3 -> return 13.0f
            4 -> return 16.0f
            5 -> return 21.0f
            6 -> return 29.0f
            7 -> return 42.0f
            else -> return 63.0f
        }
    }

    fun getFontSizeSpec(fontNumber: Int): String {
        when (fontNumber) {
            1 -> return "10px"
            2 -> return "11px"
            3 -> return "13px"
            4 -> return "16px"
            5 -> return "21px"
            6 -> return "29px"
            7 -> return "42px"
            else -> return "63px"
        }
    }

    fun getFontSize(spec: String, parentRenderState: RenderState?): Float {
        val specifiedFontSize = getFontSizeImpl(spec, parentRenderState)
        if (specifiedFontSize == 0f) {
            return specifiedFontSize
        }
        return max(MINIMUM_FONT_SIZE_PIXELS.toFloat(), specifiedFontSize)
    }

    private fun getFontSizeImpl(spec: String, parentRenderState: RenderState?): Float {
        val specTL = spec.lowercase(Locale.getDefault())
        if (specTL.endsWith("em")) {
            if (parentRenderState == null) {
                return DEFAULT_FONT_SIZE
            }
            val font = parentRenderState.font
            val pxText = specTL.substring(0, specTL.length - 2)
            val value: Double
            try {
                value = pxText.toDouble()
            } catch (nfe: NumberFormatException) {
                return DEFAULT_FONT_SIZE
            }
            return Math.round(font!!.getSize() * value).toInt().toFloat()
        } else if (specTL.endsWith("px") || specTL.endsWith("pt") || specTL.endsWith("cm") || specTL.endsWith(
                "pc"
            ) || specTL.endsWith("cm")
            || specTL.endsWith("mm") || specTL.endsWith("ex")
        ) {
            val pixelSize = getPixelSize(spec, parentRenderState, DEFAULT_FONT_SIZE_INT)

            /* Disabling for GH-185
      final int dpi = getDpi();
      Normally the factor below should be 72, but the font-size concept in HTML is handled differently.
      return (pixelSize * 96f) / dpi;
      */
            return pixelSize.toFloat()
        } else if (specTL.endsWith("%")) {
            val value = specTL.substring(0, specTL.length - 1)
            try {
                val valued = value.toDouble()
                val parentFontSize =
                    if (parentRenderState == null) 14.0 else parentRenderState.font!!.getSize()
                        .toDouble()
                return ((parentFontSize * valued) / 100.0).toFloat()
            } catch (nfe: NumberFormatException) {
                return DEFAULT_FONT_SIZE
            }
        } else if ("small" == specTL) {
            return 12.0f
        } else if ("medium" == specTL) {
            return 14.0f
        } else if ("large" == specTL) {
            return 20.0f
        } else if ("x-small" == specTL) {
            return 11.0f
        } else if ("xx-small" == specTL) {
            return 10.0f
        } else if ("x-large" == specTL) {
            return 26.0f
        } else if ("xx-large" == specTL) {
            return 40.0f
        } else if ("larger" == specTL) {
            val parentFontSize =
                if (parentRenderState == null) DEFAULT_FONT_SIZE_INT else parentRenderState.font!!
                    .getSize()
            return parentFontSize * 1.2f
        } else if ("smaller" == specTL) {
            val parentFontSize =
                if (parentRenderState == null) DEFAULT_FONT_SIZE_INT else parentRenderState.font!!
                    .getSize()
            return parentFontSize / 1.2f
        } else {
            return getPixelSize(spec, parentRenderState, DEFAULT_FONT_SIZE_INT).toFloat()
        }
    }

    fun getPixelSize(
        spec: String,
        renderState: RenderState?,
        errorValue: Int,
        availSize: Int
    ): Int {
        if (spec.endsWith("%")) {
            val perText = spec.substring(0, spec.length - 1)
            try {
                val `val` = perText.toDouble()
                return Math.round((availSize * `val`) / 100.0).toInt()
            } catch (nfe: NumberFormatException) {
                return errorValue
            }
        } else {
            return getPixelSize(spec, renderState, errorValue)
        }
    }

    fun getPixelSize(spec: String, renderState: RenderState?, errorValue: Int): Int {
        val lcSpec = spec.lowercase(Locale.getDefault())
        if (lcSpec.endsWith("px")) {
            val pxText = lcSpec.substring(0, lcSpec.length - 2)
            try {
                val `val` = pxText.toDouble()
                val dpi: Int = dpi
                val inches = `val` / 96
                return Math.round(dpi * inches).toInt()
            } catch (nfe: NumberFormatException) {
                return errorValue
            }
        } else if (lcSpec.endsWith("em") && (renderState != null)) {
            val f = renderState.font
            val valText = lcSpec.substring(0, lcSpec.length - 2)
            try {
                val `val` = valText.toDouble()
                // Get fontSize in points (1/72 of an inch).
                val fontSizePt = f!!.getSize2D()
                /* Formula: fontSize in CSS pixels = (fontSizePt / 72.0) * 96.0;
                 *          fontSize in device pixels = (font size in css pixels * dpi) / 96.0
                 *                                    = (fontSizePt / 72.0) * dpi
                 *
                 * Although, we should be using the factor 72 as per above, the actual factor used below is 96.
                 * This is because the font-height is calculated differently in CSS. TODO: Add a reference for this.
                 */
                /* Disabling for GH-185
        final int dpi = getDpi();
        final double fontSizeDevicePixels = (fontSizePt * dpi) / 96;
        return (int) Math.round(fontSizeDevicePixels * val);
        */
                return Math.round(fontSizePt * `val`).toInt()
            } catch (nfe: NumberFormatException) {
                return errorValue
            }
        } else if (lcSpec.endsWith("pt")) {
            val valText = lcSpec.substring(0, lcSpec.length - 2)
            try {
                val `val` = valText.toDouble()
                val dpi: Int = dpi
                val inches = `val` / 72
                return Math.round(dpi * inches).toInt()
            } catch (nfe: NumberFormatException) {
                return errorValue
            }
        } else if (lcSpec.endsWith("in")) {
            val valText = lcSpec.substring(0, lcSpec.length - 2)
            try {
                val inches = valText.toDouble()
                val dpi: Int = dpi
                return Math.round(dpi * inches).toInt()
            } catch (nfe: NumberFormatException) {
                return errorValue
            }
        } else if (lcSpec.endsWith("pc")) {
            val valText = lcSpec.substring(0, lcSpec.length - 2)
            try {
                val `val` = valText.toDouble()
                val dpi: Int = dpi
                val inches = `val` / 6
                return Math.round(dpi * inches).toInt()
            } catch (nfe: NumberFormatException) {
                return errorValue
            }
        } else if (lcSpec.endsWith("cm")) {
            val valText = lcSpec.substring(0, lcSpec.length - 2)
            try {
                val `val` = valText.toDouble()
                val dpi: Int = dpi
                val inches = `val` / 2.54
                return Math.round(dpi * inches).toInt()
            } catch (nfe: NumberFormatException) {
                return errorValue
            }
        } else if (lcSpec.endsWith("mm")) {
            val valText = lcSpec.substring(0, lcSpec.length - 2)
            try {
                val `val` = valText.toDouble()
                val dpi: Int = dpi
                val inches = `val` / 25.4
                return Math.round(dpi * inches).toInt()
            } catch (nfe: NumberFormatException) {
                return errorValue
            }
        } else if (lcSpec.endsWith("ex") && (renderState != null)) {
            val xHeight = renderState.fontXHeight
            val valText = lcSpec.substring(0, lcSpec.length - 2)
            try {
                val `val` = valText.toDouble()
                return Math.round(xHeight * `val`).toInt()
            } catch (nfe: NumberFormatException) {
                return errorValue
            }
        } else {
            val pxText = lcSpec
            try {
                return Math.round(pxText.toDouble()).toInt()
            } catch (nfe: NumberFormatException) {
                return errorValue
            }
        }
    }

    private val dpi: Int
        get() {
            if (GraphicsEnvironment.isHeadless()) {
                // TODO: Why is this 72? The CSS native resolution seems to be 96, so we could use that instead.
                return 72
            } else {
                val screenResolution =
                    Toolkit.getDefaultToolkit().screenResolution
                // TODO: Hack: converting to a multiple of 16. See GH-185
                return (screenResolution + 15) and -0x10
            }
        }

    fun scaleToDevicePixels(cssPixels: Double): Int {
        return Math.round(cssPixels * dpi / 96.0).toInt()
    }

    // TODO: move this functionality to the attribute -> CSS style functionality
    fun getOldSyntaxPixelSize(spec: String?, availSize: Int, errorValue: Int): Int {
        var spec = spec
        if (spec == null) {
            return errorValue
        }
        spec = spec.trim { it <= ' ' }
        try {
            if (spec.endsWith("%")) {
                return (availSize * spec.substring(0, spec.length - 1).toInt()) / 100
            }
            if (spec.endsWith("px")) {
                val `val` = spec.substring(0, spec.length - 2).toDouble()
                return scaleToDevicePixels(`val`)
            } else {
                return scaleToDevicePixels(spec.toInt().toDouble())
            }
        } catch (nfe: NumberFormatException) {
            return errorValue
        }
    }

    fun getURIFromStyleValue(fullURLStyleValue: String): URL? {
        val start = "url("
        if (!fullURLStyleValue.lowercase(Locale.getDefault()).startsWith(start)) {
            return null
        }
        val startIdx = start.length
        val closingIdx = fullURLStyleValue.lastIndexOf(')')
        if (closingIdx == -1) {
            return null
        }
        val quotedUri = fullURLStyleValue.substring(startIdx, closingIdx)
        val tentativeUri = unquoteAndUnescape(quotedUri)
        try {
            return Urls.createURL(null, tentativeUri)
        } catch (mfu: MalformedURLException) {
            logger.log(Level.WARNING, "Unable to create URL for URI=[" + tentativeUri + "].", mfu)
            return null
        }
    }

    /* This was called from BodyRenderState.getMarginInsets() which has now been commented out
  public static int getOldSyntaxPixelSizeSimple(String spec, final int errorValue) {
    if (spec == null) {
      return errorValue;
    }
    spec = spec.trim();
    try {
      return Integer.parseInt(spec);
    } catch (final NumberFormatException nfe) {
      return errorValue;
    }
  }*/
    fun unquoteAndUnescape(text: String): String {
        val result = StringBuffer()
        var index = 0
        val length = text.length
        var escape = false
        var single = false
        if (index < length) {
            val ch = text.get(index)
            when (ch) {
                '\'' -> single = true
                '"' -> {}
                '\\' -> escape = true
                else -> result.append(ch)
            }
            index++
        }
        OUTER@ while (index < length) {
            val ch = text.get(index)
            when (ch) {
                '\'' -> if (escape || !single) {
                    escape = false
                    result.append(ch)
                } else {
                    break@OUTER
                }

                '"' -> if (escape || single) {
                    escape = false
                    result.append(ch)
                } else {
                    break@OUTER
                }

                '\\' -> if (escape) {
                    escape = false
                    result.append(ch)
                } else {
                    escape = true
                }

                else -> {
                    if (escape) {
                        escape = false
                        result.append('\\')
                    }
                    result.append(ch)
                }
            }
            index++
        }
        return result.toString()
    }

    fun quoteAndEscape(text: String): String {
        val result = StringBuffer()
        result.append("'")
        var index = 0
        val length = text.length
        while (index < length) {
            val ch = text.get(index)
            when (ch) {
                '\'' -> result.append("\\'")
                '\\' -> result.append("\\\\")
                else -> result.append(ch)
            }
            index++
        }
        result.append("'")
        return result.toString()
    }

    fun isLength(token: String): Boolean {
        if (token.endsWith("px") || token.endsWith("pt") || token.endsWith("pc") || token.endsWith("cm") || token.endsWith(
                "mm"
            )
            || token.endsWith("ex") || token.endsWith("em")
        ) {
            return true
        }
        try {
            token.toDouble()
            return true
        } catch (nfe: NumberFormatException) {
            return false
        }
    }

    /*
  public static String getColorFromBackground(final String background) {
    final String[] backgroundParts = HtmlValues.splitCssValue(background);
    for (final String token : backgroundParts) {
      if (ColorFactory.getInstance().isColor(token)) {
        return token;
      }
    }
    return null;
  }
  */
    fun splitCssValue(cssValue: String): Array<String> {
        val tokens = ArrayList<String>(4)
        val len = cssValue.length
        var parenCount = 0
        var currentWord: StringBuffer? = null
        for (i in 0..<len) {
            val ch = cssValue.get(i)
            when (ch) {
                '(' -> {
                    parenCount++
                    if (currentWord == null) {
                        currentWord = StringBuffer()
                    }
                    currentWord.append(ch)
                }

                ')' -> {
                    parenCount--
                    if (currentWord == null) {
                        currentWord = StringBuffer()
                    }
                    currentWord.append(ch)
                }

                ' ', '\t', '\n', '\r' -> {
                    if (parenCount == 0) {
                        tokens.add(currentWord.toString())
                        currentWord = null
                        break
                    } else {
                        // Fall through - no break
                    }
                    if (currentWord == null) {
                        currentWord = StringBuffer()
                    }
                    currentWord.append(ch)
                }

                else -> {
                    if (currentWord == null) {
                        currentWord = StringBuffer()
                    }
                    currentWord.append(ch)
                }
            }
        }
        if (currentWord != null) {
            tokens.add(currentWord.toString())
        }
        return tokens.toTypedArray()
    }

    fun isUrl(token: String): Boolean {
        return token.lowercase(Locale.getDefault()).startsWith("url(")
    }

    fun getListStyleType(token: String): Int {
        val tokenTL = token.lowercase(Locale.getDefault())
        if ("none" == tokenTL) {
            return ListStyle.Companion.TYPE_NONE
        } else if ("disc" == tokenTL) {
            return ListStyle.Companion.TYPE_DISC
        } else if ("circle" == tokenTL) {
            return ListStyle.Companion.TYPE_CIRCLE
        } else if ("square" == tokenTL) {
            return ListStyle.Companion.TYPE_SQUARE
        } else if ("decimal" == tokenTL) {
            return ListStyle.Companion.TYPE_DECIMAL
        } else if ("lower-alpha" == tokenTL || "lower-latin" == tokenTL) {
            return ListStyle.Companion.TYPE_LOWER_ALPHA
        } else if ("upper-alpha" == tokenTL || "upper-latin" == tokenTL) {
            return ListStyle.Companion.TYPE_UPPER_ALPHA
        } else {
            // TODO: Many types missing here
            return ListStyle.Companion.TYPE_UNSET
        }
    }

    fun getListStyleTypeDeprecated(token: String): Int {
        val tokenTL = token.lowercase(Locale.getDefault())
        if ("disc" == tokenTL) {
            return ListStyle.Companion.TYPE_DISC
        } else if ("circle" == tokenTL) {
            return ListStyle.Companion.TYPE_CIRCLE
        } else if ("square" == tokenTL) {
            return ListStyle.Companion.TYPE_SQUARE
        } else if ("1" == tokenTL) {
            return ListStyle.Companion.TYPE_DECIMAL
        } else if ("a" == tokenTL) {
            return ListStyle.Companion.TYPE_LOWER_ALPHA
        } else if ("A" == tokenTL) {
            return ListStyle.Companion.TYPE_UPPER_ALPHA
        } else {
            // TODO: Missing i, I.
            return ListStyle.Companion.TYPE_UNSET
        }
    }

    fun getListStylePosition(token: String): Int {
        val tokenTL = token.lowercase(Locale.getDefault())
        if ("inside" == tokenTL) {
            return ListStyle.Companion.POSITION_INSIDE
        } else if ("outside" == tokenTL) {
            return ListStyle.Companion.POSITION_OUTSIDE
        } else {
            return ListStyle.Companion.POSITION_UNSET
        }
    }

    fun getListStyle(listStyleText: String): ListStyle {
        val listStyle = ListStyle()
        val tokens = splitCssValue(listStyleText)
        for (token in tokens) {
            val listStyleType = getListStyleType(token)
            if (listStyleType != ListStyle.Companion.TYPE_UNSET) {
                listStyle.type = listStyleType
            } else if (isUrl(token)) {
                // TODO: listStyle.image
            } else {
                val listStylePosition = getListStylePosition(token)
                if (listStylePosition != ListStyle.Companion.POSITION_UNSET) {
                    listStyle.position = listStylePosition
                }
            }
        }
        return listStyle
    }

    fun isFontStyle(token: String?): Boolean {
        return "italic" == token || "normal" == token || "oblique" == token
    }

    fun isFontVariant(token: String?): Boolean {
        return "small-caps" == token || "normal" == token
    }

    fun isFontWeight(token: String): Boolean {
        if ("bold" == token || "bolder" == token || "lighter" == token) {
            return true
        }
        try {
            val value = token.toInt()
            return ((value % 100) == 0) && (value >= 100) && (value <= 900)
        } catch (nfe: NumberFormatException) {
            return false
        }
    }

    fun getBorderInfo(properties: CSS2Properties, renderState: RenderState): BorderInfo {
        val binfo = BorderInfo()

        binfo.topStyle = getBorderStyle(properties.borderTopStyle)
        binfo.rightStyle = getBorderStyle(properties.borderRightStyle)
        binfo.bottomStyle = getBorderStyle(properties.borderBottomStyle)
        binfo.leftStyle = getBorderStyle(properties.borderLeftStyle)

        val cf = ColorFactory.instance!!

        binfo.topColor = getBorderColor(cf, properties.borderTopColor, renderState)
        binfo.rightColor = getBorderColor(cf, properties.borderRightColor, renderState)
        binfo.bottomColor = getBorderColor(cf, properties.borderBottomColor, renderState)
        binfo.leftColor = getBorderColor(cf, properties.borderLeftColor, renderState)

        populateBorderInsets(binfo, properties, renderState)

        return binfo
    }

    private fun getBorderColor(
        cf: ColorFactory,
        colorSpec: String?,
        renderState: RenderState
    ): Color? {
        if (colorSpec != null && (colorSpec.trim { it <= ' ' }.length != 0)) {
            return cf.getColor(colorSpec)
        } else {
            return renderState.color
        }
    }

    fun getBorderStyles(properties: CSS2Properties): Insets {
        val topStyle = getBorderStyle(properties.borderTopStyle)
        val rightStyle = getBorderStyle(properties.borderRightStyle)
        val bottomStyle = getBorderStyle(properties.borderBottomStyle)
        val leftStyle = getBorderStyle(properties.borderLeftStyle)
        return Insets(topStyle, leftStyle, bottomStyle, rightStyle)
    }

    private fun getBorderStyle(styleText: String?): Int {
        if ((styleText == null) || (styleText.length == 0)) {
            return BORDER_STYLE_NONE
        }
        val stl = styleText.lowercase(Locale.getDefault())
        if ("solid" == stl) {
            return BORDER_STYLE_SOLID
        } else if ("dashed" == stl) {
            return BORDER_STYLE_DASHED
        } else if ("dotted" == stl) {
            return BORDER_STYLE_DOTTED
        } else if ("none" == stl) {
            return BORDER_STYLE_NONE
        } else if ("hidden" == stl) {
            return BORDER_STYLE_HIDDEN
        } else if ("double" == stl) {
            return BORDER_STYLE_DOUBLE
        } else if ("groove" == stl) {
            return BORDER_STYLE_GROOVE
        } else if ("ridge" == stl) {
            return BORDER_STYLE_RIDGE
        } else if ("inset" == stl) {
            return BORDER_STYLE_INSET
        } else if ("outset" == stl) {
            return BORDER_STYLE_OUTSET
        } else {
            return BORDER_STYLE_NONE
        }
    }

    fun isBackgroundRepeat(repeat: String): Boolean {
        val repeatTL = repeat.lowercase(Locale.getDefault())
        return repeatTL.indexOf("repeat") != -1
    }

    fun isBackgroundPosition(token: String): Boolean {
        return isLength(token) || token.endsWith("%") || token.equals(
            "top",
            ignoreCase = true
        ) || token.equals("center", ignoreCase = true)
                || token.equals("bottom", ignoreCase = true) || token.equals(
            "left",
            ignoreCase = true
        ) || token.equals("right", ignoreCase = true)
    }

    private interface InsetUpdater {
        fun updateValue(insets: HtmlInsets, value: Int)

        fun updateType(insets: HtmlInsets, type: Int)
    }
}
