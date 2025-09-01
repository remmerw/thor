package io.github.remmerw.thor.cobra.html.dom

import io.github.remmerw.thor.cobra.html.BrowserFrame

/**
 * Tag interface for frame nodes.
 */
interface FrameNode {

    fun getBrowserFrame(): BrowserFrame?

    fun setBrowserFrame(frame: BrowserFrame?)
}
