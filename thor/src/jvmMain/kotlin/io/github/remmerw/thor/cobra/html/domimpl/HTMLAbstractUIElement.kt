package io.github.remmerw.thor.cobra.html.domimpl

import io.github.remmerw.thor.cobra.html.js.Executor
import io.github.remmerw.thor.cobra.js.JavaScript
import org.mozilla.javascript.Context
import org.mozilla.javascript.EcmaError
import org.mozilla.javascript.Function
import org.mozilla.javascript.Scriptable
import java.util.logging.Level

/**
 * Implements common functionality of most elements.
 */
open class HTMLAbstractUIElement(name: String) : HTMLElementImpl(name) {
    var onfocus: Function? = null
        get() = this.getEventFunction(field, "onfocus")
    var onblur: Function? = null
        get() = this.getEventFunction(field, "onblur")
    var onclick: Function? = null
        get() = this.getEventFunction(field, "onclick")
    var ondblclick: Function? = null
        get() = this.getEventFunction(field, "ondblclick")
    var onmousedown: Function? = null
        get() = this.getEventFunction(field, "onmousedown")
    var onmouseup: Function? = null
        get() = this.getEventFunction(field, "onmouseup")
    var onmouseover: Function? = null
        get() = this.getEventFunction(field, "onmouseover")
    var onmousemove: Function? = null
        get() = this.getEventFunction(field, "onmousemove")
    var onmouseout: Function? = null
        get() = this.getEventFunction(field, "onmouseout")
    var onkeypress: Function? = null
        get() = this.getEventFunction(field, "onkeypress")
    var onkeydown: Function? = null
        get() = this.getEventFunction(field, "onkeydown")
    var onkeyup: Function? = null
        get() = this.getEventFunction(field, "onkeyup")
    var oncontextmenu: Function? = null
        get() = this.getEventFunction(field, "oncontextmenu")
    private var functionByAttribute: MutableMap<String?, Function?>? = null

    open fun focus() {
        val node = this.uINode
        if (node != null) {
            node.focus()
        }
    }

    open fun blur() {
        val node = this.uINode
        if (node != null) {
            node.blur()
        }
    }

    protected fun getEventFunction(varValue: Function?, attributeName: String): Function? {
        if (varValue != null) {
            return varValue
        }
        val normalAttributeName: String =
            normalizeAttributeName(attributeName)
        synchronized(this) {
            var fba = this.functionByAttribute
            var f = if (fba == null) null else fba.get(normalAttributeName)
            if (f != null) {
                return f
            }
            val uac = this.userAgentContext
            checkNotNull(uac) { "No user agent context." }
            if (uac.isScriptingEnabled()) {
                val attributeValue = this.getAttribute(attributeName)
                if ((attributeValue != null) && (attributeValue.length != 0)) {
                    val functionCode =
                        ("function " + normalAttributeName + "_" + System.identityHashCode(this) + "() { " + attributeValue
                                + " }")
                    val doc = this.document
                    checkNotNull(doc) { "Element does not belong to a document." }
                    val window = (doc as HTMLDocumentImpl).window
                    val ctx = Executor.createContext(
                        this.getDocumentURL(),
                        uac,
                        window.contextFactory
                    )
                    try {
                        val scope = window.getWindowScope()
                        checkNotNull(scope) { "Scriptable (scope) instance was null" }
                        val thisScope =
                            JavaScript.instance.getJavascriptObject(this, scope) as Scriptable?
                        try {
                            // TODO: Get right line number for script. //TODO: Optimize this
                            // in case it's called multiple times? Is that done?
                            f = ctx.compileFunction(
                                thisScope,
                                functionCode,
                                this.tagName + "[" + this.id + "]." + attributeName,
                                1,
                                null
                            )
                        } catch (ecmaError: EcmaError) {
                            logger.log(
                                Level.WARNING,
                                ("Javascript error at " + ecmaError.sourceName() + ":" + ecmaError.lineNumber() + ": "
                                        + ecmaError.message),
                                ecmaError
                            )
                            f = null
                        } catch (err: Exception) {
                            logger.log(
                                Level.WARNING,
                                "Unable to evaluate Javascript code",
                                err
                            )
                            f = null
                        }
                    } finally {
                        Context.exit()
                    }
                }
                if (fba == null) {
                    fba = HashMap<String?, Function?>(1)
                    this.functionByAttribute = fba
                }
                fba.put(normalAttributeName, f)
            }
            return f
        }
    }

    override fun handleAttributeChanged(name: String, oldValue: String?, newValue: String?) {
        super.handleAttributeChanged(name, oldValue, newValue)
        if (name.startsWith("on")) {
            synchronized(this) {
                val fba = this.functionByAttribute
                if (fba != null) {
                    fba.remove(name)
                }
            }
        }
    }
}
