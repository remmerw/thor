package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import io.github.remmerw.thor.cobra.util.CollectionUtilities
import io.github.remmerw.thor.cobra.util.Threads
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.MouseEvent

class TranslatedRenderable(translatedChild: BoundableRenderable) :
    BaseBoundableRenderable(null, null), RCollection {
    private val translatedChild: BoundableRenderable

    init {
        // TODO
        this.translatedChild = translatedChild
    }

    override fun paint(g: Graphics?) {
        translatedChild.paintTranslated(g)
    }

    override fun isFixed(): Boolean {
        return translatedChild.isFixed()
    }

    override fun getModelNode(): ModelNode? {
        return translatedChild.getModelNode()
    }

    override fun getBounds(): Rectangle? {
        return translatedChild.getBounds()
    }

    override fun getVisualBounds(): Rectangle? {
        return translatedChild.getVisualBounds()
    }

    override fun contains(x: Int, y: Int): Boolean {
        return translatedChild.contains(x, y)
    }

    override fun getSize(): Dimension? {
        return translatedChild.getSize()
    }

    override fun getOrigin(): Point? {
        return translatedChild.getOrigin()
    }

    override fun getOriginRelativeTo(ancestor: RCollection?): Point {
        return translatedChild.getOriginRelativeTo(ancestor)
    }

    /*
  public RCollection getParent() {
    return translatedChild.getParent();
  }*/
    override fun getOriginRelativeToAbs(ancestor: RCollection?): Point? {
        return translatedChild.getOriginRelativeToAbs(ancestor)
    }

    override fun getOriginRelativeToNoScroll(ancestor: RCollection?): Point? {
        return translatedChild.getOriginRelativeToNoScroll(ancestor)
    }

    override fun getOriginalParent(): RCollection? {
        return translatedChild.getOriginalParent()
    }

    override fun setOriginalParent(origParent: RCollection?) {
        translatedChild.setOriginalParent(origParent)
    }

    override fun getOriginalOrCurrentParent(): RCollection? {
        return translatedChild.getOriginalOrCurrentParent()
    }

    override fun setBounds(x: Int, y: Int, with: Int, height: Int) {
        translatedChild.setBounds(x, y, with, height)
    }

    override fun setOrigin(x: Int, y: Int) {
        translatedChild.setOrigin(x, y)
    }

    override fun getX(): Int {
        return translatedChild.getX()
    }

    override fun setX(x: Int) {
        translatedChild.setX(x)
    }

    override fun getY(): Int {
        return translatedChild.getY()
    }

    override fun setY(y: Int) {
        translatedChild.setY(y)
    }

    override fun getVisualX(): Int {
        return translatedChild.getVisualX()
    }

    override fun getVisualY(): Int {
        return translatedChild.getVisualY()
    }

    override fun getHeight(): Int {
        return translatedChild.getHeight()
    }

    override fun setHeight(height: Int) {
        translatedChild.setHeight(height)
    }

    override fun getWidth(): Int {
        return translatedChild.getWidth()
    }

    override fun setWidth(width: Int) {
        translatedChild.setWidth(width)
    }

    override fun getVisualHeight(): Int {
        return translatedChild.getVisualHeight()
    }

    override fun getVisualWidth(): Int {
        return translatedChild.getVisualWidth()
    }

    override fun getLowestRenderableSpot(x: Int, y: Int): RenderableSpot? {
        return translatedChild.getLowestRenderableSpot(x, y)
    }

    override fun repaint() {
        translatedChild.repaint()
    }

    override fun onMousePressed(event: MouseEvent?, x: Int, y: Int): Boolean {
        return translatedChild.onMousePressed(event, x, y)
    }

    override fun onMouseReleased(event: MouseEvent?, x: Int, y: Int): Boolean {
        return translatedChild.onMouseReleased(event, x, y)
    }

    override fun onMouseDisarmed(event: MouseEvent?): Boolean {
        return translatedChild.onMouseDisarmed(event)
    }

    override fun onMouseClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        return translatedChild.onMouseClick(event, x, y)
    }

    override fun onMiddleClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        return translatedChild.onMiddleClick(event, x, y)
    }

    override fun onDoubleClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        return translatedChild.onDoubleClick(event, x, y)
    }

    override fun onRightClick(event: MouseEvent?, x: Int, y: Int): Boolean {
        return translatedChild.onRightClick(event, x, y)
    }

    override fun onMouseMoved(
        event: MouseEvent?,
        x: Int,
        y: Int,
        triggerEvent: Boolean,
        limit: ModelNode?
    ) {
        translatedChild.onMouseMoved(event, x, y, triggerEvent, limit)
    }

    override fun onMouseOut(event: MouseEvent?, x: Int, y: Int, limit: ModelNode?) {
        translatedChild.onMouseOut(event, x, y, limit)
    }

    override fun isContainedByNode(): Boolean {
        return translatedChild.isContainedByNode()
    }

    override fun paintSelection(
        g: Graphics?,
        inSelection: Boolean,
        startPoint: RenderableSpot?,
        endPoint: RenderableSpot?
    ): Boolean {
        return translatedChild.paintSelection(g, inSelection, startPoint, endPoint)
    }

    override fun extractSelectionText(
        buffer: StringBuffer?,
        inSelection: Boolean,
        startPoint: RenderableSpot?,
        endPoint: RenderableSpot?
    ): Boolean {
        return translatedChild.extractSelectionText(buffer, inSelection, startPoint, endPoint)
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
    override fun repaint(x: Int, y: Int, width: Int, height: Int) {
        // translatedChild.repaint(x, y, width, height);
        // getParent().repaint(x, y, width, height);
        val or = translatedChild.getOriginRelativeTo(getParent())
        run {
            val rect = Rectangle(x, y, width, height)
            rect.translate(or.x, or.y)
        }
        getParent().repaint(x + or.x, y + or.y, width, height)
    }

    override fun relayout() {
        translatedChild.relayout()
    }

    override fun getZIndex(): Int {
        return translatedChild.getZIndex()
    }

    override fun invalidateLayoutLocal() {
        // TODO
    }

    override fun toString(): String {
        return "TransRndrbl [" + translatedChild + "]"
    }

    val child: Renderable
        get() = translatedChild

    override fun getRenderables(topFirst: Boolean): MutableIterator<out Renderable> {
        return CollectionUtilities.singletonIterator<BoundableRenderable>(translatedChild)
    }

    override fun updateWidgetBounds(guiX: Int, guiY: Int) {
        // NOP
        // Just checking
        if (translatedChild is RCollection) {
            translatedChild.updateWidgetBounds(guiX, guiY)
        }
    }

    override fun invalidateLayoutDeep() {
        if (translatedChild is RCollection) {
            translatedChild.invalidateLayoutDeep()
        }
    }

    override fun focus() {
        // TODO Auto-generated method stub
        Threads.dumpStack(8)
    }

    override fun blur() {
        // TODO Auto-generated method stub
        Threads.dumpStack(8)
    }

    override fun getRenderable(x: Int, y: Int): BoundableRenderable? {
        if (translatedChild is RCollection) {
            return translatedChild.getRenderable(x, y)
        }

        return null
    }

    override fun getClipBounds(): Rectangle? {
        if (translatedChild is RCollection) {
            return translatedChild.getClipBounds()
        }

        return null
    }

    override fun getClipBoundsWithoutInsets(): Rectangle? {
        // TODO: Stub
        return getClipBounds()
    }

    override fun isReadyToPaint(): Boolean {
        return translatedChild.isReadyToPaint()
    }
}
