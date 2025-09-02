package io.github.remmerw.thor.dom

import androidx.compose.runtime.snapshots.SnapshotStateList
import org.w3c.dom.Node

interface DocumentModel : NodeModel

interface NodeModel : Node {
    fun nodes(): SnapshotStateList<NodeModel>

    override fun getNodeName(): String
}

