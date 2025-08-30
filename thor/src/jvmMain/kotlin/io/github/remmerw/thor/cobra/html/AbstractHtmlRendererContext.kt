/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The XAMJ Project

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
package io.github.remmerw.thor.cobra.html

import io.github.remmerw.thor.cobra.ua.UserAgentContext
import org.w3c.dom.html.HTMLCollection
import org.w3c.dom.html.HTMLElement
import org.w3c.dom.html.HTMLLinkElement
import java.awt.event.MouseEvent
import java.net.URL
import java.util.Optional

/**
 * Abstract implementation of the [HtmlRendererContext] interface with
 * blank methods, provided for developer convenience.
 */
abstract class AbstractHtmlRendererContext : HtmlRendererContext {
    override fun alert(message: String?) {
    }

    override fun back() {
    }

    override fun blur() {
    }

    override fun close() {
    }

    override fun confirm(message: String?): Boolean {
        return false
    }

    override fun createBrowserFrame(): BrowserFrame? {
        return null
    }

    override fun focus() {
    }

    fun getDefaultStatus(): String? {
        return null
    }

    fun setDefaultStatus(value: String?) {
    }

    override fun frames(): HTMLCollection? {
        return null
    }

    override fun getHtmlObject(element: HTMLElement?): HtmlObject? {
        return null
    }

    fun getName(): String? {
        return null
    }

    fun getOpener(): HtmlRendererContext? {
        return null
    }

    fun setOpener(opener: HtmlRendererContext?) {
    }

    fun getParent(): HtmlRendererContext? {
        return null
    }

    fun getStatus(): String? {
        return null
    }

    fun setStatus(message: String?) {
    }

    fun getTop(): HtmlRendererContext? {
        return null
    }

    fun getUserAgentContext(): UserAgentContext? {
        return null
    }

    /*
  public void linkClicked(final HTMLElement linkNode, final @NonNull URL url, final String target) {
  }

  public void navigate(final URL url, final String target) {
  }*/
    /**
     * Returns false unless overridden.
     */
    override fun isClosed(): Boolean {
        return false
    }

    /**
     * Returns true unless overridden.
     */
    override fun isImageLoadingEnabled(): Boolean {
        return true
    }

    /**
     * Returns false unless overridden.
     */
    override fun isVisitedLink(link: HTMLLinkElement?): Boolean {
        return false
    }

    /**
     * Returns true unless overridden.
     */
    override fun onContextMenu(element: HTMLElement?, event: MouseEvent?): Boolean {
        return true
    }

    override fun onMouseOut(element: HTMLElement?, event: MouseEvent?) {
    }

    override fun onMouseOver(element: HTMLElement?, event: MouseEvent?) {
    }

    @Deprecated("Use {@link #open(URL, String, String, boolean)} instead.")
    override fun open(
        absoluteUrl: String?,
        windowName: String?,
        windowFeatures: String?,
        replace: Boolean
    ): HtmlRendererContext? {
        return null
    }

    override fun open(
        url: URL,
        windowName: String?,
        windowFeatures: String?,
        replace: Boolean
    ): HtmlRendererContext? {
        return null
    }

    override fun prompt(message: String?, inputDefault: String?): String? {
        return null
    }

    override fun reload() {
    }

    override fun scroll(x: Int, y: Int) {
    }

    /*
  public void submitForm(final String method, final @NonNull URL action, final String target, final String enctype, final FormInput[] formInputs) {
  }
  */
    /**
     * Returns true unless overridden.
     */
    override fun onDoubleClick(element: HTMLElement?, event: MouseEvent?): Boolean {
        return true
    }

    /**
     * Returns true unless overridden.
     */
    override fun onMouseClick(element: HTMLElement?, event: MouseEvent?): Boolean {
        return true
    }

    override fun scrollBy(x: Int, y: Int) {
    }

    override fun resizeBy(byWidth: Int, byHeight: Int) {
    }

    override fun resizeTo(width: Int, height: Int) {
    }

    override fun forward() {
    }

    fun getCurrentURL(): String? {
        return null
    }

    fun getHistoryLength(): Int {
        return 0
    }

    fun getNextURL(): Optional<String> {
        return Optional.empty<String>()
    }

    fun getPreviousURL(): Optional<String?>? {
        return null
    }

    override fun goToHistoryURL(url: String?) {
    }

    override fun moveInHistory(offset: Int) {
    }
}
