package io.github.remmerw.thor.cobra.html.domimpl;

import io.github.remmerw.thor.cobra.html.BrowserFrame;

/**
 * Tag interface for frame nodes.
 */
public interface FrameNode {
    BrowserFrame getBrowserFrame();

    void setBrowserFrame(BrowserFrame frame);
}
