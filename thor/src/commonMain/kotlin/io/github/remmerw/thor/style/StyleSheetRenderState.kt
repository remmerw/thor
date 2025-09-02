package io.github.remmerw.thor.style

import androidx.compose.ui.graphics.Color
import cz.vutbr.web.css.CSSProperty.VerticalAlign
import io.github.remmerw.thor.dom.HTMLDocumentImpl
import io.github.remmerw.thor.dom.HTMLElementImpl
import org.w3c.dom.css.CSS2Properties
import org.w3c.dom.html.HTMLElement
import java.util.Locale
import java.util.Optional
import java.util.StringTokenizer

open class StyleSheetRenderState : RenderState {
    protected val element: HTMLElementImpl?
    protected val document: HTMLDocumentImpl?
    protected val prevRenderState: RenderState?
    protected var iBackgroundInfo: BackgroundInfo? = INVALID_BACKGROUND_INFO
    protected var iWhiteSpace: Int? = null


    private var marginInsets: HtmlInsets? = INVALID_INSETS
    fun marginInsets(value: HtmlInsets?) {
        this.marginInsets = value
    }

    private var paddingInsets: HtmlInsets? = INVALID_INSETS
    fun paddingInsets(value: HtmlInsets?) {
        this.paddingInsets = value
    }

    private var overflowX: Int = -1

    fun overflowX(value: Int) {
        this.overflowX = value
    }

    private var overflowY: Int = -1
    fun overflowY(value: Int) {
        this.overflowY = value
    }

    private var borderInfo: BorderInfo? = INVALID_BORDER_INFO
    fun borderInfo(borderInfo: BorderInfo?) {
        this.borderInfo = borderInfo
    }


    private var iFont: Font? = null

    private var iColor: Color? = null
    private var iBackgroundColor: Color? = INVALID_COLOR
    private var iTextBackgroundColor: Color? = INVALID_COLOR

    // public TextRenderState(RenderState prevRenderState) {
    // this.css2properties = new CSS2PropertiesImpl(this);
    // this.prevRenderState = prevRenderState;
    // }
    private var iOverlayColor: Color? = INVALID_COLOR
    private var iTextDecoration = -1
    private var iTextTransform = -1
    private var iBlankWidth = -1
    private var iHighlight = false
    private var iDisplay: Int? = null
    private var alignXPercent = -1
    private val alignYPercent: Int = 0 // todo
    private var counters: MutableMap<String?, ArrayList<Int?>>? = null
    private var iTextIndentText: String? = null
    private var cachedVisibility: Int? = null
    private var cachedPosition: Int? = null
    private var cachedFloat: Int? = null
    private var cachedClear: Int? = null

    constructor(prevRenderState: RenderState?, element: HTMLElementImpl) {
        this.prevRenderState = prevRenderState
        this.element = element
        this.document = element.ownerDocument as HTMLDocumentImpl?
    }

    constructor(document: HTMLDocumentImpl?) {
        this.prevRenderState = null
        this.element = null
        this.document = document
    }

    open fun getDefaultDisplay(): Int {
        return RenderState.Companion.DISPLAY_INLINE
    }

