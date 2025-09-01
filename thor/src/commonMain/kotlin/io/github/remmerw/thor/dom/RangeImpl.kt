/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2015 Uproot Labs India Pvt Ltd

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

 */
package io.github.remmerw.thor.dom

import org.w3c.dom.DOMException
import org.w3c.dom.Document
import org.w3c.dom.DocumentFragment
import org.w3c.dom.Node
import org.w3c.dom.ranges.Range
import org.w3c.dom.ranges.RangeException

class RangeImpl(doc: Document?) : Range {
    private var startContainer: Node? = null
    private var endContainer: Node? = null
    private var startOffset = 0
    private var endOffset = 0

    init {
        this.startContainer = doc
        this.endContainer = doc
    }

    @Throws(DOMException::class)
    override fun getStartContainer(): Node? {
        return startContainer
    }

    @Throws(DOMException::class)
    override fun getStartOffset(): Int {
        return startOffset
    }

    @Throws(DOMException::class)
    override fun getEndContainer(): Node? {
        return endContainer
    }

    @Throws(DOMException::class)
    override fun getEndOffset(): Int {
        return endOffset
    }

    @Throws(DOMException::class)
    override fun getCollapsed(): Boolean {
        return startContainer === endContainer && startOffset == endOffset
    }

    @Throws(DOMException::class)
    override fun getCommonAncestorContainer(): Node? {
        // TODO Auto-generated method stub
        return null
    }

    @Throws(RangeException::class, DOMException::class)
    override fun setStart(refNode: Node?, offset: Int) {
        startContainer = refNode
        startOffset = offset
    }

    @Throws(RangeException::class, DOMException::class)
    override fun setEnd(refNode: Node?, offset: Int) {
        endContainer = refNode
        endOffset = offset
    }

    @Throws(RangeException::class, DOMException::class)
    override fun setStartBefore(refNode: Node?) {
        // TODO Auto-generated method stub
        TODO()
    }

    @Throws(RangeException::class, DOMException::class)
    override fun setStartAfter(refNode: Node?) {
        // TODO Auto-generated method stub
        TODO()
    }

    @Throws(RangeException::class, DOMException::class)
    override fun setEndBefore(refNode: Node?) {
        // TODO Auto-generated method stub
        TODO()
    }

    @Throws(RangeException::class, DOMException::class)
    override fun setEndAfter(refNode: Node?) {
        // TODO Auto-generated method stub
        TODO()
    }

    @Throws(DOMException::class)
    override fun collapse(toStart: Boolean) {
        // TODO Auto-generated method stub
        TODO()
    }

    @Throws(RangeException::class, DOMException::class)
    override fun selectNode(refNode: Node?) {
        // TODO Auto-generated method stub
        TODO()
    }

    @Throws(RangeException::class, DOMException::class)
    override fun selectNodeContents(refNode: Node?) {
        // TODO Auto-generated method stub
        TODO()
    }

    @Throws(DOMException::class)
    override fun compareBoundaryPoints(how: Short, sourceRange: Range?): Short {
        // TODO Auto-generated method stub
        TODO()
    }

    @Throws(DOMException::class)
    override fun deleteContents() {
        // TODO Auto-generated method stub
        TODO()
    }

    @Throws(DOMException::class)
    override fun extractContents(): DocumentFragment? {
        // TODO Auto-generated method stub
        TODO()
    }

    @Throws(DOMException::class)
    override fun cloneContents(): DocumentFragment? {
        // TODO Auto-generated method stub
        TODO()
    }

    @Throws(DOMException::class, RangeException::class)
    override fun insertNode(newNode: Node?) {
        // TODO Auto-generated method stub
        TODO()
    }

    @Throws(DOMException::class, RangeException::class)
    override fun surroundContents(newParent: Node?) {
        // TODO Auto-generated method stub
        TODO()
    }

    @Throws(DOMException::class)
    override fun cloneRange(): Range? {
        // TODO Auto-generated method stub
        TODO()
    }

    @Throws(DOMException::class)
    override fun detach() {
        // TODO Auto-generated method stub
    }
}
