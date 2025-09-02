package io.github.remmerw.thor.model

import io.github.remmerw.thor.dom.HTMLAnchorElementModel
import io.github.remmerw.thor.dom.HTMLLinkElementModel
import java.net.MalformedURLException
import java.net.URL


fun navigate(anchor: HTMLAnchorElementModel, rendererContext: RendererContext) {

    val href = anchor.getHref()
    if (href.startsWith("#")) {
        // TODO: Scroll to the element. Issue #101
    } else if (href.startsWith("javascript:")) {
        val script = href.substring(11)
        // evalInScope adds the JS task
        println(script)
    } else {
        val url = absoluteURL(anchor, rendererContext)
        if (url != null) {
            val target = anchor.target
            rendererContext.linkClicked(anchor, url, target)
        }
    }
}


private fun absoluteURL(anchor: HTMLAnchorElementModel, rendererContext: RendererContext): URL? {
    val href = anchor.getHref()
    if (href.startsWith("javascript:")) {
        return null
    } else {
        try {
            return anchor.getFullURL(href)
        } catch (mfu: MalformedURLException) {
            rendererContext.warn("Malformed URI: [" + href + "].", mfu)
        }
    }
    return null
}

fun navigate(link: HTMLLinkElementModel, rendererContext: RendererContext) {

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
        val url = absoluteURL(link, rendererContext)
        if (url != null) {
            val target = link.target
            rendererContext.linkClicked(link, url, target)
        }
    }
}

private fun absoluteURL(link: HTMLLinkElementModel, rendererContext: RendererContext): URL? {
    val href = link.getHref()
    if (href.startsWith("javascript:")) {
        return null
    } else {
        try {
            return link.getFullURL(href)
        } catch (mfu: MalformedURLException) {
            rendererContext.warn("Malformed URI: [" + href + "].", mfu)
        }
    }
    return null
}