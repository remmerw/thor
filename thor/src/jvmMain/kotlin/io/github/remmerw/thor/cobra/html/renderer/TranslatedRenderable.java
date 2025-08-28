package io.github.remmerw.thor.cobra.html.renderer;

import org.eclipse.jdt.annotation.NonNull;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import io.github.remmerw.thor.cobra.html.domimpl.ModelNode;
import io.github.remmerw.thor.cobra.util.CollectionUtilities;
import io.github.remmerw.thor.cobra.util.Threads;

public class TranslatedRenderable extends BaseBoundableRenderable implements RCollection {

    private final @NonNull BoundableRenderable translatedChild;

    public TranslatedRenderable(final @NonNull BoundableRenderable translatedChild) {
        // TODO
        super(null, null);
        this.translatedChild = translatedChild;
    }

    public void paint(final Graphics g) {
        translatedChild.paintTranslated(g);
    }

    public boolean isFixed() {
        return translatedChild.isFixed();
    }

    public ModelNode getModelNode() {
        return translatedChild.getModelNode();
    }

    public Rectangle getBounds() {
        return translatedChild.getBounds();
    }

    public Rectangle getVisualBounds() {
        return translatedChild.getVisualBounds();
    }

    public boolean contains(final int x, final int y) {
        return translatedChild.contains(x, y);
    }

    public Dimension getSize() {
        return translatedChild.getSize();
    }

    public Point getOrigin() {
        return translatedChild.getOrigin();
    }

    public Point getOriginRelativeTo(final RCollection ancestor) {
        return translatedChild.getOriginRelativeTo(ancestor);
    }

  /*
  public RCollection getParent() {
    return translatedChild.getParent();
  }*/

    public Point getOriginRelativeToAbs(final RCollection ancestor) {
        return translatedChild.getOriginRelativeToAbs(ancestor);
    }

    public Point getOriginRelativeToNoScroll(final RCollection ancestor) {
        return translatedChild.getOriginRelativeToNoScroll(ancestor);
    }

    public RCollection getOriginalParent() {
        return translatedChild.getOriginalParent();
    }

    public void setOriginalParent(final RCollection origParent) {
        translatedChild.setOriginalParent(origParent);
    }

    public RCollection getOriginalOrCurrentParent() {
        return translatedChild.getOriginalOrCurrentParent();
    }

    public void setBounds(final int x, final int y, final int with, final int height) {
        translatedChild.setBounds(x, y, with, height);
    }

    public void setOrigin(final int x, final int y) {
        translatedChild.setOrigin(x, y);
    }

    public int getX() {
        return translatedChild.getX();
    }

    public void setX(final int x) {
        translatedChild.setX(x);
    }

    public int getY() {
        return translatedChild.getY();
    }

    public void setY(final int y) {
        translatedChild.setY(y);
    }

    public int getVisualX() {
        return translatedChild.getVisualX();
    }

    public int getVisualY() {
        return translatedChild.getVisualY();
    }

    public int getHeight() {
        return translatedChild.getHeight();
    }

    public void setHeight(final int height) {
        translatedChild.setHeight(height);
    }

    public int getWidth() {
        return translatedChild.getWidth();
    }

    public void setWidth(final int width) {
        translatedChild.setWidth(width);
    }

    public int getVisualHeight() {
        return translatedChild.getVisualHeight();
    }

    public int getVisualWidth() {
        return translatedChild.getVisualWidth();
    }

    public RenderableSpot getLowestRenderableSpot(final int x, final int y) {
        return translatedChild.getLowestRenderableSpot(x, y);
    }

    public void repaint() {
        translatedChild.repaint();
    }

    public boolean onMousePressed(final MouseEvent event, final int x, final int y) {
        return translatedChild.onMousePressed(event, x, y);
    }

    public boolean onMouseReleased(final MouseEvent event, final int x, final int y) {
        return translatedChild.onMouseReleased(event, x, y);
    }

    public boolean onMouseDisarmed(final MouseEvent event) {
        return translatedChild.onMouseDisarmed(event);
    }

