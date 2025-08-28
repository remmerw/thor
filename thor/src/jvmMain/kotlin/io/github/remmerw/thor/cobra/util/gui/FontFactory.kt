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
 * Created on Apr 17, 2005
 */
package io.github.remmerw.thor.cobra.util.gui

import io.github.remmerw.thor.cobra.util.Strings
import java.awt.Font
import java.awt.FontFormatException
import java.awt.GraphicsEnvironment
import java.awt.font.TextAttribute
import java.io.IOException
import java.io.InputStream
import java.util.Locale
import java.util.StringTokenizer
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.text.StyleContext

/**
 * Note: Undocumented class?
 */
//import sun.font.FontManager;
/**
 * @author J. H. S.
 */
class FontFactory private constructor() {
    private val fontFamilies: MutableSet<String?> = HashSet<String?>(40)
    private val fontMap: MutableMap<FontKey?, Font?> = HashMap<FontKey?, Font?>(50)
    private val registeredFonts: MutableMap<String?, Font?> = HashMap<String?, Font?>(0)
    private var defaultFontName = "SansSerif"

    /**
     *
     */
    init {
        val liflag: Boolean = loggableFine
        val ffns = GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames
        val fontFamilies = this.fontFamilies
        synchronized(this) {
            for (ffn in ffns) {
                if (liflag) {
                    logger.fine("FontFactory(): family=" + ffn)
                }
                fontFamilies.add(ffn.lowercase(Locale.getDefault()))
            }
        }
    }

    /**
     * Registers a font family. It does not close the stream provided. Fonts
     * should be registered before the renderer has a chance to cache document
     * font specifications.
     *
     * @param fontName   The name of a font as it would appear in a font-family
     * specification.
     * @param fontFormat Should be [Font.TRUETYPE_FONT].
     */
    @Throws(FontFormatException::class, IOException::class)
    fun registerFont(fontName: String, fontFormat: Int, fontStream: InputStream) {
        val f = Font.createFont(fontFormat, fontStream)
        synchronized(this) {
            this.registeredFonts.put(fontName.lowercase(Locale.getDefault()), f)
        }
    }

    /**
     * Unregisters a font previously registered with
     * [.registerFont].
     *
     * @param fontName The font name to be removed.
     */
    fun unregisterFont(fontName: String) {
        synchronized(this) {
            this.registeredFonts.remove(fontName.lowercase(Locale.getDefault()))
        }
    }

    fun getFont(
        fontFamily: String?, fontStyle: String?, fontVariant: String?, fontWeight: String?,
        fontSize: Float, locales: MutableSet<Locale?>?,
        superscript: Int?
    ): Font? {
        val key =
            FontKey(fontFamily, fontStyle, fontVariant, fontWeight, fontSize, locales, superscript)
        synchronized(this) {
            var font = this.fontMap.get(key)
            if (font == null) {
                font = this.createFont(key)
                this.fontMap.put(key, font)
            }
            return font
        }
    }

    fun getDefaultFontName(): String {
        return defaultFontName
    }

    /**
     * Sets the default font name to be used when a name is unrecognized or when a
     * font is determined not to be capable of diplaying characters from a given
     * language. This should be the name of a font that can display unicode text
     * across all or most languages.
     *
     * @param defaultFontName The name of a font.
     */
    fun setDefaultFontName(defaultFontName: String) {
        requireNotNull(defaultFontName) { "defaultFontName cannot be null" }
        this.defaultFontName = defaultFontName
    }

    private fun createFont(key: FontKey): Font? {
        val font = createFont_Impl(key)
        return superscriptFont(font, key.superscript)
    }

    private fun createFont_Impl(key: FontKey): Font {
        val fontNames = key.fontFamily
        var matchingFace: String? = null
        val fontFamilies = this.fontFamilies
        val registeredFonts = this.registeredFonts
        var baseFont: Font? = null
        if (fontNames != null) {
            val tok = StringTokenizer(fontNames, ",")
            while (tok.hasMoreTokens()) {
                val face = Strings.unquoteSingle(tok.nextToken().trim { it <= ' ' })
                val faceTL = face.lowercase(Locale.getDefault())
                if (registeredFonts.containsKey(faceTL)) {
                    baseFont = registeredFonts.get(faceTL)
                    break
                } else if (fontFamilies.contains(faceTL)) {
                    matchingFace = faceTL
                    break
                } else if ("monospace" == faceTL) {
                    baseFont = Font.decode("monospaced")
                }
            }
        }
        var fontStyle = Font.PLAIN
        if ("italic".equals(key.fontStyle, ignoreCase = true)) {
            fontStyle = fontStyle or Font.ITALIC
        }
        if ("bold".equals(key.fontWeight, ignoreCase = true) || "bolder".equals(
                key.fontWeight,
                ignoreCase = true
            )
        ) {
            fontStyle = fontStyle or Font.BOLD
        }
        if (baseFont != null) {
            return baseFont.deriveFont(fontStyle, key.fontSize)
        } else if (matchingFace != null) {
            val font: Font = createFont(matchingFace, fontStyle, Math.round(key.fontSize))
            val locales = key.locales
            if (locales == null) {
                val locale = Locale.getDefault()
                if (font.canDisplayUpTo(locale.getDisplayLanguage(locale)) == -1) {
                    return font
                }
            } else {
                val i: MutableIterator<Locale> = locales.iterator() as MutableIterator<Locale>
                var allMatch = true
                while (i.hasNext()) {
                    val locale = i.next()
                    if (font.canDisplayUpTo(locale.getDisplayLanguage(locale)) != -1) {
                        allMatch = false
                        break
                    }
                }
                if (allMatch) {
                    return font
                }
            }
            // Otherwise, fall through.
        }
        // Last resort:
        return createFont(this.defaultFontName, fontStyle, Math.round(key.fontSize))
    }

