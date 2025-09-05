package io.github.remmerw.thor.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.remmerw.thor.dom.DocumentImpl
import io.github.remmerw.thor.dom.ElementImpl
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

    fun attribute(node: NodeModel, attribute: String): String? {
        return (node(node) as Element?)?.getAttribute(attribute)
    }

    fun node(node: NodeModel): Node? {
        return document?.node(node.uid())
    }
    fun text(node: NodeModel): String {
        return document?.text(node.uid()) ?: ""
    }

    fun childNodes(node: NodeModel?): List<NodeModel> {
        if (document == null) {
            return emptyList()
        }
        if (node == null) {
            return emptyList()
        }
        return document!!.childNodes(node.uid()).map { node ->
            when (node) {
                is ElementImpl -> ElementModel(node.uid(), node.nodeName)
                is TextImpl -> TextModel(node.uid(), node.nodeName, node.textContent)
                else -> TodoModel((node as NodeImpl).uid(), node.nodeName)
            }
        }.toList()
    }

    fun childNodes(): List<NodeModel> {
        if (document == null) {
            return emptyList()
        }
        return document!!.children().map { node ->
            when (node) {
                is ElementImpl -> ElementModel(node.uid(), node.nodeName)
                is TextImpl -> TextModel(node.uid(), node.nodeName, node.textContent)
                else -> TodoModel((node as NodeImpl).uid(), node.nodeName)
            }
        }.toList()
    }

    fun bodyNode(): NodeModel? {
        if (document == null) {
            return null
        }
        val body = document!!.getBody()

        return when (body) {
            is ElementImpl -> ElementModel(body.uid(), body.nodeName)
            else -> null
        }
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