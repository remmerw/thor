package io.github.remmerw.thor.style

import java.util.Locale

class Font(
    fontFamily: String?, fontStyle: String?, fontVariant: String?, fontWeight: String?,
    val fontSize: Float,  val superscript: Int?
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
        val ors: Font
        try {
            ors = other as Font
        } catch (cce: ClassCastException) {
            // Not expected
            return false
        }
        // Note that we use String.intern() for all string fields,
        // so we can do instance comparisons.
        return (this.fontSize == ors.fontSize) && (this.fontFamily === ors.fontFamily) && (this.fontStyle === ors.fontStyle)
                && (this.fontWeight === ors.fontWeight) && (this.fontVariant === ors.fontVariant) && (this.superscript === ors.superscript)

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