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

import cz.vutbr.web.css.CSSProperty.VerticalAlign
import java.awt.Color
import java.awt.Cursor
import java.awt.Font
import java.awt.FontMetrics
import java.util.Optional

abstract class RenderStateDelegator(protected val delegate: RenderState) : RenderState {
    fun getAlignXPercent(): Int {
        return delegate.alignXPercent
    }

    fun getAlignYPercent(): Int {
        return delegate.alignYPercent
    }

    fun getBlankWidth(): Int {
        return delegate.blankWidth
    }

    fun getColor(): Color? {
        return delegate.color
    }

    fun getFont(): Font? {
        return delegate.font
    }

    open fun getFontBase(): Int {
        return delegate.fontBase
    }

    fun getFontMetrics(): FontMetrics? {
        return delegate.fontMetrics
    }

    fun getOverlayColor(): Color? {
        return delegate.overlayColor
    }

    fun getBackgroundColor(): Color? {
        return delegate.backgroundColor
    }

    fun getTextDecorationMask(): Int {
        return delegate.textDecorationMask
    }

    fun getTextTransform(): Int {
        return delegate.textTransform
    }

    override val position: Int
        get() = TODO("Not yet implemented")
    override val float: Int
        get() = TODO("Not yet implemented")
    override val clear: Int
        get() = TODO("Not yet implemented")
    override val visibility: Int
        get() = TODO("Not yet implemented")
    override val font: Font?
        get() = TODO("Not yet implemented")
    override val fontBase: Int
        get() = TODO("Not yet implemented")

    override fun getWordInfo(word: String): WordInfo {
        return delegate.getWordInfo(word)
    }

    override val color: Color?
        get() = TODO("Not yet implemented")
    override val backgroundColor: Color?
        get() = TODO("Not yet implemented")
    override val textBackgroundColor: Color?
        get() = TODO("Not yet implemented")
    override val backgroundInfo: BackgroundInfo?
        get() = TODO("Not yet implemented")
    override val overlayColor: Color?
        get() = TODO("Not yet implemented")
    override val textTransform: Int
        get() = TODO("Not yet implemented")
    override val textDecorationMask: Int
        get() = TODO("Not yet implemented")
    override val fontMetrics: FontMetrics?
        get() = TODO("Not yet implemented")
    override val fontXHeight: Double
        get() = TODO("Not yet implemented")
    override val blankWidth: Int
        get() = TODO("Not yet implemented")
    override var isHighlight: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override val alignXPercent: Int
        get() = TODO("Not yet implemented")
    override val alignYPercent: Int
        get() = TODO("Not yet implemented")

    override fun invalidate() {
        delegate.invalidate()
    }

    override val borderInfo: BorderInfo?
        get() = TODO("Not yet implemented")
    override val cursor: Optional<Cursor>?
        get() = TODO("Not yet implemented")
    override val left: String?
        get() = TODO("Not yet implemented")
    override val top: String?
        get() = TODO("Not yet implemented")
    override val right: String?
        get() = TODO("Not yet implemented")
    override val bottom: String?
        get() = TODO("Not yet implemented")


    fun isHighlight(): Boolean {
        return delegate.isHighlight
    }

    fun setHighlight(highlight: Boolean) {
        delegate.isHighlight = (highlight)
    }

    override fun getCount(counter: String?, nesting: Int): Int {
        return this.delegate.getCount(counter, nesting)
    }


    override fun resetCount(counter: String?, nesting: Int, value: Int) {
        this.delegate.resetCount(counter, nesting, value)
    }

    override fun incrementCount(counter: String?, nesting: Int): Int {
        return this.delegate.incrementCount(counter, nesting)
    }

    fun getBackgroundInfo(): BackgroundInfo? {
        return this.delegate.backgroundInfo
    }

    override fun getDisplay(): Int {
        return this.delegate.getDisplay()
    }

    fun getTextBackgroundColor(): Color? {
        return this.delegate.textBackgroundColor
    }

    override fun getTextIndent(availWidth: Int): Int {
        return this.delegate.getTextIndent(availWidth)
    }

    override val textIndentText: String?
        get() = TODO("Not yet implemented")
    override val whiteSpace: Int
        get() = TODO("Not yet implemented")
    override val marginInsets: HtmlInsets?
        get() = TODO("Not yet implemented")
    override val paddingInsets: HtmlInsets?
        get() = TODO("Not yet implemented")
    override val overflowX: Int
        get() = TODO("Not yet implemented")
    override val overflowY: Int
        get() = TODO("Not yet implemented")

    fun getTextIndentText(): String? {
        return this.delegate.textIndentText
    }

    fun getWhiteSpace(): Int {
        return this.delegate.whiteSpace
    }

    fun getMarginInsets(): HtmlInsets? {
        return this.delegate.marginInsets
    }

    fun getPaddingInsets(): HtmlInsets? {
        return this.delegate.paddingInsets
    }

    fun getVisibility(): Int {
        return this.delegate.visibility
    }

    fun getPosition(): Int {
        return this.delegate.position
    }

    fun getFloat(): Int {
        return this.delegate.float
    }

    fun getClear(): Int {
        return this.delegate.clear
    }

    fun getOverflowX(): Int {
        return this.delegate.overflowX
    }

    fun getOverflowY(): Int {
        return this.delegate.overflowY
    }

    fun getBorderInfo(): BorderInfo? {
        return this.delegate.borderInfo
    }

    fun getCursor(): Optional<Cursor>? {
        return this.delegate.cursor
    }

    fun getLeft(): String? {
        return this.delegate.left
    }

    fun getTop(): String? {
        return this.delegate.top
    }

    fun getBottom(): String? {
        return this.delegate.bottom
    }

    fun getRight(): String? {
        return this.delegate.right
    }

    fun getFontXHeight(): Double {
        return this.delegate.fontXHeight
    }

    override fun getVerticalAlign(): VerticalAlign? {
        return this.delegate.getVerticalAlign()
    }
}
