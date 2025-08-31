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
package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import io.github.remmerw.thor.cobra.html.style.RenderState
import org.w3c.dom.html.HTMLDocument
import org.w3c.dom.html.HTMLHtmlElement
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle

class PositionedRenderable(
    private val renderable: BoundableRenderable,
    private val verticalAlignable: Boolean,
    private val ordinal: Int,
    private val isFloat: Boolean,
    private val isFixed: Boolean,
    private val isDelegated: Boolean
) : Renderable {

    fun ordinal() : Int{
        return ordinal
    }

    fun renderable(): BoundableRenderable {
        return renderable
    }
    fun isFloat() : Boolean{
        return isFloat
    }

    fun verticalAlignable() : Boolean{
        return verticalAlignable
    }

    override fun paint(gIn: Graphics) {
        if (isDelegated) {
            return
        }

        val originalParent = this.renderable.originalParent()
        val rparent = renderable.parent()

        /*
    System.out.println("pr: " + this);
    System.out.println("  parent     : " + rparent);
    System.out.println("  orig parent: " + originalParent);
    */
        val or = originalParent!!.getOriginRelativeTo(rparent)
        val pos = this.renderable.modelNode()!!.renderState()!!.getPosition()

        if (isFloat || pos == RenderState.POSITION_ABSOLUTE || pos == RenderState.POSITION_FIXED) {
            val g2 = gIn.create()
            val some = this.some

            if (some != null) {
                g2.translate(some.x, some.y)
            }


            // g2.setColor(Color.GREEN);
            // g2.fillRect(0, 0, renderable.getWidth(), 100);
            this.renderable.paintTranslated(g2)
        } else {
            val orNoScroll = originalParent.getOriginRelativeToNoScroll(rparent)

            // System.out.println("  orNoScroll: " + orNoScroll);
            // System.out.println("  or        : " + or);

            // final Rectangle bounds = originalParent.getClipBounds();
            val bounds = this.relativeBounds
            // System.out.println("  clip bounds: " + bounds);
            val g2: Graphics
            if (bounds != null) {
                val tx = bounds.x + orNoScroll!!.x
                val ty = bounds.y + orNoScroll.y
                g2 = gIn.create(tx, ty, bounds.width, bounds.height)
                g2.translate(-tx, -ty)
            } else {
                g2 = gIn.create()
            }

            g2.translate(or.x, or.y)

            // g2.setColor(new java.awt.Color(0.5f, 0.5f, 0f, 0.8f));
            // g2.fillRect(0, 0, bounds.width, bounds.height);
            try {
                this.renderable.paintTranslated(g2)
            } finally {
                g2.dispose()
            }
        }
    }

    private val relativeBounds: Rectangle?
        get() {
            val origParent = this.renderable.originalParent()
            var current = origParent
            var currentBounds = current!!.clipBoundsWithoutInsets()
            val parent = this.renderable.parent()
            while (current !== parent) {
                current = current!!.parent()!!
                if (current.modelNode() is HTMLHtmlElement) {
                    break
                }
                val newBounds = current.clipBoundsWithoutInsets()
                if (newBounds != null) {
                    val or = origParent.getOriginRelativeToNoScroll(current)
                    newBounds.translate(-or!!.x, -or!!.y)
                    if (currentBounds == null) {
                        currentBounds = newBounds
                    } else {
                        currentBounds = currentBounds.intersection(newBounds)
                    }
                }
            }
            return currentBounds
        }

    override fun modelNode(): ModelNode? {
        return this.renderable.modelNode()
    }

    override fun isFixed(): Boolean {
        return isFixed
    }

    override fun toString(): String {
        return "PosRndrbl [" + renderable + "]"
    }

    val visualBounds: Rectangle
        get() {
            val bounds = renderable.visualBounds()!!
            val offset = this.offset
            bounds.translate(offset.x, offset.y)
            return bounds
        }

    val offset: Point
        get() {
            val offset = Point()
            val pos = this.renderable.modelNode()!!.renderState()!!.getPosition()

            val originalParent = this.renderable.originalParent()!!
            val rparent = renderable.parent()
            val or = originalParent.getOriginRelativeTo(rparent)
            if (isFloat || pos == RenderState.POSITION_ABSOLUTE || pos == RenderState.POSITION_FIXED) {
                val some = this.some
                if (some != null) {
                    offset.translate(some.x, some.y)
                }
            } else {
                offset.translate(or.x, or.y)
            }
            return offset
        }

    private val some: Point?
        // TODO: name this function well: what exactly does it compute?
        get() {
            val rparent = renderable.parent()!!
            if (!isFixed && rparent.modelNode() is HTMLDocument) {
                var htmlRenderable = RenderUtils.findHtmlRenderable(rparent)
                if (htmlRenderable is PositionedRenderable) {
                    htmlRenderable = htmlRenderable.renderable
                }
                // TODO: Handle other renderable types such as RTable
                if (htmlRenderable is RBlock) {
                    val htmlOffset = Point(0, 0) // TODO htmlRenderable.bodyLayout.getOrigin()
                    val htmlInsets = htmlRenderable.getInsetsMarginBorder(
                        htmlRenderable.hasHScrollBar,
                        htmlRenderable.hasVScrollBar
                    )

                    return Point(
                        htmlOffset.getX().toInt() - htmlInsets.left,
                        htmlOffset.getY().toInt() - htmlInsets.top
                    )
                }
            }

            return null
        }

    fun contains(x: Int, y: Int): Boolean {
        return this.visualBounds.contains(x, y)
    }

    override fun isReadyToPaint(): Boolean {
        return renderable.isReadyToPaint()
    }
}