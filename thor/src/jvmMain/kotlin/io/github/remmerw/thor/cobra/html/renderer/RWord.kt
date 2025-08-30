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
/*
 * Created on Apr 17, 2005
 */
package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import io.github.remmerw.thor.cobra.html.style.RenderState
import java.awt.Dimension
import java.awt.FontMetrics
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.MouseEvent
import java.util.Locale

open class RWord(
    me: ModelNode?, word: String, container: RenderableContainer?, fontMetrics: FontMetrics,
    descent: Int, ascentPlusLeading: Int,
    height: Int, textTransform: Int
) : BaseBoundableRenderable(container, me) {
    val fontMetrics: FontMetrics
    val descent: Int
    val ascentPlusLeading: Int
    val shownWord: String

    init {
        val renderedWord =
            (if (textTransform == RenderState.TEXTTRANSFORM_NONE) word else transformText(
                word,
                textTransform
            ))!!
        this.shownWord = renderedWord
        this.fontMetrics = fontMetrics
        this.descent = descent
        this.ascentPlusLeading = ascentPlusLeading
        this.height = height
        // TODO: In anti-aliasing, stringWidth is said not to be reliable.
        // Dimensions set when constructed.
        this.width = fontMetrics.stringWidth(renderedWord)
    }

    override fun invalidateLayoutLocal() {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * net.sourceforge.xamj.domimpl.markup.Renderable#paint(java.awt.Graphics)
     */
    override fun paint(g: Graphics) {
        val rs: RenderState = this.modelNode!!.renderState()!!

        if (rs.visibility != RenderState.VISIBILITY_VISIBLE) {
            // Just don't paint it.
            return
        }

        val word = this.shownWord
        val width = this.width
        val ascentPlusLeading = this.ascentPlusLeading
        val height = this.height
        val textDecoration = rs.textDecorationMask
        val bkg = rs.textBackgroundColor
        if (bkg != null) {
            val oldColor = g.color
            try {
                g.color = bkg
                g.fillRect(0, 0, width, height)
            } finally {
                g.color = oldColor
            }
        }
        g.drawString(word, 0, ascentPlusLeading)
        val td = textDecoration
        if (td != 0) {
            if ((td and RenderState.MASK_TEXTDECORATION_UNDERLINE) != 0) {
                val lineOffset = ascentPlusLeading + 2
                g.drawLine(0, lineOffset, width, lineOffset)
            }
            if ((td and RenderState.MASK_TEXTDECORATION_LINE_THROUGH) != 0) {
                val fm = this.fontMetrics
                val lineOffset = fm.leading + ((fm.ascent + fm.descent) / 2)
                g.drawLine(0, lineOffset, width, lineOffset)
            }
            if ((td and RenderState.MASK_TEXTDECORATION_OVERLINE) != 0) {
                val fm = this.fontMetrics
                val lineOffset = fm.leading
                g.drawLine(0, lineOffset, width, lineOffset)
            }
            if ((td and RenderState.MASK_TEXTDECORATION_BLINK) != 0) {
                // TODO
            }
        }
        val over = rs.overlayColor
        if (over != null) {
            val oldColor = g.color
            try {
                g.color = over
                g.fillRect(0, 0, width, height)
            } finally {
                g.color = oldColor
            }
        }
    }

    override fun paintSelection(
        g: Graphics,
        inSelection: Boolean,
        startPoint: RenderableSpot,
        endPoint: RenderableSpot
    ): Boolean {
        var startX = -1
        var endX = -1
        if (this === startPoint.renderable) {
            startX = startPoint.x
        }
        if (this === endPoint.renderable) {
            endX = endPoint.x
        }
        if (!inSelection && (startX == -1) && (endX == -1)) {
            return false
        }
        if ((startX != -1) && (endX != -1)) {
            if (endX < startX) {
                val temp = startX
                startX = endX
                endX = temp
            }
        } else if ((startX != -1) && (endX == -1) && inSelection) {
            endX = startX
            startX = -1
        } else if ((startX == -1) && (endX != -1) && !inSelection) {
            startX = endX
            endX = -1
        }
        var width1 = -1
        var width2 = -1
        val wordChars = this.shownWord.toCharArray()
        if (startX != -1) {
            width1 = 0
            val fm = this.fontMetrics
            for (len in wordChars.indices) {
                val w = fm.charsWidth(wordChars, 0, len)
                if (w > startX) {
                    break
                }
                width1 = w
            }
        }
        if (endX != -1) {
            width2 = 0
            val fm = this.fontMetrics
            for (len in wordChars.indices) {
                val w = fm.charsWidth(wordChars, 0, len)
                if (w > endX) {
                    break
                }
                width2 = w
            }
        }
        if ((width1 != -1) || (width2 != -1)) {
            val startPaint = if (width1 == -1) 0 else width1
            val endPaint = if (width2 == -1) this.width else width2
            g.color = SELECTION_COLOR
            g.setXORMode(SELECTION_XOR)
            g.fillRect(startPaint, 0, endPaint - startPaint, this.height)
            g.setPaintMode()
            return (width2 == -1)
        } else {
            if (inSelection) {
                g.color = SELECTION_COLOR
                g.setXORMode(SELECTION_XOR)
                g.fillRect(0, 0, this.width, this.height)
                g.setPaintMode()
            }
            return inSelection
        }
    }

    override fun extractSelectionText(
        buffer: StringBuffer, inSelection: Boolean, startPoint: RenderableSpot,
        endPoint: RenderableSpot
    ): Boolean {

        var startX = -1
        var endX = -1
        if (this === startPoint.renderable) {
            startX = startPoint.x
        }
        if (this === endPoint.renderable) {
            endX = endPoint.x
        }
        if (!inSelection && (startX == -1) && (endX == -1)) {
            return false
        }
        if ((startX != -1) && (endX != -1)) {
            if (endX < startX) {
                val temp = startX
                startX = endX
                endX = temp
            }
        } else if ((startX != -1) && (endX == -1) && inSelection) {
            endX = startX
            startX = -1
        } else if ((startX == -1) && (endX != -1) && !inSelection) {
            startX = endX
            endX = -1
        }
        var index1 = -1
        var index2 = -1
        val wordChars = this.shownWord.toCharArray()
        if (startX != -1) {
            index1 = 0
            val fm = this.fontMetrics
            for (len in wordChars.indices) {
                val w = fm.charsWidth(wordChars, 0, len)
                if (w > startX) {
                    break
                }
                index1 = len
            }
        }
        if (endX != -1) {
            index2 = 0
            val fm = this.fontMetrics
            for (len in wordChars.indices) {
                val w = fm.charsWidth(wordChars, 0, len)
                if (w > endX) {
                    break
                }
                index2 = len
            }
        }
        if ((index1 != -1) || (index2 != -1)) {
            val startIndex = if (index1 == -1) 0 else index1
            val endIndex = if (index2 == -1) wordChars.size else index2
            buffer.append(wordChars, startIndex, endIndex - startIndex)
        } else {
            if (inSelection) {
                buffer.append(wordChars)
                return true
            }
        }
        if ((index1 != -1) && (index2 != -1)) {
            return false
        } else {
            return !inSelection
        }
    }

    override val isDelegated: Boolean
        get() = TODO("Not yet implemented")

    override fun onMouseClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        val me = this.modelNode
        if (me != null) {
            return HtmlController.Companion.instance.onMouseClick(me, event, x, y)
        } else {
            return true
        }
    }

    override fun onDoubleClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        val me = this.modelNode
        if (me != null) {
            return HtmlController.Companion.instance.onDoubleClick(me, event, x, y)
        } else {
            return true
        }
    }

    override fun onMousePressed(event: MouseEvent?, x: Int, y: Int): Boolean {
        val me = this.modelNode
        if (me != null) {
            return HtmlController.Companion.instance.onMouseDown(me, event, x, y)
        } else {
            return true
        }
    }

    override fun onMouseReleased(event: MouseEvent?, x: Int, y: Int): Boolean {
        val me = this.modelNode
        if (me != null) {
            return HtmlController.Companion.instance.onMouseUp(me, event, x, y)
        } else {
            return true
        }
    }

    override fun onMouseDisarmed(event: MouseEvent?): Boolean {
        val me = this.modelNode
        if (me != null) {
            return HtmlController.Companion.instance.onMouseDisarmed(me, event)
        } else {
            return true
        }
    }

    override val size: Dimension?
        get() = TODO("Not yet implemented")
    override val origin: Point?
        get() = TODO("Not yet implemented")
    override var parent: RCollection?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var originalParent: RCollection?
        get() = TODO("Not yet implemented")
        set(value) {}
    override val originalOrCurrentParent: RCollection?
        get() = TODO("Not yet implemented")
    override val visualX: Int
        get() = TODO("Not yet implemented")
    override val visualY: Int
        get() = TODO("Not yet implemented")
    override val visualHeight: Int
        get() = TODO("Not yet implemented")
    override val visualWidth: Int
        get() = TODO("Not yet implemented")

    override fun getLowestRenderableSpot(x: Int, y: Int): RenderableSpot {
        return RenderableSpot(this, x, y)
    }

    fun isContainedByNode(): Boolean {
        return true
    }

    override fun onRightClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        val me = this.modelNode
        if (me != null) {
            return HtmlController.Companion.instance.onContextMenu(me, event, x, y)
        } else {
            return true
        }
    }

    override val isContainedByNode: Boolean
        get() = TODO("Not yet implemented")

    override fun toString(): String {
        return "RWord[word=" + this.shownWord + "]"
    }

    companion object {
        private fun transformText(word: String, textTransform: Int): String? {
            val string: String?
            when (textTransform) {
                RenderState.TEXTTRANSFORM_CAPITALIZE -> string =
                    word.get(0).titlecaseChar().toString() + word.substring(1).lowercase(
                        Locale.getDefault()
                    )

                RenderState.TEXTTRANSFORM_LOWERCASE -> string = word.lowercase(Locale.getDefault())
                RenderState.TEXTTRANSFORM_UPPERCASE -> string = word.uppercase(Locale.getDefault())
                else -> string = word
            }
            return string
        }
    }
}
