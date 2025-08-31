/*    GNU LESSER GENERAL PUBLIC LICENSE
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
 * Created on Apr 16, 2005
 */
package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.HtmlObject
import io.github.remmerw.thor.cobra.html.HtmlRendererContext
import io.github.remmerw.thor.cobra.html.domimpl.DocumentFragmentImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLBaseInputElement
import io.github.remmerw.thor.cobra.html.domimpl.HTMLCanvasElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLIFrameElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLImageElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLTableElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import io.github.remmerw.thor.cobra.html.domimpl.NodeImpl
import io.github.remmerw.thor.cobra.html.style.HtmlInsets
import io.github.remmerw.thor.cobra.html.style.JStyleProperties
import io.github.remmerw.thor.cobra.html.style.RenderState
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import io.github.remmerw.thor.cobra.util.ArrayUtilities
import io.github.remmerw.thor.cobra.util.CollectionUtilities
import org.w3c.dom.Node
import org.w3c.dom.html.HTMLDocument
import org.w3c.dom.html.HTMLHtmlElement
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Insets
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.MouseEvent
import java.util.LinkedList
import java.util.Locale
import java.util.SortedSet
import java.util.TreeSet
import java.util.function.Function
import java.util.logging.Level
import kotlin.math.max
import kotlin.math.min

/**
 * A substantial portion of the HTML rendering logic of the package can be found
 * in this class. This class is in charge of laying out the DOM subtree of a
 * node, usually on behalf of an RBlock. It creates a renderer subtree
 * consisting of RLine's or RBlock's. RLine's in turn contain RWord's and so on.
 * This class also happens to be used as an RBlock scrollable viewport.
 *
 * @author J. H. S.
 */
