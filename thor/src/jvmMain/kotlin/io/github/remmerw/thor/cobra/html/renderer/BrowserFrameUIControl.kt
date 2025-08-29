package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.BrowserFrame
import io.github.remmerw.thor.cobra.html.style.HtmlInsets
import io.github.remmerw.thor.cobra.html.style.HtmlValues
import io.github.remmerw.thor.cobra.html.style.RenderState
import org.w3c.dom.html.HTMLElement
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics

internal class BrowserFrameUIControl(
    private val element: HTMLElement,
    private val browserFrame: BrowserFrame
) : UIControl {

    private var ruiControl: RUIControl? = null
    private var availWidth = 0
    private var availHeight = 0

    init {
        this.component = browserFrame.component
    }

    fun getBackgroundColor(): Color? {
        return this.component?.getBackground()
    }

    fun getComponent(): Component {
        return this.component!!
    }

    override fun reset(availWidth: Int, availHeight: Int) {
        this.availWidth = availWidth
        this.availHeight = availHeight
        val ruiControl = this.ruiControl
        if (ruiControl != null) {
            val node = ruiControl.getModelNode()
            val element = node as HTMLElement?
            val renderState: RenderState = node?.renderState!!
            var insets: HtmlInsets? = null
            var marginwidth = element!!.getAttribute("marginwidth")
            var marginheight = element.getAttribute("marginheight")
            if ((marginwidth != null) && (marginwidth.length != 0)) {
                insets = HtmlInsets()
                marginwidth = marginwidth.trim { it <= ' ' }
                if (marginwidth.endsWith("%")) {
                    var value: Int
                    try {
                        value = marginwidth.substring(0, marginwidth.length - 1).toInt()
                    } catch (nfe: NumberFormatException) {
                        value = 0
                    }
                    insets.left = value
                    insets.right = value
                    insets.leftType = HtmlInsets.TYPE_PERCENT
                    insets.rightType = HtmlInsets.TYPE_PERCENT
                } else {
                    var value: Int
                    try {
                        value = marginwidth.toInt()
                    } catch (nfe: NumberFormatException) {
                        value = 0
                    }
                    insets.left = value
                    insets.right = value
                    insets.leftType = HtmlInsets.TYPE_PIXELS
                    insets.rightType = HtmlInsets.TYPE_PIXELS
                }
            }
            if ((marginheight != null) && (marginheight.length != 0)) {
                if (insets == null) {
                    insets = HtmlInsets()
                }
                marginheight = marginheight.trim { it <= ' ' }
                if (marginheight.endsWith("%")) {
                    var value: Int
                    try {
                        value = marginheight.substring(0, marginheight.length - 1).toInt()
                    } catch (nfe: NumberFormatException) {
                        value = 0
                    }
                    insets.top = value
                    insets.bottom = value
                    insets.topType = HtmlInsets.TYPE_PERCENT
                    insets.bottomType = HtmlInsets.TYPE_PERCENT
                } else {
                    var value: Int
                    try {
                        value = marginheight.toInt()
                    } catch (nfe: NumberFormatException) {
                        value = 0
                    }
                    insets.top = value
                    insets.bottom = value
                    insets.topType = HtmlInsets.TYPE_PIXELS
                    insets.bottomType = HtmlInsets.TYPE_PIXELS
                }
            }
            val awtMarginInsets =
                if (insets == null) null else insets.getSimpleAWTInsets(availWidth, availHeight)
            val overflowX = renderState.overflowX
            val overflowY = renderState.overflowY
            if (awtMarginInsets != null) {
                this.browserFrame.setDefaultMarginInsets(awtMarginInsets)
            }
            if (overflowX != RenderState.OVERFLOW_NONE) {
                this.browserFrame.setDefaultOverflowX(overflowX)
            }
            if (overflowY != RenderState.OVERFLOW_NONE) {
                this.browserFrame.setDefaultOverflowY(overflowY)
            }
        }
    }

    fun getPreferredSize(): Dimension {
        val width =
            HtmlValues.getOldSyntaxPixelSize(element.getAttribute("width"), this.availWidth, 100)
        val height =
            HtmlValues.getOldSyntaxPixelSize(element.getAttribute("height"), this.availHeight, 100)
        return Dimension(width, height)
    }

    override fun invalidate() {
        this.component!!.invalidate()
    }

    override fun paint(g: Graphics?) {
        // We actually have to paint it.
        this.component!!.paint(g)
    }

    fun paintSelection(
        g: Graphics?,
        inSelection: Boolean,
        startPoint: RenderableSpot?,
        endPoint: RenderableSpot?
    ): Boolean {
        // Selection does not cross in here?
        return false
    }

    override fun setBounds(x: Int, y: Int, width: Int, height: Int) {
        this.component!!.setBounds(x, y, width, height)
    }

    override fun setRUIControl(ruicontrol: RUIControl?) {
        this.ruiControl = ruicontrol
    }
}