    override fun getDisplay(): Int {
        val d = this.iDisplay
        if (d != null) {
            return d
        }
        val props: CSS2Properties? = this.cssProperties()
        val displayText = if (props == null) null else props.display
        val displayInt: Int
        if (displayText != null) {
            val displayTextTL = displayText.lowercase(Locale.getDefault())
            if ("block" == displayTextTL) {
                displayInt = RenderState.Companion.DISPLAY_BLOCK
            } else if ("inline" == displayTextTL) {
                displayInt = RenderState.Companion.DISPLAY_INLINE
            } else if ("none" == displayTextTL) {
                displayInt = RenderState.Companion.DISPLAY_NONE
            } else if ("list-item" == displayTextTL) {
                displayInt = RenderState.Companion.DISPLAY_LIST_ITEM
            } else if ("table-row-group" == displayTextTL) {
                displayInt = RenderState.Companion.DISPLAY_TABLE_ROW_GROUP
            } else if ("table-header-group" == displayTextTL) {
                displayInt = RenderState.Companion.DISPLAY_TABLE_HEADER_GROUP
            } else if ("table-footer-group" == displayTextTL) {
                displayInt = RenderState.Companion.DISPLAY_TABLE_FOOTER_GROUP
            } else if ("table" == displayTextTL) {
                displayInt = RenderState.Companion.DISPLAY_TABLE
            } else if ("inline-table" == displayTextTL) {
                displayInt = RenderState.Companion.DISPLAY_INLINE_TABLE
            } else if ("table-cell" == displayTextTL) {
                displayInt = RenderState.Companion.DISPLAY_TABLE_CELL
            } else if ("table-row" == displayTextTL) {
                displayInt = RenderState.Companion.DISPLAY_TABLE_ROW
            } else if ("inline-block" == displayTextTL) {
                displayInt = RenderState.Companion.DISPLAY_INLINE_BLOCK
            } else if ("table-column" == displayTextTL) {
                displayInt = RenderState.Companion.DISPLAY_TABLE_COLUMN
            } else if ("table-column-group" == displayTextTL) {
                displayInt = RenderState.Companion.DISPLAY_TABLE_COLUMN_GROUP
            } else if ("table-caption" == displayTextTL) {
                displayInt = RenderState.Companion.DISPLAY_TABLE_CAPTION
            } else {
                displayInt = this.getDefaultDisplay()

            }
        } else {
            displayInt = this.getDefaultDisplay()
        }
        this.iDisplay = (displayInt)
        return displayInt
    }

    override fun getFontBase(): Int {
        return 3
    }

    fun repaint() {
        // Dummy implementation
    }

    fun cssProperties(): CssProperties? {
        val element = this.element
        return if (element == null) null else element.cssProperties()
    }

    override fun invalidate() {

        this.iFont = null

        this.iColor = null
        this.iTextDecoration = -1
        this.iBlankWidth = -1
        this.alignXPercent = -1
        this.iBackgroundColor = INVALID_COLOR
        this.iTextBackgroundColor = INVALID_COLOR
        this.iOverlayColor = INVALID_COLOR
        this.iBackgroundInfo = INVALID_BACKGROUND_INFO
        this.iDisplay = null
        this.iTextIndentText = null
        this.iWhiteSpace = null
        this.marginInsets = INVALID_INSETS
        this.paddingInsets = INVALID_INSETS
        this.overflowX = -1
        this.overflowY = -1
        this.borderInfo = INVALID_BORDER_INFO
        // Should NOT invalidate parent render state.
    }

    override fun getFont(): Font? {
        var f = this.iFont
        if (f != null) {
            return f
        }
        val style = this.cssProperties()
        val prs = this.prevRenderState
        if (style == null) {
            if (prs != null) {
                val font = prs.getFont()
                return font
            }
            f = DEFAULT_FONT
            this.iFont = f
            return f
        }
        var fontSize: Float? = null
        var fontStyle: String? = null
        var fontVariant: String? = null
        var fontWeight: String? = null
        var fontFamily: String? = null

        val newFontSize = style.fontSize
        val newFontFamily = style.fontFamily
        val newFontStyle = style.fontStyle
        val newFontVariant = style.fontVariant
        val newFontWeight = style.fontWeight
        val verticalAlign = style.verticalAlign
        val isSuper = (verticalAlign != null) && verticalAlign.equals("super", ignoreCase = true)
        val isSub = (verticalAlign != null) && verticalAlign.equals("sub", ignoreCase = true)
        if ((newFontSize == null) && (newFontWeight == null) && (newFontStyle == null) && (newFontFamily == null) && (newFontVariant == null)) {
            if (!isSuper && !isSub) {
                if (prs != null) {
                    return prs.getFont()
                } else {
                    f = DEFAULT_FONT
                    this.iFont = f
                    return f
                }
            }
        }
        if (newFontSize != null) {
            try {
                fontSize = (HtmlValues.getFontSize(newFontSize, prs))
            } catch (err: Exception) {
                fontSize = HtmlValues.DEFAULT_FONT_SIZE_BOX
            }
        } else {
            if (prs != null) {
                fontSize = (prs.getFont()?.fontSize)?.toFloat()
            } else {
                fontSize = HtmlValues.DEFAULT_FONT_SIZE_BOX
            }
        }
        if (newFontFamily != null) {
            fontFamily = newFontFamily
        }
        if (fontFamily == null) {
            fontFamily = DEFAULT_FONT_FAMILY
        }
        if (newFontStyle != null) {
            fontStyle = newFontStyle
        }
        if (newFontVariant != null) {
            fontVariant = newFontVariant
        }
        if (newFontWeight != null) {
            fontWeight = newFontWeight
        }

        var superscript: Int? = null
        if (isSuper) {
            superscript = (1)
        } else if (isSub) {
            superscript = (-1)
        }
        f = Font(
            fontFamily,
            fontStyle,
            fontVariant,
            fontWeight,
            fontSize!!,
            superscript
        )
        this.iFont = f
        return f
    }

