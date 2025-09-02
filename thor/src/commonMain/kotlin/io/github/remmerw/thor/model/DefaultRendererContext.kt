package io.github.remmerw.thor.model

import io.github.remmerw.thor.dom.FormInput
import io.github.remmerw.thor.ua.UserAgentContext
import org.w3c.dom.html.HTMLCollection
import org.w3c.dom.html.HTMLElement
import org.w3c.dom.html.HTMLLinkElement
import java.net.URL
import java.util.Optional

class DefaultRendererContext : RendererContext {
    override fun navigate(url: URL, target: String?) {
        TODO("Not yet implemented")
    }

    override fun linkClicked(
        linkNode: HTMLElement?,
        url: URL,
        target: String?
    ) {
        TODO("Not yet implemented")
    }

    override fun frames(): HTMLCollection? {
        TODO("Not yet implemented")
    }

    override fun submitForm(
        method: String?,
        action: URL,
        target: String?,
        enctype: String?,
        formInputs: Array<FormInput?>?
    ) {
        TODO("Not yet implemented")
    }

    override fun userAgentContext(): UserAgentContext {
        TODO("Not yet implemented")
    }

    override fun isImageLoadingEnabled(): Boolean {
        return true
    }

    override fun alert(message: String?) {
        TODO("Not yet implemented")
    }

    override fun back() {
        TODO("Not yet implemented")
    }

    override fun blur() {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun confirm(message: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun focus() {
        TODO("Not yet implemented")
    }

    override fun open(
        absoluteUrl: String?,
        windowName: String?,
        windowFeatures: String?,
        replace: Boolean
    ): RendererContext? {
        TODO("Not yet implemented")
    }

    override fun open(
        url: URL,
        windowName: String?,
        windowFeatures: String?,
        replace: Boolean
    ): RendererContext? {
        TODO("Not yet implemented")
    }

    override fun prompt(message: String?, inputDefault: String?): String? {
        TODO("Not yet implemented")
    }

    override fun scroll(x: Int, y: Int) {
        TODO("Not yet implemented")
    }

    override fun scrollBy(x: Int, y: Int) {
        TODO("Not yet implemented")
    }

    override fun resizeTo(width: Int, height: Int) {
        TODO("Not yet implemented")
    }

    override fun resizeBy(byWidth: Int, byHeight: Int) {
        TODO("Not yet implemented")
    }

    override fun isClosed(): Boolean {
        TODO("Not yet implemented")
    }

    override fun defaultStatus(): String? {
        TODO("Not yet implemented")
    }

    override fun name(): String? {
        TODO("Not yet implemented")
    }

    override fun parent(): RendererContext? {
        TODO("Not yet implemented")
    }

    override fun opener(): RendererContext? {
        TODO("Not yet implemented")
    }

    override fun status(): String? {
        TODO("Not yet implemented")
    }

    override fun top(): RendererContext? {
        TODO("Not yet implemented")
    }

    override fun isVisitedLink(link: HTMLLinkElement?): Boolean {
       return true
    }

    override fun reload() {
        TODO("Not yet implemented")
    }

    override fun historyLength(): Int {
        TODO("Not yet implemented")
    }

    override fun currentURL(): String? {
        TODO("Not yet implemented")
    }

    override fun nextURL(): Optional<String>? {
        TODO("Not yet implemented")
    }

    override fun previousURL(): Optional<String>? {
        TODO("Not yet implemented")
    }

    override fun forward() {
        TODO("Not yet implemented")
    }

    override fun moveInHistory(offset: Int) {
        TODO("Not yet implemented")
    }

    override fun goToHistoryURL(url: String?) {
        TODO("Not yet implemented")
    }

    override fun jobsFinished() {
        TODO("Not yet implemented")
    }

    override fun setJobFinishedHandler(runnable: Runnable?) {
        TODO("Not yet implemented")
    }
}