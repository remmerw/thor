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
 * Created on Aug 28, 2005
 */
package io.github.remmerw.thor.cobra.html

import io.github.remmerw.thor.cobra.ua.UserAgentContext
import org.w3c.dom.html.HTMLCollection
import org.w3c.dom.html.HTMLElement
import org.w3c.dom.html.HTMLLinkElement
import java.awt.Cursor
import java.awt.event.MouseEvent
import java.net.URL
import java.util.Optional

/**
 * The `HtmlRendererContext` interface must be implemented in order
 * to use the Cobra HTML renderer. An instance of this interface will be called
 * back whenever the renderer needs to perform an action that it is not designed
 * to know how to perform on its own, e.g. opening a browser window or a context
 * menu. In many ways this interface parallers the Javascript
 * `Window` class (which in reality represents a browser frame, not a
 * window).
 *
 *
 * A simple implementation of this interface is provided in
 * [ SimpleHtmlRendererContext][org.cobraparser.html.test.SimpleHtmlRendererContext].
 *
 * @see HtmlPanel.setDocument
 */
interface HtmlRendererContext {
    /**
     * Navigates to the location given. Implementations should retrieve the URL
     * content, parse it and render it.
     *
     * @param url    The destination URL.
     * @param target Same as the target attribute in the HTML anchor tag, i.e. _top,
     * _blank, etc.
     */
    fun navigate(url: URL, target: String?)

    /**
     * Performs a link click. Implementations should invoke
     * [.navigate].
     *
     * @param linkNode The HTML node that was clicked.
     * @param url      The destination URL.
     * @param target   Same as the target attribute in the HTML anchor tag, i.e. _top,
     * _blank, etc.
     */
    fun linkClicked(linkNode: HTMLElement?, url: URL, target: String?)

    /**
     * Gets a collection of frames from the document currently in the context.
     */
    val frames: HTMLCollection?

    /**
     * Submits a HTML form. Note that when the the method is "GET", parameters are
     * still expected to be part of `formInputs`.
     *
     * @param method     The request method, GET or POST.
     * @param action     The destination URL.
     * @param target     Same as the target attribute in the FORM tag, i.e. _blank, _top,
     * etc.
     * @param enctype    The encoding type.
     * @param formInputs An array of [FormInput] instances.
     */
    fun submitForm(
        method: String?,
        action: URL,
        target: String?,
        enctype: String?,
        formInputs: Array<FormInput?>?
    )

    /**
     * Creates a [BrowserFrame] instance.
     */
    fun createBrowserFrame(): BrowserFrame?

    /**
     * Gets the user agent context.
     */

    val userAgentContext: UserAgentContext

    /**
     * Gets a `HtmlObject` instance that implements a OBJECT tag from
     * HTML.
     *
     * @param element The DOM element for the object, which may either represent an
     * OBJECT, EMBED or an APPLET tag.
     * @return Implementations of this method must return `null` if
     * they have any problems producing a `HtmlObject`
     * instance. This is particularly true of OBJECT tags, where inner
     * HTML of the tag must be rendered if the OBJECT content cannot be
     * handled.
     */
    fun getHtmlObject(element: HTMLElement?): HtmlObject?

    /**
     * This method is called when a visual element is middle-clicked.
     *
     * @param element The narrowest element enclosing the mouse location.
     * @param event   The mouse event.
     * @return The method should return true to continue propagating the event, or
     * false to stop propagating it.
     */
    fun onMiddleClick(element: HTMLElement?, event: MouseEvent?): Boolean

    /**
     * This method is called when a visual element is right-clicked.
     *
     * @param element The narrowest element enclosing the mouse location.
     * @param event   The mouse event.
     * @return The method should return true to continue propagating the event, or
     * false to stop propagating it.
     */
    fun onContextMenu(element: HTMLElement?, event: MouseEvent?): Boolean

    /**
     * This method is called when there's a mouse click on an element.
     *
     * @param element The narrowest element enclosing the mouse location.
     * @param event   The mouse event.
     * @return The method should return true to continue propagating the event, or
     * false to stop propagating it.
     */
    fun onMouseClick(element: HTMLElement?, event: MouseEvent?): Boolean

    /**
     * This method is called when there's a mouse double-click on an element.
     *
     * @param element The narrowest element enclosing the mouse location.
     * @param event   The mouse event.
     * @return The method should return true to continue propagating the event, or
     * false to stop propagating it.
     */
    fun onDoubleClick(element: HTMLElement?, event: MouseEvent?): Boolean

    /**
     * This method is called when the mouse first hovers over an element.
     *
     * @param element The element that the mouse has just entered.
     * @param event   The mouse event.
     */
    fun onMouseOver(element: HTMLElement?, event: MouseEvent?)

