package io.github.remmerw.thor.css

import org.w3c.dom.css.CSSStyleSheet

/**
 * Interface for communicating the changes to the caller and getting data
 * dynamically from the caller.
 */
interface StyleSheetBridge {
    /**
     * Notifies the listener that the style sheet has been changed.
     *
     * @param styleSheet The style sheet that has changed
     */
    fun notifyStyleSheetChanged(styleSheet: CSSStyleSheet)

    /**
     * @return a list of style sheet associated with the document to which this
     * handler is attached.
     */
    val docStyleSheets: MutableList<JStyleSheetWrapper>?
}
