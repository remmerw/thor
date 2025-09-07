package io.github.remmerw.thor.model

import io.github.remmerw.thor.dom.Entity
import io.ktor.http.Url

object Utils {

    fun getHref(href: String): String {
        return Urls.removeControlCharacters(href)
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
        href: String,
        stateModel: StateModel
    ): Url? {
        if (href.startsWith("javascript:")) {
            return null
        } else {
            try {

                return getFullURL2(stateModel.documentUri!!, href)
            } catch (throwable: Throwable) {
                stateModel.warn("Malformed URI: [" + href + "].", throwable)
            }
        }
        return null
    }

    fun navigate(entity: Entity, stateModel: StateModel, href: String, target: String) {

        if (entity.name == Type.ANCHOR.name ||
            entity.name == Type.A.name
        ) {

            if (href.startsWith("#")) {
                // TODO: Scroll to the element. Issue #101
            } else if (href.startsWith("javascript:")) {
                val script = href.substring(11)
                // evalInScope adds the JS task
                println(script)
            } else {
                val url = absoluteURL(href, stateModel)
                if (url != null) {
                    stateModel.linkClicked(url, target)
                }
            }
        }
        if (entity.name == Type.LINK.name) {

            if (href.startsWith("#")) {
                // TODO: Scroll to the element. Issue #101
            } else if (href.startsWith("javascript:")) {
                val script = href.substring(11)
                // evalInScope adds the JS task
                println(script)
            } else {
                val url = absoluteURL(href, stateModel)
                if (url != null) {
                    stateModel.linkClicked(url, target)
                }
            }
        }
    }

}