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
 * Created on Mar 19, 2005
 */
package io.github.remmerw.thor.cobra.util.gui

import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import java.awt.LayoutManager

/**
 * @author J. H. S.
 */
class WrapperLayout : LayoutManager {
    /*
     * (non-Javadoc)
     *
     * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String,
     * java.awt.Component)
     */
    override fun addLayoutComponent(arg0: String?, arg1: Component?) {
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
     */
    override fun removeLayoutComponent(arg0: Component?) {
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
     */
    override fun preferredLayoutSize(arg0: Container): Dimension {
        val insets = arg0.insets
        val count = arg0.componentCount
        if (count > 0) {
            val d = arg0.getComponent(0).preferredSize
            return Dimension(
                d.width + insets.left + insets.right,
                d.height + insets.top + insets.bottom
            )
        } else {
            return Dimension(insets.left + insets.right, insets.top + insets.bottom)
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
     */
    override fun minimumLayoutSize(arg0: Container): Dimension {
        val insets = arg0.insets
        val count = arg0.componentCount
        if (count > 0) {
            val d = arg0.getComponent(0).minimumSize
            return Dimension(
                d.width + insets.left + insets.right,
                d.height + insets.top + insets.bottom
            )
        } else {
            return Dimension(insets.left + insets.right, insets.top + insets.bottom)
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
     */
    override fun layoutContainer(arg0: Container) {
        val count = arg0.componentCount
        if (count > 0) {
            val child = arg0.getComponent(0)
            val insets = arg0.insets
            child.setBounds(
                insets.left,
                insets.top,
                arg0.getWidth() - insets.left - insets.right,
                arg0.getHeight() - insets.top - insets.bottom
            )
        }
    }

    companion object {
        val instance: WrapperLayout = WrapperLayout()
    }
}
