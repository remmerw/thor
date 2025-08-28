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
 * Created on Apr 16, 2005
 */
package io.github.remmerw.thor.cobra.html.renderer;

import java.awt.Graphics;

import io.github.remmerw.thor.cobra.html.domimpl.ModelNode;

/**
 * Represents a renderer (view) node.
 */
public interface Renderable {
    Renderable[] EMPTY_ARRAY = new Renderable[0];

    void paint(Graphics g);

    ModelNode getModelNode();

    default boolean isFixed() {
        return false;
    }

    default boolean isReadyToPaint() {
        return true;
    }

    default boolean isReplacedElement() {
        // TODO: Match all other replaced elements such as audio, video, canvas, etc. Refer: http://stackoverflow.com/a/12468620
        return getModelNode().getNodeName().equalsIgnoreCase("IMG");
    }
}
