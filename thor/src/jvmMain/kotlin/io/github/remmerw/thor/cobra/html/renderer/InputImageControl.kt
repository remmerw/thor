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

import cz.vutbr.web.css.CSSProperty.VerticalAlign
import io.github.remmerw.thor.cobra.html.domimpl.HTMLBaseInputElement
import io.github.remmerw.thor.cobra.html.domimpl.ImageEvent
import io.github.remmerw.thor.cobra.html.domimpl.ImageListener
import io.github.remmerw.thor.cobra.html.style.HtmlValues
import io.github.remmerw.thor.cobra.ua.ImageResponse
import io.github.remmerw.thor.cobra.util.gui.WrapperLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Image
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.SwingUtilities

internal class InputImageControl(modelNode: HTMLBaseInputElement) : BaseInputControl(modelNode),
    ImageListener {
    // private JButton button;
    private var mouseBeingPressed = false
    private var preferredSize: Dimension? = null
    private var declaredWidth = 0
    private var declaredHeight = 0
    private var imageResponse: ImageResponse? = null

    init {
        this.layout = WrapperLayout.instance
        // JButton button = new LocalButton();
        // this.button = button;
        // button.setMargin(RBlockViewport.ZERO_INSETS);
        // button.setBorder(null);
        // this.add(button);
        modelNode.addImageListener(this)
        this.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent?) {
                mouseBeingPressed = true
                repaint()
            }

            // public void mouseExited(MouseEvent e) {
            // mouseBeingPressed = false;
            // repaint();
            // }
            override fun mouseReleased(e: MouseEvent) {
                mouseBeingPressed = false
                repaint()
                HtmlController.Companion.getInstance().onPressed(modelNode, e, e.getX(), e.getY())
            }
        })
    }

    override fun reset(availWidth: Int, availHeight: Int) {
        super.reset(availWidth, availHeight)
        val element = this.controlElement
        val dw = HtmlValues.getOldSyntaxPixelSize(element.getAttribute("width"), availWidth, -1)
        val dh = HtmlValues.getOldSyntaxPixelSize(element.getAttribute("height"), availHeight, -1)
        this.declaredWidth = dw
        this.declaredHeight = dh
        this.preferredSize = this.createPreferredSize(dw, dh)
    }

    override fun getVAlign(): VerticalAlign? {
        val element = this.controlElement
        return element.getRenderState().verticalAlign
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val size = this.size
        val insets = this.insets
        synchronized(this) {}
        val imageResponse = this.imageResponse!!
        if (imageResponse.state == ImageResponse.State.loaded) {
            g.drawImage(
                imageResponse.img,
                insets.left,
                insets.top,
                size.width - insets.left - insets.right,
                size.height - insets.top - insets.bottom,
                this
            )
        } else {
            // TODO: alt
        }
        if (this.mouseBeingPressed) {
            val over = Color(255, 100, 100, 64)
            val oldColor = g.color
            try {
                g.color = over
                g.fillRect(0, 0, size.width, size.height)
            } finally {
                g.color = oldColor
            }
        }
    }

    override fun getPreferredSize(): Dimension {
        val ps = this.preferredSize
        return if (ps == null) Dimension(0, 0) else ps
    }

    fun createPreferredSize(dw: Int, dh: Int): Dimension {
        var dw = dw
        var dh = dh
        val imgResponse = this.imageResponse!!
        if (!imgResponse.isDecoded()) {
            return Dimension(if (dw == -1) 0 else dw, if (dh == -1) 0 else dh)
        }

        checkNotNull(imgResponse.img)
        val img: Image = imgResponse.img

        if (dw == -1) {
            dw = HtmlValues.scaleToDevicePixels(img.getWidth(this).toDouble())
        }
        if (dh == -1) {
            dh = HtmlValues.scaleToDevicePixels(img.getHeight(this).toDouble())
        }
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
                    ruicontrol.preferredSizeInvalidated()
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
                ruicontrol.preferredSizeInvalidated()
            }
        })
    }

    override fun paintSelection(
        g: Graphics?,
        inSelection: Boolean,
        startPoint: RenderableSpot?,
        endPoint: RenderableSpot?
    ): Boolean {
        return inSelection
    }

    override fun imageLoaded(event: ImageEvent) {
        // Implementation of ImageListener. Invoked in a request thread most likely.
        val imageResponseLocal = event.imageResponse
        // ImageIcon imageIcon = new ImageIcon(image);
        // this.button.setIcon(imageIcon);
        this.imageResponse = imageResponseLocal
        if (imageResponseLocal.isDecoded()) {
            checkNotNull(imageResponseLocal.img)
            val image = imageResponseLocal.img
            val width = image.getWidth(this)
            val height = image.getHeight(this)
            this.imageUpdate(image, width, height)
        }
    }

    override fun imageAborted() {
        // do nothing
    }

    override fun resetInput() {
        // NOP
    }

    override fun isReadyToPaint(): Boolean {
        return imageResponse!!.isReadyToPaint()
    } // private static class LocalButton extends JButton {
    // public void revalidate() {
    // // ignore
    // }
    //
    // public void repaint() {
    // // ignore
    // }
    // }

    companion object {
        private val serialVersionUID = -2242175570423778798L
    }
}
