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
 * Created on Jan 29, 2006
 */
package io.github.remmerw.thor.cobra.html.gui

import io.github.remmerw.thor.cobra.html.HtmlRendererContext
import io.github.remmerw.thor.cobra.html.domimpl.FrameNode
import io.github.remmerw.thor.cobra.html.domimpl.HTMLElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.NodeImpl
import io.github.remmerw.thor.cobra.html.renderer.NodeRenderer
import io.github.remmerw.thor.cobra.html.style.HtmlLength
import io.github.remmerw.thor.cobra.util.gui.WrapperLayout
import java.awt.Component
import java.awt.Dimension
import java.util.StringTokenizer
import java.util.logging.Logger
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSplitPane

/**
 * A Swing panel used to render FRAMESETs only. It is used by [HtmlPanel]
 * when a document is determined to be a FRAMESET.
 *
 * @see HtmlPanel
 *
 * @see HtmlBlockPanel
 */
class FrameSetPanel : JComponent(), NodeRenderer {
    private var rootNode: HTMLElementImpl? = null
    private var htmlContext: HtmlRendererContext? = null
    private var frameComponents: Array<Component>?
    private var domInvalid = true

    init {
        this.layout = WrapperLayout.instance
        // TODO: This should be a temporary preferred size
        this.preferredSize = Dimension(600, 400)
    }

    /**
     * Sets the FRAMESET node and invalidates the component so it can be rendered
     * immediately in the GUI thread.
     */
    override fun setRootNode(node: NodeImpl?) {
        // Method expected to be called in the GUI thread.
        require(node is HTMLElementImpl) { "node=" + node }
        this.rootNode = node
        val context = node.htmlRendererContext
        this.htmlContext = context
        this.domInvalid = true
        this.invalidate()
        this.validateAll()
        this.repaint()
    }

    protected fun validateAll() {
        var toValidate: Component? = this
        while (true) {
            val parent = toValidate!!.getParent()
            if ((parent == null) || parent.isValid) {
                break
            }
            toValidate = parent
        }
        toValidate.validate()
    }

    fun processDocumentNotifications(notifications: Array<DocumentNotification?>) {
        // Called in the GUI thread.
        if (notifications.size > 0) {
            // Not very efficient, but it will do.
            this.domInvalid = true
            this.invalidate()
            if (this.isVisible) {
                this.validate()
                this.repaint()
            }
        }
    }

    /**
     * This method is invoked by AWT in the GUI thread to lay out the component.
     * This implementation is an override.
     */
    override fun doLayout() {
        if (this.domInvalid) {
            this.domInvalid = false
            this.removeAll()
            val context = this.htmlContext
            if (context != null) {
                val element = this.rootNode!!
                val rows = element.getAttribute("rows")
                val cols = element.getAttribute("cols")
                val rowLengths: Array<HtmlLength> = getLengths(rows)
                val colLengths: Array<HtmlLength> = getLengths(cols)
                val subframes: Array<HTMLElementImpl> = getSubFrames(element)
                val frameComponents = arrayOfNulls<Component>(subframes.size)
                this.frameComponents = frameComponents
                for (i in subframes.indices) {
                    val frameElement = subframes[i]
                    if ((frameElement != null) && "FRAMESET".equals(
                            frameElement.tagName,
                            ignoreCase = true
                        )
                    ) {
                        val fsp = FrameSetPanel()
                        fsp.setRootNode(frameElement)
                        frameComponents[i] = fsp
                    } else {
                        if (frameElement is FrameNode) {
                            if (frameElement.browserFrame == null) {
                                val frame = context.createBrowserFrame()
                                frameElement.browserFrame = frame
                                frameComponents[i] = frame?.component
                            } else {
                                frameComponents[i] = frameElement.browserFrame!!.component
                            }
                        } else {
                            frameComponents[i] = JPanel()
                        }
                    }
                }
                val rhl = rowLengths
                val chl = colLengths
                val fc = this.frameComponents
                if ((rhl != null) && (chl != null) && (fc != null)) {
                    val size = this.size
                    val insets = this.insets
                    val width = size.width - insets.left - insets.right
                    val height = size.height - insets.left - insets.right
                    val absColLengths: IntArray = getAbsoluteLengths(chl, width)
                    val absRowLengths: IntArray = getAbsoluteLengths(rhl, height)
                    this.add(
                        this.getSplitPane(
                            this.htmlContext,
                            absColLengths,
                            0,
                            absColLengths.size,
                            absRowLengths,
                            0,
                            absRowLengths.size,
                            fc
                        )
                    )
                }
            }
        }
        super.doLayout()
    }