    override fun getColor(): Color? {
        var c = this.iColor
        if (c != null) {
            return c
        }
        val props = this.cssProperties()
        var colorValue = if (props == null) null else props.color
        if ((colorValue == null) || "" == colorValue) {
            colorValue = "black"
        }
        c = ColorFactory.instance?.getColor(colorValue)
        this.iColor = c
        return c
    }

    override fun getBackgroundColor(): Color? {
        val c = this.iBackgroundColor
        if (c != INVALID_COLOR) {
            return c
        }
        val localColor: Color?
        val binfo = this.getBackgroundInfo()
        localColor = if (binfo == null) null else binfo.backgroundColor
        this.iBackgroundColor = localColor
        return localColor
    }

    override fun getTextBackgroundColor(): Color? {
        val c = this.iTextBackgroundColor
        if (c != INVALID_COLOR) {
            return c
        }
        val localColor: Color?
        if (this.getDisplay() != RenderState.Companion.DISPLAY_INLINE) {
            // Background painted by block.
            localColor = null
        } else {
            val binfo = this.getBackgroundInfo()
            localColor = if (binfo == null) null else binfo.backgroundColor
        }
        this.iTextBackgroundColor = localColor
        return localColor
    }

    override fun getOverlayColor(): Color? {
        var c = this.iOverlayColor
        if (c != INVALID_COLOR) {
            return c
        }
        val props = this.cssProperties()
        var colorValue = if (props == null) null else props.overlayColor
        if ((colorValue == null) || (colorValue.length == 0)) {
            colorValue = null
        }
        c = if (colorValue == null) null else ColorFactory.instance?.getColor(colorValue)
        this.iOverlayColor = c
        return c
    }

    override fun getTextDecorationMask(): Int {
        var td = this.iTextDecoration
        if (td != -1) {
            return td
        }
        val props = this.cssProperties()
        val tdText = if (props == null) null else props.textDecoration
        td = 0
        if (tdText != null) {
            val tok = StringTokenizer(tdText.lowercase(Locale.getDefault()), ", \t\n\r")
            while (tok.hasMoreTokens()) {
                val token = tok.nextToken()
                if ("none" == token) {
                    // continue
                } else if ("underline" == token) {
                    td = td or RenderState.Companion.MASK_TEXTDECORATION_UNDERLINE
                } else if ("line-through" == token) {
                    td = td or RenderState.Companion.MASK_TEXTDECORATION_LINE_THROUGH
                } else if ("blink" == token) {
                    td = td or RenderState.Companion.MASK_TEXTDECORATION_BLINK
                } else if ("overline" == token) {
                    td = td or RenderState.Companion.MASK_TEXTDECORATION_OVERLINE
                }
            }
        }
        this.iTextDecoration = td
        return td
    }

    override fun getTextTransform(): Int {
        var tt = this.iTextTransform
        if (tt != -1) {
            return tt
        }
        val props = this.cssProperties()
        val tdText = if (props == null) null else props.textTransform
        tt = 0
        if (tdText != null) {
            if ("none" == tdText) {
                // continue
            } else if ("capitalize" == tdText) {
                tt = RenderState.Companion.TEXTTRANSFORM_CAPITALIZE
            } else if ("uppercase" == tdText) {
                tt = RenderState.Companion.TEXTTRANSFORM_UPPERCASE
            } else if ("lowercase" == tdText) {
                tt = RenderState.Companion.TEXTTRANSFORM_LOWERCASE
            }
            // TODO how the explicit "inherit" value is to be handled?
            // Who is responsible for CSS cascading?
            // ... painting code? prevRenderState?
            //
            // else if("inherit".equals(tdText)) {
            // tt = TEXTTRANSFORM_INHERIT;
            // }
        }
        this.iTextTransform = tt
        return tt
    }

