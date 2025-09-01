package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.HtmlRendererContext
import io.github.remmerw.thor.cobra.html.dom.NodeImpl
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
        colSizes: List<ColSizeInfo>,
        rowSizes: List<RowSizeInfo>,
        hasBorder: Int,
        cellSpacingX: Int,
        cellSpacingY: Int
    )

    abstract fun getWidthText(): String?

    abstract fun getHeightText(): String?

    abstract fun getRowSpan(): Int

    abstract fun getColSpan(): Int

    abstract fun doCellLayout(
        width: Int, height: Int, expandWidth: Boolean, expandHeight: Boolean,
        sizeOnly: Boolean
    ): Dimension?

    abstract fun getRenderState(): RenderState

    fun virtualColumn(): Int
        {
            val vc = this.topLeftVirtualCell
            return if (vc == null) 0 else vc.column
        }

    fun virtualRow(): Int {
            val vc = this.topLeftVirtualCell
            return if (vc == null) 0 else vc.row
        }

    abstract fun setColSpan(cs: Int)
    abstract fun setRowSpan(cs: Int)

}