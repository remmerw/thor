package io.github.remmerw.thor.dom


abstract class Node(
    var document: Document?,
    val parent: Node?,
    val uid: Long,
    val name: String,
    val type: Short
) {
    init {
        if (document is Document) {
            (document as Document).addNode(this)
        }
    }

    private val children = mutableListOf<Node>()

    fun children(): List<Node> {
        return children
    }

    internal fun appendChild(newChild: Node) {
        this.children.add(newChild)
    }

    fun getDocumentOwner(): Document {
        return document!!
    }

    internal fun setOwnerDocument(value: Document) {
        this.document = value
    }


    fun entity(): Entity {
        return Entity(uid, name)
    }

    override fun toString(): String {
        return "$name($uid)"
    }


    companion object {
        const val ELEMENT_NODE: Short = 1
        const val TEXT_NODE: Short = 3
        const val CDATA_SECTION_NODE: Short = 4
        const val PROCESSING_INSTRUCTION_NODE: Short = 7
        const val COMMENT_NODE: Short = 8
        const val DOCUMENT_NODE: Short = 9
        const val DOCUMENT_TYPE_NODE: Short = 10
    }
}

