package io.github.remmerw.thor.style

import cz.vutbr.web.css.NodeData
import cz.vutbr.web.css.StyleSheet
import cz.vutbr.web.domassign.DirectAnalyzer
import io.github.remmerw.thor.dom.HTMLElementModel
import org.w3c.dom.DOMException
import org.w3c.dom.Element
import java.util.Locale

class LocalCssProperties(private val element: HTMLElementModel) :
    CssProperties(element, false) {
    @Throws(DOMException::class)
    override fun setAzimuth(azimuth: String?) {
        updateInlineStyle("azimuth", azimuth)
    }

    @Throws(DOMException::class)
    override fun setBackground(background: String?) {
        updateInlineStyle("background", background)
    }

    @Throws(DOMException::class)
    override fun setBackgroundAttachment(backgroundAttachment: String?) {
        updateInlineStyle("background-attachment", backgroundAttachment)
    }

    @Throws(DOMException::class)
    override fun setBackgroundColor(backgroundColor: String?) {
        updateInlineStyle("background-color", backgroundColor)
    }

    @Throws(DOMException::class)
    override fun setBackgroundImage(backgroundImage: String?) {
        updateInlineStyle("background-image", backgroundImage)
    }

    @Throws(DOMException::class)
    override fun setBackgroundPosition(backgroundPosition: String?) {
        updateInlineStyle("background-position", backgroundPosition)
    }

    @Throws(DOMException::class)
    override fun setBackgroundRepeat(backgroundRepeat: String?) {
        updateInlineStyle("background-repeat", backgroundRepeat)
    }

    @Throws(DOMException::class)
    override fun setBorder(border: String?) {
        updateInlineStyle("border", border)
    }

    @Throws(DOMException::class)
    override fun setBorderCollapse(borderCollapse: String?) {
        updateInlineStyle("border-collapse", borderCollapse)
    }

    @Throws(DOMException::class)
    override fun setBorderColor(borderColor: String?) {
        updateInlineStyle("border-color", borderColor)
    }

    @Throws(DOMException::class)
    override fun setBorderSpacing(borderSpacing: String?) {
        updateInlineStyle("border-spacing", borderSpacing)
    }

    @Throws(DOMException::class)
    override fun setBorderStyle(borderStyle: String?) {
        updateInlineStyle("border-style", borderStyle)
    }

    @Throws(DOMException::class)
    override fun setBorderTop(borderTop: String?) {
        updateInlineStyle("border-top", borderTop)
    }

    @Throws(DOMException::class)
    override fun setBorderRight(borderRight: String?) {
        updateInlineStyle("border-right", borderRight)
    }

    @Throws(DOMException::class)
    override fun setBorderBottom(borderBottom: String?) {
        updateInlineStyle("border-bottom", borderBottom)
    }

    @Throws(DOMException::class)
    override fun setBorderLeft(borderLeft: String?) {
        updateInlineStyle("border-left", borderLeft)
    }

    @Throws(DOMException::class)
    override fun setBorderTopColor(borderTopColor: String?) {
        updateInlineStyle("border-top-color", borderTopColor)
    }

    @Throws(DOMException::class)
    override fun setBorderRightColor(borderRightColor: String?) {
        updateInlineStyle("border-right-color", borderRightColor)
    }

    @Throws(DOMException::class)
    override fun setBorderBottomColor(borderBottomColor: String?) {
        updateInlineStyle("border-bottom-color", borderBottomColor)
    }

    @Throws(DOMException::class)
    override fun setBorderLeftColor(borderLeftColor: String?) {
        updateInlineStyle("border-left-color", borderLeftColor)
    }

    @Throws(DOMException::class)
    override fun setBorderTopStyle(borderTopStyle: String?) {
        updateInlineStyle("border-top-style", borderTopStyle)
    }

    @Throws(DOMException::class)
    override fun setBorderRightStyle(borderRightStyle: String?) {
        updateInlineStyle("border-right-style", borderRightStyle)
    }

    @Throws(DOMException::class)
    override fun setBorderBottomStyle(borderBottomStyle: String?) {
        updateInlineStyle("border-bottom-style", borderBottomStyle)
    }

    @Throws(DOMException::class)
    override fun setBorderLeftStyle(borderLeftStyle: String?) {
        updateInlineStyle("border-left-style", borderLeftStyle)
    }

    @Throws(DOMException::class)
    override fun setBorderTopWidth(borderTopWidth: String?) {
        updateInlineStyle("border-top-width", borderTopWidth)
    }

    @Throws(DOMException::class)
    override fun setBorderRightWidth(borderRightWidth: String?) {
        updateInlineStyle("border-right-width", borderRightWidth)
    }

    @Throws(DOMException::class)
    override fun setBorderBottomWidth(borderBottomWidth: String?) {
        updateInlineStyle("border-bottom-width", borderBottomWidth)
    }

    @Throws(DOMException::class)
    override fun setBorderLeftWidth(borderLeftWidth: String?) {
        updateInlineStyle("border-left-width", borderLeftWidth)
    }

    @Throws(DOMException::class)
    override fun setBorderWidth(borderWidth: String?) {
        updateInlineStyle("border-width", borderWidth)
    }

    @Throws(DOMException::class)
    override fun setBottom(bottom: String?) {
        updateInlineStyle("bottom", bottom)
    }

    @Throws(DOMException::class)
    override fun setCaptionSide(captionSide: String?) {
        updateInlineStyle("caption-side", captionSide)
    }

    @Throws(DOMException::class)
    override fun setClear(clear: String?) {
        updateInlineStyle("clear", clear)
    }

    @Throws(DOMException::class)
    override fun setClip(clip: String?) {
        updateInlineStyle("clip", clip)
    }

    @Throws(DOMException::class)
    override fun setColor(color: String?) {
        updateInlineStyle("color", color)
    }

    @Throws(DOMException::class)
    override fun setContent(content: String?) {
        updateInlineStyle("content", content)
    }

    @Throws(DOMException::class)
    override fun setCounterIncrement(counterIncrement: String?) {
        updateInlineStyle("counter-increment", counterIncrement)
    }

    @Throws(DOMException::class)
    override fun setCounterReset(counterReset: String?) {
        updateInlineStyle("counter-reset", counterReset)
    }

    @Throws(DOMException::class)
    override fun setCue(cue: String?) {
        updateInlineStyle("cue", cue)
    }

    @Throws(DOMException::class)
    override fun setCueAfter(cueAfter: String?) {
        updateInlineStyle("cue-after", cueAfter)
    }

    @Throws(DOMException::class)
    override fun setCueBefore(cueBefore: String?) {
        updateInlineStyle("cue-before", cueBefore)
    }

    @Throws(DOMException::class)
    override fun setCursor(cursor: String?) {
        updateInlineStyle("cursor", cursor)
    }

    @Throws(DOMException::class)
    override fun setDirection(direction: String?) {
        updateInlineStyle("direction", direction)
    }

    @Throws(DOMException::class)
    override fun setDisplay(display: String?) {
        updateInlineStyle("display", display)
    }

    @Throws(DOMException::class)
    override fun setElevation(elevation: String?) {
        updateInlineStyle("elevation", elevation)
    }

    @Throws(DOMException::class)
    override fun setEmptyCells(emptyCells: String?) {
        updateInlineStyle("empty-cells", emptyCells)
    }

    @Throws(DOMException::class)
    override fun setCssFloat(cssFloat: String?) {
        updateInlineStyle("css-float", cssFloat)
    }

    @Throws(DOMException::class)
    override fun setFont(font: String?) {
        updateInlineStyle("font", font)
    }

    @Throws(DOMException::class)
    override fun setFontFamily(fontFamily: String?) {
        updateInlineStyle("font-family", fontFamily)
    }

    @Throws(DOMException::class)
    override fun setFontSize(fontSize: String?) {
        updateInlineStyle("font-size", fontSize)
    }

    @Throws(DOMException::class)
    override fun setFontSizeAdjust(fontSizeAdjust: String?) {
        updateInlineStyle("font-size-adjust", fontSizeAdjust)
    }

    @Throws(DOMException::class)
    override fun setFontStretch(fontStretch: String?) {
        updateInlineStyle("font-stretch", fontStretch)
    }

    @Throws(DOMException::class)
    override fun setFontStyle(fontStyle: String?) {
        updateInlineStyle("font-style", fontStyle)
    }

    @Throws(DOMException::class)
    override fun setFontVariant(fontVariant: String?) {
        updateInlineStyle("font-Variant", fontVariant)
    }

    @Throws(DOMException::class)
    override fun setFontWeight(fontWeight: String?) {
        updateInlineStyle("font-weight", fontWeight)
    }

    @Throws(DOMException::class)
    override fun setHeight(height: String?) {
        updateInlineStyle("height", height)
    }

    @Throws(DOMException::class)
    override fun setLeft(left: String?) {
        updateInlineStyle("left", left)
    }

    @Throws(DOMException::class)
    override fun setLetterSpacing(letterSpacing: String?) {
        updateInlineStyle("letter-spacing", letterSpacing)
    }

    @Throws(DOMException::class)
    override fun setLineHeight(lineHeight: String?) {
        updateInlineStyle("line-height", lineHeight)
    }

    @Throws(DOMException::class)
    override fun setListStyle(listStyle: String?) {
        updateInlineStyle("list-Style", listStyle)
    }

    @Throws(DOMException::class)
    override fun setListStyleImage(listStyleImage: String?) {
        updateInlineStyle("list-style-image", listStyleImage)
    }

    @Throws(DOMException::class)
    override fun setListStylePosition(listStylePosition: String?) {
        updateInlineStyle("list-style-position", listStylePosition)
    }

    @Throws(DOMException::class)
    override fun setListStyleType(listStyleType: String?) {
        updateInlineStyle("list-style-type", listStyleType)
    }

    @Throws(DOMException::class)
    override fun setMargin(margin: String?) {
        updateInlineStyle("margin", margin)
    }

    @Throws(DOMException::class)
    override fun setMarginTop(marginTop: String?) {
        updateInlineStyle("margin-top", marginTop)
    }

    @Throws(DOMException::class)
    override fun setMarginRight(marginRight: String?) {
        updateInlineStyle("margin-right", marginRight)
    }

    @Throws(DOMException::class)
    override fun setMarginBottom(marginBottom: String?) {
        updateInlineStyle("margin-bottom", marginBottom)
    }

    @Throws(DOMException::class)
    override fun setMarginLeft(marginLeft: String?) {
        updateInlineStyle("margin-left", marginLeft)
    }

    @Throws(DOMException::class)
    override fun setMarkerOffset(markerOffset: String?) {
        updateInlineStyle("marker-offset", markerOffset)
    }

    @Throws(DOMException::class)
    override fun setMarks(marks: String?) {
        updateInlineStyle("marks", marks)
    }

    @Throws(DOMException::class)
    override fun setMaxHeight(maxHeight: String?) {
        updateInlineStyle("max-height", maxHeight)
    }

    @Throws(DOMException::class)
    override fun setMaxWidth(maxWidth: String?) {
        updateInlineStyle("max-width", maxWidth)
    }

    @Throws(DOMException::class)
    override fun setMinHeight(minHeight: String?) {
        updateInlineStyle("min-height", minHeight)
    }

    @Throws(DOMException::class)
    override fun setMinWidth(minWidth: String?) {
        updateInlineStyle("min-width", minWidth)
    }

    @Throws(DOMException::class)
    override fun setOrphans(orphans: String?) {
        updateInlineStyle("orphans", orphans)
    }

    @Throws(DOMException::class)
    override fun setOutline(outline: String?) {
        updateInlineStyle("outline", outline)
    }

    @Throws(DOMException::class)
    override fun setOutlineColor(outlineColor: String?) {
        updateInlineStyle("outline-color", outlineColor)
    }

    @Throws(DOMException::class)
    override fun setOutlineStyle(outlineStyle: String?) {
        updateInlineStyle("outline-style", outlineStyle)
    }

    @Throws(DOMException::class)
    override fun setOutlineWidth(outlineWidth: String?) {
        updateInlineStyle("outline-width", outlineWidth)
    }

    @Throws(DOMException::class)
    override fun setOverflow(overflow: String?) {
        updateInlineStyle("overflow", overflow)
    }

    @Throws(DOMException::class)
    override fun setPadding(padding: String?) {
        updateInlineStyle("padding", padding)
    }

    @Throws(DOMException::class)
    override fun setPaddingTop(paddingTop: String?) {
        updateInlineStyle("padding-top", paddingTop)
    }

    @Throws(DOMException::class)
    override fun setPaddingRight(paddingRight: String?) {
        updateInlineStyle("padding-right", paddingRight)
    }

    @Throws(DOMException::class)
    override fun setPaddingBottom(paddingBottom: String?) {
        updateInlineStyle("padding-bottom", paddingBottom)
    }

    @Throws(DOMException::class)
    override fun setPaddingLeft(paddingLeft: String?) {
        updateInlineStyle("padding-left", paddingLeft)
    }

    @Throws(DOMException::class)
    override fun setPage(page: String?) {
        updateInlineStyle("page", page)
    }

    @Throws(DOMException::class)
    override fun setPageBreakAfter(pageBreakAfter: String?) {
        updateInlineStyle("page-break-after", pageBreakAfter)
    }

    @Throws(DOMException::class)
    override fun setPageBreakBefore(pageBreakBefore: String?) {
        updateInlineStyle("page-break-before", pageBreakBefore)
    }

    @Throws(DOMException::class)
    override fun setPageBreakInside(pageBreakInside: String?) {
        updateInlineStyle("page-break-inside", pageBreakInside)
    }

    @Throws(DOMException::class)
    override fun setPause(pause: String?) {
        updateInlineStyle("pause", pause)
    }

    @Throws(DOMException::class)
    override fun setPauseAfter(pauseAfter: String?) {
        updateInlineStyle("pause-after", pauseAfter)
    }

    @Throws(DOMException::class)
    override fun setPauseBefore(pauseBefore: String?) {
        updateInlineStyle("pause-before", pauseBefore)
    }

    @Throws(DOMException::class)
    override fun setPitch(pitch: String?) {
        updateInlineStyle("pitch", pitch)
    }

    @Throws(DOMException::class)
    override fun setPitchRange(pitchRange: String?) {
        updateInlineStyle("pitch-range", pitchRange)
    }

    @Throws(DOMException::class)
    override fun setPlayDuring(playDuring: String?) {
        updateInlineStyle("play-during", playDuring)
    }

    @Throws(DOMException::class)
    override fun setPosition(position: String?) {
        updateInlineStyle("position", position)
    }

    @Throws(DOMException::class)
    override fun setQuotes(quotes: String?) {
        updateInlineStyle("quotes", quotes)
    }

    @Throws(DOMException::class)
    override fun setRichness(richness: String?) {
        updateInlineStyle("richness", richness)
    }

    @Throws(DOMException::class)
    override fun setRight(right: String?) {
        updateInlineStyle("right", right)
    }

    @Throws(DOMException::class)
    override fun setSize(size: String?) {
        updateInlineStyle("size", size)
    }

    @Throws(DOMException::class)
    override fun setSpeak(speak: String?) {
        updateInlineStyle("speak", speak)
    }

    @Throws(DOMException::class)
    override fun setSpeakHeader(speakHeader: String?) {
        updateInlineStyle("speak-header", speakHeader)
    }

    @Throws(DOMException::class)
    override fun setSpeakNumeral(speakNumeral: String?) {
        updateInlineStyle("speak-numeral", speakNumeral)
    }

    @Throws(DOMException::class)
    override fun setSpeakPunctuation(speakPunctuation: String?) {
        updateInlineStyle("speak-punctuation", speakPunctuation)
    }

    @Throws(DOMException::class)
    override fun setSpeechRate(speechRate: String?) {
        updateInlineStyle("speech-rate", speechRate)
    }

    @Throws(DOMException::class)
    override fun setStress(stress: String?) {
        updateInlineStyle("stress", stress)
    }

    @Throws(DOMException::class)
    override fun setTableLayout(tableLayout: String?) {
        updateInlineStyle("table-layout", tableLayout)
    }

    @Throws(DOMException::class)
    override fun setTextAlign(textAlign: String?) {
        updateInlineStyle("text-align", textAlign)
    }

    @Throws(DOMException::class)
    override fun setTextDecoration(textDecoration: String?) {
        updateInlineStyle("text-decoration", textDecoration)
    }

    @Throws(DOMException::class)
    override fun setTextIndent(textIndent: String?) {
        updateInlineStyle("text-indent", textIndent)
    }

    @Throws(DOMException::class)
    override fun setTextShadow(textShadow: String?) {
        updateInlineStyle("text-shadow", textShadow)
    }

    @Throws(DOMException::class)
    override fun setTextTransform(textTransform: String?) {
        updateInlineStyle("text-transform", textTransform)
    }

    @Throws(DOMException::class)
    override fun setTop(top: String?) {
        updateInlineStyle("top", top)
    }

    @Throws(DOMException::class)
    override fun setUnicodeBidi(unicodeBidi: String?) {
        updateInlineStyle("unicode-bidi", unicodeBidi)
    }

    @Throws(DOMException::class)
    override fun setVerticalAlign(verticalAlign: String?) {
        updateInlineStyle("vertical-align", verticalAlign)
    }

    @Throws(DOMException::class)
    override fun setVisibility(visibility: String?) {
        updateInlineStyle("visibility", visibility)
    }

    @Throws(DOMException::class)
    override fun setVoiceFamily(voiceFamily: String?) {
        updateInlineStyle("voice-family", voiceFamily)
    }

    @Throws(DOMException::class)
    override fun setVolume(volume: String?) {
        updateInlineStyle("volume", volume)
    }

    @Throws(DOMException::class)
    override fun setWhiteSpace(whiteSpace: String?) {
        updateInlineStyle("white-space", whiteSpace)
    }

    @Throws(DOMException::class)
    override fun setWidows(widows: String?) {
        updateInlineStyle("widows", widows)
    }

    @Throws(DOMException::class)
    override fun setWidth(width: String?) {
        updateInlineStyle("width", width)
    }

    @Throws(DOMException::class)
    override fun setWordSpacing(wordSpacing: String?) {
        updateInlineStyle("word-spacing", wordSpacing)
    }

    @Throws(DOMException::class)
    override fun setZIndex(zIndex: String?) {
        updateInlineStyle("z-index", zIndex)
    }

    override fun getNodeData(): NodeData? {
        val ele = this.element
        val inlineStyle = ele.getAttribute("style")
        if ((inlineStyle != null) && (inlineStyle.length > 0)) {
            val jSheets: MutableList<StyleSheet?> = ArrayList<StyleSheet?>()
            val jSheet = CSSUtilities.jParseInlineStyle(inlineStyle, null, ele, true)
            jSheets.add(jSheet)
            val domAnalyser = DirectAnalyzer(jSheets)
            return domAnalyser.getElementStyle(ele, null, "screen")
        }
        return null
    }

    private fun updateInlineStyle(propertyName: String, propertyValue: String?) {
        val ele: Element? = this.element
        if (ele != null) {
            val sb = StringBuilder()
            val inlineStyle = ele.getAttribute("style")
            if ((inlineStyle != null) && (inlineStyle.length > 0)) {
                val propertyNameLC = propertyName.lowercase(Locale.getDefault())
                val styleDeclarations =
                    inlineStyle.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (styleDeclaration in styleDeclarations) {
                    val nameValue =
                        styleDeclaration.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    if (nameValue.size == 2) {
                        val oldPropertyName =
                            nameValue[0].lowercase(Locale.getDefault()).trim { it <= ' ' }
                        if (!(oldPropertyName == propertyNameLC)) {
                            sb.append(styleDeclaration + ";")
                        }
                    }
                }
            }
            sb.append(propertyName + ":" + propertyValue + ";")
            ele.setAttribute("style", sb.toString())
        }
    }

}
