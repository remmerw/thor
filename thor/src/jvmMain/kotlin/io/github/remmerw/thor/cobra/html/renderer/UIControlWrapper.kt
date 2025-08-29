package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.HtmlObject
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics

internal class UIControlWrapper(ho: HtmlObject?) : UIControl {

    private val htmlObject: HtmlObject?

    init {
        this.htmlObject = ho
        val c: Component
        if (ho == null) {
            c = BrokenComponent()
        } else {
            c = ho.component!!
        }
        this.component = c
    }

    override fun reset(availWidth: Int, availHeight: Int) {
        this.htmlObject!!.reset(availWidth, availHeight)
    }

    override var preferredSize: Dimension?
        get() = TODO("Not yet implemented")
        set(value) {}

    fun getComponent(): Component {
        return this.component!!
    }

    fun getBackgroundColor(): Color? {
        return this.component!!.getBackground()
    }

    fun getPreferredSize(): Dimension? {
        return this.component!!.preferredSize
    }

    override fun invalidate() {
        // Calls its AWT parent's invalidate, but I guess that's OK.
        this.component!!.invalidate()
    }

    override val backgroundColor: Color?
        get() = TODO("Not yet implemented")

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

    override var component: Component?
        get() = TODO("Not yet implemented")
        set(value) {}
}