    private fun getSplitPane(
        context: HtmlRendererContext?, colLengths: IntArray, firstCol: Int, numCols: Int,
        rowLengths: IntArray, firstRow: Int,
        numRows: Int, frameComponents: Array<Component>
    ): Component? {
        if (numCols == 1) {
            val frameindex = (colLengths.size * firstRow) + firstCol
            val topComponent =
                if (frameindex < frameComponents.size) frameComponents[frameindex] else null
            if (numRows == 1) {
                return topComponent
            } else {
                val bottomComponent = this.getSplitPane(
                    context, colLengths, firstCol, numCols, rowLengths, firstRow + 1, numRows - 1,
                    frameComponents
                )
                val sp = JSplitPane(JSplitPane.VERTICAL_SPLIT, topComponent, bottomComponent)
                sp.dividerLocation = rowLengths[firstRow]
                return sp
            }
        } else {
            val rightComponent = this.getSplitPane(
                context, colLengths, firstCol + 1, numCols - 1, rowLengths, firstRow, numRows,
                frameComponents
            )
            val leftComponent = this.getSplitPane(
                context,
                colLengths,
                firstCol,
                1,
                rowLengths,
                firstRow,
                numRows,
                frameComponents
            )
            val sp = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftComponent, rightComponent)
            sp.dividerLocation = colLengths[firstCol]
            return sp
        }
    }

    companion object {
        private const val serialVersionUID = 5048031593959987324L
        private val logger: Logger = Logger.getLogger(FrameSetPanel::class.java.name)
        private fun getLengths(spec: String?): Array<HtmlLength> {
            if (spec == null) {
                return arrayOf<HtmlLength>(HtmlLength("1*"))
            }
            val tok = StringTokenizer(spec, ",")
            val lengths = ArrayList<HtmlLength?>()
            while (tok.hasMoreTokens()) {
                val token = tok.nextToken().trim { it <= ' ' }
                try {
                    lengths.add(HtmlLength(token))
                } catch (err: Exception) {
                    logger.warning("Frame rows or cols value [" + spec + "] is invalid.")
                }
            }
            return lengths.toArray<HtmlLength?>(HtmlLength.EMPTY_ARRAY)
        }

        private fun getSubFrames(parent: HTMLElementImpl): Array<HTMLElementImpl> {
            val children = parent.childrenArray
            val subFrames = ArrayList<NodeImpl>()
            for (child in children!!) {
                if (child is HTMLElementImpl) {
                    val nodeName = child.nodeName
                    if ("FRAME".equals(nodeName, ignoreCase = true) || "FRAMESET".equals(
                            nodeName,
                            ignoreCase = true
                        )
                    ) {
                        subFrames.add(child)
                    }
                }
            }
            return subFrames.toTypedArray<HTMLElementImpl>()
        }

        private fun getAbsoluteLengths(htmlLengths: Array<HtmlLength>, totalSize: Int): IntArray {
            val absLengths = IntArray(htmlLengths.size)
            var totalSizeNonMulti = 0
            var sumMulti = 0
            for (i in htmlLengths.indices) {
                val htmlLength = htmlLengths[i]
                val lengthType = htmlLength.lengthType
                if (lengthType == HtmlLength.PIXELS) {
                    val absLength = htmlLength.rawValue
                    totalSizeNonMulti += absLength
                    absLengths[i] = absLength
                } else if (lengthType == HtmlLength.LENGTH) {
                    val absLength = htmlLength.getLength(totalSize)
                    totalSizeNonMulti += absLength
                    absLengths[i] = absLength
                } else {
                    sumMulti += htmlLength.rawValue
                }
            }
            val remaining = totalSize - totalSizeNonMulti
            if ((remaining > 0) && (sumMulti > 0)) {
                for (i in htmlLengths.indices) {
                    val htmlLength = htmlLengths[i]
                    if (htmlLength.lengthType == HtmlLength.MULTI_LENGTH) {
                        val absLength = (remaining * htmlLength.rawValue) / sumMulti
                        absLengths[i] = absLength
                    }
                }
            }
            return absLengths
        }
    }
}