class RBlockViewport(
    modelNode: ModelNode?, container: RenderableContainer?, listNesting: Int,
    pcontext: UserAgentContext,
    rcontext: HtmlRendererContext, frameContext: FrameContext?,
    private var parent: RCollection?
) : BaseRCollection(container, modelNode) {

    init {
        setParent(parent)
    }
    private val listNesting: Int
    private val userAgentContext: UserAgentContext
    private val rendererContext: HtmlRendererContext
    private val frameContext: FrameContext?
    var scrollX: Int = 0
    var scrollY: Int = 0
    private var positionedRenderables: SortedSet<PositionedRenderable>? = null
    private var seqRenderables: ArrayList<BoundableRenderable>? = null
    private var exportableFloats: ArrayList<ExportableFloat?>? = null

    // private Collection exportedRenderables;
    private var currentLine: RLine? = null
    private var maxX = 0
    private var maxY = 0

    // private int availHeight;
    private var desiredWidth = 0 // includes insets
    private var desiredHeight = 0 // includes insets
    private var availContentHeight = 0 // does not include insets

    // private void addParagraphBreak(ModelNode startNode) {
    // // This needs to get replaced with paragraph collapsing
    // this.addLineBreak(startNode, LineBreak.NONE);
    // this.addLineBreak(startNode, LineBreak.NONE);
    // }
    var availContentWidth: Int = 0 // does not include insets
        private set
    private var yLimit = 0
    private var positionedOrdinal = 0
    private var currentCollapsibleMargin = 0
    private var paddingInsets: Insets? = null
    private val overrideNoWrap = false
    private var floatBounds: FloatingBounds? = null
    private var sizeOnly = false
    private var lastSeqBlock: BoundableRenderable? = null
    private var firstElementProcessed = false
    private var lastElementBeingProcessed = false
    private var armedRenderable: BoundableRenderable? = null
    private var pendingFloats: MutableCollection<RFloatInfo?>? = null
    private var isFloatLimit: Boolean? = null
    private var cachedVisualHeight: Int? = null
    private var cachedVisualWidth: Int? = null

    private var layoutUpTreeCanBeInvalidated = false

    /**
     * Constructs an HtmlBlockLayout.
     *
     * @param container    This is usually going to be an RBlock.
     * @param listNesting  The nesting level for lists. This is zero except inside a list.
     * @param pcontext     The HTMLParserContext instance.
     * @param frameContext This is usually going to be HtmlBlock, an object where text
     * selections are contained.
     * @param parent       This is usually going to be the parent of `container`.
     */
    init {

        this.userAgentContext = pcontext
        this.rendererContext = rcontext
        this.frameContext = frameContext
        this.listNesting = listNesting
        // Layout here can always be "invalidated"
        this.layoutUpTreeCanBeInvalidated = true
    }

    public override fun invalidateLayoutLocal() {
        // Workaround for fact that RBlockViewport does not
        // get validated or invalidated.
        this.layoutUpTreeCanBeInvalidated = true
    }

    private fun initCollapsibleMargin(): Int {
        val parent: Any? = this.parent
        if (parent !is RBlock) {
            return 0
        }
        return parent.collapsibleMarginTop()
    }

    // final RBlockViewport getParentViewport(ExportedRenderable er) {
    // if(er.alignment == 0) {
    // return this.getParentViewport();
    // }
    // else {
    // return this.getParentViewportForAlign();
    // }
    // }
    //
    // final boolean isImportable(ExportedRenderable er) {
    // if(er.alignment == 0) {
    // return this.positionsAbsolutes();
    // }
    // else {
    // return this.getParentViewportForAlign() == null;
    // }
    // }
    /**
     * Builds the layout/renderer tree from scratch. Note: Returned dimension
     * needs to be actual size needed for rendered content, not the available
     * container size. This is relied upon by table layout.
     *
     * @param yLimit If other than -1, `layout` will throw
     * `SizeExceededException` in the event that the layout
     * goes beyond this y-coordinate point.
     */
    fun layout(
        desiredWidth: Int, desiredHeight: Int, paddingInsets: Insets, yLimit: Int,
        floatBounds: FloatingBounds?, sizeOnly: Boolean
    ) {
        this.cachedVisualHeight = null
        this.cachedVisualWidth = null

        // final RenderableContainer container = this.container;
        this.paddingInsets = paddingInsets
        this.yLimit = yLimit
        this.desiredHeight = desiredHeight
        this.desiredWidth = desiredWidth
        this.floatBounds = floatBounds
        this.isFloatLimit = null
        this.pendingFloats = null
        this.sizeOnly = sizeOnly
        this.lastSeqBlock = null
        // this.currentCollapsibleMargin = this.initCollapsibleMargin();
        this.currentCollapsibleMargin = 0

        // maxX and maxY should not be reset by layoutPass.
        this.maxX = paddingInsets.left
        this.maxY = paddingInsets.top

        var availw = desiredWidth - paddingInsets.left - paddingInsets.right
        if (availw < 0) {
            availw = 0
        }
        var availh = desiredHeight - paddingInsets.top - paddingInsets.bottom
        if (availh == 0) {
            availh = 0
        }
        this.availContentHeight = availh
        this.availContentWidth = availw

        // New floating algorithm.
        this.layoutPass((this.modelNode() as NodeImpl?)!!)

        // Compute maxY according to last block.
        var maxY = this.maxY
        var maxYWholeBlock = maxY
        val lastSeqBlock = this.lastSeqBlock
        if (lastSeqBlock != null) {
            val effBlockHeight = this.getEffectiveBlockHeight(lastSeqBlock)
            if ((lastSeqBlock.y() + effBlockHeight) > maxY) {
                maxY = lastSeqBlock.y() + effBlockHeight
                this.maxY = maxY
                maxYWholeBlock = lastSeqBlock.y() + lastSeqBlock.height()
            }
        }

        // See if line should increase maxY. Empty
        // lines shouldn't, except in cases where
        // there was a BR.
        val lastLine = this.currentLine!!
        val lastBounds = lastLine.bounds()
        if ((lastBounds.height > 0) || (lastBounds.y > maxYWholeBlock)) {
            val lastTopX = lastBounds.x + lastBounds.width
            if (lastTopX > this.maxX) {
                this.maxX = lastTopX
            }
            val lastTopY = lastBounds.y + lastBounds.height
            if (lastTopY > maxY) {
                maxY = lastTopY
                this.maxY = maxY
            }
        }

        // Check positioned renderables for maxX and maxY
        val posRenderables = this.positionedRenderables
        if (posRenderables != null) {
            val isFloatLimit = this.isFloatLimit()
            val i: MutableIterator<PositionedRenderable?> = posRenderables.iterator()
            while (i.hasNext()) {
                val pr = i.next()!!
                val br = pr.renderable
                if ((br.x() + br.width()) > this.maxX) {
                    this.maxX = br.x() + br.width()
                }
                if (isFloatLimit || !pr.isFloat) {
                    if ((br.y() + br.height()) > maxY) {
                        maxY = br.y() + br.height()
                        this.maxY = maxY
                    }
                }
            }
        }

        this.setWidth(paddingInsets.right + this.maxX)
        this.setHeight(paddingInsets.bottom + maxY)
    }

    private fun layoutPass(rootNode: NodeImpl) {
        val container = this.container!!
        container.clearDelayedPairs()
        this.positionedOrdinal = 0

        // Remove sequential renderables...
        this.seqRenderables = null

        // Remove other renderables...
        this.positionedRenderables = null

        // Remove exporatable floats...
        this.exportableFloats = null

        this.cachedVisualHeight = null
        this.cachedVisualWidth = null
        // Call addLine after setting margins
        this.currentLine = this.addLine(rootNode, null, this.paddingInsets!!.top)

        // Start laying out...
        // The parent is expected to have set the RenderState already.
        this.layoutChildren(rootNode)

        // This adds last-line floats.
        this.lineDone(this.currentLine)
    }

    /**
     * Applies any horizonal aLignment. It may adjust height if necessary.
     *
     * @param canvasWidth   The new width of the viewport. It could be different to the
     * previously calculated width.
     * @param paddingInsets
     */
    fun alignX(alignXPercent: Int, canvasWidth: Int, paddingInsets: Insets?) {
        val prevMaxY = this.maxY
        // Horizontal alignment
        if (alignXPercent > 0) {
            val renderables = this.seqRenderables
            if (renderables != null) {
                val insets = this.paddingInsets!!
                // final FloatingBounds floatBounds = this.floatBounds;
                val numRenderables = renderables.size
                val yoffset = 0 // This may get adjusted due to blocks and floats.
                for (i in 0..<numRenderables) {
                    val r: Any = renderables.get(i)
                    if (r is BoundableRenderable) {
                        val y = r.y()
                        val newY: Int
                        if (yoffset > 0) {
                            newY = y + yoffset
                            r.setY(newY)
                            if ((newY + r.height()) > this.maxY) {
                                this.maxY = newY + r.height()
                            }
                        } else {
                            newY = y
                        }
                        val isVisibleBlock = (r is RBlock) && r.isOverflowVisibleX
                        val leftOffset =
                            if (isVisibleBlock) insets.left else this.fetchLeftOffset(y)
                        val rightOffset =
                            if (isVisibleBlock) insets.right else this.fetchRightOffset(y)
                        val actualAvailWidth = canvasWidth - leftOffset - rightOffset
                        val difference = actualAvailWidth - r.width()
                        if (difference > 0) {

                            val shift = (difference * alignXPercent) / 100
                            if (!isVisibleBlock) {
                                val newX = leftOffset + shift
                                r.setX(newX)
                            }
                        }
                    }
                }
            }
        }
        if (prevMaxY != this.maxY) {
            this.setHeight (this.height() + (this.maxY - prevMaxY))
        }
    }

    /**
     * Applies vertical alignment.
     *
     * @param canvasHeight
     * @param paddingInsets
     */
    fun alignY(alignYPercent: Int, canvasHeight: Int, paddingInsets: Insets) {
        val prevMaxY = this.maxY
        if (alignYPercent > 0) {
            val availContentHeight = canvasHeight - paddingInsets.top - paddingInsets.bottom
            val usedHeight = this.maxY - paddingInsets.top
            val difference = availContentHeight - usedHeight
            if (difference > 0) {
                val shift = (difference * alignYPercent) / 100
                val rlist = this.seqRenderables
                if (rlist != null) {
                    // Try sequential renderables first.
                    val renderables: MutableIterator<BoundableRenderable?> = rlist.iterator()
                    while (renderables.hasNext()) {
                        val r: Any? = renderables.next()
                        if (r is BoundableRenderable) {
                            val newY = r.y() + shift
                            r.setY (newY)
                            if ((newY + r.height()) > this.maxY) {
                                this.maxY = newY + r.height()
                            }
                        }
                    }
                }

                // Now other renderables, but only those that can be
                // vertically aligned
                val others: MutableSet<PositionedRenderable?>? = this.positionedRenderables
                if (others != null) {
                    val i2: MutableIterator<PositionedRenderable?> = others.iterator()
                    while (i2.hasNext()) {
                        val pr = i2.next()!!
                        if (pr.verticalAlignable) {
                            val br = pr.renderable
                            val newY = br.y() + shift
                            br.setY (newY)
                            if ((newY + br.height()) > this.maxY) {
                                this.maxY = newY + br.height()
                            }
                        }
                    }
                }
            }
        }
        if (prevMaxY != this.maxY) {
            this.setHeight(this.height()+(this.maxY - prevMaxY))
        }
    }

    // /**
    // *
    // * @param block A block needing readjustment due to horizontal alignment.
    // * @return
    // */
    // private int readjustBlock(RBlock block, final int newX, final int newY,
    // final FloatingBounds floatBounds) {
    // final int rightInsets = this.paddingInsets.right;
    // final int expectedWidth = this.desiredWidth - rightInsets - newX;
    // final int blockShiftRight = rightInsets;
    // final int prevHeight = block.height;
    // FloatingBoundsSource floatBoundsSource = new FloatingBoundsSource() {
    // public FloatingBounds getChildBlockFloatingBounds(int apparentBlockWidth) {
    // int actualRightShift = blockShiftRight + (expectedWidth -
    // apparentBlockWidth);
    // return new ShiftedFloatingBounds(floatBounds, -newX, -actualRightShift,
    // -newY);
    // }
    // };
    // block.adjust(expectedWidth, this.availContentHeight, true, false,
    // floatBoundsSource, true);
    // return block.height - prevHeight;
    // }
    //
    private fun addLine(startNode: ModelNode?, prevLine: RLine?, newLineY: Int): RLine {
        // lineDone must be called before we try to
        // get float bounds.
        this.lineDone(prevLine)
        this.checkY(newLineY)
        val leftOffset = this.fetchLeftOffset(newLineY)
        var newX = leftOffset
        var newMaxWidth = this.desiredWidth - this.fetchRightOffset(newLineY) - leftOffset
        val rline: RLine
        val initialAllowOverflow: Boolean
        if (prevLine == null) {
            // Note: Assumes that prevLine == null means it's the first line.
            val rs = this.modelNode()!!.renderState()
            initialAllowOverflow = rs != null && rs.whiteSpace == RenderState.WS_NOWRAP
            // Text indentation only applies to the first line in the block.
            val textIndent = if (rs == null) 0 else rs.getTextIndent(this.availContentWidth)
            if (textIndent != 0) {
                newX += textIndent
                // Line width also changes!
                newMaxWidth += (leftOffset - newX)
            }
        } else {
            val prevLineHeight = prevLine.height()
            if (prevLineHeight > 0) {
                this.currentCollapsibleMargin = 0
            }
            initialAllowOverflow = prevLine.isAllowOverflow()
            if ((prevLine.x() + prevLine.width()) > this.maxX) {
                this.maxX = prevLine.x() + prevLine.width()
            }
        }
        rline =
            RLine(startNode, this.container, newX, newLineY, newMaxWidth, 0, initialAllowOverflow)
        rline.setParent(this)
        var sr = this.seqRenderables
        if (sr == null) {
            sr = ArrayList<BoundableRenderable>(1)
            this.seqRenderables = sr
        }
        sr.add(rline)
        this.currentLine = rline
        return rline
    }

    private fun layoutMarkup(node: NodeImpl) {
        // This is the "inline" layout of an element.
        // The difference with layoutChildren is that this
        // method checks for padding and margin insets.
        val rs = node.getRenderState()
        val mi = rs.marginInsets
        val marginInsets = if (mi == null) null else mi.getSimpleAWTInsets(
            this.availContentWidth,
            this.availContentHeight
        )
        val pi = rs.paddingInsets
        val paddingInsets = if (pi == null) null else pi.getSimpleAWTInsets(
            this.availContentWidth,
            this.availContentHeight
        )

        var leftSpacing = 0
        var rightSpacing = 0
        if (marginInsets != null) {
            leftSpacing += marginInsets.left
            rightSpacing += marginInsets.right
        }
        if (paddingInsets != null) {
            leftSpacing += paddingInsets.left
            rightSpacing += paddingInsets.right
        }
        if (leftSpacing > 0) {
            val line = this.currentLine!!
            line.addSpacing(RSpacing(node, this.container, leftSpacing, line.height()))
        }
        this.layoutChildren(node)
        if (rightSpacing > 0) {
            val line = this.currentLine!!
            line.addSpacing(RSpacing(node, this.container, rightSpacing, line.height()))
        }
    }

    /*
  private void addAsSeqBlockCheckStyle(final @NonNull RElement block, final HTMLElementImpl element, final boolean usesAlignAttribute) {
    if (this.addElsewhereIfPositioned(block, element, usesAlignAttribute, false, true)) {
      return;
    }
    this.addAsSeqBlock(block);
  }*/
    private fun layoutChildren(node: NodeImpl) {
        firstElementProcessed = false
        lastElementBeingProcessed = false

        val childrenArray = getAllNodeChildren(node)
        if (childrenArray != null) {
            val length = childrenArray.size
            for (i in 0..<length) {
                val child: NodeImpl = childrenArray[i]!!
                val nodeType = child.getNodeType()
                if (nodeType == Node.TEXT_NODE) {
                    this.layoutText(child)
                } else if (nodeType == Node.ELEMENT_NODE) {
                    // Note that scanning for node bounds (anchor location)
                    // depends on there being a style changer for inline elements.
                    this.currentLine!!.addStyleChanger(RStyleChanger(child))
                    val nodeName = child.getNodeName().uppercase(Locale.getDefault())
                    var ml: MarkupLayout? = elementLayout.get(nodeName)
                    if (ml == null) {
                        ml = commonLayout
                    }
                    if (isLastElement(i, childrenArray)) {
                        lastElementBeingProcessed = true
                    }
                    ml.layoutMarkup(this, child as HTMLElementImpl)
                    this.currentLine!!.addStyleChanger(RStyleChanger(node))
                    firstElementProcessed = true
                } else if ((nodeType == Node.COMMENT_NODE) || (nodeType == Node.PROCESSING_INSTRUCTION_NODE)) {
                    // ignore
                } else if (nodeType == Node.DOCUMENT_FRAGMENT_NODE) {
                    val fragment = child as DocumentFragmentImpl
                    for (fragChild in fragment.getChildrenArray()!!) {
                        layoutChildren(fragChild!!)
                    }
                } else {
                    /* TODO: This case is encountered in some web-platform-tests,
                                        * for example: /dom/ranges/Range-deleteContents.html
                                        */
                    // throw new IllegalStateException("Unknown node: " + child);

                    System.err.println("Unknown node: " + child)
                }
            }
        }
    }

    private fun getAllNodeChildren(node: NodeImpl): Array<NodeImpl?>? {
        val childrenArray = node.getChildrenArray()
        var beforeNode: NodeImpl? = null
        var afterNode: NodeImpl? = null
        if (node is HTMLElementImpl) {
            beforeNode = node.getBeforeNode()
            afterNode = node.getAfterNode()
        }
        if (beforeNode == null && afterNode == null) {
            return childrenArray
        } else {
            val totalNodes = (if (childrenArray == null) 0 else childrenArray.size) +
                    (if (beforeNode == null) 0 else 1) +
                    (if (afterNode == null) 0 else 1)
            if (totalNodes == 0) {
                return null
            } else {
                val result = arrayOfNulls<NodeImpl>(totalNodes)
                var count = 0
                if (beforeNode != null) {
                    result[count++] = beforeNode
                }
                if (childrenArray != null) {
                    System.arraycopy(childrenArray, 0, result, count, childrenArray.size)
                    count += childrenArray.size
                }
                if (afterNode != null) {
                    result[count++] = afterNode
                }
                return result
            }
        }
    }

    private fun positionRBlock(markupElement: HTMLElementImpl, renderable: RBlock) {
        run {
            val rs: RenderState = renderable.modelNode()!!.renderState()!!
            val clear = rs.clear
            if (clear != LineBreak.Companion.NONE) {
                addLineBreak(renderable.modelNode()!!, clear)
            }
        }
        if (!this.addElsewhereIfPositioned(renderable, markupElement, false, true, false)) {
            val availContentHeight = this.availContentHeight
            val line = this.currentLine
            // Inform line done before layout so floats are considered.
            this.lineDone(line)
            val paddingInsets = this.paddingInsets!!
            val newLineY = if (line == null) paddingInsets.top else line.y() + line.height()
            // int leftOffset = this.fetchLeftOffset(newLineY);
            // int rightOffset = this.fetchRightOffset(newLineY);
            // Float offsets are ignored with block.
            val availContentWidth = this.availContentWidth
            val expectedWidth = availContentWidth
            val blockShiftRight = paddingInsets.right
            val newX = paddingInsets.left
            val newY = newLineY
            val floatBounds = this.floatBounds
            val floatBoundsSource: FloatingBoundsSource? =
                if (floatBounds == null) null else ParentFloatingBoundsSource(
                    blockShiftRight,
                    expectedWidth,
                    newX, newY, floatBounds
                )

            if (this.isFirstBlock) {
                this.currentCollapsibleMargin = 0
            }

            val isFirstCollapsibleBlock = isFirstCollapsibleBlock(renderable)
            val isLastCollapsibleBlock = isLastCollapsibleBlock(renderable)
            renderable.setCollapseTop(isFirstCollapsibleBlock)
            renderable.setCollapseBottom(isLastCollapsibleBlock)

            /*
      if (isFirstCollapsibleBlock) {
        System.out.println("First block: " + renderable);
      }
      if (isLastCollapsibleBlock) {
        System.out.println("Last block: " + renderable);
      }
      */
            renderable.layout(
                availContentWidth,
                availContentHeight,
                true,
                false,
                floatBoundsSource,
                this.sizeOnly
            )

            if (isFirstCollapsibleBlock) {
                val pBlock = this.parent as RBlock
                pBlock.absorbMarginTopChild(renderable.marginTopOriginal)
            }
            if (isLastCollapsibleBlock) {
                val pBlock = this.parent as RBlock
                pBlock.absorbMarginBottomChild(renderable.marginBottomOriginal)
            }


            // if pos:relative then send it to parent for drawing along with other positioned elements.
            this.addAsSeqBlock(renderable, false, false, false, false, false)
            // Calculate new floating bounds after block has been put in place.
            val floatingInfo = renderable.exportableFloatingInfo
            if (floatingInfo != null) {
                this.importFloatingInfo(floatingInfo, renderable)
            }
            // Now add line, after float is set.
            this.addLineAfterBlock(renderable, false)

            bubbleUpIfRelative(markupElement, renderable)
        }
    }

    /* This is used to bubble up relative elements (on the z-axis) */
    private fun bubbleUpIfRelative(markupElement: HTMLElementImpl, renderable: RElement): Boolean {
        val position: Int = getPosition(markupElement)
        val isRelative = position == RenderState.POSITION_RELATIVE
        if (isRelative) {
            val con: RenderableContainer? = getPositionedAncestor(container)
            val dp = DelayedPair(
                container!!,
                con!!,
                renderable,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                0,
                position
            )
            container.addDelayedPair(dp)
            if (renderable is RUIControl) {
                this.container.addComponent(renderable.widget.component())
            }
            return true
        }

        return false
    }

    private fun positionRElement(
        markupElement: HTMLElementImpl, renderable: RElement, usesAlignAttribute: Boolean,
        obeysFloats: Boolean,
        alignCenterAttribute: Boolean
    ) {
        if (!this.addElsewhereIfPositioned(
                renderable,
                markupElement,
                usesAlignAttribute,
                true,
                true
            )
        ) {
            var availContentWidth = this.availContentWidth
            val availContentHeight = this.availContentHeight
            val line = this.currentLine
            // Inform line done before layout so floats are considered.
            this.lineDone(line)
            if (obeysFloats) {
                val newLineY = if (line == null) this.paddingInsets!!.top else line.y() + line.height()
                val leftOffset = this.fetchLeftOffset(newLineY)
                val rightOffset = this.fetchRightOffset(newLineY)
                availContentWidth = this.desiredWidth - leftOffset - rightOffset
            }
            renderable.layout(availContentWidth, availContentHeight, this.sizeOnly)
            var centerBlock = false
            if (alignCenterAttribute) {
                val align = markupElement.getAttribute("align")
                centerBlock = (align != null) && align.equals("center", ignoreCase = true)
            }
            this.addAsSeqBlock(renderable, obeysFloats, false, true, centerBlock, false)
            bubbleUpIfRelative(markupElement, renderable)
        }
    }

    private fun layoutRBlock(markupElement: HTMLElementImpl) {
        val uiNode = markupElement.uINode
        var renderable: RBlock? = null
        if (uiNode is RBlock) {
            renderable = markupElement.uINode as RBlock?
        }
        if (renderable == null) {
            renderable = RBlock(
                markupElement,
                this.listNesting,
                this.userAgentContext,
                this.rendererContext,
                this.frameContext,
                this.container
            )
            markupElement.uINode = renderable
        }
        renderable.setOriginalParent(this)
        this.positionRBlock(markupElement, renderable)
    }

    private fun layoutRTable(markupElement: HTMLElementImpl) {
        var renderable = markupElement.uINode as RElement?
        if (renderable == null) {
            renderable = RTable(
                modelNode = markupElement,
                pcontext = this.userAgentContext,
                rcontext = this.rendererContext,
                frameContext = this.frameContext,
                container = container
            )
            markupElement.uINode = renderable
        }
        renderable.setOriginalParent(this)
        this.positionRElement(
            markupElement,
            renderable,
            markupElement is HTMLTableElementImpl,
            true,
            true
        )
    }

    private fun layoutListItem(markupElement: HTMLElementImpl) {
        var renderable = markupElement.uINode as RListItem?
        if (renderable == null) {
            renderable = RListItem(
                markupElement,
                this.listNesting,
                this.userAgentContext,
                this.rendererContext,
                this.frameContext,
                this.container,
                null
            )
            markupElement.uINode = renderable
        }
        renderable.setOriginalParent(this)
        this.positionRBlock(markupElement, renderable)
    }

    private fun layoutList(markupElement: HTMLElementImpl) {
        var renderable = markupElement.uINode as RList?
        if (renderable == null) {
            renderable = RList(
                markupElement,
                this.listNesting,
                this.userAgentContext,
                this.rendererContext,
                this.frameContext,
                this.container,
                null
            )
            markupElement.uINode = renderable
        }
        renderable.setOriginalParent(this)
        this.positionRBlock(markupElement, renderable)
    }

    private fun addLineBreak(startNode: ModelNode, breakType: Int) {
        var line = this.currentLine
        if (line == null) {
            val insets = this.paddingInsets!!
            this.addLine(startNode, null, insets.top)
            line = this.currentLine
        }
        if (line!!.height() == 0) {
            val rs: RenderState = startNode.renderState()!!
            val fontHeight = rs.fontMetrics!!.height
            line.setHeight(fontHeight)
        }
        line.lineBreak = (LineBreak(breakType))
        val newLineY: Int
        val fb = this.floatBounds
        if ((breakType == LineBreak.Companion.NONE) || (fb == null)) {
            newLineY = line.y() + line.height()
        } else {
            val prevY = line.y() + line.height()
            when (breakType) {
                LineBreak.Companion.LEFT -> newLineY = fb.getLeftClearY(prevY)
                LineBreak.Companion.RIGHT -> newLineY = fb.getRightClearY(prevY)
                else -> newLineY = fb.getClearY(prevY)
            }
        }
        this.currentLine = this.addLine(startNode, line, newLineY)
    }

    private fun addElsewhereIfFloat(
        renderable: RElement, element: HTMLElementImpl,
        usesAlignAttribute: Boolean,
        style: JStyleProperties?, layout: Boolean
    ): Boolean {
        // "static" handled here
        var align: String? = null
        if (style != null) {
            align = style.float
            if ((align != null) && (align.length == 0)) {
                align = null
            }
        }
        if ((align == null) && usesAlignAttribute) {
            align = element.getAttribute("align")
        }
        if (align != null) {
            if ("left".equals(align, ignoreCase = true)) {
                this.layoutFloat(renderable, layout, true)
                return true
            } else if ("right".equals(align, ignoreCase = true)) {
                this.layoutFloat(renderable, layout, false)
                return true
            } else {
                // fall through
            }
        }
        return false
    }

    val parentViewport: RBlockViewport?
        get() {
            // Use originalParent, which for one, is not going to be null during layout.
            var parent = this.originalOrCurrentParent()
            while ((parent != null) && parent !is RBlockViewport) {
                parent = parent.originalOrCurrentParent()
            }
            return parent
        }

    /**
     * Checks for position and float attributes.
     *
     * @param container
     * @param containerSize
     * @param insets
     * @param renderable
     * @param element
     * @param usesAlignAttribute
     * @return True if it was added elsewhere.
     */
    private fun addElsewhereIfPositioned(
        renderable: RElement, element: HTMLElementImpl, usesAlignAttribute: Boolean,
        layoutIfPositioned: Boolean, obeysFloats: Boolean
    ): Boolean {
        // At this point block already has bounds.
        val style = element.getCurrentStyle()
        val position: Int = getPosition(element)
        val absolute = position == RenderState.POSITION_ABSOLUTE
        val fixed = position == RenderState.POSITION_FIXED
        if (absolute || fixed) {
            if (layoutIfPositioned) {
                // Presumes the method will return true.
                if (renderable is RBlock) {
                    renderable.layout(
                        this.availContentWidth,
                        this.availContentHeight,
                        false,
                        false,
                        null,
                        this.sizeOnly
                    )
                } else {
                    renderable.layout(
                        this.availContentWidth,
                        this.availContentHeight,
                        this.sizeOnly
                    )
                }
            }

            val rs = element.getRenderState()
            val leftText = style.left
            val rightText = style.right
            val bottomText = style.bottom
            val topText = style.top
            val widthText = style.width
            val heightText = style.height

            // Schedule as delayed pair. Will be positioned after everything else.
            this.scheduleAbsDelayedPair(
                renderable,
                leftText,
                rightText,
                topText,
                bottomText,
                widthText,
                heightText,
                rs,
                currentLine!!.x(),
                currentLine!!.y() + currentLine!!.height(),
                absolute
            )
            // Does not affect bounds of this viewport yet.
            return true
        } else {
            return this.addElsewhereIfFloat(
                renderable,
                element,
                usesAlignAttribute,
                style,
                layoutIfPositioned
            )
        }
    }

    /**
     * Checks property 'float' and in some cases attribute 'align'.
     */
    private fun addRenderableToLineCheckStyle(
        renderable: RElement,
        element: HTMLElementImpl,
        usesAlignAttribute: Boolean
    ) {
        if (this.addElsewhereIfPositioned(renderable, element, usesAlignAttribute, true, true)) {
            return
        }
        renderable.layout(this.availContentWidth, this.availContentHeight, this.sizeOnly)
        this.addRenderableToLine(renderable)
        bubbleUpIfRelative(element, renderable)
    }

    /**
     * @param others         An ordered collection.
     * @param seqRenderables
     * @param destination
     */
    /*
  private static void populateZIndexGroupsIterator(final Collection<PositionedRenderable> others,
      final Collection<? extends Renderable> seqRenderables,
      final ArrayList<Renderable> destination) {
    populateZIndexGroups(others, seqRenderables == null ? null : seqRenderables.iterator(), destination);
  }*/
    private fun addRenderableToLine(renderable: Renderable) {
        // this.skipLineBreakBefore = false;
        val line = this.currentLine!!
        val liney = line.y()
        val emptyLine = line.isEmpty
        val floatBounds = this.floatBounds
        val cleary: Int
        if (floatBounds != null) {
            cleary = floatBounds.getFirstClearY(liney)
        } else {
            cleary = liney + line.height()
        }
        try {
            line.add(renderable)
            // Check if the line goes into the float.
            if ((floatBounds != null) && (cleary > liney)) {
                val rightOffset = this.fetchRightOffset(liney)
                val topLineX = this.desiredWidth - rightOffset
                if ((line.x() + line.width()) > topLineX) {
                    // Shift line down to clear area
                    line.setY(cleary)
                }
            }
        } catch (oe: OverflowException) {
            val nextY = if (emptyLine) cleary else liney + line.height()
            this.addLine(renderable.modelNode(), line, nextY)
            val renderables = oe.renderables
            val i: MutableIterator<Renderable?> = renderables!!.iterator()
            while (i.hasNext()) {
                val r = i.next()!!
                this.addRenderableToLine(r)
            }
        }
        if (renderable is RUIControl) {
            this.container?.addComponent(renderable.widget.component())
        }
    }

    private fun addWordToLine(renderable: RWord) {
        // this.skipLineBreakBefore = false;
        val line = this.currentLine!!
        val liney = line.y()
        val emptyLine = line.isEmpty
        val floatBounds = this.floatBounds
        val cleary: Int
        if (floatBounds != null) {
            cleary = floatBounds.getFirstClearY(liney)
        } else {
            cleary = liney + line.height()
        }
        try {
            line.addWord(renderable)
            // Check if the line goes into the float.
            if (!line.isAllowOverflow() && (floatBounds != null) && (cleary > liney)) {
                val rightOffset = this.fetchRightOffset(liney)
                val topLineX = this.desiredWidth - rightOffset
                if ((line.x() + line.width()) > topLineX) {
                    // Shift line down to clear area
                    line.setY(cleary)
                }
            }
        } catch (oe: OverflowException) {
            val nextY = if (emptyLine) cleary else liney + line.height()
            this.addLine(renderable.modelNode(), line, nextY)
            val renderables = oe.renderables
            val i: MutableIterator<Renderable?> = renderables!!.iterator()
            while (i.hasNext()) {
                val r = i.next()!!
                this.addRenderableToLine(r)
            }
        }
    }

    private fun addAsSeqBlock(block: RElement) {
        this.addAsSeqBlock(block, true, true, true, false, false)
    }

    private fun getNewBlockY(newBlock: BoundableRenderable?, expectedY: Int): Int {
        // Assumes the previous block is not a line with height > 0.
        if (newBlock !is RElement) {
            return expectedY
        }
        val ccm = this.currentCollapsibleMargin
        val topMargin = newBlock.marginTop()
        return expectedY - min(topMargin, ccm)
    }

    /*
  @Override
  public BoundableRenderable getRenderable(final int x, final int y) {
    // TODO: Optimize. Find only the first renderable instead of all of them
    final Iterator<? extends Renderable> i = this.getRenderables(x, y);
    return i == null ? null : (i.hasNext() ? (BoundableRenderable) i.next() : null);
  }
  */
    private fun getEffectiveBlockHeight(block: BoundableRenderable): Int {
        // Assumes block is the last one in the sequence.
        if (block !is RElement) {
            return block.height()
        }
        val parent = this.parent()
        if (parent !is RElement) {
            return block.height()
        }
        val blockMarginBottom = block.marginBottom()
        val parentMarginBottom = parent.collapsibleMarginBottom()
        return block.height() - min(blockMarginBottom, parentMarginBottom)
    }

    private fun addAsSeqBlock(
        block: BoundableRenderable, obeysFloats: Boolean, informLineDone: Boolean,
        addLine: Boolean, centerBlock: Boolean, isRelative: Boolean
    ) {
        val insets = this.paddingInsets!!
        val insetsl = insets.left
        var sr = this.seqRenderables
        if (sr == null) {
            sr = ArrayList<BoundableRenderable>(1)
            this.seqRenderables = sr
        }
        val prevLine = this.currentLine
        val initialAllowOverflow: Boolean
        if (prevLine != null) {
            initialAllowOverflow = prevLine.isAllowOverflow()
            if (informLineDone) {
                this.lineDone(prevLine)
            }
            if ((prevLine.x() + prevLine.width()) > this.maxX) {
                this.maxX = prevLine.x() + prevLine.width()
            }
            // Check height only with floats.
        } else {
            initialAllowOverflow = false
        }
        val prevLineHeight = if (prevLine == null) 0 else prevLine.height()
        var newLineY = if (prevLine == null) insets.top else prevLine.y() + prevLineHeight
        var blockX: Int
        var blockY = if (prevLineHeight == 0) this.getNewBlockY(block, newLineY) else newLineY
        val blockWidth = block.width()
        if (obeysFloats) {
            // TODO: execution of fetchLeftOffset done twice with positionRElement.
            val floatBounds = this.floatBounds
            val actualAvailWidth: Int
            if (floatBounds != null) {
                val blockOffset = this.fetchLeftOffset(newLineY)
                blockX = blockOffset
                val rightOffset = this.fetchRightOffset(newLineY)
                actualAvailWidth = this.desiredWidth - rightOffset - blockOffset
                if (blockWidth > actualAvailWidth) {
                    blockY = floatBounds.getClearY(newLineY)
                }
            } else {
                actualAvailWidth = this.availContentWidth
                blockX = insetsl
            }
            if (centerBlock) {
                val roomX = actualAvailWidth - blockWidth
                if (roomX > 0) {
                    blockX += (roomX / 2)
                }
            }
        } else {
            // Block does not obey alignment margins
            blockX = insetsl
        }
        block.setOrigin(blockX, blockY)
        if (!isRelative) {
            sr.add(block)
            block.setParent(this)
        }
        if ((blockX + blockWidth) > this.maxX) {
            this.maxX = blockX + blockWidth
        }
        this.lastSeqBlock = block
        this.currentCollapsibleMargin = if (block is RElement) block.marginBottom() else 0
        if (addLine) {
            newLineY = blockY + block.height()
            this.checkY(newLineY)
            val leftOffset = this.fetchLeftOffset(newLineY)
            val newX = leftOffset
            val newMaxWidth = this.desiredWidth - this.fetchRightOffset(newLineY) - leftOffset
            val lineNode = block.modelNode()!!.parentModelNode()
            val newLine = RLine(
                lineNode,
                this.container,
                newX,
                newLineY,
                newMaxWidth,
                0,
                initialAllowOverflow
            )
            newLine.setParent(this)
            sr.add(newLine)
            this.currentLine = newLine
        }
        if (!isRelative) {
            if (block is RUIControl) {
                this.container!!.addComponent(block.widget.component())
            }
        }
    }

    private fun isFirstCollapsibleBlock(child: RBlock): Boolean {
        return this.isFirstBlock && isCollapsibleBlock(
            child,
            Function { insets: HtmlInsets? -> checkTopInset(insets) })
                && isCollapsibleParentBlock(
            (this.parent as RBlock?)!!,
            Function { insets: HtmlInsets? -> checkTopInset(insets) })
    }

    private fun isLastCollapsibleBlock(child: RBlock): Boolean {
        return lastElementBeingProcessed && isCollapsibleBlock(
            child,
            Function { insets: HtmlInsets? -> checkBottomInset(insets) })
                && isCollapsibleParentBlock(
            (this.parent as RBlock?)!!,
            Function { insets: HtmlInsets? -> checkBottomInset(insets) })
    }

    private val isFirstBlock: Boolean
        get() {
            val sr = this.seqRenderables
            return (!firstElementProcessed) && (sr == null || ((sr.size == 1) && (sr.get(0) is RLine) && (sr.get(
                0
            ) as RLine).isEmpty))
        }

    private fun addLineAfterBlock(block: RBlock, informLineDone: Boolean) {
        var sr = this.seqRenderables
        if (sr == null) {
            sr = ArrayList<BoundableRenderable>(1)
            this.seqRenderables = sr
        }
        val prevLine = this.currentLine
        val initialAllowOverflow: Boolean
        if (prevLine != null) {
            initialAllowOverflow = prevLine.isAllowOverflow()
            if (informLineDone) {
                this.lineDone(prevLine)
            }
            if ((prevLine.x() + prevLine.width()) > this.maxX) {
                this.maxX = prevLine.x() + prevLine.width()
            }
            // Check height only with floats.
        } else {
            initialAllowOverflow = false
        }
        val lineNode = block.modelNode()!!.parentModelNode()
        val newLineY = block.y() + block.height()
        this.checkY(newLineY)
        val leftOffset = this.fetchLeftOffset(newLineY)
        val newX = leftOffset
        val newMaxWidth = this.desiredWidth - this.fetchRightOffset(newLineY) - leftOffset
        val newLine =
            RLine(lineNode, this.container, newX, newLineY, newMaxWidth, 0, initialAllowOverflow)
        newLine.setParent(this)
        sr.add(newLine)
        this.currentLine = newLine
    }

    private fun layoutText(textNode: NodeImpl) {
        val text = textNode.getNodeValue()
        val length = text!!.length
        val renderState = textNode.getRenderState()
        val fm = renderState.fontMetrics!!
        val descent = fm.descent
        val ascentPlusLeading = fm.ascent + fm.leading
        val wordHeight = fm.height
        val blankWidth = fm.charWidth(' ')
        val whiteSpace =
            if (this.overrideNoWrap) RenderState.WS_NOWRAP else renderState.whiteSpace
        val textTransform = renderState.textTransform
        if (whiteSpace != RenderState.WS_PRE) {
            val prevAllowOverflow = this.currentLine!!.isAllowOverflow()
            val allowOverflow = whiteSpace == RenderState.WS_NOWRAP
            this.currentLine!!.setAllowOverflow(allowOverflow)
            try {
                val word = StringBuffer(12)
                var i = 0
                while (i < length) {
                    var ch = text.get(i)
                    if (Character.isWhitespace(ch)) {
                        val wlen = word.length
                        if (wlen > 0) {
                            val rword = RWord(
                                textNode,
                                word.toString(),
                                container,
                                fm,
                                descent,
                                ascentPlusLeading,
                                wordHeight,
                                textTransform
                            )
                            this.addWordToLine(rword)
                            word.delete(0, wlen)
                        }
                        val line = this.currentLine!!
                        if (line.width() > 0) {
                            val rblank = RBlank(
                                textNode,
                                fm,
                                container,
                                ascentPlusLeading,
                                blankWidth,
                                wordHeight
                            )
                            line.addBlank(rblank)
                        }
                        i++
                        while (i < length) {
                            ch = text.get(i)
                            if (!Character.isWhitespace(ch)) {
                                word.append(ch)
                                break
                            }
                            i++
                        }
                    } else {
                        word.append(ch)
                    }
                    i++
                }
                if (word.length > 0) {
                    val rword = RWord(
                        textNode,
                        word.toString(),
                        container,
                        fm,
                        descent,
                        ascentPlusLeading,
                        wordHeight,
                        textTransform
                    )
                    this.addWordToLine(rword)
                }
            } finally {
                this.currentLine!!.setAllowOverflow(prevAllowOverflow)
            }
        } else {
            var lastCharSlashR = false
            val line = StringBuffer()
            for (i in 0..<length) {
                val ch = text.get(i)
                when (ch) {
                    '\r' -> lastCharSlashR = true
                    '\n' -> {
                        val rword = RWord(
                            textNode,
                            line.toString(),
                            container,
                            fm,
                            descent,
                            ascentPlusLeading,
                            wordHeight,
                            textTransform
                        )
                        this.addWordToLine(rword)
                        line.delete(0, line.length)
                        val prevLine = this.currentLine!!
                        prevLine.lineBreak = (LineBreak(LineBreak.Companion.NONE))
                        this.addLine(textNode, prevLine, prevLine.y() + prevLine.height())
                    }

                    else -> {
                        if (lastCharSlashR) {
                            line.append('\r')
                            lastCharSlashR = false
                        }
                        if (ch == '\t') {
                            /* Tabs are not recognized as advancing width in FontMetrics. There are two approaches possible:
               1. Convert to spaces. Simple, but when copying selection spaces are copied.
               2. Define a new class call RTab, that manages tabs.
               3. Modify the width calculation logic of RWord to account for tab character.
            */

                            // TODO: The number of spaces is hard-coded right now. But when CSS `tab-size` property is supported, it could be made variable.

                            val NUM_SPACES = 8

                            // Solution 1.
                            // line.append("        ");

                            // Solution 2.
                            addWordToLine(
                                RTab(
                                    textNode,
                                    container,
                                    fm,
                                    descent,
                                    ascentPlusLeading,
                                    wordHeight,
                                    NUM_SPACES
                                )
                            )
                        } else {
                            line.append(ch)
                        }
                    }
                }
            }
            if (line.length > 0) {
                val rword = RWord(
                    textNode,
                    line.toString(),
                    container,
                    fm,
                    descent,
                    ascentPlusLeading,
                    wordHeight,
                    textTransform
                )
                this.addWordToLine(rword)
            }
        }
    }

    override fun getRenderables(topFirst: Boolean): MutableIterator<Renderable>? {
        val others = this.positionedRenderables
        val sr: ArrayList<out Renderable>? = this.seqRenderables
        if ((others == null) || (others.size == 0)) {
            return if (sr == null) null else sr.iterator()
        } else {
            val allRenderables = ArrayList<Renderable>()
            val srIterator = if (sr == null) null else sr.iterator()
            if (topFirst) {
                populateZIndexGroupsTopFirst(
                    _root_ide_package_.java.util.ArrayList<PositionedRenderable>(others),
                    srIterator,
                    allRenderables
                )
            } else {
                populateZIndexGroups(
                    others as MutableCollection<PositionedRenderable>,
                    srIterator, allRenderables
                )
            }
            return allRenderables.iterator()
        }
    }



    private fun getRenderables(clipBounds: Rectangle): MutableIterator<Renderable?>? {
        val sr = this.seqRenderables
        var baseIterator: MutableIterator<Renderable?>? = null
        if (sr != null) {
            val array = sr.toArray<Renderable?>(Renderable.Companion.EMPTY_ARRAY)
            val range = MarkupUtilities.findRenderables(array, clipBounds, true)
            baseIterator = ArrayUtilities.iterator<Renderable?>(array, range.offset, range.length)
        }

        val others = this.positionedRenderables
        if ((others == null) || (others.size == 0)) {
            return baseIterator
        } else {
            val matches = ArrayList<PositionedRenderable>()
            // ArrayList "matches" keeps the order from "others".
            val i: MutableIterator<PositionedRenderable?> = others.iterator()
            while (i.hasNext()) {
                val pr = i.next()!!

                if (pr.isFixed() || clipBounds.intersects(pr.visualBounds)) {
                    matches.add(pr)
                }
                // matches.add(pr);
            }
            if (matches.size == 0) {
                return baseIterator
            } else {
                val destination = ArrayList<Renderable>()
                populateZIndexGroups(matches, baseIterator, destination)
                return destination.iterator()
            }
        }
    }

    fun getRenderable(point: Point): BoundableRenderable? {
        return this.getRenderable(point.x, point.y)
    }

    fun getRenderables(point: Point): MutableIterator<Renderable?>? {
        return this.getRenderables(point.x, point.y)
    }

    fun getRenderables(pointx: Int, pointy: Int): MutableIterator<Renderable?>? {
        var result: MutableCollection<BoundableRenderable?>? = null
        val others = this.positionedRenderables
        val size = if (others == null) 0 else others.size
        val otherArray: Array<PositionedRenderable>? =
            if (size == 0) null else emptyArray()
        // Try to find in other renderables with z-index >= 0 first.
        var index = 0
        if (otherArray != null) {
            // Must go in reverse order
            index = size
            while (--index >= 0) {
                val pr = otherArray[index]
                val br = pr.renderable
                if (br.zIndex() < 0) {
                    break
                }
                if (br.contains(pointx, pointy)) {
                    if (result == null) {
                        result = LinkedList<BoundableRenderable?>()
                    }
                    result.add(br)
                }
            }
        }

        // Now do a "binary" search on sequential renderables.
        /*
    final ArrayList<BoundableRenderable> sr = this.seqRenderables;
    if (sr != null) {
      final Renderable[] array = sr.toArray(Renderable.EMPTY_ARRAY);
      final BoundableRenderable found = MarkupUtilities.findRenderable(array, pointx, pointy, true);
      if (found != null) {
        if (result == null) {
          result = new LinkedList<>();
        }
        result.add(found);
      }
    }*/

        /* Get all sequential renderables that contain the point */
        val sr = this.seqRenderables
        if (sr != null) {
            val array = sr.toArray<Renderable?>(Renderable.Companion.EMPTY_ARRAY)
            val found = MarkupUtilities.findRenderables(array, pointx, pointy, true)
            if (found != null) {
                if (result == null) {
                    result = LinkedList<BoundableRenderable?>()
                }
                result.addAll(found)
            }
        }

        // Finally, try to find it in renderables with z-index < 0.
        if (otherArray != null) {
            // Must go in reverse order
            while (index >= 0) {
                val pr = otherArray[index]
                val br = pr.renderable
                if (br.contains(pointx, pointy)) {
                    if (result == null) {
                        result = LinkedList<BoundableRenderable?>()
                    }
                    result.add(br)
                }
                index--
            }
        }
        return if (result == null) null else result.iterator()
    }

    private fun setupNewUIControl(
        container: RenderableContainer?,
        element: HTMLElementImpl,
        control: UIControl
    ): RElement {
        val renderable: RElement =
            RUIControl(
                element, control, container,
                this.frameContext!!, this.userAgentContext
            )
        element.uINode = renderable
        return renderable
    }

    private fun addAlignableAsBlock(markupElement: HTMLElementImpl, renderable: RElement) {
        // TODO: Get rid of this method?
        // At this point block already has bounds.
        var regularAdd = false
        val align = markupElement.getAttribute("align")
        if (align != null) {
            if ("left".equals(align, ignoreCase = true)) {
                this.layoutFloat(renderable, false, true)
            } else if ("right".equals(align, ignoreCase = true)) {
                this.layoutFloat(renderable, false, false)
            } else {
                regularAdd = true
            }
        } else {
            regularAdd = true
        }
        if (regularAdd) {
            this.addAsSeqBlock(renderable)
        }
    }

    private fun layoutHr(markupElement: HTMLElementImpl) {
        var renderable = markupElement.uINode as RElement?
        if (renderable == null) {
            renderable = this.setupNewUIControl(container, markupElement, HrControl(markupElement))
        }
        renderable.layout(this.availContentWidth, this.availContentHeight, this.sizeOnly)
        this.addAlignableAsBlock(markupElement, renderable)
    }

    /**
     * Gets offset from the left due to floats. It includes padding.
     */
    private fun fetchLeftOffset(newLineY: Int): Int {
        val paddingInsets = this.paddingInsets!!
        val floatBounds = this.floatBounds
        if (floatBounds == null) {
            return paddingInsets.left
        }
        val left = floatBounds.getLeft(newLineY)
        return max(left, paddingInsets.left)
    }

    /**
     * Gets offset from the right due to floats. It includes padding.
     */
    private fun fetchRightOffset(newLineY: Int): Int {
        val paddingInsets = this.paddingInsets!!
        val floatBounds = this.floatBounds
        if (floatBounds == null) {
            return paddingInsets.right
        }
        val right = floatBounds.getRight(newLineY)
        return max(right, paddingInsets.right)
    }

    private fun checkY(y: Int) {
        if ((this.yLimit != -1) && (y > this.yLimit)) {
            throw SEE
        }
    }

    private fun layoutFloat(renderable: RElement, layout: Boolean, leftFloat: Boolean) {
        renderable.setOriginalParent(this)
        if (layout) {
            val availWidth = this.availContentWidth
            val availHeight = this.availContentHeight
            if (renderable is RBlock) {
                // Float boxes don't inherit float bounds?
                renderable.layout(availWidth, availHeight, false, false, null, this.sizeOnly)
            } else {
                renderable.layout(availWidth, availHeight, this.sizeOnly)
            }
        }
        val floatInfo = RFloatInfo(renderable.modelNode(), renderable, leftFloat)

        // TODO: WHy is this required? Could RFloatInfo be removed completely?
        this.currentLine!!.simplyAdd(floatInfo)

        this.scheduleFloat(floatInfo)
    }

    /**
     * @param absolute if true, then position is absolute, else fixed
     */
    private fun scheduleAbsDelayedPair(
        renderable: BoundableRenderable,
        leftText: String?, rightText: String?, topText: String?, bottomText: String?,
        widthText: String?, heightText: String?,
        rs: RenderState?, currX: Int, currY: Int, absolute: Boolean
    ) {
        // It gets reimported in the local
        // viewport if it turns out it can't be exported up.
        val containingBlock: RenderableContainer? =
            if (absolute) getPositionedAncestor(this.container) else getRootContainer(container!!)
        val pos = if (absolute) RenderState.POSITION_ABSOLUTE else RenderState.POSITION_FIXED
        val pair = DelayedPair(
            this.container!!,
            containingBlock!!,
            renderable,
            leftText,
            rightText,
            topText,
            bottomText,
            widthText,
            heightText,
            rs,
            currX,
            currY,
            pos
        )
        this.container.addDelayedPair(pair)
    }

    fun importDelayedPair(pair: DelayedPair) {
        // if (!pair.isAdded()) {
        // pair.markAdded();
        val r = pair.positionPairChild()
        // final BoundableRenderable r = pair.child;
        this.addPositionedRenderable(r, false, false, pair.isFixed)
        // Size of block does not change - it's  set in stone?
        // }
    }


    private fun addPositionedRenderable(
        renderable: BoundableRenderable,
        verticalAlignable: Boolean,
        isFloat: Boolean,
        isFixed: Boolean,
        isDelegated: Boolean = false
    ) {
        // Expected to be called only in GUI thread.
        val pr = PositionedRenderable(
            renderable,
            verticalAlignable,
            this.positionedOrdinal++,
            isFloat,
            isFixed,
            isDelegated
        )
        addPosRenderable(pr)
        renderable.setParent(this)
        if (renderable is RUIControl) {
            this.container?.addComponent(renderable.widget.component())
        }
    }

    private fun addPosRenderable(pr: PositionedRenderable?) {
        // System.out.println("Adding : " + pr);
        // System.out.println("  to: " + this);
        var others = this.positionedRenderables
        if (others == null) {
            others = TreeSet(ZIndexComparator())
            this.positionedRenderables = others
        }
        others.add(pr)
        // System.out.println("  total: " + others.size());
    }

    val firstLineHeight: Int
        get() {
            val renderables = this.seqRenderables
            if (renderables != null) {
                val size = renderables.size
                if (size == 0) {
                    return 0
                }
                for (i in 0..<size) {
                    val br = renderables.get(0)
                    val height = br.height()
                    if (height != 0) {
                        return height
                    }
                }
            }
            // Not found!!
            return 1
        }

    val firstBaselineOffset: Int
        /*
              * (non-Javadoc)
              *
              * @see
              * org.xamjwg.html.renderer.BoundableRenderable#onMousePressed(java.awt.event
              * .MouseEvent, int, int)
              */
        get() {
            val renderables = this.seqRenderables
            if (renderables != null) {
                val i: MutableIterator<BoundableRenderable?> =
                    renderables.iterator()
                while (i.hasNext()) {
                    val r: Any? = i.next()
                    if (r is RLine) {
                        val blo = r.baselineOffset
                        if (blo != 0) {
                            return blo
                        }
                    } else if (r is RBlock) {
                        if (r.height() > 0) {
                            val insets = r.getInsetsMarginBorder(false, false)
                            val paddingInsets = this.paddingInsets
                            return r.firstBaselineOffset + insets.top + (if (paddingInsets == null) 0 else paddingInsets.top)
                        }
                    }
                }
            }
            return 0
        }



    override val visualX: Int
        get() = TODO("Not yet implemented")
    override val visualY: Int
        get() = TODO("Not yet implemented")


    override fun getLowestRenderableSpot(x: Int, y: Int): RenderableSpot? {
        val br = this.getRenderable(Point(x, y))
        if (br != null) {
            return br.getLowestRenderableSpot(x - br.x(), y - br.y())
        } else {
            return RenderableSpot(this, x, y)
        }
    }

    override fun onDoubleClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        val i = this.getRenderables(Point(x, y))
        if (i != null) {
            while (i.hasNext()) {
                val br = i.next() as BoundableRenderable?
                if (br != null) {

                    val or = br.getOriginRelativeTo(this)
                    if (!br.onMouseClick(event, x - or.x, y - or.y)) {
                        return false
                    }
                }
            }
        }
        return true
    }


    override fun zIndex(): Int {
        TODO("Not yet implemented")
    }



  
    override fun onMouseDisarmed(event: MouseEvent?): Boolean {
        val br = this.armedRenderable
        if (br != null) {
            try {
                return br.onMouseDisarmed(event)
            } finally {
                this.armedRenderable = null
            }
        } else {
            return true
        }
    }

    override fun onMouseReleased(event: MouseEvent?, x: Int, y: Int): Boolean {
        val i = this.getRenderables(Point(x, y))
        if (i != null) {
            while (i.hasNext()) {
                val br = i.next() as BoundableRenderable?
                if (br != null) {

                    val or = br.getOriginRelativeTo(this)
                    if (!br.onMouseReleased(event, x - or.x, y - or.y)) {
                        val oldArmedRenderable = this.armedRenderable
                        if ((oldArmedRenderable != null) && (br !== oldArmedRenderable)) {
                            oldArmedRenderable.onMouseDisarmed(event)
                            this.armedRenderable = null
                        }
                        return false
                    }
                }
            }
        }
        val oldArmedRenderable = this.armedRenderable
        if (oldArmedRenderable != null) {
            oldArmedRenderable.onMouseDisarmed(event)
            this.armedRenderable = null
        }
        return true
    }

    override fun paint(gIn: Graphics) {
        paint(gIn, gIn)
    }

    fun paint(gIn: Graphics, gInUnClipped: Graphics) {
        val translationRequired = (x() or y()) != 0
        val g = if (translationRequired) gIn.create() else gIn
        if (translationRequired) {
            g.translate(x(), y())
        }
        val gUnClipped = if (translationRequired) gInUnClipped.create() else gInUnClipped
        if (translationRequired) {
            gUnClipped.translate(x(), y())
        }
        try {
            val clipBounds = gUnClipped.clipBounds
            val i = this.getRenderables(clipBounds)
            if (i != null) {
                while (i.hasNext()) {
                    val robj = i.next()
                    // The expected behavior in HTML is for boxes
                    // not to be clipped unless overflow=hidden.
                    if (robj is BoundableRenderable) {
                        // if (!((renderable instanceof RBlock) && renderable.getModelNode().getRenderState().getPosition() == RenderState.POSITION_RELATIVE)) {
                        if (!robj.isDelegated()) {
                            robj.paintTranslated(g)
                        }
                    } else {
                        // PositionedRenderable, etc because they don't inherit from BoundableRenderable
                        val isReplacedElement = robj!!.isReplacedElement
                        val selectedG =
                            if (isReplacedElement) (if (robj.isFixed()) gIn else g) else (if (robj.isFixed()) gInUnClipped else gUnClipped)

                        if (modelNode() is HTMLDocument) {
                            var htmlRenderable = RenderUtils.findHtmlRenderable(this)
                            if (htmlRenderable is PositionedRenderable) {
                                htmlRenderable = htmlRenderable.renderable
                            }
                            // TODO: Handle other renderable types such as RTable
                            if (htmlRenderable is RBlock) {
                                val htmlBounds = htmlRenderable.clipBoundsWithoutInsets()
                                if (htmlBounds != null) {
                                    val clippedG =
                                        selectedG.create(0, 0, htmlBounds.width, htmlBounds.height)
                                    try {
                                        robj.paint(clippedG)
                                    } finally {
                                        clippedG.dispose()
                                    }
                                } else {
                                    robj.paint(selectedG)
                                }
                            } else {
                                robj.paint(selectedG)
                            }
                        } else {
                            robj.paint(selectedG)
                        }
                    }
                }
            }
        } finally {
            if (translationRequired) {
                g.dispose()
                gUnClipped.dispose()
            }
        }
    }

    override fun isContainedByNode(): Boolean {
        return false
    }

    private fun layoutRInlineBlock(markupElement: HTMLElementImpl) {
        val uINode = markupElement.uINode
        var inlineBlock: RInlineBlock? = null
        if (uINode is RInlineBlock) {
            inlineBlock = uINode
        } else {
            val newInlineBlock = RInlineBlock(
                container,
                markupElement,
                userAgentContext,
                rendererContext,
                frameContext
            )
            markupElement.uINode = newInlineBlock
            inlineBlock = newInlineBlock
        }
        inlineBlock.doLayout(availContentWidth, availContentHeight, sizeOnly)
        addRenderableToLine(inlineBlock)
        inlineBlock.setOriginalParent(inlineBlock.parent())
        bubbleUpIfRelative(markupElement, inlineBlock)
    }

    override fun toString(): String {
        return "RBlockViewport[node=" + this.modelNode() + "]"
    }

    private fun scheduleFloat(floatInfo: RFloatInfo) {
        val line = this.currentLine
        if (line == null) {
            val y = this.paddingInsets!!.top
            this.placeFloat(floatInfo.renderable!!, y, floatInfo.isLeftFloat)
        } else if (line.width() == 0) {
            val y = line.y()
            this.placeFloat(floatInfo.renderable!!, y, floatInfo.isLeftFloat)
            val leftOffset = this.fetchLeftOffset(y)
            val rightOffset = this.fetchRightOffset(y)
            line.changeLimits(leftOffset, this.desiredWidth - leftOffset - rightOffset)
        } else {
            // These pending floats are positioned when
            // lineDone() is called.
            var c = this.pendingFloats
            if (c == null) {
                c = LinkedList<RFloatInfo?>()
                this.pendingFloats = c
            }
            c.add(floatInfo)
        }
    }

    private fun lineDone(line: RLine?) {
        val pfs = this.pendingFloats
        if (pfs != null) {
            this.pendingFloats = null
            val i: MutableIterator<RFloatInfo?> = pfs.iterator()
            var yAfterLine = 0
            var yComputed = false
            while (i.hasNext()) {
                val pf = i.next()!!
                if (!yComputed) {
                    yAfterLine =
                        if (line == null) this.paddingInsets!!.top else (if (line.checkFit(pf.renderable!!)) line.y() else line.y() + line.height())
                    yComputed = true
                }
                this.placeFloat(pf.renderable!!, yAfterLine, pf.isLeftFloat)
            }
        }
    }

    private fun addExportableFloat(
        element: RElement?,
        leftFloat: Boolean,
        origX: Int,
        origY: Int,
        pendingPlacement: Boolean
    ) {
        var ep = this.exportableFloats
        if (ep == null) {
            ep = ArrayList<ExportableFloat?>(1)
            this.exportableFloats = ep
        }
        val ef = ExportableFloat(element, leftFloat, origX, origY)
        ef.pendingPlacement = pendingPlacement
        ep.add(ef)
    }

    private fun addFloat(renderable: RElement, newX: Int, newY: Int) {
        renderable.setOrigin(newX, newY)
        // TODO: Enabling this causes problems in some cases. See GH #153
        // if (!bubbleUpIfRelative((HTMLElementImpl) renderable.getModelNode(), renderable)) {
        this.addPositionedRenderable(renderable, true, true, false)
        // } else {
        // this.addPositionedRenderable(renderable, true, true, false, true);
        // }
    }

    // ------------------------------------------------------------------------
    /**
     * @param element
     * @param y         The desired top position of the float element.
     * @param floatType -1 (left) or +1 (right)
     */
    private fun placeFloat(element: RElement, y: Int, leftFloat: Boolean) {
        val insets = this.paddingInsets!!
        var boxY = y
        var boxWidth = element.width()
        var boxHeight = element.height()
        val desiredWidth = this.desiredWidth
        var boxX: Int
        while (true) {
            val leftOffset = this.fetchLeftOffset(boxY)
            val rightOffset = this.fetchRightOffset(boxY)
            boxX = if (leftFloat) leftOffset else desiredWidth - rightOffset - boxWidth
            if ((leftOffset == insets.left) && (rightOffset == insets.right)) {
                // Probably typical scenario. If it's overflowing to the left,
                // we need to correct.
                if (!leftFloat && (boxX < leftOffset)) {
                    boxX = leftOffset
                }
                break
            }
            if ((desiredWidth <= 0) || (boxWidth <= (desiredWidth - rightOffset - leftOffset))) {
                // Size is fine.
                break
            }
            // At this point the float doesn't fit at the current Y position.
            if (element is RBlock) {
                // Try shrinking it.
                if (!element.hasDeclaredWidth()) {
                    val availableBoxWidth = desiredWidth - rightOffset - leftOffset
                    element.layout(availableBoxWidth, this.availContentHeight, this.sizeOnly)
                    if (element.width() < boxWidth) {
                        if (element.width() > (desiredWidth - rightOffset - leftOffset)) {
                            // Didn't work out. Put it back the way it was.
                            element.layout(
                                this.availContentWidth,
                                this.availContentHeight,
                                this.sizeOnly
                            )
                        } else {
                            // Retry
                            boxWidth = element.width()
                            boxHeight = element.height()
                            continue
                        }
                    }
                }
            }
            val fb = this.floatBounds
            val newY = if (fb == null) boxY + boxHeight else fb.getFirstClearY(boxY)
            if (newY == boxY) {
                // Possible if prior box has height zero?
                break
            }
            boxY = newY
        }
        // Position element
        // element.setOrigin(boxX, boxY);
        // Update float bounds accordingly
        val offsetFromBorder = if (leftFloat) boxX + boxWidth else desiredWidth - boxX
        this.floatBounds =
            FloatingViewportBounds(this.floatBounds, leftFloat, boxY, offsetFromBorder, boxHeight)
        // Add element to collection
        val isFloatLimit = this.isFloatLimit()

        var placementPending = true
        if (getPosition((modelNode() as HTMLElementImpl?)!!) != RenderState.POSITION_STATIC) {
            addFloat(element, boxX, boxY)
            placementPending = false
        }

        if (isFloatLimit) {
            // System.out.println("Created float as renderable in " + this);
            // System.out.println("  r: " + element);
            if (placementPending) {
                addFloat(element, boxX, boxY)
            }
        } else {
            this.addExportableFloat(element, leftFloat, boxX, boxY, placementPending)
        }
        // Adjust maxX based on float.
        if ((boxX + boxWidth) > this.maxX) {
            this.maxX = boxX + boxWidth
        }
        // Adjust maxY based on float, but only if this viewport is the float limit.
        if (isFloatLimit) {
            if ((boxY + boxHeight) > this.maxY) {
                this.maxY = boxY + boxHeight
            }
        }
    }

    private fun isFloatLimit(): Boolean {
        var fl = this.isFloatLimit
        if (fl == null) {
            fl = this.isFloatLimitImpl
            this.isFloatLimit = fl
        }
        return fl
    }

    private val isFloatLimitImpl: Boolean
        get() {
            val parent: Any? = this.originalOrCurrentParent()
            if (parent !is RBlock) {
                return java.lang.Boolean.TRUE
            }
            val grandParent: Any? = parent.originalOrCurrentParent()
            if (grandParent !is RBlockViewport) {
                // Could be contained in a table, or it could
                // be a list item, for example.
                return java.lang.Boolean.TRUE
            }
            val node = this.modelNode()
            if (node !is HTMLElementImpl) {
                // Can only be a document here.
                return java.lang.Boolean.TRUE
            }
            val position: Int = getPosition(node)
            if ((position == RenderState.POSITION_ABSOLUTE) || (position == RenderState.POSITION_FIXED)) {
                return java.lang.Boolean.TRUE
            }
            val rs = node.getRenderState()
            val floatValue = rs.float
            if (floatValue != RenderState.FLOAT_NONE) {
                return java.lang.Boolean.TRUE
            }
            return !isOverflowVisibleOrNone(rs)
        }

    val exportableFloatingInfo: FloatingInfo?
        get() {
            val ef = this.exportableFloats
            if (ef == null) {
                return null
            }
            val floats =
                ef.toArray<ExportableFloat?>(ExportableFloat.Companion.EMPTY_ARRAY)
            return FloatingInfo(0, 0, floats)
        }

    private fun importFloatingInfo(floatingInfo: FloatingInfo, block: BoundableRenderable) {
        val shiftX = floatingInfo.shiftX + block.x()
        val shiftY = floatingInfo.shiftY + block.y()
        val floats = floatingInfo.floats
        val length = floats!!.size
        for (i in 0..<length) {
            val ef = floats.get(i)
            this.importFloat(ef!!, shiftX, shiftY)
        }
    }

    // /**
    // * Performs layout adjustment step.
    // * @param desiredWidth The desired viewport width, including padding.
    // * @param desiredHeight The desired viewport height, including padding.
    // * @param paddingInsets The padding insets.
    // * @param floatBounds The starting float bounds, including floats
    // * in ancestors.
    // */
    // public void adjust(int desiredWidth, int desiredHeight, Insets
    // paddingInsets, FloatingBounds floatBounds) {
    // // Initializations
    // this.paddingInsets = paddingInsets;
    // this.desiredHeight = desiredHeight;
    // this.desiredWidth = desiredWidth;
    // this.floatBounds = floatBounds;
    //
    // int availw = desiredWidth - paddingInsets.left - paddingInsets.right;
    // if(availw < 0) {
    // availw = 0;
    // }
    // int availh = desiredHeight - paddingInsets.top - paddingInsets.bottom;
    // if(availh < 0) {
    // availh = 0;
    // }
    // this.availContentWidth = availw;
    // this.availContentHeight = availh;
    //
    // // maxX and maxY should not be reset by layoutPass.
    // this.maxX = paddingInsets.left;
    // this.maxY = paddingInsets.top;
    //
    // // Keep copy of old sequential renderables,
    // // and clear the list.
    // ArrayList oldSeqRenderables = this.seqRenderables;
    // this.seqRenderables = null;
    //
    // // Clear current line
    // this.currentLine = null;
    //
    // // Reprocess all sequential renderables
    // if(oldSeqRenderables != null) {
    // Iterator i = oldSeqRenderables.iterator();
    // while(i.hasNext()) {
    // Renderable r = (Renderable) i.next();
    // this.reprocessSeqRenderable(r);
    // }
    // }
    //
    // RLine lastLine = this.currentLine;
    //
    // // This adds any pending floats
    // this.lineDone(this.currentLine);
    //
    // // Calculate maxX and maxY.
    // if(lastLine != null) {
    // Rectangle lastBounds = lastLine.getBounds();
    // int lastTopX = lastBounds.x + lastBounds.width;
    // if(lastTopX > this.maxX) {
    // this.maxX = lastTopX;
    // }
    // int lastTopY = lastBounds.y + lastBounds.height;
    // int maxY = this.maxY;
    // if(lastTopY > maxY) {
    // this.maxY = maxY = lastTopY;
    // }
    // }
    //
    // // Check positioned renderables for maxX and maxY
    // SortedSet posRenderables = this.positionedRenderables;
    // if(posRenderables != null) {
    // Iterator i = posRenderables.iterator();
    // while(i.hasNext()) {
    // PositionedRenderable pr = (PositionedRenderable) i.next();
    // BoundableRenderable br = pr.renderable;
    // if(br.getX() + br.getWidth() > this.maxX) {
    // this.maxX = br.getX() + br.getWidth();
    // }
    // if(br.getY() + br.getHeight() > this.maxY) {
    // this.maxY = br.getY() + br.getHeight();
    // }
    // }
    // }
    //
    // this.width = paddingInsets.right + this.maxX;
    // this.height = paddingInsets.bottom + maxY;
    // }
    // private void reprocessSeqRenderable(Renderable r) {
    // if(r instanceof RLine) {
    // this.reprocessLine((RLine) r);
    // }
    // else if(r instanceof RElement) {
    // this.reprocessElement((RElement) r);
    // }
    // else if(r instanceof RRelative) {
    // this.reprocessRelative((RRelative) r);
    // }
    // else {
    // throw new IllegalStateException("Unexpected Renderable: " + r);
    // }
    // }
    //
    // private void reprocessLine(RLine line) {
    // Iterator renderables = line.getRenderables();
    // if(renderables != null) {
    // while(renderables.hasNext()) {
    // Renderable r = (Renderable) renderables.next();
    // if(this.currentLine == null) {
    // // Must add at this point in case there was a float.
    // this.currentLine = this.addLine(r.getModelNode(), null,
    // this.paddingInsets.top);
    // }
    // if(r instanceof RWord) {
    // RWord word = (RWord) r;
    // this.addWordToLine(word);
    // }
    // else if (r instanceof RFloatInfo) {
    // RFloatInfo oldr = (RFloatInfo) r;
    // // Switch to a float info with registerElement=true.
    // this.scheduleFloat(new RFloatInfo(oldr.getModelNode(),
    // oldr.getRenderable(), oldr.isLeftFloat()));
    // }
    // else if (r instanceof RStyleChanger) {
    // RStyleChanger sc = (RStyleChanger) r;
    // RenderState rs = sc.getModelNode().getRenderState();
    // int whiteSpace = rs == null ? RenderState.WS_NORMAL : rs.getWhiteSpace();
    // boolean isAO = this.currentLine.isAllowOverflow();
    // if(!isAO && whiteSpace == RenderState.WS_NOWRAP) {
    // this.currentLine.setAllowOverflow(true);
    // }
    // else if(isAO && whiteSpace != RenderState.WS_NOWRAP) {
    // this.currentLine.setAllowOverflow(false);
    // }
    // this.addRenderableToLine(r);
    // }
    // else {
    // this.addRenderableToLine(r);
    // }
    // }
    // }
    // LineBreak br = line.getLineBreak();
    // if(br != null) {
    // this.addLineBreak(br.getModelNode(), br.getBreakType());
    // }
    // }
    //
    // private void reprocessElement(RElement element) {
    // RLine line = this.currentLine;
    // this.lineDone(line);
    // boolean isRBlock = element instanceof RBlock;
    // boolean obeysFloats = !isRBlock || !((RBlock) element).isOverflowVisibleY()
    // || !((RBlock) element).isOverflowVisibleY();
    // if(obeysFloats) {
    // if(isRBlock) {
    // RBlock block = (RBlock) element;
    // int newLineY = line == null ? this.paddingInsets.top : line.y +
    // line.height;
    // int leftOffset = this.fetchLeftOffset(newLineY);
    // int rightOffset = this.fetchRightOffset(newLineY);
    // int availContentWidth = this.desiredWidth - leftOffset - rightOffset;
    // block.adjust(availContentWidth, this.availContentHeight, true, false, null,
    // true);
    // // Because a block that obeys margins is also a float limit,
    // // we don't expect exported float bounds.
    // }
    // else if(element instanceof RTable) {
    // RTable table = (RTable) element;
    // int newLineY = line == null ? this.paddingInsets.top : line.y +
    // line.height;
    // int leftOffset = this.fetchLeftOffset(newLineY);
    // int rightOffset = this.fetchRightOffset(newLineY);
    // int availContentWidth = this.desiredWidth - leftOffset - rightOffset;
    // table.adjust(availContentWidth, this.availContentHeight);
    // }
    // }
    // else {
    // RBlock block = (RBlock) element;
    // final FloatingBounds currentFloatBounds = this.floatBounds;
    // FloatingBoundsSource blockFloatBoundsSource = null;
    // if(currentFloatBounds != null) {
    // Insets paddingInsets = this.paddingInsets;
    // final int blockShiftX = paddingInsets.left;
    // final int blockShiftRight = paddingInsets.right;
    // final int blockShiftY = line == null ? paddingInsets.top : line.y +
    // line.height;
    // final int expectedBlockWidth = this.availContentWidth;
    // blockFloatBoundsSource = new FloatingBoundsSource() {
    // public FloatingBounds getChildBlockFloatingBounds(int apparentBlockWidth) {
    // int actualRightShift = blockShiftRight + (expectedBlockWidth -
    // apparentBlockWidth);
    // return new ShiftedFloatingBounds(currentFloatBounds, -blockShiftX,
    // -actualRightShift, -blockShiftY);
    // }
    // };
    // }
    // block.adjust(this.availContentWidth, this.availContentHeight, true, false,
    // blockFloatBoundsSource, true);
    // FloatingBounds blockBounds = block.getExportableFloatingBounds();
    // if(blockBounds != null) {
    // FloatingBounds prevBounds = this.floatBounds;
    // FloatingBounds newBounds;
    // if(prevBounds == null) {
    // newBounds = blockBounds;
    // }
    // else {
    // newBounds = new CombinedFloatingBounds(prevBounds, blockBounds);
    // }
    // if(newBounds.getMaxY() > this.maxY && this.isFloatLimit()) {
    // this.maxY = newBounds.getMaxY();
    // }
    // }
    // }
    // this.addAsSeqBlock(element, obeysFloats, false);
    // }
    // private void reprocessRelative(RRelative relative) {
    // RLine line = this.currentLine;
    // this.lineDone(line);
    // boolean obeysFloats = false;
    // RElement element = relative.getElement();
    // if(element instanceof RBlock) {
    // obeysFloats = false;
    // RBlock block = (RBlock) element;
    // final FloatingBounds currentFloatBounds = this.floatBounds;
    // FloatingBoundsSource blockFloatBoundsSource = null;
    // if(currentFloatBounds != null) {
    // Insets paddingInsets = this.paddingInsets;
    // final int blockShiftX = paddingInsets.left + relative.getXOffset();
    // final int blockShiftRight = paddingInsets.right - relative.getXOffset();
    // final int blockShiftY = (line == null ? paddingInsets.top : line.y +
    // line.height) + relative.getYOffset();
    // final int expectedBlockWidth = this.availContentWidth;
    // blockFloatBoundsSource = new FloatingBoundsSource() {
    // public FloatingBounds getChildBlockFloatingBounds(int apparentBlockWidth) {
    // int actualRightShift = blockShiftRight + (expectedBlockWidth -
    // apparentBlockWidth);
    // return new ShiftedFloatingBounds(currentFloatBounds, -blockShiftX,
    // -actualRightShift, -blockShiftY);
    // }
    // };
    // }
    // block.adjust(this.availContentWidth, this.availContentHeight, true, false,
    // blockFloatBoundsSource, true);
    // relative.assignDimension();
    // FloatingBounds blockBounds = relative.getExportableFloatingBounds();
    // if(blockBounds != null) {
    // FloatingBounds prevBounds = this.floatBounds;
    // FloatingBounds newBounds;
    // if(prevBounds == null) {
    // newBounds = blockBounds;
    // }
    // else {
    // newBounds = new CombinedFloatingBounds(prevBounds, blockBounds);
    // }
    // if(newBounds.getMaxY() > this.maxY && this.isFloatLimit()) {
    // this.maxY = newBounds.getMaxY();
    // }
    // }
    // }
    // else {
    // obeysFloats = true;
    // }
    // this.addAsSeqBlock(relative, obeysFloats, false);
    // }
    private fun importFloat(ef: ExportableFloat, shiftX: Int, shiftY: Int) {
        val renderable = ef.element
        val newX = ef.origX + shiftX
        val newY = ef.origY + shiftY
        // final int newX = ef.origX;
        // final int newY = ef.origY;
        // renderable.setOrigin(ef.origX + ef.visualX, ef.origY + ef.visualY);
        val prevBounds = this.floatBounds
        val offsetFromBorder: Int
        val leftFloat = ef.leftFloat
        if (leftFloat) {
            offsetFromBorder = newX + renderable!!.width()
        } else {
            offsetFromBorder = this.desiredWidth - newX
        }
        this.floatBounds = FloatingViewportBounds(
            prevBounds,
            leftFloat,
            newY,
            offsetFromBorder,
            renderable!!.height()
        )

        if (ef.pendingPlacement && getPosition((modelNode() as HTMLElementImpl?)!!) != RenderState.POSITION_STATIC) {
            // System.out.println("Adding float as renderable to " + this);
            addFloat(renderable!!, newX, newY)
            ef.pendingPlacement = false
        }

        if (this.isFloatLimit()) {
            // this.addPositionedRenderable(renderable, true, true, false);
            if (ef.pendingPlacement) {
                // System.out.println("  r: " + renderable);
                addFloat(renderable!!, newX, newY)
                ef.pendingPlacement = false
            }
        } else {
            this.addExportableFloat(renderable, leftFloat, newX, newY, ef.pendingPlacement)
        }
    }

    fun positionDelayed() {
        val delayedPairs = container!!.delayedPairs()
        if ((delayedPairs != null) && (delayedPairs.size > 0)) {
            // Add positioned renderables that belong here
            val i: MutableIterator<DelayedPair?> = delayedPairs.iterator()
            while (i.hasNext()) {
                val pair = i.next()
                if (pair!!.containingBlock === container) {
                    this.importDelayedPair(pair)
                }
            }
        }
    }

    override fun visualHeight(): Int {
        if (cachedVisualHeight != null) {
            return cachedVisualHeight!!
        }
        var maxY = height().toDouble()
        val renderables = renderables
        if (renderables != null) {
            for (r in renderables) {
                if (r is RenderableContainer) {

                    val rcMaxY = r.visualBounds()!!.maxY //  + rcInsets.bottom;
                    if (rcMaxY > maxY) {
                        maxY = rcMaxY
                    }
                } else if (r is BoundableRenderable) {
                    val brMaxY = r.visualBounds()!!.maxY
                    if (brMaxY > maxY) {
                        maxY = brMaxY
                    }
                } else if (r is PositionedRenderable) {
                    val rcMaxY = r.renderable.visualBounds()!!.maxY
                    if (rcMaxY > maxY) {
                        maxY = rcMaxY
                    }
                } else {
                    System.err.println("Unhandled renderable: " + r)
                }
            }
        }
        cachedVisualHeight = maxY.toInt()
        return cachedVisualHeight!!
    }

    override fun visualWidth(): Int {
        if (cachedVisualWidth != null) {
            return cachedVisualWidth!!
        }
        var maxX = width().toDouble()
        val renderables = renderables
        if (renderables != null) {
            renderables.forEach { r ->

                if (r is RenderableContainer) {
                    val rcInsets = r.getInsetsMarginBorder(false, false)!!
                    val rcMaxX =
                        (r.x() + r.visualWidth() + rcInsets.left + rcInsets.right).toDouble()
                    if (rcMaxX > maxX) {
                        maxX = rcMaxX
                    }
                } else if (r is BoundableRenderable) {
                    val brMaxX = r.visualBounds()!!.maxX
                    if (brMaxX > maxX) {
                        maxX = brMaxX
                    }
                } else if (r is PositionedRenderable) {
                    val rcMaxX = r.renderable.visualBounds()!!.maxX
                    if (rcMaxX > maxX) {
                        maxX = rcMaxX
                    }
                } else {
                    System.err.println("Unhandled renderable: " + r)
                    Thread.dumpStack()
                }
            }
        }
        cachedVisualWidth = maxX.toInt()
        return cachedVisualWidth!!
    }

    override fun clipBounds(): Rectangle? {
        return (container as RBlock).clipBounds()
        // return new Rectangle(0, 0, width, height);
    }

    private class NopLayout : MarkupLayout {
        override fun layoutMarkup(bodyLayout: RBlockViewport, markupElement: HTMLElementImpl) {
        }
    }

    private class NoScriptLayout : MarkupLayout {
        override fun layoutMarkup(bodyLayout: RBlockViewport, markupElement: HTMLElementImpl) {
            val ucontext = bodyLayout.userAgentContext
            if (!ucontext.isScriptingEnabled()) {
                bodyLayout.layoutMarkup(markupElement)
            } else {
                // NOP
            }
        }
    }

    private class BrLayout : MarkupLayout {
        /*
         * (non-Javadoc)
         *
         * @see
         * org.xamjwg.html.renderer.MarkupLayout#layoutMarkup(java.awt.Container,
         * java.awt.Insets, org.xamjwg.html.domimpl.HTMLElementImpl)
         */
        override fun layoutMarkup(bodyLayout: RBlockViewport, markupElement: HTMLElementImpl) {
            val clear = markupElement.getAttribute("clear")
            bodyLayout.addLineBreak(markupElement, LineBreak.Companion.getBreakType(clear))
        }
    }

    private class HrLayout : MarkupLayout {
        /*
         * (non-Javadoc)
         *
         * @see
         * org.xamjwg.html.renderer.MarkupLayout#layoutMarkup(java.awt.Container,
         * java.awt.Insets, org.xamjwg.html.domimpl.HTMLElementImpl)
         */
        override fun layoutMarkup(bodyLayout: RBlockViewport, markupElement: HTMLElementImpl) {
            bodyLayout.layoutHr(markupElement)
        }
    }

    // /**
    // * Gets FloatingBounds from this viewport that should
    // * be considered by an ancestor block.
    // */
    // public FloatingBounds getExportableFloatingBounds() {
    // FloatingBounds floatBounds = this.floatBounds;
    // if(floatBounds == null) {
    // return null;
    // }
    // if(this.isFloatLimit()) {
    // return null;
    // }
    // int maxY = floatBounds.getMaxY();
    // if(maxY > this.height) {
    // return floatBounds;
    // }
    // return null;
    // }
    private class ObjectLayout(tryToRenderContent: Boolean, usesAlignAttribute: Boolean) :
        CommonWidgetLayout(
            ADD_INLINE, usesAlignAttribute
        ) {
        private val tryToRenderContent: Boolean

        /**
         * Must use this ThreadLocal because an ObjectLayout instance is shared
         * across renderers.
         */
        private val htmlObject = ThreadLocal<HtmlObject?>()

        /**
         * @param tryToRenderContent If the object is unknown, content is rendered as HTML.
         * @param usesAlignAttribute
         */
        init {
            this.tryToRenderContent = tryToRenderContent
        }

        override fun layoutMarkup(bodyLayout: RBlockViewport, markupElement: HTMLElementImpl) {
            val ho = bodyLayout.rendererContext.getHtmlObject(markupElement)
            if ((ho == null) && this.tryToRenderContent) {
                // Don't know what to do with it - render contents.
                bodyLayout.layoutMarkup(markupElement)
            } else if (ho != null) {
                this.htmlObject.set(ho)
                super.layoutMarkup(bodyLayout, markupElement)
            }
        }

        override fun createRenderable(
            bodyLayout: RBlockViewport,
            markupElement: HTMLElementImpl
        ): RElement {
            val ho = this.htmlObject.get()
            val c: Component
            if (ho == null) {
                c = BrokenComponent()
            } else {
                c = ho.component()!!
            }
            val uiControl: UIControl = UIControlWrapper(ho!!, c)
            val ruiControl = RUIControl(
                markupElement, uiControl, bodyLayout.container, bodyLayout.frameContext!!,
                bodyLayout.userAgentContext
            )
            return ruiControl
        }
    }

    private class ImgLayout : CommonWidgetLayout(ADD_INLINE, true) {
        override fun createRenderable(
            bodyLayout: RBlockViewport,
            markupElement: HTMLElementImpl
        ): RElement {
            val control = ImgControl(markupElement as HTMLImageElementImpl)
            return RImgControl(
                markupElement,
                control,
                bodyLayout.container,
                bodyLayout.frameContext!!,
                bodyLayout.userAgentContext
            )
        }
    }

    private class CanvasLayout : CommonWidgetLayout(ADD_INLINE, false) {
        override fun createRenderable(
            bodyLayout: RBlockViewport,
            markupElement: HTMLElementImpl
        ): RElement {
            val canvasImpl = markupElement as HTMLCanvasElementImpl
            return RUIControl(
                markupElement,
                CanvasControl(canvasImpl),
                bodyLayout.container,
                bodyLayout.frameContext!!,
                bodyLayout.userAgentContext
            )
        }

        class CanvasControl(private val canvasNode: HTMLCanvasElementImpl) :
            BaseControl(canvasNode) {

            override fun paintComponent(g: Graphics) {
                canvasNode.paintComponent(g)
            }



            override fun setBounds(x: Int, y: Int, width: Int, height: Int) {
                super.setBounds(x, y, width, height)
                val insets = ruicontrol?.insetsMarginPadding!!
                canvasNode.setBounds(
                    insets.left,
                    insets.top,
                    width - (insets.left + insets.right),
                    height - (insets.top + insets.bottom)
                )
            }

        }
    }

    private class InputLayout2 : CommonWidgetLayout(ADD_INLINE, true) {
        override fun createRenderable(
            bodyLayout: RBlockViewport,
            markupElement: HTMLElementImpl
        ): RElement {
            val bie = markupElement as HTMLBaseInputElement
            val uiControl: BaseInputControl = createInputControl(bie)!!

            bie.setInputContext(uiControl)
            return RUIControl(
                markupElement,
                uiControl,
                bodyLayout.container,
                bodyLayout.frameContext!!,
                bodyLayout.userAgentContext
            )
        }
    }

    private class SelectLayout : CommonWidgetLayout(ADD_INLINE, true) {
        override fun createRenderable(
            bodyLayout: RBlockViewport,
            markupElement: HTMLElementImpl
        ): RElement {
            val bie = markupElement as HTMLBaseInputElement
            val uiControl: BaseInputControl = InputSelectControl(bie)
            bie.setInputContext(uiControl)
            return RUIControl(
                markupElement,
                uiControl,
                bodyLayout.container,
                bodyLayout.frameContext!!,
                bodyLayout.userAgentContext
            )
        }
    }

    private class TextAreaLayout2 : CommonWidgetLayout(ADD_INLINE, true) {
        override fun createRenderable(
            bodyLayout: RBlockViewport,
            markupElement: HTMLElementImpl
        ): RElement {
            val bie = markupElement as HTMLBaseInputElement
            val control: BaseInputControl = InputTextAreaControl(bie)
            bie.setInputContext(control)
            return RUIControl(
                markupElement,
                control,
                bodyLayout.container,
                bodyLayout.frameContext!!,
                bodyLayout.userAgentContext
            )
        }
    }

    private class IFrameLayout : CommonWidgetLayout(ADD_INLINE, true) {
        override fun createRenderable(
            bodyLayout: RBlockViewport,
            markupElement: HTMLElementImpl
        ): RElement {
            val frame = bodyLayout.rendererContext.createBrowserFrame()
            (markupElement as HTMLIFrameElementImpl).setBrowserFrame(frame)
            val control: UIControl = BrowserFrameUIControl(markupElement, frame!!)
            return RUIControl(
                markupElement,
                control,
                bodyLayout.container,
                bodyLayout.frameContext!!,
                bodyLayout.userAgentContext
            )
        }
    }

    /**
     * This is layout common to elements that render themselves, except RBlock,
     * RTable and RList.
     */
    private abstract class CommonWidgetLayout(method: Int, usesAlignAttribute: Boolean) :
        MarkupLayout {
        private val method: Int
        private val useAlignAttribute: Boolean

        init {
            this.method = method
            this.useAlignAttribute = usesAlignAttribute
        }

        override fun layoutMarkup(bodyLayout: RBlockViewport, markupElement: HTMLElementImpl) {
            val style = markupElement.getCurrentStyle()
            var currMethod = this.method
            run {
                val display = style.display
                if (display != null) {
                    if ("none".equals(display, ignoreCase = true)) {
                        // For hidden iframes: GH-140
                        // return;
                        if (!"iframe".equals(markupElement.nodeName, ignoreCase = true)) {
                            return
                        }
                    } else if ("block".equals(display, ignoreCase = true)) {
                        currMethod = ADD_AS_BLOCK
                    } else if ("inline".equals(display, ignoreCase = true)) {
                        currMethod = ADD_INLINE
                    } else if ("display-inline".equals(display, ignoreCase = true)) {
                        currMethod = ADD_INLINE_BLOCK
                    }
                }
            }
            val node = markupElement.uINode
            var renderable: RElement? = null
            if (node == null) {
                renderable = this.createRenderable(bodyLayout, markupElement)
                if (renderable == null) {
                    if (logger.isLoggable(Level.INFO)) {
                        logger.info("layoutMarkup(): Don't know how to render " + markupElement + ".")
                    }
                    return
                }
                markupElement.uINode = renderable
            } else {
                renderable = node as RElement
            }
            renderable.setOriginalParent(bodyLayout)
            when (currMethod) {
                ADD_INLINE, ADD_INLINE_BLOCK -> bodyLayout.addRenderableToLineCheckStyle(
                    renderable,
                    markupElement,
                    this.useAlignAttribute
                )

                ADD_AS_BLOCK -> bodyLayout.positionRElement(
                    markupElement,
                    renderable,
                    this.useAlignAttribute,
                    true,
                    false
                )
            }
        }

        protected abstract fun createRenderable(
            bodyLayout: RBlockViewport,
            markupElement: HTMLElementImpl
        ): RElement

        companion object {
            protected const val ADD_INLINE: Int = 0
            protected const val ADD_AS_BLOCK: Int = 1
            protected const val ADD_INLINE_BLOCK: Int = 2
        }
    }

    private class CommonLayout : MarkupLayout {
        override fun layoutMarkup(bodyLayout: RBlockViewport, markupElement: HTMLElementImpl) {
            val rs = markupElement.getRenderState()
            var display = rs.display
            if (display == RenderState.DISPLAY_INLINE || display == RenderState.DISPLAY_INLINE_BLOCK || display == RenderState.DISPLAY_INLINE_TABLE) {
                // Inline elements with absolute or fixed positions need to be treated as blocks.
                // TODO: ^^Verify; is that an internal hack or a spec requirement?
                val position = rs.position
                if ((position == RenderState.POSITION_ABSOLUTE) || (position == RenderState.POSITION_FIXED)) {
                    display = RenderState.DISPLAY_BLOCK
                } else {
                    val boxFloat = rs.float
                    if (boxFloat != RenderState.FLOAT_NONE) {
                        display = RenderState.DISPLAY_BLOCK
                    }
                }
            }
            when (display) {
                RenderState.DISPLAY_TABLE_COLUMN, RenderState.DISPLAY_TABLE_COLUMN_GROUP, RenderState.DISPLAY_NONE -> {
                    // skip it completely.
                    val node = markupElement.uINode
                    if (node is BaseBoundableRenderable) {
                        // This is necessary so that if the element is made
                        // visible again, it can be invalidated.
                        (node as BaseBoundableRenderable).markLayoutValid()
                    }
                }

                RenderState.DISPLAY_BLOCK -> {
                    //TODO refer issue #87
                    val tagName = markupElement.tagName
                    if ("UL".equals(tagName, ignoreCase = true) || "OL".equals(
                            tagName,
                            ignoreCase = true
                        )
                    ) {
                        bodyLayout.layoutList(markupElement)
                    } else {
                        bodyLayout.layoutRBlock(markupElement)
                    }
                }

                RenderState.DISPLAY_LIST_ITEM -> bodyLayout.layoutListItem(markupElement)
                RenderState.DISPLAY_TABLE -> bodyLayout.layoutRTable(markupElement)
                RenderState.DISPLAY_INLINE_TABLE -> bodyLayout.layoutRInlineBlock(markupElement)
                RenderState.DISPLAY_INLINE_BLOCK -> bodyLayout.layoutRInlineBlock(markupElement)
                else ->                     // Assume INLINE
                    bodyLayout.layoutMarkup(markupElement)
            }
        }
    }

    companion object {
        // GENERAL NOTES
        // An RBlockViewport basically consists of two collections:
        // seqRenderables and positionedRenderables. The seqRenderables
        // collection is a sequential list of RLine's and RBlock's
        // that is amenable to a binary search by Y position. The
        // positionedRenderables collection is a z-index ordered
        // collection meant for blocks with position=absolute and such.
        //
        // HOW FLOATS WORK
        // Float boxes are scheduled to be added on the next available line.
        // Line layout is bounded by the current floatBounds.
        // When a float is placed with placeFloat(), an absolutely positioned
        // box is added. Whether the float height expands the RBlockViewport
        // height is determined by isFloatLimit().
        //
        // FloatingBounds are inherited by sub-boxes, but the bounds are
        // shifted.
        //
        // The RBlockViewport also publishes a collection of "exporatable
        // floating bounds." These are float boxes that go beyond the bounds
        // of the RBlockViewport, so ancestor blocks can obtain them to adjust
        // their own bounds.
        val ZERO_INSETS: Insets = Insets(0, 0, 0, 0)
        private val elementLayout: MutableMap<String?, MarkupLayout?> =
            HashMap<String?, MarkupLayout?>(70)
        private val commonLayout: MarkupLayout = CommonLayout()
        private val SEE = SizeExceededException()

        init {
            val el: MutableMap<String?, MarkupLayout?> = elementLayout
            el.put("BR", BrLayout())
            el.put("NOSCRIPT", NoScriptLayout())
            val nop = NopLayout()
            el.put("SCRIPT", nop)
            el.put("HEAD", nop)
            el.put("TITLE", nop)
            el.put("META", nop)
            el.put("STYLE", nop)
            el.put("LINK", nop)
            el.put("IMG", ImgLayout())
            el.put("INPUT", InputLayout2())
            el.put("TEXTAREA", TextAreaLayout2())
            el.put("SELECT", SelectLayout())
            el.put("HR", HrLayout())
            val ol = ObjectLayout(false, true)
            el.put("OBJECT", ObjectLayout(true, true))
            el.put("APPLET", ol)
            el.put("EMBED", ol)
            el.put("IFRAME", IFrameLayout())

            el.put("CANVAS", CanvasLayout())
        }

        private fun isLastElement(indx: Int, childrenArray: Array<NodeImpl?>): Boolean {
            for (i in indx + 1..<childrenArray.size) {
                if (childrenArray[i]!!.getNodeType() == Node.ELEMENT_NODE) {
                    return false
                }
            }
            return true
        }

        // final RBlockViewport getParentViewportForAlign() {
        // // Use originalParent, which for one, is not going to be null during
        // layout.
        // Object parent = this.getOriginalOrCurrentParent();
        // if(parent instanceof RBlock) {
        // RBlock block = (RBlock) parent;
        // if(!block.couldBeScrollable()) {
        // parent = ((BaseElementRenderable) parent).getOriginalOrCurrentParent();
        // if(parent instanceof RBlockViewport) {
        // return (RBlockViewport) parent;
        // }
        // }
        // }
        // return null;
        // }
        //
        private fun getPosition(element: HTMLElementImpl): Int {
            val rs = element.getRenderState()
            return rs.position
        }

        private fun isCollapsibleBlock(
            block: RBlock,
            insetChecker: Function<HtmlInsets?, Boolean?>
        ): Boolean {
            val mn = block.modelNode()!!
            val rs: RenderState = mn.renderState()!!
            val isDisplayBlock = rs.display == RenderState.DISPLAY_BLOCK
            val isPosStaticOrRelative =
                rs.position == RenderState.POSITION_STATIC || rs.position == RenderState.POSITION_RELATIVE
            val borderInsets = rs.borderInfo!!.insets
            val paddingInsets = rs.paddingInsets
            val isZeroBorderAndPadding =
                insetChecker.apply(borderInsets) == true && insetChecker.apply(paddingInsets) == true
            return (mn !is HTMLHtmlElement) && isDisplayBlock && isPosStaticOrRelative && isZeroBorderAndPadding
        }

        private fun checkTopInset(insets: HtmlInsets?): Boolean {
            return insets == null || insets.top == 0
        }

        private fun checkBottomInset(insets: HtmlInsets?): Boolean {
            return insets == null || insets.bottom == 0
        }

        private fun isCollapsibleParentBlock(
            block: RBlock,
            insetChecker: Function<HtmlInsets?, Boolean?>
        ): Boolean {
            val mn = block.modelNode()!!
            val rs: RenderState = mn.renderState()!!
            return isCollapsibleBlock(block, insetChecker) && isOverflowVisibleOrNone(rs)
        }

        private fun isOverflowVisibleOrNone(rs: RenderState): Boolean {
            val overflowX = rs.overflowX
            val overflowY = rs.overflowY
            val xOverflowFine =
                (overflowX == RenderState.OVERFLOW_VISIBLE) || (overflowX == RenderState.OVERFLOW_NONE)
            val yOverflowFine =
                (overflowY == RenderState.OVERFLOW_VISIBLE) || (overflowY == RenderState.OVERFLOW_NONE)
            val overflowFine = xOverflowFine && yOverflowFine
            return overflowFine
        }

        /**
         * @param others                 An ordered collection.
         * @param seqRenderablesIterator
         * @param destination
         */
        private fun populateZIndexGroups(
            others: MutableCollection<PositionedRenderable>,
            seqRenderablesIterator: MutableIterator<Renderable?>?,
            destination: ArrayList<Renderable>
        ) {
            // First, others with z-index < 0
            val i1: MutableIterator<PositionedRenderable?> = others.iterator()
            var pending: Renderable? = null
            while (i1.hasNext()) {
                val pr = i1.next()!!
                val r = pr.renderable
                if (r.zIndex() >= 0) {
                    pending = pr
                    break
                }
                destination.add(pr)
            }

            // Second, sequential renderables
            val i2 = seqRenderablesIterator
            if (i2 != null) {
                while (i2.hasNext()) {
                    destination.add(i2.next()!!)
                }
            }

            // Third, other renderables with z-index >= 0.
            if (pending != null) {
                destination.add(pending)
                while (i1.hasNext()) {
                    val pr: PositionedRenderable? = i1.next()
                    destination.add(pr!!)
                }
            }
        }

        private fun populateZIndexGroupsTopFirst(
            others: MutableList<PositionedRenderable>,
            seqRenderablesIterator: MutableIterator<Renderable>?,
            destination: ArrayList<Renderable>
        ) {
            // First, others with z-index >= 0
            val i1 =
                CollectionUtilities.reverseIterator<PositionedRenderable?>(others as MutableList<PositionedRenderable?>)
            var pending: Renderable? = null
            while (i1.hasNext()) {
                val pr = i1.next()!!
                val r = pr.renderable
                if (r.zIndex() < 0) {
                    pending = pr
                    break
                }
                destination.add(pr)
            }

            // Second, sequential renderables
            val i2 = seqRenderablesIterator
            if (i2 != null) {
                while (i2.hasNext()) {
                    destination.add(i2.next())
                }
            }

            // Third, other renderables with z-index >= 0.
            if (pending != null) {
                destination.add(pending)
                while (i1.hasNext()) {
                    val pr: PositionedRenderable? = i1.next()
                    destination.add(pr!!)
                }
            }
        }

        private fun createInputControl(markupElement: HTMLBaseInputElement): BaseInputControl? {
            var type = markupElement.getAttribute("type")
            if (type == null) {
                return InputTextControl(markupElement)
            }
            type = type.lowercase(Locale.getDefault())
            if ("text" == type || "url" == type || "number" == type || "search" == type || (type.length == 0)) {
                return InputTextControl(markupElement)
            } else if ("hidden" == type) {
                return null
            } else if ("submit" == type) {
                return InputButtonControl(markupElement)
            } else if ("password" == type) {
                return InputPasswordControl(markupElement)
            } else if ("radio" == type) {
                return InputRadioControl(markupElement)
            } else if ("checkbox" == type) {
                return InputCheckboxControl(markupElement)
            } else if ("image" == type) {
                return InputImageControl(markupElement)
            } else if ("reset" == type) {
                return InputButtonControl(markupElement)
            } else if ("button" == type) {
                return InputButtonControl(markupElement)
            } else if ("file" == type) {
                return InputFileControl(markupElement)
            } else {
                return null
            }
        }

        private fun getRootContainer(container: RenderableContainer): RenderableContainer {
            var c = container.parentContainer()
            var prevC: RenderableContainer? = container
            while (true) {
                val newContainer = c!!.parentContainer()
                if (newContainer == null) {
                    break
                }
                prevC = c
                c = newContainer
            }
            return prevC!!
        }

        /**
         * Gets an ancestor which is "positioned" (that is whose position is not static).
         * Stops searching when HTML element is encountered.
         */
        private fun getPositionedAncestor(containingBlock: RenderableContainer?): RenderableContainer? {
            var containingBlock = containingBlock
            while (true) {
                if (containingBlock is Renderable) {
                    val node = (containingBlock as Renderable).modelNode()
                    if (node is HTMLElementImpl) {
                        val position: Int = getPosition(node)
                        // if (position != RenderState.POSITION_STATIC || (element instanceof HTMLHtmlElement)) {
                        if (position != RenderState.POSITION_STATIC) {
                            break
                        }
                        val newContainer = containingBlock.parentContainer()
                        if (newContainer == null) {
                            break
                        }
                        containingBlock = newContainer
                    } else {
                        break
                    }
                } else {
                    break
                }
            }
            return containingBlock
        }
    }
}
