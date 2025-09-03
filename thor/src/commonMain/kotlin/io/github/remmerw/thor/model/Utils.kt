package io.github.remmerw.thor.model

import io.github.remmerw.thor.dom.HTMLAnchorElementModel
import io.github.remmerw.thor.dom.HTMLLinkElementModel
import java.net.MalformedURLException
import java.net.URL

object Utils {


    fun navigate(anchor: HTMLAnchorElementModel, stateModel: StateModel) {

        val href = anchor.getHref()
        if (href.startsWith("#")) {
            // TODO: Scroll to the element. Issue #101
        } else if (href.startsWith("javascript:")) {
            val script = href.substring(11)
            // evalInScope adds the JS task
            println(script)
        } else {
            val url = absoluteURL(anchor, stateModel)
            if (url != null) {
                val target = anchor.target
                stateModel.linkClicked(url, target)
            }
        }
    }


    private fun absoluteURL(
        anchor: HTMLAnchorElementModel,
        stateModel: StateModel
    ): URL? {
        val href = anchor.getHref()
        if (href.startsWith("javascript:")) {
            return null
        } else {
            try {
                return anchor.getFullURL(href)
            } catch (mfu: MalformedURLException) {
                stateModel.warn("Malformed URI: [" + href + "].", mfu)
            }
        }
        return null
    }

    fun navigate(link: HTMLLinkElementModel, stateModel: StateModel) {

        if (link.disabled) {
            return
        }
        val href = link.getHref()
        if (href.startsWith("#")) {
            // TODO: Scroll to the element. Issue #101
        } else if (href.startsWith("javascript:")) {
            val script = href.substring(11)
            // evalInScope adds the JS task
            println(script)
        } else {
            val url = absoluteURL(link, stateModel)
            if (url != null) {
                val target = link.target
                stateModel.linkClicked(url, target)
            }
        }
    }

    private fun absoluteURL(link: HTMLLinkElementModel, stateModel: StateModel): URL? {
        val href = link.getHref()
        if (href.startsWith("javascript:")) {
            return null
        } else {
            try {
                return link.getFullURL(href)
            } catch (mfu: MalformedURLException) {
                stateModel.warn("Malformed URI: [" + href + "].", mfu)
            }
        }
        return null
    }
}