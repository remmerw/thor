package io.github.remmerw.thor.dom

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import org.w3c.dom.CharacterData
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.Text

interface DocumentModel : NodeModel, Document

interface NodeModel : Node {
    fun nodes(): SnapshotStateList<NodeModel>
    fun uid(): Long
}


interface ElementModel : NodeModel, Element {
    fun attributes(): SnapshotStateMap<String, String>
    fun properties(): SnapshotStateMap<String, String>
    fun elementType(): ElementType
}

interface TextModel : Text, CharacterDataModel

interface CharacterDataModel : NodeModel, CharacterData {
    fun text(): String
}