    override fun getBlankWidth(): Int {
        var bw = this.iBlankWidth
        if (bw == -1) {
            bw = 16
            this.iBlankWidth = bw
        }
        return bw
    }


    override fun isHighlight(): Boolean {
        return this.iHighlight
    }


    override fun setHighlight(highlight: Boolean) {
        this.iHighlight = highlight
    }


    override fun getAlignXPercent(): Int {
        var axp = this.alignXPercent
        if (axp != -1) {
            return axp
        }
        val props: CSS2Properties? = this.cssProperties()
        var textAlign = if (props == null) null else props.textAlign
        if ((textAlign == null) || (textAlign.length == 0)) {
            // Fall back to align attribute.
            val element: HTMLElement? = this.element
            if (element != null) {
                textAlign = element.getAttribute("align")
                if ((textAlign == null) || (textAlign.length == 0)) {
                    textAlign = null
                }
            }
        }
        if (textAlign == null) {
            axp = 0
        } else if ("center".equals(textAlign, ignoreCase = true)) {
            axp = 50
        } else if ("right".equals(textAlign, ignoreCase = true)) {
            axp = 100
        } else {
            // TODO: justify, <string>
            axp = 0
        }
        this.alignXPercent = axp
        return axp
    }

    override fun getAlignYPercent(): Int {
        // This is only settable in table cells.
        // TODO: Does it work with display: table-cell?
        return 0
    }

    override fun getCount(counter: String?, nesting: Int): Int {
        // Expected to be called only in GUI thread.
        val prs = this.prevRenderState
        if (prs != null) {
            return prs.getCount(counter, nesting)
        }
        val counters = this.counters
        if (counters == null) {
            return 0
        }
        val counterArray: ArrayList<Int?> = counters.get(counter)!!
        if ((nesting < 0) || (nesting >= counterArray.size)) {
            return 0
        }
        val integer = counterArray.get(nesting)
        return if (integer == null) 0 else integer
    }


    override fun resetCount(counter: String?, nesting: Int, value: Int) {
        // Expected to be called only in the GUI thread.
        val prs = this.prevRenderState
        if (prs != null) {
            prs.resetCount(counter, nesting, value)
        } else {
            var counters = this.counters
            if (counters == null) {
                counters = HashMap<String?, ArrayList<Int?>>(2)
                this.counters = counters
                counters.put(counter, ArrayList<Int?>(0))
            }
            val counterArray: ArrayList<Int?> = counters.get(counter)!!
            while (counterArray.size <= nesting) {
                counterArray.add(null)
            }
            counterArray.set(nesting, (value))
        }
    }

    override fun incrementCount(counter: String?, nesting: Int): Int {
        // Expected to be called only in the GUI thread.
        val prs = this.prevRenderState
        if (prs != null) {
            return prs.incrementCount(counter, nesting)
        }
        var counters = this.counters
        if (counters == null) {
            counters = HashMap<String?, ArrayList<Int?>>(2)
            this.counters = counters
            counters.put(counter, ArrayList<Int?>(0))
        }
        val counterArray: ArrayList<Int?> = counters.get(counter)!!
        while (counterArray.size <= nesting) {
            counterArray.add(null)
        }
        val integer = counterArray.get(nesting)
        val prevValue = if (integer == null) 0 else integer
        counterArray.set(nesting, (prevValue + 1))
        return prevValue
    }

    override fun getBackgroundInfo(): BackgroundInfo? {
        run {
            val binfo = this.iBackgroundInfo
            if (binfo !== INVALID_BACKGROUND_INFO) {
                return binfo
            }
        }

        var binfo: BackgroundInfo? = null

        val props = this.cssProperties()
        if (props != null) {
            val backgroundColorText = props.backgroundColor
            if (backgroundColorText != null) {
                binfo = BackgroundInfo()
                binfo.backgroundColor = ColorFactory.instance?.getColor(backgroundColorText)
            }
            val backgroundImageText = props.getBackgroundImage()
            if ((backgroundImageText != null) && (!backgroundImageText.isEmpty())) {
                val backgroundImage = HtmlValues.getURIFromStyleValue(backgroundImageText)
                if (backgroundImage != null) {
                    if (binfo == null) {
                        binfo = BackgroundInfo()
                    }
                    binfo.backgroundImage = backgroundImage
                }
            }
            val backgroundRepeatText = props.backgroundRepeat
            if (backgroundRepeatText != null) {
                if (binfo == null) {
                    binfo = BackgroundInfo()
                }
                applyBackgroundRepeat(binfo, backgroundRepeatText)
            }
            val backgroundPositionText = props.backgroundPosition
            if (backgroundPositionText != null) {
                if (binfo == null) {
                    binfo = BackgroundInfo()
                }
                this.applyBackgroundPosition(binfo, backgroundPositionText)
            }
        }
        this.iBackgroundInfo = binfo
        return binfo
    }

