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
package io.github.remmerw.thor.cobra.html;

import org.eclipse.jdt.annotation.NonNull;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLLinkElement;

import java.awt.Cursor;
import java.net.URL;
import java.util.Optional;

import io.github.remmerw.thor.cobra.html.gui.HtmlPanel;
import io.github.remmerw.thor.cobra.ua.UserAgentContext;

/**
 * The <code>HtmlRendererContext</code> interface must be implemented in order
 * to use the Cobra HTML renderer. An instance of this interface will be called
 * back whenever the renderer needs to perform an action that it is not designed
 * to know how to perform on its own, e.g. opening a browser window or a context
 * menu. In many ways this interface parallers the Javascript
 * <code>Window</code> class (which in reality represents a browser frame, not a
 * window).
 * <p>
 * A simple implementation of this interface is provided in
 * {@link org.cobraparser.html.test.SimpleHtmlRendererContext
 * SimpleHtmlRendererContext}.
 *
 * @see HtmlPanel#setDocument(org.w3c.dom.Document,
 * HtmlRendererContext)
 */
public interface HtmlRendererContext {
    /**
     * Navigates to the location given. Implementations should retrieve the URL
     * content, parse it and render it.
     *
     * @param url    The destination URL.
     * @param target Same as the target attribute in the HTML anchor tag, i.e. _top,
     *               _blank, etc.
     */
    void navigate(@NonNull URL url, String target);

    /**
     * Performs a link click. Implementations should invoke
     * {@link #navigate(URL, String)}.
     *
     * @param linkNode The HTML node that was clicked.
     * @param url      The destination URL.
     * @param target   Same as the target attribute in the HTML anchor tag, i.e. _top,
     *                 _blank, etc.
     */
    void linkClicked(HTMLElement linkNode, @NonNull URL url, String target);

    /**
     * Gets a collection of frames from the document currently in the context.
     */
    HTMLCollection getFrames();

    /**
     * Submits a HTML form. Note that when the the method is "GET", parameters are
     * still expected to be part of <code>formInputs</code>.
     *
     * @param method     The request method, GET or POST.
     * @param action     The destination URL.
     * @param target     Same as the target attribute in the FORM tag, i.e. _blank, _top,
     *                   etc.
     * @param enctype    The encoding type.
     * @param formInputs An array of {@link FormInput} instances.
     */
    void submitForm(String method, @NonNull URL action, String target, String enctype, FormInput[] formInputs);

    /**
     * Creates a {@link BrowserFrame} instance.
     */
    BrowserFrame createBrowserFrame();

    /**
     * Gets the user agent context.
     */
    UserAgentContext getUserAgentContext();

    /**
     * Gets a <code>HtmlObject</code> instance that implements a OBJECT tag from
     * HTML.
     *
     * @param element The DOM element for the object, which may either represent an
     *                OBJECT, EMBED or an APPLET tag.
     * @return Implementations of this method must return <code>null</code> if
     * they have any problems producing a <code>HtmlObject</code>
     * instance. This is particularly true of OBJECT tags, where inner
     * HTML of the tag must be rendered if the OBJECT content cannot be
     * handled.
     */
    HtmlObject getHtmlObject(HTMLElement element);

    /**
     * This method is called when a visual element is middle-clicked.
     *
     * @param element The narrowest element enclosing the mouse location.
     * @param event   The mouse event.
     * @return The method should return true to continue propagating the event, or
     * false to stop propagating it.
     */
    boolean onMiddleClick(HTMLElement element, java.awt.event.MouseEvent event);

    /**
     * This method is called when a visual element is right-clicked.
     *
     * @param element The narrowest element enclosing the mouse location.
     * @param event   The mouse event.
     * @return The method should return true to continue propagating the event, or
     * false to stop propagating it.
     */
    boolean onContextMenu(HTMLElement element, java.awt.event.MouseEvent event);

    /**
     * This method is called when there's a mouse click on an element.
     *
     * @param element The narrowest element enclosing the mouse location.
     * @param event   The mouse event.
     * @return The method should return true to continue propagating the event, or
     * false to stop propagating it.
     */
    boolean onMouseClick(HTMLElement element, java.awt.event.MouseEvent event);

    /**
     * This method is called when there's a mouse double-click on an element.
     *
     * @param element The narrowest element enclosing the mouse location.
     * @param event   The mouse event.
     * @return The method should return true to continue propagating the event, or
     * false to stop propagating it.
     */
    boolean onDoubleClick(HTMLElement element, java.awt.event.MouseEvent event);

    /**
     * This method is called when the mouse first hovers over an element.
     *
     * @param element The element that the mouse has just entered.
     * @param event   The mouse event.
     */
    void onMouseOver(HTMLElement element, java.awt.event.MouseEvent event);

