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
 * Created on Apr 16, 2005
 */
package io.github.remmerw.thor.cobra.html.renderer

import cz.vutbr.web.css.CSSProperty.VerticalAlign
import io.github.remmerw.thor.cobra.html.dom.ModelNode
import io.github.remmerw.thor.cobra.html.style.RenderState
import java.awt.Color
import java.awt.Graphics
import java.awt.Rectangle
import java.awt.event.MouseEvent
import kotlin.math.max

class RLine(
    modelNode: ModelNode?, container: RenderableContainer?, x: Int, y: Int, desiredMaxWidth: Int,
    height: Int,
    initialAllowOverflow: Boolean
) : BaseRCollection(container, modelNode) {
    val renderabl = ArrayList<Renderable>(8)


    /*
     * (non-Javadoc)
     *
     * @see
     * net.sourceforge.xamj.domimpl.markup.Renderable#paint(java.awt.Graphics)
     */
    // private final RenderState startRenderState;
    var baselineOffset: Int = 0
        private set
    private var desiredMaxWidth: Int

    /**
     * Offset where next renderable should be placed. This can be different to
     * width.
     */
    private var xoffset = 0

    private var allowOverflow = false
    private var firstAllowOverflowWord = false
    private var mousePressTarget: BoundableRenderable? = null
    var lineBreak: LineBreak? = null
    var layoutUpTreeCanBeInvalidated = false

    init {
        // Note that in the case of RLine, modelNode is the context node
        // at the beginning of the line, not a node that encloses the whole line.
        this.setX(x)
        this.setY(y)
        this.setHeight(height)
        this.desiredMaxWidth = desiredMaxWidth
        // Layout here can always be "invalidated"
        this.layoutUpTreeCanBeInvalidated = true
        this.allowOverflow = initialAllowOverflow
    }

    fun isAllowOverflow(): Boolean {
        return this.allowOverflow
    }

    fun setAllowOverflow(flag: Boolean) {
        if (flag != this.allowOverflow) {
            this.allowOverflow = flag
            if (flag) {
                // Set to true only if allowOverflow was
                // previously false.
                this.firstAllowOverflowWord = true
            }
        }
    }

    /**
     * This method should only be invoked when the line has no items yet.
     */
    fun changeLimits(x: Int, desiredMaxWidth: Int) {
        this.setX(x)
        this.desiredMaxWidth = desiredMaxWidth
    }

    override fun invalidateLayoutLocal() {
        // Workaround for fact that RBlockViewport does not
        // get validated or invalidated.
        this.layoutUpTreeCanBeInvalidated = true
    }

    override fun paint(g: Graphics) {
        // Paint according to render state of the start of line first.
        val rs = this.modelNode()!!.renderState()

        if ((rs != null) && (rs.getVisibility() != RenderState.VISIBILITY_VISIBLE)) {
            // Just don't paint it.
            return
        }

        if (rs != null) {
            val textColor = rs.getColor()
            g.color = textColor
            val font = rs.getFont()
            g.font = font
        }
        // Note that partial paints of the line can only be done
        // if all RStyleChanger's are applied first.
        val i = this.renderabl.iterator()
        while (i.hasNext()) {
            val r = i.next()
            if (r is RElement) {
                // RElements should be translated.
                if (!r.isDelegated()) {
                    val newG = g.create()
                    newG.translate(r.visualX(), r.visualY())
                    try {
                        r.paint(newG)
                    } finally {
                        newG.dispose()
                    }
                }
            } else if (r is BoundableRenderable) {
                if (!r.isDelegated()) {
                    r.paintTranslated(g)
                }
            } else {
                r.paint(g)
            }
        }
    }

    override fun extractSelectionText(
        buffer: StringBuffer, inSelection: Boolean, startPoint: RenderableSpot,
        endPoint: RenderableSpot
    ): Boolean {
        val result = super.extractSelectionText(buffer, inSelection, startPoint, endPoint)
        if (result) {
            val br = this.lineBreak
            if (br != null) {
                buffer.append(System.getProperty("line.separator"))
            } else {
                val renderables = this.renderabl
                val size = renderables.size
                if ((size > 0) && renderables.get(size - 1) !is RBlank) {
                    buffer.append(" ")
                }
            }
        }
        return result
    }

    override fun zIndex(): Int {
        TODO("Not yet implemented")
    }


    fun addStyleChanger(sc: RStyleChanger) {
        this.renderabl.add(sc)
    }

    fun simplyAdd(r: Renderable) {
        this.renderabl.add(r)
    }

    /**
     * This method adds and positions a renderable in the line, if possible. Note
     * that RLine does not set sizes, but only origins.
     *
     * @throws OverflowException Thrown if the renderable overflows the line. All overflowing
     * renderables are added to the exception.
     */
    @Throws(OverflowException::class)
    fun add(renderable: Renderable?) {
        if (renderable is RWord) {
            this.addWord(renderable)
        } else if (renderable is RBlank) {
            this.addBlank(renderable)
        } else if (renderable is RElement) {
            this.addElement(renderable)
        } else if (renderable is RSpacing) {
            this.addSpacing(renderable)
        } else if (renderable is RStyleChanger) {
            this.addStyleChanger(renderable)
        } else if (renderable is RFloatInfo) {
            this.simplyAdd(renderable)
        } else {
            throw IllegalArgumentException("Can't add " + renderable)
        }
    }

    @Throws(OverflowException::class)
    fun addWord(rword: RWord) {
        // Check if it fits horzizontally
        var offset = this.xoffset
        val wiwidth = rword.width()
        val allowOverflow = this.allowOverflow
        val firstAllowOverflowWord = this.firstAllowOverflowWord
        if (allowOverflow && firstAllowOverflowWord) {
            this.firstAllowOverflowWord = false
        }
        if ((!allowOverflow || firstAllowOverflowWord) && (offset != 0) && ((offset + wiwidth) > this.desiredMaxWidth)) {
            val renderables = this.renderabl
            var overflow: ArrayList<Renderable?>? = null
            var cancel = false
            // Check if other words need to be overflown (for example,
            // a word just before a markup tag adjacent to the word
            // we're trying to add). An RBlank between words prevents
            // a word from being overflown to the next line (and this
            // is the usefulness of RBlank.)
            var newOffset = offset
            var newWidth = offset
            var i = renderables.size
            while (--i >= 0) {
                val renderable: Renderable? = renderables.get(i)
                if ((renderable is RWord) || renderable !is BoundableRenderable) {
                    if (overflow == null) {
                        overflow = ArrayList<Renderable?>()
                    }
                    if ((renderable !== rword) && (renderable is RWord) && (renderable.x() == 0)) {
                        // Can't overflow words starting at offset zero.
                        // Note that all or none should be overflown.
                        cancel = true
                        // No need to set offset - set later.
                        break
                    }
                    overflow.add(0, renderable)
                    renderables.removeAt(i)
                } else {
                    if (renderable is RBlank) {
                        newWidth = renderable.x()
                        newOffset = newWidth + renderable.width()
                    } else {
                        newOffset = renderable.x() + renderable.width()
                        newWidth = newOffset
                    }
                    break
                }
            }
            if (cancel) {
                // Oops. Need to undo overflow.
                if (overflow != null) {
                    val i = overflow.iterator()
                    while (i.hasNext()) {
                        renderables.add(i.next()!!)
                    }
                }
            } else {
                this.xoffset = newOffset
                this.setWidth(newWidth)
                if (overflow == null) {
                    throw OverflowException(mutableSetOf<Renderable?>(rword))
                } else {
                    overflow.add(rword)
                    throw OverflowException(overflow)
                }
            }
        }

        // Add it
        var extraHeight = 0
        val maxDescent = this.height() - this.baselineOffset
        if (rword.descent > maxDescent) {
            extraHeight += (rword.descent - maxDescent)
        }
        val maxAscentPlusLeading = this.baselineOffset
        if (rword.ascentPlusLeading > maxAscentPlusLeading) {
            extraHeight += (rword.ascentPlusLeading - maxAscentPlusLeading)
        }
        if (extraHeight > 0) {
            val newHeight = this.height() + extraHeight
            this.adjustHeight(newHeight, newHeight, VerticalAlign.BOTTOM)
        }
        this.renderabl.add(rword)
        rword.setParent(this)
        val x = offset
        offset += wiwidth
        this.xoffset = offset
        this.setWidth(this.xoffset)
        rword.setOrigin(x, this.baselineOffset - rword.ascentPlusLeading)
    }

    fun addBlank(rblank: RBlank) {
        // NOTE: Blanks may be added without concern for wrapping (?)
        val x = this.xoffset
        val width = rblank.width()
        rblank.setOrigin(x, this.baselineOffset - rblank.ascentPlusLeading)
        this.renderabl.add(rblank)
        rblank.setParent(this)
        // Only move xoffset, but not width
        this.xoffset = x + width
    }

    fun addSpacing(rblank: RSpacing) {
        // NOTE: Spacing may be added without concern for wrapping (?)
        val x = this.xoffset
        val width = rblank.width()
        rblank.setOrigin(x, (this.height() - rblank.height()) / 2)
        this.renderabl.add(rblank)
        rblank.setParent(this)
        this.xoffset = x + width
        this.setWidth(this.xoffset)
    }

    /**
     * @param relement
     * @param x
     * @param elementHeight The required new line height.
     * @param valign
     */
    private fun setElementY(relement: RElement, elementHeight: Int, valign: VerticalAlign?) {
        // At this point height should be more than what's needed.
        val yoffset: Int
        if (valign != null) {
            when (valign) {
                VerticalAlign.BOTTOM -> yoffset = this.height() - elementHeight
                VerticalAlign.MIDDLE -> yoffset = (this.height() - elementHeight) / 2
                VerticalAlign.BASELINE -> yoffset = this.baselineOffset - elementHeight
                VerticalAlign.TOP -> yoffset = 0
                else -> yoffset = this.baselineOffset - elementHeight
            }
        } else {
            yoffset = this.baselineOffset - elementHeight
        }
        // RLine only sets origins, not sizes.
        // relement.setBounds(x, yoffset, width, height);
        relement.setY(yoffset)
    }

    /**
     * Positions line elements vertically.
     */
    /*
  final void positionVertically() {
    final ArrayList<Renderable> renderables = this.renderables;

    // System.out.println("pos vertically: " + this + " : " + renderables.size());
    // Find word maximum metrics.
    int maxDescent = 0;
    int maxAscentPlusLeading = 0;
    int maxWordHeight = 0;
    for (final Iterator<Renderable> i = renderables.iterator(); i.hasNext();) {
      final Renderable r = i.next();
      if (r instanceof RWord) {
        final RWord rword = (RWord) r;
        final int descent = rword.descent;
        if (descent > maxDescent) {
          maxDescent = descent;
        }
        final int ascentPlusLeading = rword.ascentPlusLeading;
        if (ascentPlusLeading > maxAscentPlusLeading) {
          maxAscentPlusLeading = ascentPlusLeading;
        }
        if (rword.height > maxWordHeight) {
          maxWordHeight = rword.height;
        }
      }
    }

    // Determine proper baseline
    final int lineHeight = this.height;
    int baseLine = lineHeight - maxDescent;
    for (final Iterator<Renderable> i = renderables.iterator(); i.hasNext();) {
      final Renderable r = i.next();
      if (r instanceof RElement) {
        final RElement relement = (RElement) r;
        // System.out.println("Placing: " + r + "\n  with: " + relement.getVAlign());
        @Nullable VerticalAlign vAlign = relement.getVAlign();
        if (vAlign != null) {
          switch (vAlign) {
          case BOTTOM:
            // This case was implemented by HRJ, but not tested
            relement.setY(lineHeight - relement.getHeight());
            break;
          case MIDDLE:
            int midWord = baseLine + maxDescent - maxWordHeight / 2;
            final int halfElementHeight = relement.getHeight() / 2;
            if (midWord + halfElementHeight > lineHeight) {
              // Change baseLine
              midWord = lineHeight - halfElementHeight;
              baseLine = midWord + maxWordHeight / 2 - maxDescent;
            } else if (midWord - halfElementHeight < 0) {
              midWord = halfElementHeight;
              baseLine = midWord + maxWordHeight / 2 - maxDescent;
            } else {
              relement.setY(midWord - halfElementHeight);
            }
            break;
          default:
            // TODO
            System.out.println("Not implemented yet");
          }
        } else {
          // NOP
        }
      }
    }
  }
  */
    // Check if it fits horizontally
    fun checkFit(relement: RElement): Boolean {
        val origXOffset = this.xoffset
        val desiredMaxWidth = this.desiredMaxWidth
        val pw = relement.width()
        val allowOverflow = this.allowOverflow
        val firstAllowOverflowWord = this.firstAllowOverflowWord
        if (allowOverflow && firstAllowOverflowWord) {
            this.firstAllowOverflowWord = false
        }
        val overflows =
            (!allowOverflow || firstAllowOverflowWord) && (origXOffset != 0) && ((origXOffset + pw) > desiredMaxWidth)
        return !overflows
    }

    @Throws(OverflowException::class)
    private fun addElement(relement: RElement) {
        if (!checkFit(relement)) {
            throw OverflowException(mutableSetOf<Renderable?>(relement))
        }

        // Note: Renderable for widget doesn't paint the widget, but
        // it's needed for height readjustment.
        val boundsh = this.height()
        val origXOffset = this.xoffset
        val pw = relement.width()
        val ph = relement.height()
        val requiredHeight: Int

        val valign = relement.vAlign()
        if (valign != null) {
            when (valign) {
                VerticalAlign.BASELINE -> requiredHeight = ph + (boundsh - this.baselineOffset)
                VerticalAlign.MIDDLE ->                     // TODO: This code probably only works with the older ABS-MIDDLE type of alignment.
                    requiredHeight = max(ph, (ph / 2) + (boundsh - this.baselineOffset))

                else -> requiredHeight = ph
            }
        } else {
            requiredHeight = ph
        }

        if (requiredHeight > boundsh) {
            // Height adjustment depends on bounds being already set.
            this.adjustHeight(requiredHeight, ph, valign)
        }
        this.renderabl.add(relement)
        relement.setParent(this)
        relement.setX(origXOffset)
        this.setElementY(relement, ph, valign)
        val newX = origXOffset + pw
        this.xoffset = newX
        this.setWidth(this.xoffset)

    }

    /**
     * Rearrange line elements based on a new line height and alignment provided.
     * All line elements are expected to have bounds preset.
     *
     * @param newHeight
     * @param alignmentY
     */
    private fun adjustHeight(newHeight: Int, elementHeight: Int, valign: VerticalAlign?) {
        // Set new line height
        // int oldHeight = this.height;
        this.setHeight(newHeight)
        val renderables = this.renderabl
        // Find max baseline
        val firstFm = this.modelNode()!!.renderState()!!.getFontMetrics()
        var maxDescent = firstFm!!.descent
        var maxAscentPlusLeading = firstFm.ascent + firstFm.leading
        for (renderable in renderables) {
            val r: Any? = renderable
            if (r is RStyleChanger) {
                val fm = r.modelNode().renderState()!!.getFontMetrics()!!
                val descent = fm.descent
                if (descent > maxDescent) {
                    maxDescent = descent
                }
                val ascentPlusLeading = fm.ascent + fm.leading
                if (ascentPlusLeading > maxAscentPlusLeading) {
                    maxAscentPlusLeading = ascentPlusLeading
                }
            }
        }
        val textHeight = maxDescent + maxAscentPlusLeading

        // TODO: Need to take into account previous RElement's and
        // their alignments?
        val baseline: Int
        if (valign != null) {
            when (valign) {
                VerticalAlign.BOTTOM -> baseline = newHeight - maxDescent
                VerticalAlign.MIDDLE -> baseline = ((newHeight + textHeight) / 2) - maxDescent
                VerticalAlign.BASELINE -> baseline = elementHeight
                VerticalAlign.TOP -> baseline = maxAscentPlusLeading
                else -> baseline = elementHeight
            }
        } else {
            baseline = elementHeight
        }
        this.baselineOffset = baseline

        // Change bounds of renderables accordingly
        for (renderable in renderables) {
            val r: Any? = renderable
            if (r is RWord) {
                r.setY(baseline - r.ascentPlusLeading)
            } else if (r is RBlank) {
                r.setY(baseline - r.ascentPlusLeading)
            } else if (r is RElement) {
                // int w = relement.getWidth();
                this.setElementY(r, r.height(), r.vAlign())
            } else {
                // RSpacing and RStyleChanger don't matter?
            }
        }
        // TODO: Could throw OverflowException when we add floating widgets
    }

    override fun onMouseClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        val rarray = this.renderabl.toArray<Renderable?>(Renderable.Companion.EMPTY_ARRAY)
        val r = MarkupUtilities.findRenderable(rarray, x, y, false)
        if (r != null) {
            val rbounds = r.visualBounds()!!
            return r.onMouseClick(event, x - rbounds.x, y - rbounds.y)
        } else {
            return true
        }
    }

    /*
  public boolean onMousePressed(final java.awt.event.MouseEvent event, final int x, final int y) {
    final Renderable[] rarray = this.renderables.toArray(Renderable.EMPTY_ARRAY);
    final BoundableRenderable r = MarkupUtilities.findRenderable(rarray, x, y, false);
    if (r != null) {
      this.mousePressTarget = r;
      final Rectangle rbounds = r.getBounds();
      return r.onMousePressed(event, x - rbounds.x, y - rbounds.y);
    } else {
      return true;
    }
  }*/
    override fun onDoubleClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        val rarray = this.renderabl.toArray<Renderable?>(Renderable.Companion.EMPTY_ARRAY)
        val r = MarkupUtilities.findRenderable(rarray, x, y, false)
        if (r != null) {
            val rbounds = r.visualBounds()!!
            return r.onDoubleClick(event, x - rbounds.x, y - rbounds.y)
        } else {
            return true
        }
    }


    override fun getLowestRenderableSpot(x: Int, y: Int): RenderableSpot? {
        val rarray = this.renderabl.toArray<Renderable?>(Renderable.Companion.EMPTY_ARRAY)
        val br = MarkupUtilities.findRenderable(rarray, x, y, false)
        if (br != null) {
            val rbounds = br.visualBounds()!!
            return br.getLowestRenderableSpot(x - rbounds.x, y - rbounds.y)
        } else {
            return RenderableSpot(this, x, y)
        }
    }

    override fun onMouseReleased(event: MouseEvent?, x: Int, y: Int): Boolean {
        val rarray = this.renderabl.toArray<Renderable?>(Renderable.Companion.EMPTY_ARRAY)
        val r = MarkupUtilities.findRenderable(rarray, x, y, false)
        if (r != null) {
            val rbounds = r.visualBounds()!!
            val oldArmedRenderable = this.mousePressTarget
            if ((oldArmedRenderable != null) && (r !== oldArmedRenderable)) {
                oldArmedRenderable.onMouseDisarmed(event)
                this.mousePressTarget = null
            }
            return r.onMouseReleased(event, x - rbounds.x, y - rbounds.y)
        } else {
            val oldArmedRenderable = this.mousePressTarget
            if (oldArmedRenderable != null) {
                oldArmedRenderable.onMouseDisarmed(event)
                this.mousePressTarget = null
            }
            return true
        }
    }

    override fun onMouseDisarmed(event: MouseEvent?): Boolean {
        val target = this.mousePressTarget
        if (target != null) {
            this.mousePressTarget = null
            return target.onMouseDisarmed(event)
        } else {
            return true
        }
    }

    // public final void adjustHorizontalBounds(int newX, int newMaxWidth) throws
    // OverflowException {
    // this.x = newX;
    // this.desiredMaxWidth = newMaxWidth;
    // int topX = newX + newMaxWidth;
    // ArrayList renderables = this.renderables;
    // int size = renderables.size();
    // ArrayList overflown = null;
    // Rectangle lastInLine = null;
    // for(int i = 0; i < size; i++) {
    // Object r = renderables.get(i);
    // if(overflown == null) {
    // if(r instanceof BoundableRenderable) {
    // BoundableRenderable br = (BoundableRenderable) r;
    // Rectangle brb = br.getBounds();
    // int x2 = brb.x + brb.width;
    // if(x2 > topX) {
    // overflown = new ArrayList(1);
    // }
    // else {
    // lastInLine = brb;
    // }
    // }
    // }
    // /* must not be else here */
    // if(overflown != null) {
    // //TODO: This could break a word across markup boundary.
    // overflown.add(r);
    // renderables.remove(i--);
    // size--;
    // }
    // }
    // if(overflown != null) {
    // if(lastInLine != null) {
    // this.width = this.xoffset = lastInLine.x + lastInLine.width;
    // }
    // throw new OverflowException(overflown);
    // }
    // }
    override fun blockBackgroundColor(): Color? {
        return this.container!!.paintedBackgroundColor()
    }

    override fun getRenderables(topFirst: Boolean): MutableIterator<Renderable> {
        // TODO: Returning Renderables in order always, assuming that they don't overlap.
        //       Need to check the assumption
        return this.renderabl.iterator()

    }

    override fun isContainedByNode(): Boolean {
        return false
    }

    val isEmpty: Boolean
        get() = this.xoffset == 0

    override fun clipBounds(): Rectangle? {
        // throw new NotImplementedYetException("This method is not expected to be called for RLine");
        return null
    }

    override fun toString(): String {
        return "RLine belonging to: " + parent()
    }
}
