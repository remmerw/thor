package io.github.remmerw.thor.model

import io.github.remmerw.thor.dom.Element
import io.ktor.http.Url

object Utils {

    fun getHref(elementModel: Element): String {
        val href = elementModel.getAttribute("href")
        return if (href == null) "" else Urls.removeControlCharacters(href)
    }


    fun getFullURL(baseURI: String, uri: String): Url {
        try {
            val documentURL = Url(baseURI)
            return Urls.createURL(documentURL, uri)
        } catch (_: Throwable) {
            return Url(uri)
        }
    }

    fun getFullURL2(baseURI: String, spec: String): Url {
        val cleanSpec = Urls.encodeIllegalCharacters(spec)
        return getFullURL(baseURI, cleanSpec)
    }


    private fun absoluteURL(
        anchor: Element,
        stateModel: StateModel
    ): Url? {
        val href = getHref(anchor)
        if (href.startsWith("javascript:")) {
            return null
        } else {
            try {

                return getFullURL2(stateModel.documentUri!! , href)
            } catch (throwable: Throwable) {
                stateModel.warn("Malformed URI: [" + href + "].", throwable)
            }
        }
        return null
    }

    fun navigate(elementModel: Element, stateModel: StateModel) {

        if (elementModel.name == Type.ANCHOR.name ||
            elementModel.name == Type.A.name
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
        if (elementModel.name == Type.LINK.name) {
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