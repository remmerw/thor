/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Contact info: lobochief@users.sourceforge.net
 */
/*
 * Created on Oct 8, 2005
 */
package io.github.remmerw.thor.cobra.html.domimpl

import io.github.remmerw.thor.cobra.html.js.Executor
import io.github.remmerw.thor.cobra.html.js.Window.JSRunnableTask
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import io.github.remmerw.thor.cobra.ua.UserAgentContext.RequestKind
import io.github.remmerw.thor.cobra.util.SecurityUtil
import org.mozilla.javascript.Context
import org.w3c.dom.html.HTMLScriptElement
import java.io.IOException
import java.net.MalformedURLException
import java.security.PrivilegedAction
import java.util.Arrays
import java.util.logging.Level
import kotlin.math.min

class HTMLScriptElementImpl : HTMLElementImpl, HTMLScriptElement {
    private var text: String? = null
    private var defer = false

    constructor() : super("SCRIPT", true)

    constructor(name: String) : super(name, true)

    override fun getText(): String? {
        val t = this.text
        if (t == null) {
            return this.getRawInnerText(true)
        } else {
            return t
        }
    }

    override fun setText(text: String?) {
        this.text = text
    }

    override fun getHtmlFor(): String? {
        return this.getAttribute("htmlFor")
    }

    override fun setHtmlFor(htmlFor: String?) {
        this.setAttribute("htmlFor", htmlFor)
    }

    override fun getEvent(): String? {
        return this.getAttribute("event")
    }

    override fun setEvent(event: String?) {
        this.setAttribute("event", event)
    }

    override fun getDefer(): Boolean {
        return this.defer
    }

    override fun setDefer(defer: Boolean) {
        this.defer = defer
    }

    override fun getSrc(): String? {
        return this.getAttribute("src")
    }

    override fun setSrc(src: String?) {
        this.setAttribute("src", src)
    }

    override fun getType(): String? {
        return this.getAttribute("type")
    }

    override fun setType(type: String?) {
        this.setAttribute("type", type)
    }

    protected fun processScript() {
        val scriptType = type
        if (scriptType != null) {
            if (Arrays.stream<String?>(jsTypes).noneMatch { e: String? -> e == scriptType }) {
                (this@HTMLScriptElementImpl.document as HTMLDocumentImpl).markJobsFinished(1, false)
                return
            }
        }
        val bcontext = this.userAgentContext
        checkNotNull(bcontext) { "No user agent context." }
        val docObj = this.document
        check(docObj is HTMLDocumentImpl) { "no valid document" }
        if (bcontext.isScriptingEnabled()) {
            val text: String?
            val scriptURI: String?
            val baseLineNumber: Int
            val src = this.src
            if (src == null) {
                val request =
                    UserAgentContext.Request(docObj.getDocumentURL(), RequestKind.JavaScript)
                if (bcontext.isRequestPermitted(request)) {
                    text = this.getText()
                    scriptURI = docObj.getBaseURI()
                    baseLineNumber = 1 // TODO: Line number of inner text??
                } else {
                    text = null
                    scriptURI = null
                    baseLineNumber = -1
                }
            } else {
                this.informExternalScriptLoading()
                try {
                    val scriptURL = docObj.getFullURL(src)
                    scriptURI = scriptURL.toExternalForm()
                    // Perform a synchronous request
                    val request = bcontext.createHttpRequest() !!
                    SecurityUtil.doPrivileged<Any?>(PrivilegedAction {
                        // Code might have restrictions on accessing
                        // items from elsewhere.
                        try {
                            request.open("GET", scriptURI, false)
                            request.send(
                                null,
                                UserAgentContext.Request(scriptURL, RequestKind.JavaScript)
                            )
                        } catch (thrown: IOException) {
                            logger.log(Level.WARNING, "processScript()", thrown)
                        }
                        null
                    })
                    val status = request.status
                    if ((status != 200) && (status != 0)) {
                        this.warn("Script at [" + scriptURI + "] failed to load; HTTP status: " + status + ".")
                        return
                    }
                    text = request.responseText
                    baseLineNumber = 1
                } catch (mfe: MalformedURLException) {
                    throw IllegalArgumentException(mfe)
                }
            }

            val window = docObj.window
            if (text != null) {
                val textSub = text.substring(0, min(50, text.length)).replace("\n".toRegex(), "")
                window.addJSTaskUnchecked(
                    JSRunnableTask(
                        0,
                        "script: " + textSub,
                        object : Runnable {
                            override fun run() {
                                // final Context ctx = Executor.createContext(HTMLScriptElementImpl.this.getDocumentURL(), bcontext);
                                val ctx = Executor.createContext(
                                    this@HTMLScriptElementImpl.documentURL,
                                    bcontext,
                                    window.contextFactory
                                )
                                try {
                                    val scope = window.getWindowScope()
                                    checkNotNull(scope) { "Scriptable (scope) instance was null" }
                                    try {
                                        ctx.evaluateString(
                                            scope,
                                            text,
                                            scriptURI,
                                            baseLineNumber,
                                            null
                                        )
                                        // Why catch this?
                                        // } catch (final EcmaError ecmaError) {
                                        // logger.log(Level.WARNING,
                                        // "Javascript error at " + ecmaError.sourceName() + ":" + ecmaError.lineNumber() + ": " + ecmaError.getMessage(),
                                        // ecmaError);
                                    } catch (err: Exception) {
                                        Executor.logJSException(err)
                                    }
                                } finally {
                                    Context.exit()
                                    docObj.markJobsFinished(1, false)
                                }
                            }
                        })
                )
            } else {
                docObj.markJobsFinished(1, false)
            }
        } else {
            docObj.markJobsFinished(1, false)
        }
    }

    override fun appendInnerTextImpl(buffer: StringBuffer) {
        // nop
    }

    override fun handleDocumentAttachmentChanged() {
        if (isAttachedToDocument) {
            (document as HTMLDocumentImpl).addJob(Runnable { processScript() }, false)
        } else {
            // TODO What does script element do when detached?
        }
        super.handleDocumentAttachmentChanged()
    }

    companion object {
        private val jsTypes = arrayOf<String?>(
            "application/ecmascript",
            "application/javascript",
            "application/x-ecmascript",
            "application/x-javascript",
            "text/ecmascript",
            "text/javascript",
            "text/javascript1.0",
            "text/javascript1.1",
            "text/javascript1.2",
            "text/javascript1.3",
            "text/javascript1.4",
            "text/javascript1.5",
            "text/jscript",
            "text/livescript",
            "text/x-ecmascript",
            "text/x-javascript"
        )
    }
}
