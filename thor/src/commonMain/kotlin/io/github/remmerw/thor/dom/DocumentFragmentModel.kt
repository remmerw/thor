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
 * Created on Oct 9, 2005
 */
package io.github.remmerw.thor.dom

import org.w3c.dom.DOMException
import org.w3c.dom.DocumentFragment
import org.w3c.dom.Node.DOCUMENT_FRAGMENT_NODE

/* TODO: extends ElementImpl as a hack, to get the ParentNode functionality.
         Better solution would be to extend from NodeImpl and create a ParentNode interface with default methods.
         Issue #88 */
class DocumentFragmentModel : ElementImpl("#document-fragment"), DocumentFragment {
    override fun getLocalName(): String? {
        return null
    }

    override fun getNodeName(): String {
        return "#document-fragment"
    }

    @Throws(DOMException::class)
    override fun getNodeValue(): String? {
        return null
    }

    @Throws(DOMException::class)
    override fun setNodeValue(nodeValue: String?) {
    }

    override fun getNodeType(): Short {
        return DOCUMENT_FRAGMENT_NODE
    }


    override fun getId(): String? {
        TODO("Not yet implemented")
    }
}
