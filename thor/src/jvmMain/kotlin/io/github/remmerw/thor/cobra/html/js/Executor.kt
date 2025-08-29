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
package io.github.remmerw.thor.cobra.html.js

import io.github.remmerw.thor.cobra.html.domimpl.HTMLDocumentImpl
import io.github.remmerw.thor.cobra.html.domimpl.NodeImpl
import io.github.remmerw.thor.cobra.js.JavaScript
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import io.github.remmerw.thor.cobra.ua.UserAgentContext.RequestKind
import org.mozilla.javascript.Context
import org.mozilla.javascript.ContextFactory
import org.mozilla.javascript.Function
import org.mozilla.javascript.RhinoException
import org.mozilla.javascript.Scriptable
import java.net.URL
import java.util.logging.Level
import java.util.logging.Logger

object Executor {
    private val logger: Logger = Logger.getLogger(Executor::class.java.name)

    /**
     * This method should be invoked instead of `Context.enter`.
     *
     * @param codeSource
     * @param ucontext
     */
    fun createContext(
        codeSource: URL?,
        ucontext: UserAgentContext,
        factory: ContextFactory
    ): Context {
        val prev = Context.getCurrentContext()

        // final Context ctx = Context.enter();
        val ctx = factory.enterContext()

        if (!ctx.isSealed) {
            ctx.optimizationLevel = ucontext.getScriptingOptimizationLevel()
            ctx.setLanguageVersion(Context.VERSION_ES6)

            if (prev == null) {
                // If there was a previous context, this one must be nested.
                // We still need to create a context because of exit() but
                // we cannot set a new security controller.
                ctx.setSecurityController(
                    SecurityControllerImpl(
                        codeSource
                    )
                )
            }

            // Sealing is recommended for untrusted scripts
            ctx.seal(null)
        }
        return ctx
    }

    @JvmStatic
    fun executeFunction(
        element: NodeImpl,
        f: Function,
        event: Any?,
        contextFactory: ContextFactory
    ): Boolean {
        return executeFunction(element, element, f, event, contextFactory)
    }

    private fun executeFunction(
        element: NodeImpl, thisObject: Any?, f: Function, event: Any?,
        contextFactory: ContextFactory
    ): Boolean {
        val doc = element.ownerDocument
        checkNotNull(doc) { "Element does not belong to a document." }

        val uaContext = element.userAgentContext
        if (uaContext!!.isRequestPermitted(
                UserAgentContext.Request(
                    element.documentURL,
                    RequestKind.JavaScript
                )
            )
        ) {
            val ctx = createContext(
                element.documentURL,
                element.userAgentContext!!,
                contextFactory
            )
            // ctx.setGenerateObserverCount(true);
            try {
                val scope = (doc as HTMLDocumentImpl).window.getWindowScope()
                checkNotNull(scope) { "Scriptable (scope) instance is null" }
                val js = JavaScript.instance
                val thisScope = js.getJavascriptObject(thisObject, scope) as Scriptable?
                try {
                    // final Scriptable eventScriptable = (Scriptable) js.getJavascriptObject(event, thisScope);
                    val eventScriptable = js.getJavascriptObject(event, thisScope)
                    scope.put("event", thisScope, eventScriptable)
                    // ScriptableObject.defineProperty(thisScope, "event",
                    // eventScriptable,
                    // ScriptableObject.READONLY);
                    val result = f.call(ctx, thisScope, thisScope, arrayOf<Any?>(eventScriptable))
                    if (result !is Boolean) {
                        return true
                    }
                    return result
                } catch (thrown: Exception) {
                    logJSException(thrown)
                    return true
                }
            } finally {
                Context.exit()
            }
        } else {
            // TODO: Should this be true? I am copying the return from the exception clause above.
            println("Rejected request to execute script")
            return true
        }
    }

    fun logJSException(err: Throwable?) {
        logger.log(Level.WARNING, "Unable to evaluate Javascript code", err)
        if (err is RhinoException) {
            logger.log(
                Level.WARNING,
                "JS Error: " + err.details() + "\nJS Stack:\n" + err.scriptStackTrace
            )
        }
    }

    fun executeFunction(
        thisScope: Scriptable?, f: Function, codeSource: URL?,
        ucontext: UserAgentContext, contextFactory: ContextFactory
    ): Boolean {
        val ctx = createContext(codeSource, ucontext, contextFactory)
        try {
            try {
                val result = f.call(ctx, thisScope, thisScope, arrayOfNulls<Any>(0))
                if (result !is Boolean) {
                    return true
                }
                return result
            } catch (err: Exception) {
                logJSException(err)
                return true
            }
        } finally {
            Context.exit()
        }
    }
}
