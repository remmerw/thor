package io.github.remmerw.thor.model

import io.github.remmerw.thor.core.Urls
import io.github.remmerw.thor.dom.DocumentImpl
import io.github.remmerw.thor.dom.ElementModel
import io.github.remmerw.thor.dom.ElementType
import org.w3c.dom.Element
import java.net.MalformedURLException
import java.net.URL

object Utils {

    fun getHref(elementModel: ElementModel): String {
        val href = elementModel.getAttribute("href")
        return if (href == null) "" else Urls.removeControlCharacters(href)
    }

    fun getBaseURI(doc: DocumentImpl): String? {
        val buri = doc.baseURI
        return if (buri == null) doc.documentURI else buri
    }

    fun getFullURL(doc: DocumentImpl, uri: String): URL {
        try {
            val baseURI = getBaseURI(doc)
            val documentURL = if (baseURI == null) null else URL(baseURI)
            return Urls.createURL(documentURL, uri)
        } catch (mfu: MalformedURLException) {
            return URL(uri)
        }
    }

    fun getFullURL(elementModel: Element, spec: String): URL {
        val doc: Any? = elementModel.ownerDocument
        val cleanSpec = Urls.encodeIllegalCharacters(spec)
        return if (doc is DocumentImpl) {
            getFullURL(doc, cleanSpec)
        } else {
            URL(cleanSpec)
        }
    }


    private fun absoluteURL(
        anchor: ElementModel,
        stateModel: StateModel
    ): URL? {
        val href = getHref(anchor)
        if (href.startsWith("javascript:")) {
            return null
        } else {
            try {
                return getFullURL(anchor, href)
            } catch (mfu: MalformedURLException) {
                stateModel.warn("Malformed URI: [" + href + "].", mfu)
            }
        }
        return null
    }

    fun navigate(elementModel: ElementModel, stateModel: StateModel) {

        if (elementModel.elementType() == ElementType.ANCHOR ||
            elementModel.elementType() == ElementType.A
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
        if (elementModel.elementType() == ElementType.LINK) {
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