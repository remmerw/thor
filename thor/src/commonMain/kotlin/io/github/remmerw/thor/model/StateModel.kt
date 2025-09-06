package io.github.remmerw.thor.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.remmerw.thor.dom.DocumentImpl
import io.github.remmerw.thor.dom.ElementImpl
import io.github.remmerw.thor.dom.Entity
import io.github.remmerw.thor.dom.NodeImpl
import io.github.remmerw.thor.dom.TextImpl
import io.ktor.http.Url
import org.w3c.dom.Element
import org.w3c.dom.Node

class StateModel() : ViewModel() {
    var isImageLoadingEnabled: Boolean by mutableStateOf(true)

    var document: DocumentImpl? by mutableStateOf(null)

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

    fun childNodes(entity: Entity?): List<Entity> {
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


    fun bodyNode(): Entity? {
        if (document == null) {
            return null
        }
        val body = document!!.getBody()
        if (body != null) {
            body as ElementImpl
            return Entity(body.uid(), body.nodeName)
        }
        return null
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