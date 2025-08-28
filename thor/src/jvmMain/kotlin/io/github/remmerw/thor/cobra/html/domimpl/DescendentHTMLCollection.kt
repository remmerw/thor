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
 * Created on Dec 3, 2005
 */
package io.github.remmerw.thor.cobra.html.domimpl

import io.github.remmerw.thor.cobra.js.AbstractScriptableDelegate
import io.github.remmerw.thor.cobra.js.JavaScript
import io.github.remmerw.thor.cobra.util.Nodes
import io.github.remmerw.thor.cobra.util.Objects
import org.w3c.dom.Node
import org.w3c.dom.html.HTMLCollection
import java.lang.ref.WeakReference

open class DescendentHTMLCollection @JvmOverloads constructor(
    private val rootNode: NodeImpl,
    private val nodeFilter: NodeFilter?,
    private val treeLock: Any,
    private val nestIntoMatchingNodes: Boolean = true
) : AbstractScriptableDelegate(), HTMLCollection {
    private var itemsByName: MutableMap<String?, ElementImpl?>? = null
    private var itemsByIndex: MutableList<NodeImpl?>? = null

    /**
     * @param rootNode
     * @param nodeFilter
     */
    init {
        val document = rootNode.ownerDocument as HTMLDocumentImpl
        document.addDocumentNotificationListener(LocalNotificationListener(document, this))
    }

    private fun ensurePopulatedImpl() {
        if (this.itemsByName == null) {
            val descendents =
                this.rootNode.getDescendents(this.nodeFilter, this.nestIntoMatchingNodes)
            this.itemsByIndex = if (descendents == null) mutableListOf<NodeImpl?>() else descendents
            val size = if (descendents == null) 0 else descendents.size
            val itemsByName: MutableMap<String?, ElementImpl?> =
                HashMap<String?, ElementImpl?>((size * 3) / 2)
            this.itemsByName = itemsByName
            for (i in 0..<size) {
                val descNode = descendents!!.get(i)
                if (descNode is ElementImpl) {
                    val id = descNode.getId()
                    if ((id != null) && (id.length != 0)) {
                        itemsByName.put(id, descNode)
                    }
                    val name = descNode.getAttribute("name")
                    if ((name != null) && (name.length != 0) && (name != id)) {
                        itemsByName.put(name, descNode)
                    }
                }
            }
        }
    }

    private fun invalidate() {
        synchronized(this.treeLock) {
            this.itemsByName = null
            this.itemsByIndex = null
        }
    }

    private val isValid: Boolean
        get() {
            synchronized(this.treeLock) {
                return (this.itemsByName != null) && (this.itemsByIndex != null)
            }
        }

    override fun getLength(): Int {
        synchronized(this.treeLock) {
            this.ensurePopulatedImpl()
            return this.itemsByIndex!!.size
        }
    }

    override fun item(index: Int): Node? {
        synchronized(this.treeLock) {
            this.ensurePopulatedImpl()
            try {
                return this.itemsByIndex!!.get(index)
            } catch (iob: IndexOutOfBoundsException) {
                return null
            }
        }
    }

    // TODO: This is a quick hack. Need to support WEB-IDL Semantics. GH #67
    fun item(obj: Any?): Node? {
        if (obj is Int) {
            return item(obj)
        }
        return item(0)
    }

    // TODO: This needs to be handled in a general fashion. GH #123
    fun hasOwnProperty(obj: Any?): Boolean {
        if (Objects.isAssignableOrBox(obj, Integer.TYPE)) {
            val i = JavaScript.instance.getJavaObject(obj, Integer.TYPE) as Int
            return i < length
        } else if (Objects.isAssignableOrBox(obj, String::class.java)) {
            // This seems to be related to GH #67
            val s = JavaScript.instance.getJavaObject(obj, String::class.java) as String
            try {
                return s.toInt() < length
            } catch (nfe: NumberFormatException) {
                return false
            }
        } else {
            return false
        }
    }

    override fun namedItem(name: String?): Node? {
        synchronized(this.treeLock) {
            this.ensurePopulatedImpl()
            return this.itemsByName!!.get(name)
        }
    }

    fun indexOf(node: Node?): Int {
        synchronized(this.treeLock) {
            this.ensurePopulatedImpl()
            return this.itemsByIndex!!.indexOf(node)
        }
    }

    // private final class NodeCounter implements NodeVisitor {
    // private int count = 0;
    //
    // public final void visit(Node node) {
    // if(nodeFilter.accept(node)) {
    // this.count++;
    // throw new SkipVisitorException();
    // }
    // }
    //
    // public int getCount() {
    // return this.count;
    // }
    // }
    //
    // private final class NodeScanner implements NodeVisitor {
    // private int count = 0;
    // private Node foundNode = null;
    // private final int targetIndex;
    //
    // public NodeScanner(int idx) {
    // this.targetIndex = idx;
    // }
    //
    // public final void visit(Node node) {
    // if(nodeFilter.accept(node)) {
    // if(this.count == this.targetIndex) {
    // this.foundNode = node;
    // throw new StopVisitorException();
    // }
    // this.count++;
    // throw new SkipVisitorException();
    // }
    // }
    //
    // public Node getNode() {
    // return this.foundNode;
    // }
    // }
    //
    // private final class NodeScanner2 implements NodeVisitor {
    // private int count = 0;
    // private int foundIndex = -1;
    // private final Node targetNode;
    //
    // public NodeScanner2(Node node) {
    // this.targetNode = node;
    // }
    //
    // public final void visit(Node node) {
    // if(nodeFilter.accept(node)) {
    // if(node == this.targetNode) {
    // this.foundIndex = this.count;
    // throw new StopVisitorException();
    // }
    // this.count++;
    // throw new SkipVisitorException();
    // }
    // }
    //
    // public int getIndex() {
    // return this.foundIndex;
    // }
    // }
    private class LocalNotificationListener(// Needs to be a static class with a weak reference to
        // the collection object.
        private val document: HTMLDocumentImpl, collection: DescendentHTMLCollection?
    ) : DocumentNotificationAdapter() {
        private val collectionRef: WeakReference<DescendentHTMLCollection?>

        init {
            this.collectionRef = WeakReference<DescendentHTMLCollection?>(collection)
        }

        override fun structureInvalidated(node: NodeImpl) {
            val collection = this.collectionRef.get()
            if (collection == null) {
                // Gone!
                this.document.removeDocumentNotificationListener(this)
                return
            }
            if (collection.isValid) {
                if (Nodes.isSameOrAncestorOf(collection.rootNode, node)) {
                    collection.invalidate()
                }
            }
        }

        override fun nodeLoaded(node: NodeImpl) {
            this.structureInvalidated(node)
        }
    }
}
