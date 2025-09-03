package io.github.remmerw.thor.dom

import io.github.remmerw.thor.dom.HTMLElementBuilder.Anchor
import io.github.remmerw.thor.dom.HTMLElementBuilder.BaseFont
import io.github.remmerw.thor.dom.HTMLElementBuilder.Frameset
import io.github.remmerw.thor.dom.HTMLElementBuilder.Heading
import io.github.remmerw.thor.dom.HTMLElementBuilder.IFrame
import io.github.remmerw.thor.dom.HTMLElementBuilder.Img
import io.github.remmerw.thor.dom.HTMLElementBuilder.NonStandard
import io.github.remmerw.thor.dom.HTMLElementBuilder.Pre
import io.github.remmerw.thor.dom.HTMLElementBuilder.Quote
import io.github.remmerw.thor.dom.HTMLElementBuilder.Textarea
import org.w3c.dom.DOMException
import org.w3c.dom.html.HTMLElement

internal class ElementFactory private constructor() {
    private val builders: MutableMap<String, HTMLElementBuilder> = HashMap(80)

    init {
        // This sets up builders for each known element tag.
        val builders = this.builders
        builders.put("HTML", HTMLElementBuilder.Html())
        builders.put("TITLE", HTMLElementBuilder.Title())
        builders.put("BASE", HTMLElementBuilder.Base())

        val div: HTMLElementBuilder = HTMLElementBuilder.Div()
        builders.put("DIV", div)
        builders.put("DL", div)

        builders.put("BODY", HTMLElementBuilder.Body())
        builders.put("PRE", Pre())
        builders.put("P", HTMLElementBuilder.P())

        val qb: HTMLElementBuilder = Quote()
        builders.put("BLOCKQUOTE", qb)
        builders.put("Q", qb)

        builders.put("SPAN", HTMLElementBuilder.Span())
        builders.put("SCRIPT", HTMLElementBuilder.Script())
        builders.put("IMG", Img())
        builders.put("STYLE", HTMLElementBuilder.Style())
        builders.put("LINK", HTMLElementBuilder.Link())
        builders.put("A", Anchor())
        builders.put("ANCHOR", Anchor())
        builders.put("TABLE", HTMLElementBuilder.Table())
        builders.put("TD", HTMLElementBuilder.Td())
        builders.put("TH", HTMLElementBuilder.Th())
        builders.put("TR", HTMLElementBuilder.Tr())
        builders.put("FORM", HTMLElementBuilder.Form())
        builders.put("INPUT", HTMLElementBuilder.Input())
        builders.put("BUTTON", HTMLElementBuilder.Button())
        builders.put("TEXTAREA", Textarea())
        builders.put("SELECT", HTMLElementBuilder.Select())
        builders.put("OPTION", HTMLElementBuilder.Option())
        builders.put("FRAMESET", Frameset())
        builders.put("FRAME", HTMLElementBuilder.Frame())
        builders.put("IFRAME", IFrame())
        builders.put("UL", HTMLElementBuilder.Ul())
        builders.put("OL", HTMLElementBuilder.Ol())
        builders.put("LI", HTMLElementBuilder.Li())
        builders.put("HR", HTMLElementBuilder.Hr())
        builders.put("BR", HTMLElementBuilder.Br())
        builders.put("OBJECT", HTMLElementBuilder.HtmlObject())
        builders.put("APPLET", HTMLElementBuilder.Applet())
        builders.put("EMBED", NonStandard())
        builders.put("FONT", HTMLElementBuilder.Font())
        builders.put("BASEFONT", BaseFont())

        val heading: HTMLElementBuilder = Heading()
        builders.put("H1", heading)
        builders.put("H2", heading)
        builders.put("H3", heading)
        builders.put("H4", heading)
        builders.put("H5", heading)
        builders.put("H6", heading)

        builders.put("CANVAS", HTMLElementBuilder.Canvas())
    }

    @Throws(DOMException::class)
    fun createElement(document: HTMLDocumentImpl?, name: String): HTMLElement {
        val normalName = name.uppercase()
        // No need to synchronize; read-only map at this point.
        val builder = this.builders[normalName]
        if (builder == null) {
            // TODO: IE would assume name is html text here?
            // TODO: ^^ Other browsers throw an exception if there are illegal characters in the name.
            //          But am not sure what the legal character set is. Characters like angle-brackets
            //          do throw an exception in Chromium and Firefox. - hrj
            val element = HTMLElementModel(name)
            element.setOwnerDocument(document)
            return element
        } else {
            return builder.create(document, name)
        }
    }

    companion object {
        val instance: ElementFactory = ElementFactory()
    }
}
