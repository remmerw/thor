package io.github.remmerw.thor.dom

import java.awt.Component

/**
 * This interface should be implemented to provide OBJECT, EMBED or APPLET
 * functionality.
 */
interface HtmlObject {
    fun component(): Component?

    fun suspend()

    fun resume()

    fun destroy()

    /**
     * Called as the object is layed out, either the first time it's layed out or
     * whenever the DOM changes. This is where the object should reset its state
     * based on element children or attributes and possibly change its preferred
     * size if appropriate.
     */
    fun reset(availableWidth: Int, availableHeight: Int)
}