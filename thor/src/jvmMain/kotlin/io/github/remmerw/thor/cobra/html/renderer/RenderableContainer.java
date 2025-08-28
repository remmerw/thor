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

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * A RenderableContainer is either usually a parent block or the root GUI
 * component. It's is a Renderable or GUI component whose layout may be
 * invalidated.
 */
public interface RenderableContainer {
    // public Insets getInsets();
    Component addComponent(Component component);

    // public void remove(Component component);
    void invalidateLayoutUpTree();

    void repaint(int x, int y, int width, int height);

    void relayout();

    void updateAllWidgetBounds();

    Color getPaintedBackgroundColor();

    Point getGUIPoint(int x, int y);

    void focus();

    void addDelayedPair(DelayedPair pair);

    java.util.Collection<DelayedPair> getDelayedPairs();

    RenderableContainer getParentContainer();

    void clearDelayedPairs();

    int getHeight();

    int getWidth();

    int getX();

    int getY();

    Insets getInsets(final boolean hscroll, final boolean vscroll);

    Insets getInsetsMarginBorder(final boolean hscroll, final boolean vscroll);

    default int getInnerWidth() {
        final Insets insets = getInsetsMarginBorder(false, false);
        return getWidth() - (insets.left + insets.right);
    }

    default int getInnerMostWidth() {
        final Insets insets = getInsets(false, false);
        return getWidth() - (insets.left + insets.right);
    }

    default int getInnerMostHeight() {
        final Insets insets = getInsets(false, false);
        return getHeight() - (insets.top + insets.bottom);
    }

    default int getInnerHeight() {
        final Insets insets = getInsetsMarginBorder(false, false);
        return getHeight() - (insets.top + insets.bottom);
    }

    Rectangle getVisualBounds();

    int getVisualWidth();

    int getVisualHeight();

    Point translateDescendentPoint(BoundableRenderable descendent, int x, int y);

    Point getOriginRelativeTo(RCollection bodyLayout);

    Point getOriginRelativeToAbs(RCollection bodyLayout);
}