    // private void applyBackground(BackgroundInfo binfo, String background,
    // CSSStyleDeclaration declaration) {
    // String[] tokens = HtmlValues.splitCssValue(background);
    // boolean hasXPosition = false;
    // for(int i = 0; i < tokens.length; i++) {
    // String token = tokens[i];
    // if(ColorFactory.getInstance().isColor(token)) {
    // binfo.backgroundColor = ColorFactory.getInstance().getColor(token);
    // }
    // else if(HtmlValues.isUrl(token)) {
    // binfo.backgroundImage = HtmlValues.getURIFromStyleValue(token, declaration,
    // this.document);
    // }
    // else if(isBackgroundRepeat(token)) {
    // this.applyBackgroundRepeat(binfo, token);
    // }
    // else if(isBackgroundPosition(token)) {
    // if(hasXPosition) {
    // this.applyBackgroundVerticalPosition(binfo, token);
    // }
    // else {
    // hasXPosition = true;
    // this.applyBackgroundHorizontalPositon(binfo, token);
    // }
    // }
    // }
    // }
    override fun getTextIndentText(): String {
        var tiText = this.iTextIndentText
        if (tiText != null) {
            return tiText
        }
        val props = this.cssProperties()
        tiText = if (props == null) null else props.textIndent
        if (tiText == null) {
            tiText = ""
        }
        return tiText
    }

    override fun getTextIndent(availSize: Int): Int {
        // No caching for this one.
        val tiText = this.getTextIndentText()
        if (tiText.length == 0) {
            return 0
        } else {
            return HtmlValues.getPixelSize(tiText, this, 0, availSize)
        }
    }


    override fun getWhiteSpace(): Int {
        if (RenderThreadState.Companion.state.overrideNoWrap) {
            return RenderState.Companion.WS_NOWRAP
        }
        val ws = this.iWhiteSpace
        if (ws != null) {
            return ws
        }
        val props = this.cssProperties()
        val whiteSpaceText = if (props == null) null else props.whiteSpace
        val wsValue: Int
        if (whiteSpaceText == null) {
            wsValue = RenderState.Companion.WS_NORMAL
        } else {
            val whiteSpaceTextTL = whiteSpaceText.lowercase(Locale.getDefault())
            if ("nowrap" == whiteSpaceTextTL) {
                wsValue = RenderState.Companion.WS_NOWRAP
            } else if ("pre" == whiteSpaceTextTL) {
                wsValue = RenderState.Companion.WS_PRE
            } else {
                wsValue = RenderState.Companion.WS_NORMAL
            }
        }
        this.iWhiteSpace = (wsValue)
        return wsValue
    }

    override fun getMarginInsets(): HtmlInsets? {
        var mi = this.marginInsets
        if (mi !== INVALID_INSETS) {
            return mi
        }
        val props = this.cssProperties()
        if (props == null) {
            mi = null
        } else {
            mi = HtmlValues.getMarginInsets(props, this)
        }
        this.marginInsets = mi
        return mi
    }

    override fun getPaddingInsets(): HtmlInsets? {
        var mi = this.paddingInsets
        if (mi !== INVALID_INSETS) {
            return mi
        }
        val props = this.cssProperties()
        if (props == null) {
            mi = null
        } else {
            mi = HtmlValues.getPaddingInsets(props, this)
            this.paddingInsets = mi
        }
        return mi
    }

