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
 * Created on Oct 8, 2005
 */
package io.github.remmerw.thor.cobra.html.domimpl

import org.w3c.dom.html.HTMLDocument
import org.w3c.dom.html.HTMLElement

abstract class HTMLElementBuilder {
    fun create(document: HTMLDocument?, name: String?): HTMLElement {
        val element = this.build(name)
        element.setOwnerDocument(document)
        return element
    }

    protected abstract fun build(name: String?): HTMLElementImpl

    class Html : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLHtmlElementImpl(name)
        }
    }

    class Title : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLTitleElementImpl(name)
        }
    }

    class Base : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLBaseElementImpl(name)
        }
    }

    class Body : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLBodyElementImpl(name)
        }
    }

    class Span : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLSpanElementImpl(name)
        }
    }

    class Script : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLScriptElementImpl(name)
        }
    }

    class Img : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLImageElementImpl(name)
        }
    }

    class Style : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLStyleElementImpl(name)
        }
    }

    class Table : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLTableElementImpl(name)
        }
    }

    class Td : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLTableCellElementImpl(name)
        }
    }

    class Th : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLTableHeadElementImpl(name)
        }
    }

    class Tr : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLTableRowElementImpl(name)
        }
    }

    class Link : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLLinkElementImpl(name)
        }
    }

    class Anchor : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLLinkElementImpl(name)
        }
    }

    class Form : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLFormElementImpl(name)
        }
    }

    class Input : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLInputElementImpl(name)
        }
    }

    class Button : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLButtonElementImpl(name)
        }
    }

    class Textarea : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLTextAreaElementImpl(name)
        }
    }

    class Select : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLSelectElementImpl(name)
        }
    }

    class Option : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLOptionElementImpl(name)
        }
    }

    class Frameset : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLFrameSetElementImpl(name)
        }
    }

    class Frame : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLFrameElementImpl(name)
        }
    }

    class Ul : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLUListElementImpl(name)
        }
    }

    class Ol : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLOListElementImpl(name)
        }
    }

    class Li : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLLIElementImpl(name)
        }
    }

    class Pre : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLPreElementImpl(name)
        }
    }

    class Div : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLDivElementImpl(name)
        }
    }

    class Quote : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLQuoteElementImpl(name)
        }
    }

    class Hr : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLHRElementImpl(name)
        }
    }

    class Br : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLBRElementImpl(name)
        }
    }

    class P : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLPElementImpl(name)
        }
    }

    class GenericMarkup : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLGenericMarkupElement(name)
        }
    }

    class HtmlObject : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLObjectElementImpl(name)
        }
    }

    class Applet : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLAppletElementImpl(name)
        }
    }

    class IFrame : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLIFrameElementImpl(name)
        }
    }

    class BaseFont : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLBaseFontElementImpl(name)
        }
    }

    class Font : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLFontElementImpl(name)
        }
    }

    class Heading : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLHeadingElementImpl(name)
        }
    }

    class NonStandard : HTMLElementBuilder() {
        public override fun build(name: String?): HTMLElementImpl {
            return HTMLNonStandardElement(name)
        }
    }

    class Canvas : HTMLElementBuilder() {
        override fun build(name: String?): HTMLElementImpl {
            return HTMLCanvasElementImpl()
        }
    }
}
