package io.github.remmerw.thor.cobra.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;
import java.util.function.Consumer;

public class Nodes {
    private static final Iterable<Node> emptyIterableNode = new Iterable<Node>() {
        @Override
        public Iterator<Node> iterator() {

            return new Iterator<Node>() {

                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Node next() {
                    throw new IllegalStateException();
                }

                @Override
                public void remove() {
                    throw new NotImplementedYetException();
                }
            };
        }
    };
    private static final Iterable<Element> emptyIterableElement = new Iterable<Element>() {
        @Override
        public Iterator<Element> iterator() {

            return new Iterator<Element>() {

                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Element next() {
                    throw new IllegalStateException();
                }

                @Override
                public void remove() {
                    throw new NotImplementedYetException();
                }
            };
        }
    };

    public static Node getCommonAncestor(final Node node1, final Node node2) {
        if ((node1 == null) || (node2 == null)) {
            return null;
        }
        Node checkNode = node1;
        while (!isSameOrAncestorOf(checkNode, node2)) {
            checkNode = checkNode.getParentNode();
            if (checkNode == null) {
                return null;
            }
        }
        return checkNode;
    }

    public static boolean isSameOrAncestorOf(final Node node, final Node child) {
        if (child.isSameNode(node)) {
            return true;
        }
        final Node parent = child.getParentNode();
        if (parent == null) {
            return false;
        }
        return isSameOrAncestorOf(node, parent);
    }

    public static Iterable<Node> makeIterable(final NodeList nodeList) {
        if (nodeList == null) {
            return emptyIterableNode;
        } else {
            return new Iterable<Node>() {
                @Override
                public Iterator<Node> iterator() {

                    return new Iterator<Node>() {
                        private int i = 0;

                        @Override
                        public boolean hasNext() {
                            return i < nodeList.getLength();
                        }

                        @Override
                        public Node next() {
                            return nodeList.item(i++);
                        }

                        @Override
                        public void remove() {
                            throw new NotImplementedYetException();
                        }
                    };
                }
            };
        }
    }

    public static Iterable<Element> makeIterableElements(final NodeList nodeList) {
        if (nodeList == null) {
            return emptyIterableElement;
        } else {
            return new Iterable<Element>() {
                @Override
                public Iterator<Element> iterator() {

                    return new Iterator<Element>() {
                        private int i = 0;

                        @Override
                        public boolean hasNext() {
                            return i < nodeList.getLength();
                        }

                        @Override
                        public Element next() {
                            return (Element) nodeList.item(i++);
                        }

                        @Override
                        public void remove() {
                            throw new NotImplementedYetException();
                        }
                    };
                }
            };
        }
    }

    public static void forEachNode(final Node node, final Consumer<Node> consumer) {
        // TODO: Change from recursive to iterative
        for (final Node child : Nodes.makeIterable(node.getChildNodes())) {
            consumer.accept(child);
            forEachNode(child, consumer);
        }
    }
}
