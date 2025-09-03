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
        stateModel: StateModel,
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
            stateModel.submitForm(
                form.getMethod(), url,
                form.target, form.enctype, fia
            )
        } catch (mfu: MalformedURLException) {
            stateModel.warn("submit()", mfu)
        }

    }

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
                stateModel.linkClicked(anchor, url, target)
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
                stateModel.linkClicked(link, url, target)
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