    private class FontKey(
        fontFamily: String?, fontStyle: String?, fontVariant: String?, fontWeight: String?,
        val fontSize: Float, val locales: MutableSet<Locale?>?, val superscript: Int?
    ) {
        val fontFamily: String?
        val fontStyle: String?
        val fontVariant: String?
        val fontWeight: String?
        private var cachedHash = -1

        /**
         * @param fontFamily
         * @param fontStyle
         * @param fontVariant
         * @param fontWeight
         * @param fontSize
         */
        init {
            this.fontFamily = if (fontFamily == null) null else fontFamily.intern()
            this.fontStyle = if (fontStyle == null) null else fontStyle.intern()
            this.fontVariant = if (fontVariant == null) null else fontVariant.intern()
            this.fontWeight = if (fontWeight == null) null else fontWeight.intern()
        }

        override fun equals(other: Any?): Boolean {
            if (other === this) {
                // Quick check.
                return true
            }
            val ors: FontKey
            try {
                ors = other as FontKey
            } catch (cce: ClassCastException) {
                // Not expected
                return false
            }
            // Note that we use String.intern() for all string fields,
            // so we can do instance comparisons.
            return (this.fontSize == ors.fontSize) && (this.fontFamily === ors.fontFamily) && (this.fontStyle === ors.fontStyle)
                    && (this.fontWeight === ors.fontWeight) && (this.fontVariant === ors.fontVariant) && (this.superscript === ors.superscript)
                    && this.locales == ors.locales
        }

        override fun hashCode(): Int {
            var ch = this.cachedHash
            if (ch != -1) {
                // Object is immutable - caching is ok.
                return ch
            }
            var ff = this.fontFamily
            if (ff == null) {
                ff = ""
            }
            var fw = this.fontWeight
            if (fw == null) {
                fw = ""
            }
            var fs = this.fontStyle
            if (fs == null) {
                fs = ""
            }
            val ss = this.superscript
            ch =
                ff.hashCode() xor fw.hashCode() xor fs.hashCode() xor this.fontSize.toInt() xor (if (ss == null) 0 else ss)
            this.cachedHash = ch
            return ch
        }

        override fun toString(): String {
            return ("FontKey[family=" + this.fontFamily + ",size=" + this.fontSize + ",style=" + this.fontStyle + ",weight=" + this.fontWeight
                    + ",variant=" + this.fontVariant + ",superscript=" + this.superscript + "]")
        }
    }

    companion object {
        private val logger: Logger = Logger.getLogger(FontFactory::class.java.name)
        private val loggableFine: Boolean = logger.isLoggable(Level.FINE)
        val instance: FontFactory = FontFactory()

        fun superscriptFont(baseFont: Font, newSuperscript: Int?): Font? {
            if (newSuperscript == null) {
                return baseFont
            }
            var fontSuperScript = baseFont.attributes.get(TextAttribute.SUPERSCRIPT) as Int?
            if (fontSuperScript == null) {
                fontSuperScript = 0
            }
            if (fontSuperScript == newSuperscript) {
                return baseFont
            } else {
                val additionalAttributes: MutableMap<TextAttribute?, Int?> =
                    HashMap<TextAttribute?, Int?>()
                additionalAttributes.put(TextAttribute.SUPERSCRIPT, newSuperscript)
                return baseFont.deriveFont(additionalAttributes)
            }
        }

        private fun createFont(name: String?, style: Int, size: Int): Font {
            return StyleContext.getDefaultStyleContext().getFont(name, style, size)
            // Proprietary Sun API. Maybe shouldn't use it. Works well for Chinese.
            // return FontManager.getCompositeFontUIResource(new Font(name, style,
            // size));
        }
    }
}