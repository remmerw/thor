package io.github.remmerw.thor.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.remmerw.thor.dom.Document
import io.github.remmerw.thor.dom.Element
import io.github.remmerw.thor.dom.Entity
import io.github.remmerw.thor.dom.Node
import io.github.remmerw.thor.dom.Text
import io.ktor.http.Url
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StateModel() : ViewModel() {
    var isImageLoadingEnabled: Boolean by mutableStateOf(true)

    private var document: Document? = null

    val entity = MutableStateFlow<Entity?>(null)

    fun setDocument(documentImpl: Document) {
        viewModelScope.launch {
            document = documentImpl
            println(documentImpl.entity())
            entity.emit(documentImpl.entity())
        }
    }

    fun navigate(url: Url, target: String?) {
        TODO("Not yet implemented")
    }

    fun warn(message: String, err: Throwable?) {
        println(message)
        err?.printStackTrace()
    }

    fun attribute(entity: Entity, attribute: String): String? {
        return (node(entity) as Element?)?.getAttribute(attribute)
    }

    fun node(node: Entity): Node? {
        return document?.node(node.uid)
    }

    fun text(entity: Entity): String {
        if (document == null) {
            return ""
        }
        return (document!!.node(entity.uid) as Text).text
    }


    fun wurst(entity: Entity): StateFlow<String> {
        return document!!.data(entity)
    }


    fun children(entity: Entity?): List<Entity> {
        if (document == null) {
            return emptyList()
        }
        if (entity == null) {
            return emptyList()
        }
        return document!!.childNodes(entity.uid).map { node ->
            node as Node
            println(node.getNodeName())
            Entity(node.uid(), node.getNodeName())
        }.toList()
    }


    fun fullUri(spec: String): String {

        val cleanSpec = Urls.encodeIllegalCharacters(spec)
        return if (document != null) {
            Utils.getFullURL(document!!, cleanSpec).toString()
        } else {
            Url(cleanSpec).toString()
        }
    }


    fun linkClicked(
        url: Url,
        target: String?
    ) {
        println("TODO linkClicked $url")
    }

}