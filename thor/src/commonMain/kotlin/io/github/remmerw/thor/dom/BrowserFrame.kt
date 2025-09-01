package io.github.remmerw.thor.dom

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
    fun component(): Component?

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
    fun contentDocument(): Document?

    /**
     * Gets the [io.github.remmerw.thor.cobra.html.HtmlRendererContext] of the frame.
     */
    fun htmlRendererContext(): HtmlRendererContext?

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