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
package io.github.remmerw.thor.dom

import org.w3c.dom.html.HTMLDocument
import org.w3c.dom.html.HTMLElement

abstract class HTMLElementBuilder {
    fun create(document: HTMLDocument?, name: String): HTMLElement {
        val element = this.build(name)
        element.setOwnerDocument(document)
        return element
    }

    protected abstract fun build(name: String): HTMLElementModel

    class Html : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLHtmlElementModel(name)
        }
    }

    class Title : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLTitleElementModel(name)
        }
    }

    class Base : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLBaseElementModel(name)
        }
    }

    class Body : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLBodyElementModel(name)
        }
    }

    class Span : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLSpanElementModel(name)
        }
    }

    class Script : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLScriptElementModel(name)
        }
    }

    class Img : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLImageElementModel(name)
        }
    }

    class Style : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLStyleElementModel(name)
        }
    }

    class Table : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLTableElementModel(name)
        }
    }

    class Td : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLTableCellElementModel(name)
        }
    }

    class Th : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLTableHeadElementModel(name)
        }
    }

    class Tr : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLTableRowElementModel(name)
        }
    }

    class Link : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLLinkElementModel(name)
        }
    }

    class Anchor : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLLinkElementModel(name)
        }
    }

    class Form : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLFormElementModel(name)
        }
    }

    class Input : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLInputElementModel(name)
        }
    }

    class Button : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLButtonElementModel(name)
        }
    }

    class Textarea : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLTextAreaElementModel(name)
        }
    }

    class Select : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLSelectElementModel(name)
        }
    }

    class Option : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLOptionElementModel(name)
        }
    }

    class Frameset : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLFrameSetElementModel(name)
        }
    }

    class Frame : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLFrameElementModel(name)
        }
    }

    class Ul : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLUListElementModel(name)
        }
    }

    class Ol : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLOListElementModel(name)
        }
    }

    class Li : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLLIElementModel(name)
        }
    }

    class Pre : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLPreElementModel(name)
        }
    }

    class Div : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLDivElementModel(name)
        }
    }

    class Quote : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLQuoteElementModel(name)
        }
    }

    class Hr : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLHRElementModel(name)
        }
    }

    class Br : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLBRElementModel(name)
        }
    }

    class P : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLPElementModel(name)
        }
    }

    class GenericMarkup : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLGenericMarkupElement(name)
        }
    }

    class HtmlObject : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLObjectElementModel(name)
        }
    }

    class Applet : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLAppletElementModel(name)
        }
    }

    class IFrame : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLIFrameElementModel(name)
        }
    }

    class BaseFont : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLBaseFontElementModel(name)
        }
    }

    class Font : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLFontElementModel(name)
        }
    }

    class Heading : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLHeadingElementModel(name)
        }
    }

    class NonStandard : HTMLElementBuilder() {
        public override fun build(name: String): HTMLElementModel {
            return HTMLNonStandardElement(name)
        }
    }

    class Canvas : HTMLElementBuilder() {
        override fun build(name: String): HTMLElementModel {
            return HTMLCanvasElementModel()
        }
    }
}