    /**
     * This method is called when the mouse no longer hovers a given element.
     *
     * @param element The element that the mouse has just exited.
     * @param event   The mouse event.
     */
    fun onMouseOut(element: HTMLElement?, event: MouseEvent?)

    /**
     * This method should return true if and only if image loading needs to be
     * enabled.
     */
    val isImageLoadingEnabled: Boolean

    // ------ Methods useful for Window implementation:
    /**
     * Opens an alert dialog.
     *
     * @param message Message shown by the dialog.
     */
    fun alert(message: String?)

    /**
     * Goes to the previous page in the browser's history.
     */
    fun back()

    /**
     * Relinquishes focus.
     */
    fun blur()

    /**
     * Closes the browser window, provided this is allowed for the current
     * context.
     */
    fun close()

    /**
     * Opens a confirmation dialog.
     *
     * @param message The message shown by the confirmation dialog.
     * @return True if the user selects YES.
     */
    fun confirm(message: String?): Boolean

    /**
     * Requests focus for the current window.
     */
    fun focus()

    /**
     * Opens a separate browser window and renders a URL.
     *
     * @param absoluteUrl    The URL to be rendered.
     * @param windowName     The name of the new window.
     * @param windowFeatures The features of the new window (same as in Javascript open
     * method).
     * @param replace
     * @return A new [HtmlRendererContext] instance.
     */
    @Deprecated("Use {@link #open(URL, String, String, boolean)} instead.")
    fun open(
        absoluteUrl: String?,
        windowName: String?,
        windowFeatures: String?,
        replace: Boolean
    ): HtmlRendererContext?

    /**
     * Opens a separate browser window and renders a URL.
     *
     * @param url            The URL to be rendered.
     * @param windowName     The name of the new window.
     * @param windowFeatures The features of the new window (same as in Javascript open
     * method).
     * @param replace
     * @return A new [HtmlRendererContext] instance.
     */
    fun open(
        url: URL,
        windowName: String?,
        windowFeatures: String?,
        replace: Boolean
    ): HtmlRendererContext?

    /**
     * Shows a prompt dialog.
     *
     * @param message      The message shown by the dialog.
     * @param inputDefault The default input value.
     * @return The user's input value.
     */
    fun prompt(message: String?, inputDefault: String?): String?

    /**
     * Scrolls the client area.
     *
     * @param x Document's x coordinate.
     * @param y Document's y coordinate.
     */
    fun scroll(x: Int, y: Int)

    /**
     * Scrolls the client area.
     *
     * @param x Horizontal pixels to scroll.
     * @param y Vertical pixels to scroll.
     */
    fun scrollBy(x: Int, y: Int)

    /**
     * Resizes the window.
     *
     * @param width  The new width.
     * @param height The new height.
     */
    fun resizeTo(width: Int, height: Int)

    /**
     * Resizes the window.
     *
     * @param byWidth  The number of pixels to resize the width by.
     * @param byHeight The number of pixels to resize the height by.
     */
    fun resizeBy(byWidth: Int, byHeight: Int)

    /**
     * Gets a value indicating if the window is closed.
     */
    val isClosed: Boolean

    var defaultStatus: String?

    /**
     * Gets the window name.
     */
    val name: String?

    /**
     * Gets the parent of the frame/window in the current context.
     */
    val parent: HtmlRendererContext?

    /**
     * Gets the opener of the frame/window in the current context.
     */
    /**
     * Sets the context that opened the current frame/window.
     *
     * @param opener A [HtmlRendererContext].
     */
    var opener: HtmlRendererContext?

    /**
     * Gets the window status text.
     */
    /**
     * Sets the window status text.
     *
     * @param message A string.
     */
    var status: String?

    /**
     * Gets the top-most browser frame/window.
     */
    val top: HtmlRendererContext?

    /**
     * It should return true if the link provided has been visited.
     */
    fun isVisitedLink(link: HTMLLinkElement?): Boolean

    /**
     * Reloads the current document.
     */
    fun reload()

    /**
     * Gets the number of pages in the history list.
     */
    val historyLength: Int

    /**
     * Gets the current URL in history.
     */
    val currentURL: String?

    /**
     * Gets the next URL in the history.
     */
    val nextURL: Optional<String?>?

    /**
     * Gets the previous URL in the history.
     */
    val previousURL: Optional<String?>?

    /**
     * Goes forward one page.
     */
    fun forward()

    /**
     * Navigates the history according to the given offset.
     *
     * @param offset A positive or negative number. -1 is equivalent to [.back]
     * . +1 is equivalent to [.forward].
     */
    fun moveInHistory(offset: Int)

    /**
     * Navigates to a URL in the history list.
     */
    fun goToHistoryURL(url: String?)

    fun setCursor(cursorOpt: Optional<Cursor>)

    fun jobsFinished()

    fun setJobFinishedHandler(runnable: Runnable?)
}
