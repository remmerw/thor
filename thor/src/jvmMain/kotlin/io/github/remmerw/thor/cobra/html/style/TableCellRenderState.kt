package io.github.remmerw.thor.cobra.html.style

import io.github.remmerw.thor.cobra.html.domimpl.HTMLElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLTableCellElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLTableRowElementImpl
import io.github.remmerw.thor.cobra.util.gui.ColorFactory
import org.w3c.dom.css.CSS2Properties
import org.w3c.dom.html.HTMLElement
import org.w3c.dom.html.HTMLTableElement
import java.net.MalformedURLException
import java.util.Locale

class TableCellRenderState(prevRenderState: RenderState?, element: HTMLElementImpl) :
    StyleSheetRenderState(prevRenderState, element) {
    override var alignXPercent = -1
    override var alignYPercent = -1
    override var backgroundInfo: BackgroundInfo? =
        INVALID_BACKGROUND_INFO

    override fun invalidate() {
        super.invalidate()
        this.alignXPercent = -1
        this.alignYPercent = -1
        this.backgroundInfo = INVALID_BACKGROUND_INFO
        this.paddingInsets = INVALID_INSETS
    }

    override fun getAlignXPercent(): Int {
        var axp = this.alignXPercent
        if (axp != -1) {
            return axp
        }
        val props: CSS2Properties? = this.cssProperties
        if (props != null) {
            val textAlign = props.textAlign
            if ((textAlign != null) && (textAlign.length != 0)) {
                return super.getAlignXPercent()
            }
        }
        // Parent already knows about "align" attribute, but override because of TH.
        var align = this.element!!.getAttribute("align")
        val element: HTMLElement = this.element
        var rowElement: HTMLElement? = null
        val parent: Any? = element.parentNode
        if (parent is HTMLElement) {
            rowElement = parent
        }
        if ((align == null) || (align.length == 0)) {
            if (rowElement != null) {
                align = rowElement.getAttribute("align")
                if ((align != null) && (align.length == 0)) {
                    align = null
                }
            } else {
                align = null
            }
        }
        if (align == null) {
            if ("TH".equals(element.nodeName, ignoreCase = true)) {
                axp = 50
            } else {
                axp = 0
            }
        } else if ("center".equals(align, ignoreCase = true) || "middle".equals(
                align,
                ignoreCase = true
            )
        ) {
            axp = 50
        } else if ("left".equals(align, ignoreCase = true)) {
            axp = 0
        } else if ("right".equals(align, ignoreCase = true)) {
            axp = 100
        } else {
            // TODO: justify, etc.
            axp = 0
        }
        this.alignXPercent = axp
        return axp
    }

    override fun getAlignYPercent(): Int {
        var ayp = this.alignYPercent
        if (ayp != -1) {
            return ayp
        }
        val props: CSS2Properties? = this.cssProperties
        if (props != null) {
            val textAlign = props.verticalAlign
            if ((textAlign != null) && (textAlign.length != 0)) {
                return super.getAlignYPercent()
            }
        }
        var valign = this.element!!.getAttribute("valign")
        val element: HTMLElement = this.element
        var rowElement: HTMLElement? = null
        val parent: Any? = element.parentNode
        if (parent is HTMLElement) {
            rowElement = parent
        }
        if ((valign == null) || (valign.length == 0)) {
            if (rowElement != null) {
                valign = rowElement.getAttribute("valign")
                if ((valign != null) && (valign.length == 0)) {
                    valign = null
                }
            } else {
                valign = null
            }
        }
        if (valign == null) {
            ayp = 50
        } else if ("top".equals(valign, ignoreCase = true)) {
            ayp = 0
        } else if ("middle".equals(valign, ignoreCase = true) || "center".equals(
                valign,
                ignoreCase = true
            )
        ) {
            ayp = 50
        } else if ("bottom".equals(valign, ignoreCase = true)) {
            ayp = 100
        } else {
            // TODO: baseline, etc.
            ayp = 50
        }
        this.alignYPercent = ayp
        return ayp
    }

    override fun getBackgroundInfo(): BackgroundInfo? {
        var binfo = this.backgroundInfo
        if (binfo !== INVALID_BACKGROUND_INFO) {
            return binfo
        }
        // Apply style based on deprecated attributes.
        binfo = super.getBackgroundInfo()
        val element = this.element as HTMLTableCellElementImpl
        var rowElement: HTMLTableRowElementImpl? = null
        val parentNode: Any? = element.parentNode
        if (parentNode is HTMLTableRowElementImpl) {
            rowElement = parentNode
        }
        if ((binfo == null) || (binfo.backgroundColor == null)) {
            var bgColor = element.bgColor
            if ((bgColor == null) || "" == bgColor) {
                if (rowElement != null) {
                    bgColor = rowElement.bgColor
                }
            }
            if ((bgColor != null) && "" != bgColor) {
                val bgc = ColorFactory.instance!!.getColor(bgColor)
                if (binfo == null) {
                    binfo = BackgroundInfo()
                }
                binfo.backgroundColor = bgc
            }
        }
        if ((binfo == null) || (binfo.backgroundImage == null)) {
            val background = element.getAttribute("background")
            if ((background != null) && "" != background) {
                if (binfo == null) {
                    binfo = BackgroundInfo()
                }
                try {
                    binfo.backgroundImage = this.document?.getFullURL(background)
                } catch (mfe: MalformedURLException) {
                    throw IllegalArgumentException(mfe)
                }
            }
        }
        this.backgroundInfo = binfo
        return binfo
    }

    private val tableElement: HTMLTableElement?
        get() {
            var ancestor = this.element!!.getParentNode()
            while ((ancestor != null) && ancestor !is HTMLTableElement) {
                ancestor = ancestor.parentNode
            }
            return ancestor
        }

    override fun getPaddingInsets(): HtmlInsets? {
        var insets = this.paddingInsets
        if (insets !== INVALID_INSETS) {
            return insets
        }
        insets = super.getPaddingInsets()
        if (insets == null) {
            val tableElement = this.tableElement
            if (tableElement == null) {
                // Return without caching
                return null
            }
            var cellPaddingText = tableElement.getAttribute("cellpadding")
            if ((cellPaddingText != null) && (cellPaddingText.length != 0)) {
                cellPaddingText = cellPaddingText.trim { it <= ' ' }
                var cellPadding: Int
                val cellPaddingType: Int
                if (cellPaddingText.endsWith("%")) {
                    cellPaddingType = HtmlInsets.Companion.TYPE_PERCENT
                    try {
                        cellPadding =
                            cellPaddingText.substring(0, cellPaddingText.length - 1).toInt()
                    } catch (nfe: NumberFormatException) {
                        cellPadding = 0
                    }
                } else {
                    cellPaddingType = HtmlInsets.Companion.TYPE_PIXELS
                    try {
                        cellPadding = cellPaddingText.toInt()
                    } catch (nfe: NumberFormatException) {
                        cellPadding = 0
                    }
                }
                insets = HtmlInsets()
                insets.bottom = cellPadding
                insets.right = insets.bottom
                insets.left = insets.right
                insets.top = insets.left
                insets.bottomType = cellPaddingType
                insets.rightType = insets.bottomType
                insets.leftType = insets.rightType
                insets.topType = insets.leftType
            }
        }
        this.paddingInsets = insets
        return insets
    }

    override fun getWhiteSpace(): Int {
        // Overrides super.
        if (RenderThreadState.Companion.state.overrideNoWrap) {
            return RenderState.Companion.WS_NOWRAP
        }
        val ws = this.iWhiteSpace
        if (ws != null) {
            return ws
        }
        val props = this.cssProperties
        val whiteSpaceText = if (props == null) null else props.getWhiteSpace()
        var wsValue: Int
        if (whiteSpaceText == null) {
            val element = this.element
            if ((element != null) && element.getAttributeAsBoolean("nowrap")) {
                wsValue = RenderState.Companion.WS_NOWRAP
            } else {
                val prs = this.prevRenderState
                if (prs != null) {
                    wsValue = prs.whiteSpace
                } else {
                    wsValue = RenderState.Companion.WS_NORMAL
                }
            }
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
        if (wsValue == RenderState.Companion.WS_NOWRAP) {
            // In table cells, if the width is defined as an absolute value,
            // nowrap has no effect (IE and FireFox behavior).
            val element = this.element
            var width = if (props == null) null else props.getWidth()
            if (width == null) {
                width = element?.getAttribute("width")
                if ((width != null) && (width.length > 0) && !width.endsWith("%")) {
                    wsValue = RenderState.Companion.WS_NORMAL
                }
            } else {
                if (!width.trim { it <= ' ' }.endsWith("%")) {
                    wsValue = RenderState.Companion.WS_NORMAL
                }
            }
        }
        this.iWhiteSpace = (wsValue)
        return wsValue
    }
}
