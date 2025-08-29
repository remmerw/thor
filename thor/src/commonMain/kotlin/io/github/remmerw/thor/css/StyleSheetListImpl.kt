package io.github.remmerw.thor.css

import org.w3c.dom.css.CSSStyleSheet
import org.w3c.dom.stylesheets.StyleSheetList

internal class StyleSheetListImpl(private val bridge: StyleSheetBridge) : StyleSheetList {
    /**
     * @return The number of `style sheets` in the list. The range of
     * valid child stylesheet indices is `0` to
     * `length-1` inclusive.
     */
    override fun getLength(): Int {
        return this.bridge.docStyleSheets!!.size
    }

    /**
     * Used to retrieve a style sheet by ordinal index. If index is greater than
     * or equal to the number of style sheets in the list, this returns
     * `null`.
     *
     * @param index Index into the collection
     * @return The style sheet at the `index` position in the
     * `StyleSheetList`, or `null` if that is not a
     * valid index.
     */
    override fun item(index: Int): CSSStyleSheet? {
        return this.bridge.docStyleSheets!![index]
    }
}