    private fun applyBackgroundHorizontalPositon(binfo: BackgroundInfo, xposition: String) {
        if (xposition.endsWith("%")) {
            binfo.backgroundXPositionAbsolute = false
            try {
                binfo.backgroundXPosition =
                    xposition.substring(0, xposition.length - 1).trim { it <= ' ' }.toDouble()
                        .toInt()
            } catch (nfe: NumberFormatException) {
                binfo.backgroundXPosition = 0
            }
        } else if ("center".equals(xposition, ignoreCase = true)) {
            binfo.backgroundXPositionAbsolute = false
            binfo.backgroundXPosition = 50
        } else if ("right".equals(xposition, ignoreCase = true)) {
            binfo.backgroundXPositionAbsolute = false
            binfo.backgroundXPosition = 100
        } else if ("left".equals(xposition, ignoreCase = true)) {
            binfo.backgroundXPositionAbsolute = false
            binfo.backgroundXPosition = 0
        } else if ("bottom".equals(xposition, ignoreCase = true)) {
            // Can happen
            binfo.backgroundYPositionAbsolute = false
            binfo.backgroundYPosition = 100
        } else if ("top".equals(xposition, ignoreCase = true)) {
            // Can happen
            binfo.backgroundYPositionAbsolute = false
            binfo.backgroundYPosition = 0
        } else {
            binfo.backgroundXPositionAbsolute = true
            binfo.backgroundXPosition = HtmlValues.getPixelSize(xposition, this, 0)
        }
    }

    private fun applyBackgroundVerticalPosition(binfo: BackgroundInfo, yposition: String) {
        if (yposition.endsWith("%")) {
            binfo.backgroundYPositionAbsolute = false
            try {
                binfo.backgroundYPosition =
                    yposition.substring(0, yposition.length - 1).trim { it <= ' ' }.toDouble()
                        .toInt()
            } catch (nfe: NumberFormatException) {
                binfo.backgroundYPosition = 0
            }
        } else if ("center".equals(yposition, ignoreCase = true)) {
            binfo.backgroundYPositionAbsolute = false
            binfo.backgroundYPosition = 50
        } else if ("bottom".equals(yposition, ignoreCase = true)) {
            binfo.backgroundYPositionAbsolute = false
            binfo.backgroundYPosition = 100
        } else if ("top".equals(yposition, ignoreCase = true)) {
            binfo.backgroundYPositionAbsolute = false
            binfo.backgroundYPosition = 0
        } else if ("right".equals(yposition, ignoreCase = true)) {
            // Can happen
            binfo.backgroundXPositionAbsolute = false
            binfo.backgroundXPosition = 100
        } else if ("left".equals(yposition, ignoreCase = true)) {
            // Can happen
            binfo.backgroundXPositionAbsolute = false
            binfo.backgroundXPosition = 0
        } else {
            binfo.backgroundYPositionAbsolute = true
            binfo.backgroundYPosition = HtmlValues.getPixelSize(yposition, this, 0)
        }
    }

    private fun applyBackgroundPosition(binfo: BackgroundInfo, position: String) {
        binfo.backgroundXPositionAbsolute = false
        binfo.backgroundYPositionAbsolute = false
        binfo.backgroundXPosition = 50
        binfo.backgroundYPosition = 50
        val tok = StringTokenizer(position, " \t\r\n")
        if (tok.hasMoreTokens()) {
            val xposition = tok.nextToken()
            this.applyBackgroundHorizontalPositon(binfo, xposition)
            if (tok.hasMoreTokens()) {
                val yposition = tok.nextToken()
                this.applyBackgroundVerticalPosition(binfo, yposition)
            }
        }
    }

    override fun getVisibility(): Int {
        val v = this.cachedVisibility
        if (v != null) {
            return v
        }
        val props = this.cssProperties()
        val visibility: Int
        if (props == null) {
            visibility = RenderState.Companion.VISIBILITY_VISIBLE
        } else {
            val visibText = props.visibility
            if ((visibText == null) || (visibText.length == 0)) {
                visibility = RenderState.Companion.VISIBILITY_VISIBLE
            } else {
                val visibTextTL = visibText.lowercase(Locale.getDefault())
                if (visibTextTL == "hidden") {
                    visibility = RenderState.Companion.VISIBILITY_HIDDEN
                } else if (visibTextTL == "visible") {
                    visibility = RenderState.Companion.VISIBILITY_VISIBLE
                } else if (visibTextTL == "collapse") {
                    visibility = RenderState.Companion.VISIBILITY_COLLAPSE
                } else {
                    visibility = RenderState.Companion.VISIBILITY_VISIBLE
                }
            }
        }
        this.cachedVisibility = (visibility)
        return visibility
    }

