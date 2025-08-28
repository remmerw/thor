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
package io.github.remmerw.thor.cobra.html;

import org.eclipse.jdt.annotation.NonNull;
import org.w3c.dom.Document;

import java.awt.Component;
import java.net.URL;

import io.github.remmerw.thor.cobra.html.gui.HtmlPanel;
import io.github.remmerw.thor.cobra.html.style.RenderState;

/**
 * The <code>BrowserFrame</code> interface represents a browser frame. A simple
 * implementation of this interface is provided in
 * {@link org.cobraparser.html.test.SimpleBrowserFrame}.
 */
public interface BrowserFrame {
    /**
     * Gets the component that renders the frame. This can be a
     * {@link HtmlPanel}.
     */
    Component getComponent();

    /**
     * Loads a URL in the frame.
     */
    void loadURL(@NonNull URL url);

    /**
     * Gets the content document.
     */
    Document getContentDocument();

    /**
     * Sets the content document.
     */
    void setContentDocument(Document d);

    /**
     * Gets the {@link HtmlRendererContext} of the frame.
     */
    HtmlRendererContext getHtmlRendererContext();

    /**
     * Sets the default margin insets of the browser frame.
     *
     * @param insets The margin insets.
     */
    void setDefaultMarginInsets(java.awt.Insets insets);

    /**
     * Sets the default horizontal overflow of the browser frame.
     *
     * @param overflowX See constants in {@link RenderState}.
     */
    void setDefaultOverflowX(int overflowX);

    /**
     * Sets the default vertical overflow of the browser frame.
     *
     * @param overflowY See constants in {@link RenderState}.
     */
    void setDefaultOverflowY(int overflowY);
}
