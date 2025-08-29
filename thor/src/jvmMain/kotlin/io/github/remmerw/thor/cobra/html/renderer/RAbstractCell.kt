package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.HtmlRendererContext
import io.github.remmerw.thor.cobra.html.domimpl.NodeImpl
import io.github.remmerw.thor.cobra.html.renderer.TableMatrix.ColSizeInfo
import io.github.remmerw.thor.cobra.html.renderer.TableMatrix.RowSizeInfo
import io.github.remmerw.thor.cobra.html.style.RenderState
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import java.awt.Dimension

abstract class RAbstractCell(
    modelNode: NodeImpl?,
    listNesting: Int,
    pcontext: UserAgentContext?,
    rcontext: HtmlRendererContext?,
    frameContext: FrameContext?,
    parentContainer: RenderableContainer?
) : RBlock(modelNode, listNesting, pcontext, rcontext, frameContext, parentContainer) {
    var topLeftVirtualCell: VirtualCell? = null

    abstract fun setCellBounds(
        colSizes: Array<ColSizeInfo>,
        rowSizes: Array<RowSizeInfo>,
        hasBorder: Int,
        cellSpacingX: Int,
        cellSpacingY: Int
    )

    abstract val widthText: String?

    abstract val heightText: String?

    abstract var rowSpan: Int

    abstract val colSpan: Int

    abstract fun doCellLayout(
        width: Int, height: Int, expandWidth: Boolean, expandHeight: Boolean,
        sizeOnly: Boolean
    ): Dimension?

    abstract val renderState: RenderState

    val virtualColumn: Int
        /**
         * @return Returns the virtualColumn.
         */
        get() {
            val vc = this.topLeftVirtualCell
            return if (vc == null) 0 else vc.column
        }

    val virtualRow: Int
        /**
         * @return Returns the virtualRow.
         */
        get() {
            val vc = this.topLeftVirtualCell
            return if (vc == null) 0 else vc.row
        }
}