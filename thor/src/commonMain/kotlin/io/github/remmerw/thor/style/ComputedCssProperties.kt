package io.github.remmerw.thor.style

import cz.vutbr.web.css.NodeData
import org.w3c.dom.DOMException


class ComputedCssProperties(
    context: CSS2PropertiesContext,
    private val nodeData: NodeData?,
    nullIfAbsent: Boolean
) : CssProperties(context, nullIfAbsent) {

    override fun getNodeData(): NodeData? {
        return nodeData
    }

    //TODO need to implement all the unimplemented setters.
    @Throws(DOMException::class)
    override fun setAzimuth(azimuth: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBackground(background: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBackgroundAttachment(backgroundAttachment: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBackgroundColor(backgroundColor: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBackgroundImage(backgroundImage: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBackgroundPosition(backgroundPosition: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBackgroundRepeat(backgroundRepeat: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorder(border: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderCollapse(borderCollapse: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderColor(borderColor: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderSpacing(borderSpacing: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderStyle(borderStyle: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderTop(borderTop: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderRight(borderRight: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderBottom(borderBottom: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderLeft(borderLeft: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderTopColor(borderTopColor: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderRightColor(borderRightColor: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderBottomColor(borderBottomColor: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderLeftColor(borderLeftColor: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderTopStyle(borderTopStyle: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderRightStyle(borderRightStyle: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderBottomStyle(borderBottomStyle: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderLeftStyle(borderLeftStyle: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderTopWidth(borderTopWidth: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderRightWidth(borderRightWidth: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderBottomWidth(borderBottomWidth: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderLeftWidth(borderLeftWidth: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBorderWidth(borderWidth: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setBottom(bottom: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setCaptionSide(captionSide: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setClear(clear: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setClip(clip: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setColor(color: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setContent(content: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setCounterIncrement(counterIncrement: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setCounterReset(counterReset: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setCue(cue: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setCueAfter(cueAfter: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setCueBefore(cueBefore: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setCursor(cursor: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setDirection(direction: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setDisplay(display: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setElevation(elevation: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setEmptyCells(emptyCells: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setCssFloat(cssFloat: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setFont(font: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setFontFamily(fontFamily: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setFontSize(fontSize: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setFontSizeAdjust(fontSizeAdjust: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setFontStretch(fontStretch: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setFontStyle(fontStyle: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setFontVariant(fontVariant: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setFontWeight(fontWeight: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setHeight(height: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setLeft(left: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setLetterSpacing(letterSpacing: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setLineHeight(lineHeight: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setListStyle(listStyle: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setListStyleImage(listStyleImage: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setListStylePosition(listStylePosition: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setListStyleType(listStyleType: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setMargin(margin: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setMarginTop(marginTop: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setMarginRight(marginRight: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setMarginBottom(marginBottom: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setMarginLeft(marginLeft: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setMarkerOffset(markerOffset: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setMarks(marks: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setMaxHeight(maxHeight: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setMaxWidth(maxWidth: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setMinHeight(minHeight: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setMinWidth(minWidth: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setOrphans(orphans: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setOutline(outline: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setOutlineColor(outlineColor: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setOutlineStyle(outlineStyle: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setOutlineWidth(outlineWidth: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setOverflow(overflow: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setPadding(padding: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setPaddingTop(paddingTop: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setPaddingRight(paddingRight: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setPaddingBottom(paddingBottom: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setPaddingLeft(paddingLeft: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setPage(page: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setPageBreakAfter(pageBreakAfter: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setPageBreakBefore(pageBreakBefore: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setPageBreakInside(pageBreakInside: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setPause(pause: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setPauseAfter(pauseAfter: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setPauseBefore(pauseBefore: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setPitch(pitch: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setPitchRange(pitchRange: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setPlayDuring(playDuring: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setPosition(position: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setQuotes(quotes: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setRichness(richness: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setRight(right: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setSize(size: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setSpeak(speak: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setSpeakHeader(speakHeader: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setSpeakNumeral(speakNumeral: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setSpeakPunctuation(speakPunctuation: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setSpeechRate(speechRate: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setStress(stress: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setTableLayout(tableLayout: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setTextAlign(textAlign: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setTextDecoration(textDecoration: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setTextIndent(textIndent: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setTextShadow(textShadow: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setTextTransform(textTransform: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setTop(top: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setUnicodeBidi(unicodeBidi: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setVerticalAlign(verticalAlign: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setVisibility(visibility: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setVoiceFamily(voiceFamily: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setVolume(volume: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setWhiteSpace(whiteSpace: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setWidows(widows: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setWidth(width: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setWordSpacing(wordSpacing: String?) {
        throw UnsupportedOperationException()
    }

    @Throws(DOMException::class)
    override fun setZIndex(zIndex: String?) {
        throw UnsupportedOperationException()
    }

}
