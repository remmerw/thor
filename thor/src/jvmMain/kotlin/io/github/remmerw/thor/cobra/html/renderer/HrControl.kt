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
 * Created on Nov 19, 2005
 */
package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.domimpl.HTMLElementImpl
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics

internal class HrControl(modelNode: HTMLElementImpl?) : BaseControl(modelNode) {
    private var availWidth = 0

    fun paintSelection(
        g: Graphics?,
        inSelection: Boolean,
        startPoint: RenderableSpot?,
        endPoint: RenderableSpot?
    ): Boolean {
        return inSelection
    }

    override fun reset(availWidth: Int, availHeight: Int) {
        this.availWidth = availWidth
    }

    override var preferredSize: Dimension?
        get() = TODO("Not yet implemented")
        set(value) {}

    override var component: Component?
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun getPreferredSize(): Dimension {
        return Dimension(this.availWidth, 0)
    }

}
