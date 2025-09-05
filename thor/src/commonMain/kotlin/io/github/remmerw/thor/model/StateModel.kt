package io.github.remmerw.thor.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.remmerw.thor.core.Urls
import io.github.remmerw.thor.dom.DocumentImpl
import io.github.remmerw.thor.dom.ElementImpl
import io.github.remmerw.thor.dom.NodeImpl
import io.github.remmerw.thor.dom.TextImpl
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.net.URL
import java.util.Optional

class StateModel() : ViewModel() {
    var isImageLoadingEnabled: Boolean by mutableStateOf(true)

    var document: DocumentImpl? by mutableStateOf(null)

    fun navigate(url: URL, target: String?) {
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

    fun childNodes(node: NodeModel?): List<NodeModel> {
        if (document == null) {
            return emptyList()
        }
        if(node == null){
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
        return document!!.nodes().map { node ->
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
            Utils.getFullURL(document!!, cleanSpec).toExternalForm()
        } else {
            URL(cleanSpec).toExternalForm()
        }
    }



    fun linkClicked(
        url: URL,
        target: String?
    ) {
        println("TODO linkClicked $url")
    }


    fun alert(message: String?) {
        TODO("Not yet implemented")
    }

    fun back() {
        TODO("Not yet implemented")
    }

    fun blur() {
        TODO("Not yet implemented")
    }

    fun close() {
        TODO("Not yet implemented")
    }

    fun confirm(message: String?): Boolean {
        TODO("Not yet implemented")
    }

    fun focus() {
        TODO("Not yet implemented")
    }

    fun open(
        absoluteUrl: String?,
        windowName: String?,
        windowFeatures: String?,
        replace: Boolean
    ) {
        TODO("Not yet implemented")
    }

    fun open(
        url: URL,
        windowName: String?,
        windowFeatures: String?,
        replace: Boolean
    ) {
        TODO("Not yet implemented")
    }

    fun prompt(message: String?, inputDefault: String?): String? {
        TODO("Not yet implemented")
    }

    fun scroll(x: Int, y: Int) {
        TODO("Not yet implemented")
    }

    fun scrollBy(x: Int, y: Int) {
        TODO("Not yet implemented")
    }

    fun resizeTo(width: Int, height: Int) {
        TODO("Not yet implemented")
    }

    fun resizeBy(byWidth: Int, byHeight: Int) {
        TODO("Not yet implemented")
    }

    fun isClosed(): Boolean {
        TODO("Not yet implemented")
    }

    fun defaultStatus(): String? {
        TODO("Not yet implemented")
    }

    fun name(): String? {
        TODO("Not yet implemented")
    }


    fun status(): String? {
        TODO("Not yet implemented")
    }


    fun reload() {
        TODO("Not yet implemented")
    }

    fun historyLength(): Int {
        TODO("Not yet implemented")
    }

    fun currentURL(): String? {
        TODO("Not yet implemented")
    }

    fun nextURL(): Optional<String>? {
        TODO("Not yet implemented")
    }

    fun previousURL(): Optional<String>? {
        TODO("Not yet implemented")
    }

    fun forward() {
        TODO("Not yet implemented")
    }

    fun moveInHistory(offset: Int) {
        TODO("Not yet implemented")
    }

    fun goToHistoryURL(url: String?) {
        TODO("Not yet implemented")
    }
}