package io.github.remmerw.thor.cobra.html.style

import cz.vutbr.web.css.CSSProperty
import cz.vutbr.web.css.NodeData
import cz.vutbr.web.csskit.TermURIImpl
import io.github.remmerw.thor.cobra.js.ScriptableDelegate
import io.github.remmerw.thor.cobra.util.Urls
import org.mozilla.javascript.Scriptable
import org.w3c.dom.css.CSS2Properties
import java.net.MalformedURLException
import java.net.URL

abstract class JStyleProperties(
    private val context: CSS2PropertiesContext, // TODO: this flag can be removed when the layout can handle empty strings
    // currently there is only a check for null and not for empty string
    protected val nullIfAbsent: Boolean
) : ScriptableDelegate, CSS2Properties {
    var scriptable: Scriptable? = null

    override fun scriptable(): Scriptable? {
        return scriptable
    }
    var overlayColor: String? = null
        set(value) {
            field = value
            this.context.informLookInvalid()
        }

    //TODO All the methods that are not implemented need more detailed understanding.
    // most of them are short hand properties and they need to be constructed from the long
    // forms of the respective properties
    override fun getAzimuth(): String? {
        return helperTryBoth("azimuth")
    }

    override fun getBackground(): String {
        // TODO need to implement this method. GH #143
        return ""
    }

    override fun getBackgroundAttachment(): String? {
        return helperGetProperty("background-attachment")
    }

    override fun getBackgroundColor(): String? {
        return helperTryBoth("background-color")
    }

    override fun getBackgroundImage(): String? {
        // TODO
        // need to check if upstream can provide the absolute url of
        //  the image so that it can directly be passed.
        var quotedUri: String? = null
        val t = this.nodeData!!.getValue("background-image", false) as TermURIImpl?
        if (t != null) {
            var finalUrl: URL? = null
            try {
                finalUrl = Urls.createURL(t.getBase(), t.getValue())
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }
            quotedUri = if (finalUrl == null) null else finalUrl.toString()
        }
        return if (quotedUri == null) null else "url(" + quotedUri + ")"
    }

    override fun getBackgroundPosition(): String? {
        return helperGetValue("background-position")
    }

    override fun getBackgroundRepeat(): String? {
        return helperGetProperty("background-repeat")
    }

    override fun getBorder(): String? {
        // TODO need to implement this method
        throw UnsupportedOperationException()
    }

    override fun getBorderCollapse(): String? {
        return helperGetProperty("border-collapse")
    }

    override fun getBorderColor(): String? {
        // TODO need to implement this method
        throw UnsupportedOperationException()
    }

    override fun getBorderSpacing(): String? {
        return helperGetValue("border-spacing")
    }

    override fun getBorderStyle(): String? {
        // TODO need to implement this method
        throw UnsupportedOperationException()
    }

    override fun getBorderTop(): String? {
        // TODO need to implement this method
        throw UnsupportedOperationException()
    }

    override fun getBorderRight(): String? {
        // TODO need to implement this method
        throw UnsupportedOperationException()
    }

    override fun getBorderBottom(): String? {
        // TODO need to implement this method
        throw UnsupportedOperationException()
    }

    override fun getBorderLeft(): String? {
        // TODO need to implement this method
        throw UnsupportedOperationException()
    }

    override fun getBorderTopColor(): String? {
        return helperTryBoth("border-top-color")
    }

    override fun getBorderRightColor(): String? {
        return helperTryBoth("border-right-color")
    }

    override fun getBorderBottomColor(): String? {
        return helperTryBoth("border-bottom-color")
    }

    override fun getBorderLeftColor(): String? {
        return helperTryBoth("border-left-color")
    }

    override fun getBorderTopStyle(): String? {
        return helperGetProperty("border-top-style")
    }

    override fun getBorderRightStyle(): String? {
        return helperGetProperty("border-right-style")
    }

    override fun getBorderBottomStyle(): String? {
        return helperGetProperty("border-bottom-style")
    }

    override fun getBorderLeftStyle(): String? {
        return helperGetProperty("border-left-style")
    }

    override fun getBorderTopWidth(): String? {
        val width = helperTryBoth("border-top-width")
        // TODO
        // temp hack to support border thin/medium/thick
        // need to implement it at the place where it is actually being processed
        return border2Pixel(width)
    }

    override fun getBorderRightWidth(): String? {
        val width = helperTryBoth("border-right-width")
        // TODO
        // temp hack to support border thin/medium/thick
        // need to implement it at the place where it is actually being processed
        return border2Pixel(width)
    }

    override fun getBorderBottomWidth(): String? {
        val width = helperTryBoth("border-bottom-width")
        // TODO
        // temp hack to support border thin/medium/thick
        // need to implement it at the place where it is actually being processed
        return border2Pixel(width)
    }

    override fun getBorderLeftWidth(): String? {
        val width = helperTryBoth("border-left-width")
        // TODO
        // temp hack to support border thin/medium/thick
        // need to implement it at the place where it is actually being processed
        return border2Pixel(width)
    }

    override fun getBorderWidth(): String? {
        // TODO need to implement this method
        throw UnsupportedOperationException()
    }

    override fun getBottom(): String? {
        return helperTryBoth("bottom")
    }

    override fun getCaptionSide(): String? {
        return helperGetProperty("caption-side")
    }

    override fun getClear(): String? {
        return helperGetProperty("clear")
    }

    override fun getClip(): String? {
        return helperTryBoth("clip")
    }

    override fun getColor(): String? {
        return helperTryBoth("color")
    }

    override fun getContent(): String? {
        return helperTryBoth("content")
    }

    override fun getCounterIncrement(): String? {
        return helperTryBoth("couter-increment")
    }

    override fun getCounterReset(): String? {
        return helperTryBoth("couter-reset")
    }

    override fun getCue(): String? {
        // TODO need to implement this method
        throw UnsupportedOperationException()
    }

    override fun getCueAfter(): String? {
        return helperTryBoth("cue-after")
    }

    override fun getCueBefore(): String? {
        return helperTryBoth("cue-before")
    }

    override fun getCursor(): String? {
        return helperGetProperty("cursor")
    }

    override fun getDirection(): String? {
        return helperGetProperty("direction")
    }

    override fun getDisplay(): String? {
        return helperGetProperty("display")
    }

    override fun getElevation(): String? {
        return helperTryBoth("elevation")
    }

    override fun getEmptyCells(): String? {
        return helperGetProperty("empty-cells")
    }

    override fun getCssFloat(): String? {
        return this.float
    }

    override fun getFont(): String? {
        // TODO need to implement this method
        throw UnsupportedOperationException()
    }

    override fun getFontFamily(): String? {
        return helperTryBoth("font-family")
    }

    override fun getFontSize(): String? {
        return helperTryBoth("font-size")
    }

    override fun getFontSizeAdjust(): String? {
        return helperTryBoth("font-adjust")
    }

    override fun getFontStretch(): String? {
        return helperGetProperty("font-stretch")
    }

    override fun getFontStyle(): String? {
        return helperGetProperty("font-style")
    }

    override fun getFontVariant(): String? {
        return helperGetProperty("font-variant")
    }

    override fun getFontWeight(): String? {
        return helperGetProperty("font-weight")
    }

    override fun getHeight(): String? {
        return helperGetValue("height")
    }

    override fun getLeft(): String? {
        return helperTryBoth("left")
    }

    override fun getLetterSpacing(): String? {
        return helperTryBoth("letter-spacing")
    }

    override fun getLineHeight(): String? {
        return helperTryBoth("line-height")
    }

    override fun getListStyle(): String? {
        val listStyleType = getListStyleType()
        val listStylePosition = getListStylePosition()
        val listStyle = StringBuilder()

        if ((listStyleType != null) && !("null" == listStyleType)) {
            listStyle.append(listStyleType)
        }

        if ((listStylePosition != null) && !("null" == listStylePosition)) {
            listStyle.append(" " + listStylePosition)
        }

        val listStyleText = listStyle.toString().trim { it <= ' ' }
        return if (listStyleText.length == 0) null else listStyleText
    }

    override fun getListStyleImage(): String? {
        return helperTryBoth("list-style-image")
    }

    override fun getListStylePosition(): String? {
        return helperGetProperty("list-style-position")
    }

    override fun getListStyleType(): String? {
        return helperGetProperty("list-style-type")
    }

    override fun getMargin(): String? {
        // TODO need to implement this method
        throw UnsupportedOperationException()
    }

    override fun getMarginTop(): String? {
        return helperTryBoth("margin-top")
    }

    override fun getMarginRight(): String? {
        return helperTryBoth("margin-right")
    }

    override fun getMarginBottom(): String? {
        return helperTryBoth("margin-bottom")
    }

    override fun getMarginLeft(): String? {
        return helperTryBoth("margin-left")
    }

    override fun getMarkerOffset(): String? {
        return helperTryBoth("marker-offset")
    }

    override fun getMarks(): String? {
        return helperGetProperty("marks")
    }

    override fun getMaxHeight(): String? {
        return helperTryBoth("max-height")
    }

    override fun getMaxWidth(): String? {
        return helperTryBoth("max-width")
    }

    override fun getMinHeight(): String? {
        return helperTryBoth("min-height")
    }

    override fun getMinWidth(): String? {
        return helperTryBoth("min-width")
    }

    override fun getOrphans(): String? {
        return helperGetValue("orphans")
    }

    override fun getOutline(): String? {
        // TODO need to implement this method
        throw UnsupportedOperationException()
    }

    override fun getOutlineColor(): String? {
        return helperTryBoth("outline-color")
    }

    override fun getOutlineStyle(): String? {
        return helperGetProperty("outline-style")
    }

    //TODO add support for thick/think/medium
    override fun getOutlineWidth(): String? {
        val width = helperTryBoth("outline-border")
        return border2Pixel(width)
    }

    override fun getOverflow(): String? {
        return helperGetProperty("overflow")
    }

    override fun getPadding(): String? {
        // TODO need to implement this method
        throw UnsupportedOperationException()
    }

    override fun getPaddingTop(): String? {
        return helperGetValue("padding-top")
    }

    override fun getPaddingRight(): String? {
        return helperGetValue("padding-right")
    }

    override fun getPaddingBottom(): String? {
        return helperGetValue("padding-bottom")
    }

    override fun getPaddingLeft(): String? {
        return helperGetValue("padding-left")
    }

    override fun getPage(): String? {
        // TODO need to implement this method
        throw UnsupportedOperationException()
    }

    override fun getPageBreakAfter(): String? {
        return helperGetProperty("page-break-after")
    }

    override fun getPageBreakBefore(): String? {
        return helperGetProperty("page-break-before")
    }

    override fun getPageBreakInside(): String? {
        return helperGetProperty("page-break-inside")
    }

    override fun getPause(): String? {
        return helperGetValue("pause")
    }

    override fun getPauseAfter(): String? {
        return helperGetValue("pause-after")
    }

    override fun getPauseBefore(): String? {
        return helperGetValue("pause-before")
    }

    override fun getPitch(): String? {
        return helperTryBoth("pitch")
    }

    override fun getPitchRange(): String? {
        return helperGetValue("pitchRange")
    }

    override fun getPlayDuring(): String? {
        return helperTryBoth("play-during")
    }

    override fun getPosition(): String? {
        return helperGetProperty("position")
    }

    override fun getQuotes(): String? {
        return helperTryBoth("quotes")
    }

    override fun getRichness(): String? {
        return helperGetValue("richness")
    }

    override fun getRight(): String? {
        return helperTryBoth("right")
    }

    override fun getSize(): String? {
        // TODO need to implement this method
        throw UnsupportedOperationException()
    }

    override fun getSpeak(): String? {
        return helperGetProperty("speak")
    }

    override fun getSpeakHeader(): String? {
        return helperGetProperty("speak-header")
    }

    override fun getSpeakNumeral(): String? {
        return helperGetProperty("speak-numeral")
    }

    override fun getSpeakPunctuation(): String? {
        return helperGetProperty("speak-punctuation")
    }

    override fun getSpeechRate(): String? {
        return helperTryBoth("speech-rate")
    }

    override fun getStress(): String? {
        return helperGetValue("stress")
    }

    override fun getTableLayout(): String? {
        return helperGetProperty("table-layout")
    }

    override fun getTextAlign(): String? {
        return helperGetProperty("text-align")
    }

    override fun getTextDecoration(): String? {
        return helperTryBoth("text-decoration")
    }

    override fun getTextIndent(): String? {
        return helperGetValue("text-indent")
    }

    override fun getTextShadow(): String? {
        // TODO need to implement this method
        throw UnsupportedOperationException()
    }

    override fun getTextTransform(): String? {
        return helperGetProperty("text-transform")
    }

    override fun getTop(): String? {
        return helperTryBoth("top")
    }

    override fun getUnicodeBidi(): String? {
        return helperGetProperty("unicode-bidi")
    }

    override fun getVerticalAlign(): String? {
        return helperGetProperty("vertical-align")
    }

    override fun getVisibility(): String? {
        return helperGetProperty("visibility")
    }

    override fun getVoiceFamily(): String? {
        return helperTryBoth("voice-family")
    }

    override fun getVolume(): String? {
        return helperTryBoth("volume")
    }

    override fun getWhiteSpace(): String? {
        return helperGetProperty("white-space")
    }

    override fun getWidows(): String? {
        return helperGetValue("widows")
    }

    override fun getWidth(): String? {
        return helperGetValue("width")
    }

    override fun getWordSpacing(): String? {
        return helperTryBoth("word-spacing")
    }

    override fun getZIndex(): String {
        // TODO
        // refer to issue #77
        // According to the specs ZIndex value has to be integer but
        // jStyle Parser returns an float.
        // until then this is just a temp hack.
        val zIndex = helperGetValue("z-index")
        var fZIndex = 0.0f
        if (zIndex != null) {
            try {
                fZIndex = zIndex.toFloat()
            } catch (err: NumberFormatException) {
                err.printStackTrace()
            }
        }
        val iZIndex = fZIndex.toInt()
        return "" + iZIndex
    }

    // TODO references to this in internal code can use a more specific method.
    //      (we can implement specific methods like we have for other properties)
    fun getPropertyValue(string: String?): String? {
        return helperGetProperty(string)
    }

    val float: String?
        get() = helperGetProperty("float")

    abstract val nodeData: NodeData?

    private fun helperGetValue(propertyName: String?): String? {
        val nodeData = this.nodeData
        if (nodeData != null) {
            val value = nodeData.getValue(propertyName, true)
            // The trim() is a temporary work around for #154
            return if (value == null) null else value.toString().trim { it <= ' ' }
        } else {
            return if (nullIfAbsent) null else ""
        }
    }

    private fun helperGetProperty(propertyName: String?): String? {
        val nodeData = this.nodeData
        if (nodeData != null) {
            val property = nodeData.getProperty<CSSProperty?>(propertyName, true)
            // final CSSProperty property = nodeData.getProperty(propertyName);
            return if (property == null) null else property.toString()
        } else {
            return if (nullIfAbsent) null else ""
        }
    }

    fun helperTryBoth(propertyName: String?): String? {
        // These two implementations were deprecated after the changes in https://github.com/radkovo/jStyleParser/issues/50

        /* Original
    final String value = helperGetValue(propertyName);
    return value == null ? helperGetProperty(propertyName) : value;
    */

        /* Corrected (equivalent to below implementation, but less optimal)
    final String property = helperGetProperty(propertyName);
    return property == null || property.isEmpty() ? helperGetValue(propertyName) : property;
    */

        val nodeData = this.nodeData
        if (nodeData == null) {
            return null
        }
        return nodeData.getAsString(propertyName, true)
    }

    companion object {
        // TODO
        // temp hack to support border thin/medium/thick
        // this method should be removed once it is implemented where border is actually processed
        private fun border2Pixel(width: String?): String? {
            if (width != null) {
                if ("thin".equals(width, ignoreCase = true)) {
                    return HtmlValues.BORDER_THIN_SIZE
                }
                if ("medium".equals(width, ignoreCase = true)) {
                    return HtmlValues.BORDER_MEDIUM_SIZE
                }
                if ("thick".equals(width, ignoreCase = true)) {
                    return HtmlValues.BORDER_THICK_SIZE
                }
            }
            return width
        }
    }
}
