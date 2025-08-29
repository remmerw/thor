/*
Copyright 1994-2006 The Lobo Project. All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer. Redistributions in binary form must
reproduce the above copyright notice, this list of conditions and the following
disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE LOBO PROJECT ``AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
EVENT SHALL THE FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.github.remmerw.thor.cobra.ua

import io.github.remmerw.thor.cobra.clientlet.ClientletResponse
import io.github.remmerw.thor.cobra.clientlet.ComponentContent
import java.awt.Component
import java.net.MalformedURLException
import java.net.URL
import java.util.Optional
import java.util.Properties

/**
 * Represents a navigator frame. In many ways this interface parallels the
 * JavaScript "Window" object.
 */
interface NavigatorFrame {
    /**
     * Opens a URL in a separate window.
     *
     * @param urlOrPath The absolute URL or file path to open.
     * @throws MalformedURLException
     */
    @Throws(MalformedURLException::class)
    fun open(urlOrPath: String?): NavigatorFrame?

    /**
     * Opens a URL in a separate window.
     *
     * @param url The URL to open.
     */
    fun open(url: URL): NavigatorFrame?

    /**
     * Opens a URL in a separate window using the properties provided.
     *
     * @param url              The URL to open.
     * @param windowProperties Window properties, following Javascript Window.open() conventions.
     * @throws MalformedURLException
     */
    fun open(url: URL, windowProperties: Properties?): NavigatorFrame?

    /**
     * Opens a URL in a separate window.
     *
     * @param windowProperties Window properties, following Javascript Window.open() conventions.
     * @param url              The URL to open.
     * @param method           The request method, e.g. GET.
     * @param pinfo            The URL parameter information.
     */
    fun open(
        url: URL,
        method: String?,
        pinfo: ParameterInfo?,
        windowId: String?,
        windowProperties: Properties?
    ): NavigatorFrame?

    /**
     * Opens a URL in a separate window.
     *
     * @param url    The URL to open.
     * @param method The request method, e.g. GET.
     * @param pinfo  The URL parameter information.
     */
    fun open(url: URL, method: String?, pinfo: ParameterInfo?): NavigatorFrame?

    /**
     * Navigates to a URL in the current frame.
     *
     * @param urlOrPath An *absolute* URL or file path.
     */
    @Throws(MalformedURLException::class)
    fun navigate(urlOrPath: String?)

    /**
     * Navigates to a URL in the current frame.
     *
     * @param urlOrPath   An *absolute* URL or file path.
     * @param requestType The request type.
     */
    @Throws(MalformedURLException::class)
    fun navigate(urlOrPath: String?, requestType: RequestType?)

    /**
     * Navigates to a URL in the current frame.
     *
     * @param url An absolute URL.
     */
    fun navigate(url: URL)

    /**
     * Navigates to a URL in the current frame.
     *
     * @param url         An absolute URL.
     * @param requestType The request type.
     */
    fun navigate(url: URL, requestType: RequestType?)

    /**
     * Navigates to a URL in the current frame.
     *
     * @param url         An absolute or relative URL.
     * @param method      The request method.
     * @param paramInfo   The request parameters.
     * @param targetType  The frame target type.
     * @param requestType The request type.
     */
    fun navigate(
        url: URL,
        method: String?,
        paramInfo: ParameterInfo?,
        targetType: TargetType?,
        requestType: RequestType?
    )

    /**
     * Navigates to a URL in the current frame. This method should be used when
     * the originating frame of the request differs from the target frame.
     *
     * @param url              An absolute or relative URL.
     * @param method           The request method.
     * @param paramInfo        The request parameters.
     * @param targetType       The frame target type.
     * @param requestType      The request type.
     * @param originatingFrame The frame where the request originated.
     */
    fun navigate(
        url: URL,
        method: String?,
        paramInfo: ParameterInfo?,
        targetType: TargetType?,
        requestType: RequestType?,
        originatingFrame: NavigatorFrame?
    )

    /**
     * Similar to
     * [.navigate]
     * , except this method should be called when navigation is triggered by a
     * user click.
     *
     * @param url        An absolute or relative URL.
     * @param targetType The frame target type.
     * @param linkObject An implementation-dependent object representing what was clicked.
     * For example, in HTML documents the `linkObject` might
     * be of type `org.w3c.dom.html2.HTMLElement`.
     */
    fun linkClicked(url: URL, targetType: TargetType?, linkObject: Any?)

    /**
     * Closes the current window, if allowed.
     *
     * @throws SecurityException If closing the window is now allowed in the current context.
     */
    fun closeWindow()

    /**
     * Executes a task later in the event dispatch thread.
     */
    fun invokeLater(runnable: Runnable?)

