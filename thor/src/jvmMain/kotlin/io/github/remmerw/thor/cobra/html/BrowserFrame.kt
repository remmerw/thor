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
 * Created on Jan 29, 2006
 */
package io.github.remmerw.thor.cobra.html

import org.w3c.dom.Document
import java.awt.Component
import java.awt.Insets
import java.net.URL

/**
 * The `BrowserFrame` interface represents a browser frame. A simple
 * implementation of this interface is provided in
 * [org.cobraparser.html.test.SimpleBrowserFrame].
 */
interface BrowserFrame {
    /**
     * Gets the component that renders the frame. This can be a
     * [HtmlPanel].
     */
    val component: Component?

    /**
     * Loads a URL in the frame.
     */
    fun loadURL(url: URL)

    /**
     * Gets the content document.
     */
    /**
     * Sets the content document.
     */
    var contentDocument: Document?

    /**
     * Gets the [HtmlRendererContext] of the frame.
     */
    val htmlRendererContext: HtmlRendererContext?

    /**
     * Sets the default margin insets of the browser frame.
     *
     * @param insets The margin insets.
     */
    fun setDefaultMarginInsets(insets: Insets?)

    /**
     * Sets the default horizontal overflow of the browser frame.
     *
     * @param overflowX See constants in [RenderState].
     */
    fun setDefaultOverflowX(overflowX: Int)

    /**
     * Sets the default vertical overflow of the browser frame.
     *
     * @param overflowY See constants in [RenderState].
     */
    fun setDefaultOverflowY(overflowY: Int)
}
