package io.github.remmerw.thor.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.remmerw.thor.dom.DocumentImpl
import io.github.remmerw.thor.dom.Entity
import io.github.remmerw.thor.dom.NodeImpl
import io.github.remmerw.thor.dom.TextImpl
import io.ktor.http.Url
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.Node

class StateModel() : ViewModel() {
    var isImageLoadingEnabled: Boolean by mutableStateOf(true)

    private var document: DocumentImpl? = null

    val entity = MutableStateFlow<Entity?>(null)

    fun setDocument(documentImpl: DocumentImpl) {
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
        return (document!!.node(entity.uid) as TextImpl).text
    }


    fun wurst(entity: Entity): StateFlow<String> {
        return document!!.wurst(entity)
    }


    fun children(entity: Entity?): List<Entity> {
        if (document == null) {
            return emptyList()
        }
        if (entity == null) {
            return emptyList()
        }
        return document!!.childNodes(entity.uid).map { node ->
            node as NodeImpl
            println(node.nodeName)
            Entity(node.uid(), node.nodeName)
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