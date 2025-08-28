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
        return delegate.getAlignXPercent()
    }

    fun getAlignYPercent(): Int {
        return delegate.getAlignYPercent()
    }

    fun getBlankWidth(): Int {
        return delegate.getBlankWidth()
    }

    fun getColor(): Color? {
        return delegate.getColor()
    }

    fun getFont(): Font? {
        return delegate.getFont()
    }

    open fun getFontBase(): Int {
        return delegate.getFontBase()
    }

    fun getFontMetrics(): FontMetrics? {
        return delegate.getFontMetrics()
    }

    fun getOverlayColor(): Color? {
        return delegate.getOverlayColor()
    }

    fun getBackgroundColor(): Color? {
        return delegate.getBackgroundColor()
    }

    fun getTextDecorationMask(): Int {
        return delegate.getTextDecorationMask()
    }

    fun getTextTransform(): Int {
        return delegate.getTextTransform()
    }

    override fun getWordInfo(word: String?): WordInfo? {
        return delegate.getWordInfo(word)
    }

    override fun invalidate() {
        delegate.invalidate()
    }

    fun isHighlight(): Boolean {
        return delegate.isHighlight
    }

    fun setHighlight(highlight: Boolean) {
        delegate.setHighlight(highlight)
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
        return this.delegate.getBackgroundInfo()
    }

    fun getDisplay(): Int {
        return this.delegate.display
    }

    fun getTextBackgroundColor(): Color? {
        return this.delegate.textBackgroundColor
    }

    override fun getTextIndent(availWidth: Int): Int {
        return this.delegate.getTextIndent(availWidth)
    }

    fun getTextIndentText(): String? {
        return this.delegate.getTextIndentText()
    }

    fun getWhiteSpace(): Int {
        return this.delegate.getWhiteSpace()
    }

    fun getMarginInsets(): HtmlInsets? {
        return this.delegate.getMarginInsets()
    }

    fun getPaddingInsets(): HtmlInsets? {
        return this.delegate.getPaddingInsets()
    }

    fun getVisibility(): Int {
        return this.delegate.getVisibility()
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
        return this.delegate.getOverflowY()
    }

    fun getBorderInfo(): BorderInfo? {
        return this.delegate.getBorderInfo()
    }

    fun getCursor(): Optional<Cursor?>? {
        return this.delegate.getCursor()
    }

    fun getLeft(): String? {
        return this.delegate.getLeft()
    }

    fun getTop(): String? {
        return this.delegate.getTop()
    }

    fun getBottom(): String? {
        return this.delegate.getBottom()
    }

    fun getRight(): String? {
        return this.delegate.getRight()
    }

    fun getFontXHeight(): Double {
        return this.delegate.getFontXHeight()
    }

    fun getVerticalAlign(): VerticalAlign? {
        return this.delegate.getVerticalAlign()
    }
}