    public boolean onMouseClick(final MouseEvent event, final int x, final int y) {
        return translatedChild.onMouseClick(event, x, y);
    }

    public boolean onMiddleClick(final MouseEvent event, final int x, final int y) {
        return translatedChild.onMiddleClick(event, x, y);
    }

    public boolean onDoubleClick(final MouseEvent event, final int x, final int y) {
        return translatedChild.onDoubleClick(event, x, y);
    }

    public boolean onRightClick(final MouseEvent event, final int x, final int y) {
        return translatedChild.onRightClick(event, x, y);
    }

    public void onMouseMoved(final MouseEvent event, final int x, final int y, final boolean triggerEvent, final ModelNode limit) {
        translatedChild.onMouseMoved(event, x, y, triggerEvent, limit);
    }

    public void onMouseOut(final MouseEvent event, final int x, final int y, final ModelNode limit) {
        translatedChild.onMouseOut(event, x, y, limit);
    }

    public boolean isContainedByNode() {
        return translatedChild.isContainedByNode();
    }

    public boolean paintSelection(final Graphics g, final boolean inSelection, final RenderableSpot startPoint, final RenderableSpot endPoint) {
        return translatedChild.paintSelection(g, inSelection, startPoint, endPoint);
    }

    public boolean extractSelectionText(final StringBuffer buffer, final boolean inSelection, final RenderableSpot startPoint, final RenderableSpot endPoint) {
        return translatedChild.extractSelectionText(buffer, inSelection, startPoint, endPoint);
    }

  /*
  public Point getGUIPoint(int clientX, int clientY) {
    return translatedChild.getGUIPoint(clientX, clientY);
  }*/

  /*
  public int getOrdinal() {
    return translatedChild.getOrdinal();
  }

  public void setOrdinal(int ordinal) {
    translatedChild.setOrdinal(ordinal);
  }*/

    public void repaint(final int x, final int y, final int width, final int height) {
        // translatedChild.repaint(x, y, width, height);
        // getParent().repaint(x, y, width, height);
        final Point or = translatedChild.getOriginRelativeTo(getParent());
        {
            final Rectangle rect = new Rectangle(x, y, width, height);
            rect.translate(or.x, or.y);
        }
        getParent().repaint(x + or.x, y + or.y, width, height);
    }

    public void relayout() {
        translatedChild.relayout();
    }

    public int getZIndex() {
        return translatedChild.getZIndex();
    }

    @Override
    protected void invalidateLayoutLocal() {
        // TODO
    }

    @Override
    public String toString() {
        return "TransRndrbl [" + translatedChild + "]";
    }

    public Renderable getChild() {
        return translatedChild;
    }

    @Override
    public Iterator<@NonNull ? extends Renderable> getRenderables(boolean topFirst) {
        return CollectionUtilities.singletonIterator(translatedChild);
    }

    @Override
    public void updateWidgetBounds(int guiX, int guiY) {
        // NOP
        // Just checking
        if (translatedChild instanceof RCollection tc) {
            tc.updateWidgetBounds(guiX, guiY);
        }

    }

    @Override
    public void invalidateLayoutDeep() {
        if (translatedChild instanceof RCollection tc) {
            tc.invalidateLayoutDeep();
        }

    }

    @Override
    public void focus() {
        // TODO Auto-generated method stub
        Threads.dumpStack(8);
    }

    @Override
    public void blur() {
        // TODO Auto-generated method stub
        Threads.dumpStack(8);
    }

    @Override
    public BoundableRenderable getRenderable(int x, int y) {
        if (translatedChild instanceof RCollection tc) {
            return tc.getRenderable(x, y);
        }

        return null;
    }

    @Override
    public Rectangle getClipBounds() {
        if (translatedChild instanceof RCollection tc) {
            return tc.getClipBounds();
        }

        return null;
    }

    @Override

    public Rectangle getClipBoundsWithoutInsets() {
        // TODO: Stub
        return getClipBounds();
    }

    @Override
    public boolean isReadyToPaint() {
        return translatedChild.isReadyToPaint();
    }
}
