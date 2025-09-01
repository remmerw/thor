package io.github.remmerw.thor.parser

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.util.function.Consumer

object Nodes {
    private val emptyIterableNode: Iterable<Node?> = object : Iterable<Node?> {
        override fun iterator(): MutableIterator<Node?> {
            return object : MutableIterator<Node?> {
                override fun hasNext(): Boolean {
                    return false
                }

                override fun next(): Node? {
                    throw IllegalStateException()
                }

                override fun remove() {
                    throw TODO()
                }
            }
        }
    }
    private val emptyIterableElement: Iterable<Element?> = object : Iterable<Element?> {
        override fun iterator(): MutableIterator<Element?> {
            return object : MutableIterator<Element?> {
                override fun hasNext(): Boolean {
                    return false
                }

                override fun next(): Element? {
                    throw IllegalStateException()
                }

                override fun remove() {
                    throw TODO()
                }
            }
        }
    }

    fun getCommonAncestor(node1: Node?, node2: Node?): Node? {
        if ((node1 == null) || (node2 == null)) {
            return null
        }
        var checkNode: Node? = node1
        while (!isSameOrAncestorOf(checkNode, node2)) {
            checkNode = checkNode!!.parentNode
            if (checkNode == null) {
                return null
            }
        }
        return checkNode
    }

    fun isSameOrAncestorOf(node: Node?, child: Node): Boolean {
        if (child.isSameNode(node)) {
            return true
        }
        val parent = child.parentNode
        if (parent == null) {
            return false
        }
        return isSameOrAncestorOf(node, parent)
    }

    fun makeIterable(nodeList: NodeList?): Iterable<Node?> {
        if (nodeList == null) {
            return emptyIterableNode
        } else {
            return object : Iterable<Node?> {
                override fun iterator(): MutableIterator<Node?> {
                    return object : MutableIterator<Node?> {
                        private var i = 0

                        override fun hasNext(): Boolean {
                            return i < nodeList.length
                        }

                        override fun next(): Node? {
                            return nodeList.item(i++)
                        }

                        override fun remove() {
                            TODO()
                        }
                    }
                }
            }
        }
    }

    fun makeIterableElements(nodeList: NodeList?): Iterable<Element?> {
        if (nodeList == null) {
            return emptyIterableElement
        } else {
            return object : Iterable<Element?> {
                override fun iterator(): MutableIterator<Element?> {
                    return object : MutableIterator<Element?> {
                        private var i = 0

                        override fun hasNext(): Boolean {
                            return i < nodeList.length
                        }

                        override fun next(): Element? {
                            return nodeList.item(i++) as Element?
                        }

                        override fun remove() {
                            TODO()
                        }
                    }
                }
            }
        }
    }

    fun forEachNode(node: Node, consumer: Consumer<Node?>) {
        // TODO: Change from recursive to iterative
        for (child in makeIterable(node.childNodes)) {
            consumer.accept(child)
            forEachNode(child!!, consumer)
        }
    }
}