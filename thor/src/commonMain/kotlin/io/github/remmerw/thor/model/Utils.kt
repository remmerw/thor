package io.github.remmerw.thor.model

import io.github.remmerw.saga.Entity
import io.github.remmerw.saga.Tag
import io.ktor.http.Url

// todo import android.content.res.Resources
object Utils {


    object UnitUtil {
        val density: Float
            get() = 2F// todo Resources.getSystem().displayMetrics.density


        fun dpToPixel(dp: Float): Int = (dp * density).toInt()

        fun pixelToDp(px: Float): Int = (px / density).toInt()

    }

    fun getHref(href: String): String {
        return Urls.removeControlCharacters(href)
    }


    fun getFullURL(baseURI: String, uri: String): Url {
        try {
            val documentURL = Url(baseURI)
            return Urls.createURL(documentURL, uri)
        } catch (throwable: Throwable) {
            println(throwable)
            return Url(uri)
        }
    }

    fun getFullURL2(baseURI: String, spec: String): Url {
        val cleanSpec = Urls.encodeIllegalCharacters(spec)
        return getFullURL(baseURI, cleanSpec)
    }


    private fun absoluteURL(
        href: String,
        htmlModel: HtmlModel
    ): Url? {
        if (href.startsWith("javascript:")) {
            return null
        } else {
            try {

                return getFullURL2(htmlModel.documentUri!!, href)
            } catch (throwable: Throwable) {
                htmlModel.warn("Malformed URI: [" + href + "].", throwable)
            }
        }
        return null
    }

    fun navigate(entity: Entity, htmlModel: HtmlModel, href: String, target: String) {

        if (entity.name == Tag.ANCHOR.tag() ||
            entity.name == Tag.A.tag()
        ) {

            if (href.startsWith("#")) {
                // TODO: Scroll to the element. Issue #101
            } else if (href.startsWith("javascript:")) {
                val script = href.substring(11)
                // evalInScope adds the JS task
                println(script)
            } else {
                val url = absoluteURL(href, htmlModel)
                if (url != null) {
                    htmlModel.linkClicked(url, target)
                }
            }
        }
        if (entity.name == Tag.LINK.tag()) {

            if (href.startsWith("#")) {
                // TODO: Scroll to the element. Issue #101
            } else if (href.startsWith("javascript:")) {
                val script = href.substring(11)
                // evalInScope adds the JS task
                println(script)
            } else {
                val url = absoluteURL(href, htmlModel)
                if (url != null) {
                    htmlModel.linkClicked(url, target)
                }
            }
        }
    }

}