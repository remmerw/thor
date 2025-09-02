package io.github.remmerw.thor.model

import io.github.remmerw.thor.dom.FormInput
import io.github.remmerw.thor.dom.HTMLAnchorElementModel
import io.github.remmerw.thor.dom.HTMLElementModel
import io.github.remmerw.thor.dom.HTMLFormElementModel
import io.github.remmerw.thor.dom.HTMLLinkElementModel
import io.github.remmerw.thor.dom.NodeVisitor
import org.w3c.dom.Node
import java.net.MalformedURLException
import java.net.URL

object Utils {
    /**
     * This method should be called when form submission is done by a submit
     * button.
     *
     * @param extraFormInputs Any additional form inputs that need to be submitted, e.g. the
     * submit button parameter.
     */
    fun submit(
        form: HTMLFormElementModel,
        rendererContext: RendererContext,
        extraFormInputs: List<FormInput> = emptyList()
    ) {

        val formInputs = ArrayList<FormInput>()
        if (extraFormInputs.isNotEmpty()) {
            formInputs.addAll(extraFormInputs)
        }
        form.visit(object : NodeVisitor {
            override fun visit(node: Node) {
                if (node is HTMLElementModel) {
                    val fis = node.getFormInputs()
                    if (fis != null) {
                        for (fi in fis) {
                            checkNotNull(fi.name) { "Form input does not have a name: " + node }
                            formInputs.add(fi)
                        }
                    }
                }
            }
        })
        val fia = formInputs.toTypedArray()
        var href = form.action
        if (href == null) {
            href = form.getBaseURI()
        }
        try {
            val url = form.getFullURL(href!!)
            rendererContext.submitForm(
                form.getMethod(), url,
                form.target, form.enctype, fia
            )
        } catch (mfu: MalformedURLException) {
            rendererContext.warn("submit()", mfu)
        }

    }

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


    private fun absoluteURL(
        anchor: HTMLAnchorElementModel,
        rendererContext: RendererContext
    ): URL? {
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
}