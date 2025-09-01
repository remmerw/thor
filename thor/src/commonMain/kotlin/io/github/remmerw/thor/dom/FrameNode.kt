package io.github.remmerw.thor.dom


/**
 * Tag interface for frame nodes.
 */
interface FrameNode {

    fun getBrowserFrame(): BrowserFrame?

    fun setBrowserFrame(frame: BrowserFrame?)
}
