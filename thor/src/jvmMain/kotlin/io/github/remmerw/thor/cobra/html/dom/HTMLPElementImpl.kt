package io.github.remmerw.thor.cobra.html.dom

import org.w3c.dom.html.HTMLParagraphElement

class HTMLPElementImpl(name: String) : HTMLAbstractUIElement(name), HTMLParagraphElement {
    override fun getAlign(): String? {
        return this.getAttribute("align")
    }

    override fun setAlign(align: String?) {
        this.setAttribute("align", align)
    }

    override fun appendInnerTextImpl(buffer: StringBuffer) {
        val length = buffer.length
        var lineBreaks: Int
        if (length == 0) {
            lineBreaks = 2
        } else {
            var start = length - 4
            if (start < 0) {
                start = 0
            }
            lineBreaks = 0
            for (i in start..<length) {
                val ch = buffer.get(i)
                if (ch == '\n') {
                    lineBreaks++
                }
            }
        }
        for (i in 0..<(2 - lineBreaks)) {
            buffer.append("\r\n")
        }
        super.appendInnerTextImpl(buffer)
        buffer.append("\r\n\r\n")
    }
}
