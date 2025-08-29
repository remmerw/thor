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
 * Created on Apr 17, 2005
 */
package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import io.github.remmerw.thor.cobra.html.style.RenderState
import java.awt.Graphics
import java.awt.event.MouseEvent

//import java.util.logging.*;
/**
 * @author J. H. S.
 */
internal class RStyleChanger(override var modelNode: ModelNode?) : BaseRenderable() {
    // private final static Logger logger = Logger.getLogger(RStyleChanger.class);


    fun getModelNode(): ModelNode {
        return this.modelNode!!
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * net.sourceforge.xamj.domimpl.markup.Renderable#paint(java.awt.Graphics)
     */
    override fun paint(g: Graphics) {
        val rs: RenderState = this.modelNode!!.renderState!!
        g.color = rs.color
        g.font = rs.font
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xamjwg.html.renderer.Renderable#invalidate()
     */
    fun invalidateLayoutUpTree() {
    }

    companion object {
        fun onMouseClick(event: MouseEvent?, x: Int, y: Int) {
            throw UnsupportedOperationException("unexpected")
        }

        fun onMousePressed(event: MouseEvent?, x: Int, y: Int) {
            throw UnsupportedOperationException("unexpected")
        }

        fun onMouseReleased(event: MouseEvent?, x: Int, y: Int) {
            throw UnsupportedOperationException("unexpected")
        }
    }
}