    override fun getPosition(): Int {
        val p = this.cachedPosition
        if (p != null) {
            return p
        }
        val props = this.cssProperties()
        val position: Int
        if (props == null) {
            position = RenderState.Companion.POSITION_STATIC
        } else {
            val positionText = props.position
            if ((positionText == null) || (positionText.isEmpty())) {
                position = RenderState.Companion.POSITION_STATIC
            } else {
                val positionTextTL = positionText.lowercase(Locale.getDefault())
                if (positionTextTL == "absolute") {
                    position = RenderState.Companion.POSITION_ABSOLUTE
                } else if (positionTextTL == "static") {
                    position = RenderState.Companion.POSITION_STATIC
                } else if (positionTextTL == "relative") {
                    position = RenderState.Companion.POSITION_RELATIVE
                } else if (positionTextTL == "fixed") {
                    position = RenderState.Companion.POSITION_FIXED
                } else {
                    position = RenderState.Companion.POSITION_STATIC
                }
            }
        }
        this.cachedPosition = (position)
        return position
    }

    override fun getFloat(): Int {
        val p = this.cachedFloat
        if (p != null) {
            return p
        }
        val props = this.cssProperties()
        val floatValue: Int
        if (props == null) {
            floatValue = RenderState.Companion.FLOAT_NONE
        } else {
            val floatText = props.float
            if ((floatText == null) || (floatText.isEmpty())) {
                floatValue = RenderState.Companion.FLOAT_NONE
            } else {
                val floatTextTL = floatText.lowercase(Locale.getDefault())
                if (floatTextTL == "left") {
                    floatValue = RenderState.Companion.FLOAT_LEFT
                } else if (floatTextTL == "right") {
                    floatValue = RenderState.Companion.FLOAT_RIGHT
                } else {
                    floatValue = RenderState.Companion.FLOAT_NONE
                }
            }
        }
        this.cachedFloat = (floatValue)
        return floatValue
    }

    override fun getClear(): Int {
        if (cachedClear == null) {
            val props = this.cssProperties()
            if (props == null) {
                cachedClear = (LineBreak.NONE)
            } else {
                val clearStr = this.cssProperties()!!.clear
                if ("both" == clearStr) {
                    cachedClear = (LineBreak.ALL)
                } else if ("left" == clearStr) {
                    cachedClear = (LineBreak.LEFT)
                } else if ("right" == clearStr) {
                    cachedClear = (LineBreak.RIGHT)
                } else {
                    cachedClear = (LineBreak.NONE)
                }
            }
        }
        return cachedClear!!
    }

    override fun toString(): String {
        return "StyleSheetRenderState[font=" + this.getFont() + ",textDecoration=" + this.getTextDecorationMask() + "]"
    }

    override fun getOverflowX(): Int {
        var overflow = this.overflowX
        if (overflow != -1) {
            return overflow
        }
        val props = this.cssProperties()
        if (props == null) {
            overflow = RenderState.Companion.OVERFLOW_NONE
        } else {
            // TODO need to implement specific method for this instead of using getPropertyValue.
            var overflowText = props.getPropertyValue("overflow-x")
            if (overflowText == null) {
                overflowText = props.overflow
            }
            if (overflowText == null) {
                overflow = RenderState.Companion.OVERFLOW_NONE
            } else {
                val overflowTextTL = overflowText.lowercase(Locale.getDefault())
                if ("scroll" == overflowTextTL) {
                    overflow = RenderState.Companion.OVERFLOW_SCROLL
                } else if ("auto" == overflowTextTL) {
                    overflow = RenderState.Companion.OVERFLOW_AUTO
                } else if ("hidden" == overflowTextTL) {
                    overflow = RenderState.Companion.OVERFLOW_HIDDEN
                } else if ("visible" == overflowTextTL) {
                    overflow = RenderState.Companion.OVERFLOW_VISIBLE
                } else {
                    overflow = RenderState.Companion.OVERFLOW_NONE
                }
            }
        }
        this.overflowX = overflow
        return overflow
    }

