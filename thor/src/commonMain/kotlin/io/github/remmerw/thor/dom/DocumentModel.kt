package io.github.remmerw.thor.dom

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import org.w3c.dom.Element
import org.w3c.dom.Node

interface DocumentModel : NodeModel

interface NodeModel : Node {
    fun nodes(): SnapshotStateList<NodeModel>

    override fun getNodeName(): String
}


interface ElementModel : NodeModel, Element {
    fun attributes(): SnapshotStateMap<String, String>
    fun cssProperties(): SnapshotStateMap<String, String>
}