    /**
     * This method is called when the mouse no longer hovers a given element.
     *
     * @param element The element that the mouse has just exited.
     * @param event   The mouse event.
     */
    void onMouseOut(HTMLElement element, java.awt.event.MouseEvent event);

    /**
     * This method should return true if and only if image loading needs to be
     * enabled.
     */
    boolean isImageLoadingEnabled();

    // ------ Methods useful for Window implementation:

    /**
     * Opens an alert dialog.
     *
     * @param message Message shown by the dialog.
     */
    void alert(String message);

    /**
     * Goes to the previous page in the browser's history.
     */
    void back();

    /**
     * Relinquishes focus.
     */
    void blur();

    /**
     * Closes the browser window, provided this is allowed for the current
     * context.
     */
    void close();

    /**
     * Opens a confirmation dialog.
     *
     * @param message The message shown by the confirmation dialog.
     * @return True if the user selects YES.
     */
    boolean confirm(String message);

    /**
     * Requests focus for the current window.
     */
    void focus();

    /**
     * Opens a separate browser window and renders a URL.
     *
     * @param absoluteUrl    The URL to be rendered.
     * @param windowName     The name of the new window.
     * @param windowFeatures The features of the new window (same as in Javascript open
     *                       method).
     * @param replace
     * @return A new {@link HtmlRendererContext} instance.
     * @deprecated Use {@link #open(URL, String, String, boolean)} instead.
     */
    @Deprecated
    HtmlRendererContext open(String absoluteUrl, String windowName, String windowFeatures, boolean replace);

    /**
     * Opens a separate browser window and renders a URL.
     *
     * @param url            The URL to be rendered.
     * @param windowName     The name of the new window.
     * @param windowFeatures The features of the new window (same as in Javascript open
     *                       method).
     * @param replace
     * @return A new {@link HtmlRendererContext} instance.
     */
    HtmlRendererContext open(@NonNull URL url, String windowName, String windowFeatures, boolean replace);

    /**
     * Shows a prompt dialog.
     *
     * @param message      The message shown by the dialog.
     * @param inputDefault The default input value.
     * @return The user's input value.
     */
    String prompt(String message, String inputDefault);

    /**
     * Scrolls the client area.
     *
     * @param x Document's x coordinate.
     * @param y Document's y coordinate.
     */
    void scroll(int x, int y);

    /**
     * Scrolls the client area.
     *
     * @param x Horizontal pixels to scroll.
     * @param y Vertical pixels to scroll.
     */
    void scrollBy(int x, int y);

    /**
     * Resizes the window.
     *
     * @param width  The new width.
     * @param height The new height.
     */
    void resizeTo(int width, int height);

    /**
     * Resizes the window.
     *
     * @param byWidth  The number of pixels to resize the width by.
     * @param byHeight The number of pixels to resize the height by.
     */
    void resizeBy(int byWidth, int byHeight);

    /**
     * Gets a value indicating if the window is closed.
     */
    boolean isClosed();

    String getDefaultStatus();

    void setDefaultStatus(String value);

    /**
     * Gets the window name.
     */
    String getName();

    /**
     * Gets the parent of the frame/window in the current context.
     */
    HtmlRendererContext getParent();

    /**
     * Gets the opener of the frame/window in the current context.
     */
    HtmlRendererContext getOpener();

    /**
     * Sets the context that opened the current frame/window.
     *
     * @param opener A {@link HtmlRendererContext}.
     */
    void setOpener(HtmlRendererContext opener);

    /**
     * Gets the window status text.
     */
    String getStatus();

    /**
     * Sets the window status text.
     *
     * @param message A string.
     */
    void setStatus(String message);

    /**
     * Gets the top-most browser frame/window.
     */
    HtmlRendererContext getTop();

    /**
     * It should return true if the link provided has been visited.
     */
    boolean isVisitedLink(HTMLLinkElement link);

    /**
     * Reloads the current document.
     */
    void reload();

    /**
     * Gets the number of pages in the history list.
     */
    int getHistoryLength();

    /**
     * Gets the current URL in history.
     */
    String getCurrentURL();

    /**
     * Gets the next URL in the history.
     */
    Optional<String> getNextURL();

    /**
     * Gets the previous URL in the history.
     */
    Optional<String> getPreviousURL();

    /**
     * Goes forward one page.
     */
    void forward();

    /**
     * Navigates the history according to the given offset.
     *
     * @param offset A positive or negative number. -1 is equivalent to {@link #back()}
     *               . +1 is equivalent to {@link #forward()}.
     */
    void moveInHistory(int offset);

    /**
     * Navigates to a URL in the history list.
     */
    void goToHistoryURL(String url);

    void setCursor(Optional<Cursor> cursorOpt);

    void jobsFinished();

    void setJobFinishedHandler(Runnable runnable);
}