    override fun getOverflowY(): Int {
        var overflow = this.overflowY
        if (overflow != -1) {
            return overflow
        }
        val props = this.cssProperties()
        if (props == null) {
            overflow = RenderState.Companion.OVERFLOW_NONE
        } else {
            // TODO need to implement specific method for this instead of using getPropertyValue.
            var overflowText = props.getPropertyValue("overflow-y")
            if (overflowText == null) {
                overflowText = props.overflow
            }
            if (overflowText == null) {
                overflow = RenderState.Companion.OVERFLOW_NONE
            } else {
                val overflowTextTL = overflowText.lowercase(Locale.getDefault())
                if ("scroll" == overflowTextTL) {
                    overflow = RenderState.Companion.OVERFLOW_SCROLL
                } else if ("auto" == overflowTextTL) {
                    overflow = RenderState.Companion.OVERFLOW_AUTO
                } else if ("hidden" == overflowTextTL) {
                    overflow = RenderState.Companion.OVERFLOW_HIDDEN
                } else if ("visible" == overflowTextTL) {
                    overflow = RenderState.Companion.OVERFLOW_VISIBLE
                } else {
                    overflow = RenderState.Companion.OVERFLOW_NONE
                }
            }
        }
        this.overflowY = overflow
        return overflow
    }

    override fun getBorderInfo(): BorderInfo? {
        var binfo = this.borderInfo
        if (binfo !== INVALID_BORDER_INFO) {
            return binfo
        }
        val props = this.cssProperties()
        if (props != null) {
            binfo = HtmlValues.getBorderInfo(props, this)
        } else {
            binfo = null
        }
        this.borderInfo = binfo
        return binfo
    }

    override fun getCursor(): Optional<Cursor> {
        val prevCursorOpt: Optional<Cursor> = Optional.empty<Cursor>()
        val props = this.cssProperties()
        if (props == null) {
            return prevCursorOpt
        } else {
            // TODO need to implement specific method for this instead of using getPropertyValue.
            val cursor = props.getPropertyValue("cursor")
            if (cursor == null) {
                return prevCursorOpt
            } else {
                val cursorTL = cursor.lowercase(Locale.getDefault())
                return Optional.of(Cursor(cursorTL))
            }
        }
    }

    override fun getLeft(): String? {
        val props = this.cssProperties()
        return if (props == null) null else props.left
    }

    override fun getTop(): String? {
        val props = this.cssProperties()
        return if (props == null) null else props.top
    }

    override fun getRight(): String? {
        val props = this.cssProperties()
        return if (props == null) null else props.right
    }

    override fun getBottom(): String? {
        val props = this.cssProperties()
        return if (props == null) null else props.bottom
    }

    override fun getFontXHeight(): Double {
        // TODO:  (looks shit here)

        val font = getFont()!!

        return 0.8 * font.fontSize

    }

    // TODO: This should return a more abstract type that can represent values like length and percentage
    override fun getVerticalAlign(): VerticalAlign? {
        val props = this.cssProperties()
        val valignProperty = props!!.getNodeData()?.getProperty<VerticalAlign?>("vertical-align")
        return valignProperty
    }

    companion object {
        @JvmStatic
        protected val INVALID_INSETS: HtmlInsets = HtmlInsets()

        @JvmStatic
        protected val INVALID_BACKGROUND_INFO: BackgroundInfo = BackgroundInfo()

        @JvmStatic
        protected val INVALID_BORDER_INFO: BorderInfo = BorderInfo()
        protected val INVALID_COLOR: Color = Color(100, 0, 100)


        // Default font needs to be something that displays in all languages.
        // Serif, SansSerif, Monospaced.
        private const val DEFAULT_FONT_FAMILY = "SansSerif"
        private val DEFAULT_FONT: Font? = Font(
            DEFAULT_FONT_FAMILY, null, null, null,
            HtmlValues.DEFAULT_FONT_SIZE, null
        )

        private fun applyBackgroundRepeat(binfo: BackgroundInfo, backgroundRepeatText: String) {
            val brtl = backgroundRepeatText.lowercase(Locale.getDefault())
            if ("repeat" == brtl) {
                binfo.backgroundRepeat = BackgroundInfo.Companion.BR_REPEAT
            } else if ("repeat-x" == brtl) {
                binfo.backgroundRepeat = BackgroundInfo.Companion.BR_REPEAT_X
            } else if ("repeat-y" == brtl) {
                binfo.backgroundRepeat = BackgroundInfo.Companion.BR_REPEAT_Y
            } else if ("no-repeat" == brtl) {
                binfo.backgroundRepeat = BackgroundInfo.Companion.BR_NO_REPEAT
            }
        }
    }
}