    /**
     * Sends the window of this clientlet context to the back and may cause it to
     * lose focus.
     */
    fun windowToBack()

    /**
     * Sends the window of this clientlet context to the front and may cause it to
     * request focus.
     */
    fun windowToFront()

    /**
     * Opens a Yes/No confirmation dialog.
     *
     * @param message The question text.
     * @return True only if Yes is selected.
     */
    fun confirm(message: String?): Boolean

    /**
     * Opens a prompt dialog.
     *
     * @param message      The question text.
     * @param inputDefault The default prompt value.
     * @return The text entered by the user.
     */
    fun prompt(message: String?, inputDefault: String?): String?

    /**
     * Gets a component for the current frame which could be serve as the parent
     * of pop-up dialogs. This is *not* the rendered component.
     *
     * @see .getComponentContent
     */
    val component: Component?

    /**
     * Opens an alert dialog.
     *
     * @param message The message shown in the alert dialog.
     */
    fun alert(message: String?)

    /**
     * Gets the most recent progress event.
     *
     * @see .setProgressEvent
     */
    /**
     * Requests the frame to update its progress state.
     *
     * @param event The progress event object.
     * @see .getProgressEvent
     */
    var progressEvent: NavigatorProgressEvent?

    /**
     * Gets the frame that contains the current one, if any. Returns
     * `null` for the top frame.
     */
    val parentFrame: NavigatorFrame?

    /**
     * Gets the top-most frame in the window. Returns the current frame if its
     * parent is `null`.
     */
    val topFrame: NavigatorFrame?

    // (commenting out - gives opportunity to retain objects)
    // public void setItem(String name, Object value);
    // public Object getItem(String name);
    fun back(): Boolean

    fun forward(): Boolean

    fun canForward(): Boolean

    fun canBack(): Boolean

    fun createFrame(): NavigatorFrame?

    var defaultStatus: String?

    val windowId: String?

    val openerFrame: NavigatorFrame?

    var status: String?

    val isWindowClosed: Boolean

    fun reload()

    /**
     * Replaces the content of the frame.
     *
     * @throws java.security.SecurityException Thrown when the caller does not have permission to replace the
     * content of the frame.
     * @see ClientletContext.setResultingContent
     */
    fun replaceContent(response: ClientletResponse?, component: ComponentContent?)

    /**
     * A simple alternative to
     * [.replaceContent]
     * provided for convenience.
     *
     * @param component
     * A AWT or Swing component.
     */
    /*
  public void replaceContent(Component component);
  */
    /**
     * Gets source code for content currently showing, if any.
     *
     * @throws java.security.SecurityException Thrown when the caller does not have permission to get the source
     * code.
     */
    val sourceCode: String?

    /**
     * Creates a [NetworkRequest] object that can be used to load data over
     * HTTP and other network protocols.
     */
    fun createNetworkRequest(): NetworkRequest?

    /**
     * Gets the component content currently set in the frame.
     */
    val componentContent: ComponentContent?

    /**
     * Resizes the browser window.
     *
     * @param width  The new window width.
     * @param height The new window height.
     */
    fun resizeWindowTo(width: Int, height: Int)

    /**
     * Resizes the browser window.
     *
     * @param byWidth  The number of pixels to expand the width by.
     * @param byHeight The number of pixels to expand the height by.
     */
    fun resizeWindowBy(byWidth: Int, byHeight: Int)

    /**
     * Gets an object that represents the current navigation entry in the frame's
     * history.
     */
    val currentNavigationEntry: NavigationEntry?

    val previousNavigationEntry: Optional<NavigationEntry>?

    val nextNavigationEntry: Optional<NavigationEntry>?

    /**
     * Switches to a new navigation entry in the frame's history, according to the
     * given offset.
     *
     * @param offset A positive or negative number, where -1 is equivalent to
     * [.back] and +1 is equivalent to [.forward].
     */
    fun moveInHistory(offset: Int)

    /**
     * Navigates to a URL that exists in the frame's history.
     *
     * @param absoluteURL The target URL.
     */
    fun navigateInHistory(absoluteURL: String?)

    /**
     * Gets the length for the frame's history.
     */
    val historyLength: Int

    /**
     * Sets an implementation-dependent property of the underlying component
     * currently rendered. For example, a Cobra-based HTML component accepts
     * properties such as `defaultMarginInsets` (java.awt.Inset),
     * `defaultOverflowX` and `defaultOverflowY`.
     *
     * @param name  The name of the property.
     * @param value The value of the property. The type of the value depends on the
     * property and the underlying implementation.
     */
    fun setProperty(name: String?, value: Any?)

    fun isRequestPermitted(request: UserAgentContext.Request?): Boolean

    fun manageRequests(initiator: Any?)

    fun allowAllFirstPartyRequests()
}
