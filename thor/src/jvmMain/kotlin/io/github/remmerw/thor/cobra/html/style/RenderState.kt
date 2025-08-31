package io.github.remmerw.thor.cobra.html.style

import cz.vutbr.web.css.CSSProperty.VerticalAlign
import java.awt.Color
import java.awt.Cursor
import java.awt.Font
import java.awt.FontMetrics
import java.util.Optional

interface RenderState {
    fun getPosition(): Int

    val float: Int

    val clear: Int

    val visibility: Int

    val font: Font?

    val fontBase: Int

    fun getWordInfo(word: String): WordInfo

    val color: Color?

    val backgroundColor: Color?

    val textBackgroundColor: Color?

    val backgroundInfo: BackgroundInfo?

    val overlayColor: Color?

    val textTransform: Int

    val textDecorationMask: Int

    val fontMetrics: FontMetrics?

    val fontXHeight: Double

    val blankWidth: Int

    var isHighlight: Boolean

    val alignXPercent: Int

    val alignYPercent: Int

    fun getCount(counter: String?, nesting: Int): Int

    fun getDisplay(): Int

    fun resetCount(counter: String?, nesting: Int, value: Int)

    fun incrementCount(counter: String?, nesting: Int): Int

    fun getTextIndent(availWidth: Int): Int

    val textIndentText: String?

    val whiteSpace: Int

    val marginInsets: HtmlInsets?

    val paddingInsets: HtmlInsets?

    val overflowX: Int

    val overflowY: Int

    fun invalidate()

    val borderInfo: BorderInfo?

    val cursor: Optional<Cursor>?

    val left: String?

    val top: String?

    val right: String?

    val bottom: String?

    // TODO: This should return a more abstract type that can represent values like length and percentage
    fun getVerticalAlign(): VerticalAlign?

    companion object {
        const val MASK_TEXTDECORATION_UNDERLINE: Int = 1
        const val MASK_TEXTDECORATION_OVERLINE: Int = 2
        const val MASK_TEXTDECORATION_LINE_THROUGH: Int = 4
        const val MASK_TEXTDECORATION_BLINK: Int = 8

        const val TEXTTRANSFORM_NONE: Int = 0
        const val TEXTTRANSFORM_CAPITALIZE: Int = 1
        const val TEXTTRANSFORM_UPPERCASE: Int = 2
        const val TEXTTRANSFORM_LOWERCASE: Int = 4

        // TODO how to handle style cascading?
        // public static final int TEXTTRANSFORM_INHERIT = 8;
        const val DISPLAY_NONE: Int = 0
        const val DISPLAY_INLINE: Int = 1
        const val DISPLAY_BLOCK: Int = 2
        const val DISPLAY_LIST_ITEM: Int = 3
        const val DISPLAY_TABLE_ROW: Int = 4
        const val DISPLAY_TABLE_CELL: Int = 5
        const val DISPLAY_TABLE: Int = 6
        const val DISPLAY_INLINE_BLOCK: Int = 7
        const val DISPLAY_TABLE_ROW_GROUP: Int = 8
        const val DISPLAY_TABLE_HEADER_GROUP: Int = 9
        const val DISPLAY_TABLE_FOOTER_GROUP: Int = 10
        const val DISPLAY_TABLE_COLUMN: Int = 11
        const val DISPLAY_TABLE_COLUMN_GROUP: Int = 12
        const val DISPLAY_TABLE_CAPTION: Int = 13
        const val DISPLAY_INLINE_TABLE: Int = 14

        const val WS_NORMAL: Int = 0
        const val WS_PRE: Int = 1
        const val WS_NOWRAP: Int = 2

        const val VISIBILITY_VISIBLE: Int = 0
        const val VISIBILITY_HIDDEN: Int = 1
        const val VISIBILITY_COLLAPSE: Int = 2

        const val POSITION_STATIC: Int = 0
        const val POSITION_ABSOLUTE: Int = 1
        const val POSITION_RELATIVE: Int = 2
        const val POSITION_FIXED: Int = 3

        const val FLOAT_NONE: Int = 0
        const val FLOAT_LEFT: Int = 1
        const val FLOAT_RIGHT: Int = 2

        const val OVERFLOW_NONE: Int = 0
        const val OVERFLOW_SCROLL: Int = 1
        const val OVERFLOW_AUTO: Int = 2
        const val OVERFLOW_HIDDEN: Int = 3
        const val OVERFLOW_VISIBLE: Int = 4
    }
}

