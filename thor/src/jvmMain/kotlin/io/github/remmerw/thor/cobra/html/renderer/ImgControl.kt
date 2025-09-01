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
 * Created on Nov 19, 2005
 */
package io.github.remmerw.thor.cobra.html.renderer

import cz.vutbr.web.css.CSSProperty.VerticalAlign
import io.github.remmerw.thor.cobra.html.domimpl.HTMLImageElementImpl
import io.github.remmerw.thor.cobra.html.style.HtmlValues
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.swing.SwingUtilities
import kotlin.concurrent.Volatile


class ImgControl(modelNode: HTMLImageElementImpl) : BaseControl(modelNode) {


    // private final UserAgentContext browserContext;
    private val lastSrc: String? = null

    private var declaredWidth = 0
    private var declaredHeight = 0


    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

    }

    override fun reset(availWidth: Int, availHeight: Int) {
        // Expected in the GUI thread.
        // final HTMLElementImpl element = this.controlElement;

        // TODO: Remove the parameters dw, dh, and members declaredWith, declaredHeight completely.
        //       They seem to be used only for old style syntax.

        // final int dw = HtmlValues.getOldSyntaxPixelSize(element.getAttribute("width"), availWidth, -1);
        // final int dh = HtmlValues.getOldSyntaxPixelSize(element.getAttribute("height"), availHeight, -1);

        val dw = -1
        val dh = -1
        this.declaredWidth = dw
        this.declaredHeight = dh
        this.preferredSize = this.createPreferredSize(dw, dh)
    }



    override fun vAlign(): VerticalAlign {
        val element = this.controlElement
        val verticalAlign = element?.getRenderState()?.getVerticalAlign()
        return if (verticalAlign == null) VerticalAlign.BASELINE else verticalAlign
    }

    override fun getPreferredSize(): Dimension {
        val ps = this.preferredSize
        return if (ps == null) Dimension(0, 0) else ps
    }

    fun createPreferredSize(dw: Int, dh: Int): Dimension {
        var dw = dw
        var dh = dh

        return Dimension(dw, dh)
    }

    private fun checkPreferredSizeChange(): Boolean {
        val newPs = this.createPreferredSize(this.declaredWidth, this.declaredHeight)
        val ps = this.preferredSize
        if (ps == null) {
            return true
        }
        if ((ps.width != newPs.width) || (ps.height != newPs.height)) {
            this.preferredSize = newPs
            return true
        } else {
            return false
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.Component#imageUpdate(java.awt.Image, int, int, int, int,
     * int)
     */
    override fun imageUpdate(img: Image?, infoflags: Int, x: Int, y: Int, w: Int, h: Int): Boolean {
        if (((infoflags and ALLBITS) != 0) || ((infoflags and FRAMEBITS) != 0)) {
            SwingUtilities.invokeLater(Runnable {
                if (!checkPreferredSizeChange()) {
                    repaint()
                } else {
                    ruicontrol?.preferredSizeInvalidated()
                }
            })
        }
        return true
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.Component#imageUpdate(java.awt.Image, int, int, int, int,
     * int)
     */
    fun imageUpdate(img: Image?, w: Int, h: Int) {
        SwingUtilities.invokeLater(Runnable {
            if (!checkPreferredSizeChange()) {
                repaint()
            } else {
                ruicontrol?.preferredSizeInvalidated()
            }
        })
    }

    fun paintSelection(
        g: Graphics?,
        inSelection: Boolean,
        startPoint: RenderableSpot?,
        endPoint: RenderableSpot?
    ): Boolean {
        return inSelection
    }


    override fun toString(): String {
        return "ImgControl[src=" + this.lastSrc + "]"
    }

    // https://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
    // Adapted from: https://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
    /**
     * Convenience method that returns a scaled instance of the provided `BufferedImage`.
     *
     * @param img          the original image to be scaled
     * @param targetWidth  the desired width of the scaled instance, in pixels
     * @param targetHeight the desired height of the scaled instance, in pixels
     * @param hint         one of the rendering hints that corresponds to `RenderingHints.KEY_INTERPOLATION`
     * @return a scaled version of the original `BufferedImage`
     */
    private fun getScaledInstance(
        img: Image,
        targetWidth: Int,
        targetHeight: Int,
        hint: Any?
    ): Image {
        val type = BufferedImage.TYPE_INT_ARGB
        var ret = img
        var w = img.getWidth(this)
        var h = img.getHeight(this)

        while (w != targetWidth || h != targetHeight) {
            if (w > targetWidth) {
                w /= 2
            }
            if (w < targetWidth) {
                w = targetWidth
            }

            if (h > targetHeight) {
                h /= 2
            }
            if (h < targetHeight) {
                h = targetHeight
            }

            val tmp = BufferedImage(w, h, type)
            val g2 = tmp.createGraphics()
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint)
            g2.drawImage(ret, 0, 0, w, h, null)
            g2.dispose()

            ret = tmp
        }

        return ret
    }

    fun isReadyToPaint(): Boolean {
        return true
    }

    companion object
}
