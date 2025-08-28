package io.github.remmerw.thor.cobra.html.domimpl

/**
 * A listener of document changes.
 */
interface DocumentNotificationListener {
    /**
     * Called if a property related to the node's size has changed.
     *
     * @param node
     */
    fun sizeInvalidated(node: NodeImpl?)

    /**
     * Called if something such as a color or decoration has changed. This would
     * be something which does not affect the rendered size.
     *
     * @param node
     */
    fun lookInvalidated(node: NodeImpl?)

    /**
     * Changed if the position of the node in a parent has changed.
     *
     * @param node
     */
    fun positionInvalidated(node: NodeImpl?)

    /**
     * This is called when the node has changed, but it is unclear if it's a size
     * change or a look change. Typically, a node attribute has changed, but the
     * set of child nodes has not changed.
     *
     * @param node
     */
    fun invalidated(node: NodeImpl?)

    /**
     * Called when the node (with all its contents) is first created by the
     * parser.
     *
     * @param node
     */
    fun nodeLoaded(node: NodeImpl?)

    /**
     * The children of the node might have changed.
     *
     * @param node
     */
    fun structureInvalidated(node: NodeImpl?)

    /**
     * Called when a external script (a SCRIPT tag with a src attribute) is about
     * to start loading.
     *
     * @param node
     */
    fun externalScriptLoading(node: NodeImpl?)

    /**
     * This is called when the whole document is potentially invalid, e.g. when a
     * new style sheet has been added.
     */
    fun allInvalidated()
}
