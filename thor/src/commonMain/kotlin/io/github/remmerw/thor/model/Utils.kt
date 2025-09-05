package io.github.remmerw.thor.model

import io.github.remmerw.thor.dom.DocumentImpl
import io.github.remmerw.thor.dom.ElementImpl
import io.ktor.http.Url
import org.w3c.dom.Element

object Utils {

    fun getHref(elementModel: ElementImpl): String {
        val href = elementModel.getAttribute("href")
        return if (href == null) "" else Urls.removeControlCharacters(href)
    }

    fun getBaseURI(doc: DocumentImpl): String? {
        val buri = doc.baseURI
        return if (buri == null) doc.documentURI else buri
    }

    fun getFullURL(doc: DocumentImpl, uri: String): Url {
        try {
            val baseURI = getBaseURI(doc)
            val documentURL = if (baseURI == null) null else Url(baseURI)
            return Urls.createURL(documentURL, uri)
        } catch (_: Throwable) {
            return Url(uri)
        }
    }

    fun getFullURL(elementModel: Element, spec: String): Url {
        val doc: Any? = elementModel.ownerDocument
        val cleanSpec = Urls.encodeIllegalCharacters(spec)
        return if (doc is DocumentImpl) {
            getFullURL(doc, cleanSpec)
        } else {
            Url(cleanSpec)
        }
    }


    private fun absoluteURL(
        anchor: ElementImpl,
        stateModel: StateModel
    ): Url? {
        val href = getHref(anchor)
        if (href.startsWith("javascript:")) {
            return null
        } else {
            try {
                return getFullURL(anchor, href)
            } catch (throwable: Throwable) {
                stateModel.warn("Malformed URI: [" + href + "].", throwable)
            }
        }
        return null
    }

    fun navigate(elementModel: ElementImpl, stateModel: StateModel) {

        if (elementModel.nodeName == Type.ANCHOR.name ||
            elementModel.nodeName == Type.A.name
        ) {
            val href = getHref(elementModel)
            if (href.startsWith("#")) {
                // TODO: Scroll to the element. Issue #101
            } else if (href.startsWith("javascript:")) {
                val script = href.substring(11)
                // evalInScope adds the JS task
                println(script)
            } else {
                val url = absoluteURL(elementModel, stateModel)
                if (url != null) {
                    val target = elementModel.getAttribute("target")
                    stateModel.linkClicked(url, target)
                }
            }
        }
        if (elementModel.nodeName == Type.LINK.name) {
            val href = getHref(elementModel)
            if (href.startsWith("#")) {
                // TODO: Scroll to the element. Issue #101
            } else if (href.startsWith("javascript:")) {
                val script = href.substring(11)
                // evalInScope adds the JS task
                println(script)
            } else {
                val url = absoluteURL(elementModel, stateModel)
                if (url != null) {
                    val target = elementModel.getAttribute("target")
                    stateModel.linkClicked(url, target)
                }
            }
        }
    }

}