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
package io.github.remmerw.thor.cobra.clientlet

import io.github.remmerw.thor.cobra.io.ManagedStore
import io.github.remmerw.thor.cobra.ua.NavigatorFrame
import io.github.remmerw.thor.cobra.ua.NavigatorProgressEvent
import io.github.remmerw.thor.cobra.ua.ProgressType
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.MalformedURLException
import java.net.URL
import java.util.Properties

/**
 * The context in which a clientlet processes a web or file response.
 *
 * @see ClientletAccess.getCurrentClientletContext
 */
interface ClientletContext {
    /**
     * Sets a data item for later retrieval.
     *
     * @param name  The item name.
     * @param value The item value.
     */
    fun setItem(name: String?, value: Any?)

    /**
     * Gets a data item.
     *
     * @param name The item name.
     * @return The item value.
     */
    fun getItem(name: String?): Any?

    /**
     * Gets the clientlet request.
     */
    val request: ClientletRequest?

    /**
     * Gets the clientlet response.
     */
    val response: ClientletResponse?

    /**
     * Gets a UserAgent instance with information about the current user agent.
     */
    val userAgent: UserAgent?

    /**
     * Undocumented.
     */
    fun createContentBuffer(contentType: String?, content: ByteArray?): ContentBuffer?

    /**
     * Undocumented.
     */
    @Throws(UnsupportedEncodingException::class)
    fun createContentBuffer(
        contentType: String?,
        content: String?,
        encoding: String?
    ): ContentBuffer?

    @JvmField
    @get:Throws(IOException::class)
    val managedStore: ManagedStore?

    /**
     * Gets a managed store instance (a small file system restricted by a quota)
     * for the host name provided.
     *
     * @param hostName A host whose cookies the caller is allowed to access. For example,
     * if the response host name is `test.acme.com`, then the
     * `hostName` parameter can be `acme.com` but
     * not `com`.
     * @throws java.security.SecurityException If the caller doesn't have access to the managed store for the
     * host given.
     */
    @Throws(IOException::class)
    fun getManagedStore(hostName: String?): ManagedStore?

    /**
     * Gets the frame interface associated with this context.
     */
    val navigatorFrame: NavigatorFrame?

    /**
     * Gets content previously set with [.setResultingContent].
     * The return value may be `null`.
     */
    /**
     * After processing a response a clientlet should invoke this method to set
     * displayable frame content.
     */
    var resultingContent: ComponentContent?

    /**
     * A simple alternative to [.setResultingContent]
     * provided for convenience. It does not set any properties such as title or
     * source code.
     */
    /*
  public void setResultingContent(java.awt.Component content, final URL url);
  */

    /**
     * Navigates to the URI provided, which may be absolute or relative to the
     * response URL.
     *
     * @param uri The target URI.
     * @see NavigatorFrame.navigate
     */
    @Throws(MalformedURLException::class)
    fun navigate(uri: String?)

    /**
     * For documents requested in order to open a new window, this method may be
     * invoked to override window properties. To take effect, this method should
     * be invoked before content is set.
     *
     * @param properties A properties object following JavaScript Window.open()
     * conventions.
     */
    fun overrideWindowProperties(properties: Properties?)

    /**
     * Gets window properties previously set with
     * [.overrideWindowProperties].
     */
    val overriddingWindowProperties: Properties?

    /**
     * Returns `true` if resulting content has already been set with
     * [.setResultingContent].
     */
    val isResultingContentSet: Boolean

    /**
     * Requests the frame to update its progress bar if any.
     *
     * @param progressType The type of progress action.
     * @param value        The current progress value.
     * @param max          The maximum progress value, which may be `-1` to
     * indicate it is unknown.
     * @see NavigatorFrame.setProgressEvent
     */
    fun setProgressEvent(progressType: ProgressType?, value: Int, max: Int)

    /**
     * Requests the frame to update its progress bar if any.
     *
     * @param progressType The type of progress action.
     * @param value        The current progress value.
     * @param max          The maximum progress value, which may be `-1` to
     * indicate it is unknown.
     * @param url          The URL to be shown in progress messages.
     * @see NavigatorFrame.setProgressEvent
     */
    fun setProgressEvent(progressType: ProgressType?, value: Int, max: Int, url: URL)

    /**
     * Gets the progress event most recently set.
     *
     * @see .setProgressEvent
     * @see NavigatorFrame.setProgressEvent
     */
    /**
     * Sets the current progress state.
     *
     * @param event The progress event.
     * @see NavigatorFrame.setProgressEvent
     * @see .getProgressEvent
     */
    var progressEvent: NavigatorProgressEvent?

    /**
     * Opens an alert message dialog.
     *
     * @param message An alert message.
     */
    fun alert(message: String?)

    /**
     * Creates a lose navigator frame that may be added to GUI components.
     *
     * @see NavigatorFrame.getComponent
     * @see NavigatorFrame.navigate
     */
    fun createNavigatorFrame(): NavigatorFrame?
}
