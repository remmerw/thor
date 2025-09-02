package io.github.remmerw.thor.model

import io.github.remmerw.thor.dom.FormInput
import org.w3c.dom.html.HTMLCollection
import org.w3c.dom.html.HTMLElement
import org.w3c.dom.html.HTMLLinkElement
import java.net.URL
import java.util.Optional

/**
 * The `RendererContext` interface must be implemented in order
 * to use the Cobra HTML renderer. An instance of this interface will be called
 * back whenever the renderer needs to perform an action that it is not designed
 * to know how to perform on its own, e.g. opening a browser window or a context
 * menu. In many ways this interface parallers the Javascript
 * `Window` class (which in reality represents a browser frame, not a
 * window).

 */
interface RendererContext {
    /**
     * Navigates to the location given. Implementations should retrieve the URL
     * content, parse it and render it.
     *
     * @param url    The destination URL.
     * @param target Same as the target attribute in the HTML anchor tag, i.e. _top,
     * _blank, etc.
     */
    fun navigate(url: URL, target: String?)

    fun warn(message: String, err: Throwable?)

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
    fun frames(): HTMLCollection?

    /**
     * Submits a HTML form. Note that when the the method is "GET", parameters are
     * still expected to be part of `formInputs`.
     *
     * @param method     The request method, GET or POST.
     * @param action     The destination URL.
     * @param target     Same as the target attribute in the FORM tag, i.e. _blank, _top,
     * etc.
     * @param enctype    The encoding type.
     * @param formInputs An array of [io.github.remmerw.thor.dom.FormInput] instances.
     */
    fun submitForm(
        method: String?,
        action: URL,
        target: String?,
        enctype: String?,
        formInputs: Array<FormInput>
    )



    /**
     * This method should return true if and only if image loading needs to be
     * enabled.
     */
    fun isImageLoadingEnabled(): Boolean

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
     * @return A new [RendererContext] instance.
     */
    @Deprecated("Use {@link #open(URL, String, String, boolean)} instead.")
    fun open(
        absoluteUrl: String?,
        windowName: String?,
        windowFeatures: String?,
        replace: Boolean
    ): RendererContext?

    /**
     * Opens a separate browser window and renders a URL.
     *
     * @param url            The URL to be rendered.
     * @param windowName     The name of the new window.
     * @param windowFeatures The features of the new window (same as in Javascript open
     * method).
     * @param replace
     * @return A new [RendererContext] instance.
     */
    fun open(
        url: URL,
        windowName: String?,
        windowFeatures: String?,
        replace: Boolean
    ): RendererContext?

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
    fun isClosed(): Boolean

    fun defaultStatus(): String?

    /**
     * Gets the window name.
     */
    fun name(): String?

    /**
     * Gets the parent of the frame/window in the current context.
     */
    fun parent(): RendererContext?


    fun opener(): RendererContext?

    /**
     * Sets the window status text.
     */
    fun status(): String?

    /**
     * Gets the top-most browser frame/window.
     */
    fun top(): RendererContext?

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
    fun historyLength(): Int

    /**
     * Gets the current URL in history.
     */
    fun currentURL(): String?

    /**
     * Gets the next URL in the history.
     */
    fun nextURL(): Optional<String>?

    /**
     * Gets the previous URL in the history.
     */
    fun previousURL(): Optional<String>?

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


}