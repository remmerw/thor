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
package io.github.remmerw.thor.cobra.html.renderer;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import io.github.remmerw.thor.cobra.html.domimpl.ModelNode;

/**
 * A renderer node with well-defined bounds. Most renderer nodes implement this
 * interface.
 */
public interface BoundableRenderable extends Renderable {
    ModelNode getModelNode();

    Rectangle getBounds();

    Rectangle getVisualBounds();

    boolean contains(final int x, final int y);

    Dimension getSize();

    Point getOrigin();

    Point getOriginRelativeTo(RCollection ancestor);

    Point getOriginRelativeToAbs(RCollection ancestor);

    Point getOriginRelativeToNoScroll(RCollection ancestor);

    /**
     * Gets the parent where the renderable is rendered.
     */
    RCollection getParent();

    void setParent(RCollection parent);

    /**
     * Gets the parent set with {@link #setOriginalParent(RCollection)}. It
     * represents the parent where the renderable would have been originally
     * rendered according to the DOM. This will be non-null only if
     * {@link #getParent()} is not the parent where this renderable would have
     * been originally rendered.
     */
    RCollection getOriginalParent();

    void setOriginalParent(RCollection origParent);

    /**
     * Returns {@link #getOriginalParent()} if not null. Otherwise it returns
     * {@link #getParent()}.
     */
    RCollection getOriginalOrCurrentParent();

    void setBounds(int x, int y, int with, int height);

    void setOrigin(int x, int y);

    int getX();

    void setX(int x);

    int getY();

    void setY(int y);

    int getVisualX();

    int getVisualY();

    int getHeight();

    void setHeight(int height);

    int getWidth();

    void setWidth(int width);

    int getVisualHeight();

    int getVisualWidth();

    // public Point getRenderablePoint(int guiX, int guiY);

    RenderableSpot getLowestRenderableSpot(int x, int y);

    void repaint();

    /**
     * Returns false if the event is consumed. True to propagate further.
     */
    boolean onMousePressed(MouseEvent event, int x, int y);

    boolean onMouseReleased(MouseEvent event, int x, int y);

    boolean onMouseDisarmed(MouseEvent event);

    boolean onMouseClick(MouseEvent event, int x, int y);

    boolean onDoubleClick(MouseEvent event, int x, int y);

    boolean onMiddleClick(MouseEvent event, int x, int y);

    boolean onRightClick(MouseEvent event, int x, int y);

    void onMouseMoved(MouseEvent event, int x, int y, boolean triggerEvent, ModelNode limit);

    void onMouseOut(MouseEvent event, int x, int y, ModelNode limit);

    /**
     * Returns true if the renderable is fully contained by its modelNode, but
     * said modelNode does not fully contain an ancestor renderable.
     */
    boolean isContainedByNode();

    /**
     * Asks the Renderable to paint the selection between two points. Nothing will
     * be done if the points are outside the Renderable.
     *
     * @param g
     * @param inSelection
     * @param startPoint
     * @param endPoint
     * @return True iff it's in selection when finished painting.
     */
    boolean paintSelection(Graphics g, boolean inSelection, RenderableSpot startPoint, RenderableSpot endPoint);

    /**
     * Paints by either creating a new clipped graphics context corresponding to
     * the bounds of the Renderable, or by translating the origin.
     *
     * @param g Parent's Graphics context.
     */
    void paintTranslated(Graphics g);

    boolean extractSelectionText(StringBuffer buffer, boolean inSelection, RenderableSpot startPoint, RenderableSpot endPoint);

    void repaint(int x, int y, int width, int height);

    void relayout();

    Point getGUIPoint(int clientX, int clientY);

    int getOrdinal();

    void setOrdinal(int ordinal);

    int getZIndex();

    void invalidateLayoutUpTree();

    void setInnerWidth(Integer newWidth);

    void setInnerHeight(Integer newHeight);

    void setDelegator(final BoundableRenderable pDelegator);

    boolean isDelegated();

    default int getHorizontalScrollBarHeight() {
        return 0;
    }

    default int getVerticalScrollBarHeight() {
        return 0;
    }
}
