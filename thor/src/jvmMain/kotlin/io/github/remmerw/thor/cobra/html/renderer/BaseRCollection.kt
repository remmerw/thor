package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.MouseEvent

abstract class BaseRCollection(container: RenderableContainer?, modelNode: ModelNode?) :
    BaseBoundableRenderable(container, modelNode), RCollection {
    private var renderableWithMouse: BoundableRenderable? = null

    override fun focus() {
        this.container?.focus()
        // TODO: Plus local focus
    }

    override fun blur() {
        val parent = this.parent
        if (parent != null) {
            parent.focus()
        } else {
            // TODO: Remove local focus
        }
    }

    /**
     * Updates bounds of all descendent's GUI components, based on root bounds.
     */
    override fun updateWidgetBounds(guiX: Int, guiY: Int) {
        val i = this.getRenderables(false)
        if (i != null) {
            while (i.hasNext()) {
                val rn = i.next()
                val r = if (rn is PositionedRenderable) rn.renderable else rn
                if (r is RCollection) {
                    // RUIControl is a RCollection too.
                    val or = r.getOriginRelativeTo(this)
                    // rc.updateWidgetBounds(guiX + rc.getX(), guiY + rc.getY());
                    r.updateWidgetBounds(guiX + or.x, guiY + or.y)
                }

                /*
        int ox = guiX;
        int oy = guiY;
        Renderable r = rn;
        if (rn instanceof PositionedRenderable) {
          final PositionedRenderable pr = (PositionedRenderable) rn;
          final Point pro = pr.getOffset();
          ox += pro.x;
          oy += pro.y;
          r = pr.renderable;
        }
        if (r instanceof RCollection) {
          // RUIControl is a RCollection too.
          final RCollection rc = (RCollection) r;
          final Point or = rc.getOriginRelativeTo(this);
          // rc.updateWidgetBounds(guiX + rc.getX(), guiY + rc.getY());
          rc.updateWidgetBounds(ox + or.x, oy + or.y);
        }
        */
            }
        }
    }

    override fun paintSelection(
        g: Graphics,
        inSelection: Boolean,
        startPoint: RenderableSpot,
        endPoint: RenderableSpot
    ): Boolean {
        // TODO: Does this work with renderables that are absolutely positioned?
        var inSelection = inSelection
        var checkPoint1: Point? = null
        var checkPoint2: Point? = null
        if (!inSelection) {
            val isStart = startPoint.renderable === this
            val isEnd = endPoint.renderable === this
            if (isStart && isEnd) {
                checkPoint1 = startPoint.point
                checkPoint2 = endPoint.point
            } else if (isStart) {
                checkPoint1 = startPoint.point
            } else if (isEnd) {
                checkPoint1 = endPoint.point
            }
        } else {
            if (startPoint.renderable === this) {
                checkPoint1 = startPoint.point
            } else if (endPoint.renderable === this) {
                checkPoint1 = endPoint.point
            }
        }
        val i = this.getRenderables(true)
        if (i != null) {
            while (i.hasNext()) {
                val robj: Any? = i.next()
                if (robj is BoundableRenderable) {
                    val bounds = robj.visualBounds()!!
                    if (!inSelection) {
                        if ((checkPoint1 != null) && checkStartSelection(bounds, checkPoint1)) {
                            if (checkPoint2 != null) {
                                checkPoint1 = checkPoint2
                                checkPoint2 = null
                            } else {
                                checkPoint1 = null
                            }
                            inSelection = true
                        } else if ((checkPoint2 != null) && checkStartSelection(
                                bounds,
                                checkPoint2
                            )
                        ) {
                            checkPoint1 = null
                            checkPoint2 = null
                            inSelection = true
                        }
                    } else if (inSelection && (checkPoint1 != null) && checkEndSelection(
                            bounds,
                            checkPoint1
                        )
                    ) {
                        return false
                    }
                    val offsetX = bounds.x
                    val offsetY = bounds.y
                    g.translate(offsetX, offsetY)
                    try {
                        val newInSelection =
                            robj.paintSelection(g, inSelection, startPoint, endPoint)
                        if (inSelection && !newInSelection) {
                            return false
                        }
                        inSelection = newInSelection
                    } finally {
                        g.translate(-offsetX, -offsetY)
                    }
                }
            }
        }
        if (inSelection && (checkPoint1 != null)) {
            return false
        } else if (!inSelection && ((checkPoint1 != null) || (checkPoint2 != null)) && !((checkPoint1 != null) && (checkPoint2 != null))) {
            // Has to have started not being in selection,
            // but we must start selecting without having
            // selected anything in the block then.
            return true
        }
        return inSelection
    }

    override fun extractSelectionText(
        buffer: StringBuffer, inSelection: Boolean, startPoint: RenderableSpot,
        endPoint: RenderableSpot
    ): Boolean {
        var inSelection = inSelection
        var checkPoint1: Point? = null
        var checkPoint2: Point? = null
        if (!inSelection) {
            val isStart = startPoint.renderable === this
            val isEnd = endPoint.renderable === this
            if (isStart && isEnd) {
                checkPoint1 = startPoint.point
                checkPoint2 = endPoint.point
            } else if (isStart) {
                checkPoint1 = startPoint.point
            } else if (isEnd) {
                checkPoint1 = endPoint.point
            }
        } else {
            if (startPoint.renderable === this) {
                checkPoint1 = startPoint.point
            } else if (endPoint.renderable === this) {
                checkPoint1 = endPoint.point
            }
        }
        val i = this.getRenderables(true)
        if (i != null) {
            while (i.hasNext()) {
                val rn = i.next()
                val robj = if (rn is PositionedRenderable) rn.renderable else rn
                if (robj is BoundableRenderable) {
                    if (!inSelection) {
                        val bounds = robj.visualBounds()!!
                        if ((checkPoint1 != null) && checkStartSelection(bounds, checkPoint1)) {
                            if (checkPoint2 != null) {
                                checkPoint1 = checkPoint2
                                checkPoint2 = null
                            } else {
                                checkPoint1 = null
                            }
                            inSelection = true
                        } else if ((checkPoint2 != null) && checkStartSelection(
                                bounds,
                                checkPoint2
                            )
                        ) {
                            checkPoint1 = null
                            checkPoint2 = null
                            inSelection = true
                        }
                    } else if (inSelection && (checkPoint1 != null) && checkEndSelection(
                            robj.bounds()!!,
                            checkPoint1
                        )
                    ) {
                        return false
                    }
                    val newInSelection =
                        robj.extractSelectionText(buffer, inSelection, startPoint, endPoint)
                    if (inSelection && !newInSelection) {
                        return false
                    }
                    inSelection = newInSelection
                }
            }
        }
        if (inSelection && (checkPoint1 != null)) {
            return false
        } else if (!inSelection && ((checkPoint1 != null) || (checkPoint2 != null)) && !((checkPoint1 != null) && (checkPoint2 != null))) {
            // Has to have started not being in selection,
            // but we must start selecting without having
            // selected anything in the block then.
            return true
        }
        return inSelection
    }

    override fun invalidateLayoutDeep() {
        // TODO: May be pretty inefficient in RLine's
        // if it's true that non-layable components
        // are not in RLine's anymore.
        this.invalidateLayoutLocal()
        val renderables = this.getRenderables(true)
        if (renderables != null) {
            while (renderables.hasNext()) {
                val rn = renderables.next()
                val r = if (rn is PositionedRenderable) rn.renderable else rn
                if (r is RCollection) {
                    r.invalidateLayoutDeep()
                }
            }
        }
    }

    override fun onMouseMoved(
        event: MouseEvent?,
        x: Int,
        y: Int,
        triggerEvent: Boolean,
        limit: ModelNode?
    ) {
        super.onMouseMoved(event, x, y, triggerEvent, limit)
        val oldRenderable = this.renderableWithMouse
        val r: Renderable? = this.getRenderable(x, y)
        val newRenderable = if (r is BoundableRenderable) r else null
        val newLimit: ModelNode?
        if (this.isContainedByNode) {
            newLimit = this.modelNode
        } else {
            newLimit = limit
        }
        val changed = oldRenderable !== newRenderable
        if (changed) {
            if (oldRenderable != null) {
                oldRenderable.onMouseOut(
                    event,
                    x - oldRenderable.visualX,
                    y - oldRenderable.visualY,
                    newLimit
                )
            }
            this.renderableWithMouse = newRenderable
        }
        // Must recurse always
        if (newRenderable != null) {
            // newRenderable.onMouseMoved(event, x - newRenderable.getVisualX(), y - newRenderable.getVisualY(), changed, newLimit);
            val or = newRenderable.getOriginRelativeTo(this)
            newRenderable.onMouseMoved(event, x - or.x, y - or.y, changed, newLimit)
        }
    }

    override fun onMousePressed(event: MouseEvent?, x: Int, y: Int): Boolean {
        val r: Renderable? = this.getRenderable(x, y)
        val newRenderable = if (r is RCollection) r else null
        if (newRenderable != null) {
            val or = newRenderable.getOriginRelativeTo(this)
            if (!newRenderable.onMousePressed(event, x - or.x, y - or.y)) {
                return false
            }
        }
        return HtmlController.Companion.instance.onMouseDown(this.modelNode!!, event, x, y)
    }

    override fun onMouseClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        val r: Renderable? = this.getRenderable(x, y)
        val newRenderable = if (r is RCollection) r else null
        if (newRenderable != null) {
            val or = newRenderable.getOriginRelativeTo(this)
            if (!newRenderable.onMouseClick(event, x - or.x, y - or.y)) {
                return false
            }
        }
        return HtmlController.Companion.instance.onMouseClick(this.modelNode!!, event, x, y)
    }

    override fun onMouseOut(event: MouseEvent?, x: Int, y: Int, limit: ModelNode?) {
        super.onMouseOut(event, x, y, limit)
        val oldRenderable = this.renderableWithMouse
        if (oldRenderable != null) {
            this.renderableWithMouse = null
            val newLimit: ModelNode?
            if (this.isContainedByNode) {
                newLimit = this.modelNode
            } else {
                newLimit = limit
            }
            val or = oldRenderable.getOriginRelativeTo(this)
            oldRenderable.onMouseOut(event, x - or.x, y - or.y, newLimit)
        }
    }

    override fun getRenderable(x: Int, y: Int): BoundableRenderable? {
        val i = this.getRenderables(true)
        if (i != null) {
            while (i.hasNext()) {
                val rn = i.next()
                val r = rn
                if (r is BoundableRenderable) {
                    /*
          if (br instanceof RBlockViewport) {
            return br;
          }*/
                    if ((!r.isDelegated()) && r.contains(x, y)) {
                        return r
                    }
                } else if (r is PositionedRenderable) {
                    if (r.contains(x, y)) {
                        return r.renderable
                    }
                }
            }
        }
        return null
    }

    override fun onMiddleClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        val br = this.getRenderable(x, y)
        if (br == null) {
            return HtmlController.Companion.instance.onMiddleClick(this.modelNode!!, event, x, y)
        } else {
            val or = br.getOriginRelativeTo(this)
            return br.onMiddleClick(event, x - or.x, y - or.y)
        }
    }

    override fun onRightClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        val br = this.getRenderable(x, y)
        if (br == null) {
            return HtmlController.Companion.instance.onContextMenu(this.modelNode!!, event, x, y)
        } else {
            val or = br.getOriginRelativeTo(this)
            return br.onRightClick(event, x - or.x, y - or.y)
        }
    }

    override fun clipBoundsWithoutInsets(): Rectangle? {
        // TODO
        return clipBounds()
    }

    open fun isReadyToPaint(): Boolean {
        val renderables = renderables
        if (renderables == null) {
            return true
        }

        while (renderables.hasNext()) {
            val next = renderables.next()!!
            if (!next.isReadyToPaint) {
                return false
            }
        }
        return true
    }

    companion object {
        private fun checkStartSelection(bounds: Rectangle, selectionPoint: Point): Boolean {
            if (bounds.y > selectionPoint.y) {
                return true
            } else return (selectionPoint.y >= bounds.y) && (selectionPoint.y < (bounds.y + bounds.height)) && (bounds.x > selectionPoint.x)
        }

        private fun checkEndSelection(bounds: Rectangle, selectionPoint: Point): Boolean {
            if (bounds.y > selectionPoint.y) {
                return true
            } else return (selectionPoint.y >= bounds.y) && (selectionPoint.y < (bounds.y + bounds.height)) && (selectionPoint.x < bounds.x)
        }
    }
}
