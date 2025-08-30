package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.HtmlObject
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics

class UIControlWrapper(
    val htmlObject: HtmlObject,
    override var component: Component?
) : UIControl {


    override fun reset(availWidth: Int, availHeight: Int) {
        this.htmlObject.reset(availWidth, availHeight)
    }

    override var preferredSize: Dimension?
        get() = TODO("Not yet implemented")
        set(value) {}

    fun getComponent(): Component {
        return this.component!!
    }

    override fun backgroundColor(): Color? {
        return this.component!!.getBackground()
    }

    fun getPreferredSize(): Dimension? {
        return this.component!!.preferredSize
    }

    override fun invalidate() {
        // Calls its AWT parent's invalidate, but I guess that's OK.
        this.component!!.invalidate()
    }



    fun paintSelection(
        g: Graphics?,
        inSelection: Boolean,
        startPoint: RenderableSpot?,
        endPoint: RenderableSpot?
    ): Boolean {
        // Does not paint selection
        return inSelection
    }

    override fun setBounds(x: Int, y: Int, width: Int, height: Int) {
        this.component!!.setBounds(x, y, width, height)
    }

    override fun setRUIControl(ruicontrol: RUIControl?) {
        // Not doing anything with this.
    }

    override fun paint(g: Graphics?) {
        this.component!!.paint(g)
    }
}
