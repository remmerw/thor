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
package io.github.remmerw.thor.cobra.js

import io.github.remmerw.thor.cobra.util.Objects
import org.mozilla.javascript.Callable
import org.mozilla.javascript.Context
import org.mozilla.javascript.EvaluatorException
import org.mozilla.javascript.Function
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.WrappedException
import org.w3c.dom.DOMException
import java.lang.reflect.Array
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.Arrays
import java.util.logging.Level
import java.util.logging.Logger

class JavaFunctionObject(private val methodName: String?, private val className: String?) :
    ScriptableObject(), Function {
    private val methods = ArrayList<Method>()

    init {
        // TODO: Review
        // Quick hack for issue #98
        defineProperty("call", object : Callable {
            override fun call(
                cx: Context,
                scope: Scriptable,
                thisObj: Scriptable,
                args: kotlin.Array<out Any?>
            ): Any? {
                if ((args.isNotEmpty()) && (args.get(0) is JavaObjectWrapper)) {
                    return this@JavaFunctionObject.call(
                        cx,
                        scope,
                        args.get(0) as JavaObjectWrapper,
                        Arrays.copyOfRange<Any?>(args, 1, args.size)
                    )
                } else {
                    throw RuntimeException("Unexpected condition")
                }
            }

        }, READONLY)
    }

    fun addMethod(m: Method?) {
        this.methods.add(m!!)
    }

    /*
  private static String getTypeName(final Object object) {
    return object == null ? "[null]" : object.getClass().getName();
  }*/
    override fun getClassName(): String {
        return "JavaFunctionObject"
    }

    private fun getExactMethod(args: kotlin.Array<out Any?>?): MethodAndArguments? {
        val methods = this.methods
        val size = methods.size
        for (i in 0..<size) {
            val m = methods.get(i)
            val parameterTypes = m.parameterTypes
            if (args == null) {
                if ((parameterTypes == null) || (parameterTypes.size == 0)) {
                    return MethodAndArguments(m, emptyArray())
                }
            } else if (parameterTypes != null) {
                if (args.size == parameterTypes.size) {
                    if (Objects.areSameTo(args, parameterTypes)) {
                        return MethodAndArguments(m, emptyArray())
                    }
                } else if ((parameterTypes.size == 1) && parameterTypes[0].isArray) {
                    val arrayType = parameterTypes[0].componentType
                    val allSame = true
                    for (j in args.indices) {
                        if (!Objects.isSameOrBox(args[j], arrayType)) {
                            break
                        }
                    }
                    if (allSame) {
                        val argsInArray =
                            Array.newInstance(arrayType, args.size) as kotlin.Array<Any>
                        System.arraycopy(args, 0, argsInArray, 0, args.size)
                        return MethodAndArguments(m, argsInArray)
                    }
                }
            }
        }
        return null
    }

    private fun getBestMethod(args: kotlin.Array<Any?>?): MethodAndArguments? {
        val exactMethod = getExactMethod(args)
        if (exactMethod != null) {
            return exactMethod
        }

        val methods = this.methods
        val size = methods.size
        var matchingNumParams = 0
        var matchingMethod: Method? = null
        for (i in 0..<size) {
            val m = methods.get(i)
            val parameterTypes = m.parameterTypes
            if (args == null) {
                if ((parameterTypes == null) || (parameterTypes.size == 0)) {
                    return MethodAndArguments(m, emptyArray())
                }
            } else if ((parameterTypes != null) && (args.size >= parameterTypes.size)) {
                if (Objects.areAssignableTo(args, parameterTypes)) {
                    val actualArgs: kotlin.Array<Any?> =
                        convertArgs(args, parameterTypes.size, parameterTypes)
                    return MethodAndArguments(m, actualArgs as kotlin.Array<Any>)
                }
                if ((matchingMethod == null) || (parameterTypes.size > matchingNumParams)) {
                    matchingNumParams = parameterTypes.size
                    matchingMethod = m
                }
            }
        }
        check(size != 0) { "zero methods" }
        if (matchingMethod == null) {
            return null
        } else {
            val actualArgTypes = matchingMethod.parameterTypes
            val actualArgs: kotlin.Array<Any?> =
                convertArgs(args, matchingNumParams, actualArgTypes)
            return MethodAndArguments(matchingMethod, actualArgs as kotlin.Array<Any>)
        }
    }

    override fun call(
        cx: Context?,
        scope: Scriptable?,
        thisObj: Scriptable?,
        args: kotlin.Array<Any?>?
    ): Any? {
        val methodAndArguments = this.getBestMethod(args)
        if (methodAndArguments == null) {
            throw EvaluatorException(
                ("No method matching " + this.methodName + " with " + (if (args == null) 0 else args.size) + " arguments in "
                        + className + " .")
            )
        }
        val manager: JavaScript = JavaScript.Companion.instance
        try {
            if (thisObj is JavaObjectWrapper) {
                // if(linfo) {
                // Object javaObject = jcw.getJavaObject();
                // logger.info("call(): Calling method " + method.getName() +
                // " on object " + javaObject + " of type " +
                // this.getTypeName(javaObject));
                // }
                val raw = methodAndArguments.invoke(thisObj.javaObject)
                return manager.getJavascriptObject(raw, scope)
            } else {
                // if (args[0] instanceof Function ) {
                // Function func = (Function) args[0];
                // Object raw = func.call(cx, scope, scope, Arrays.copyOfRange(args, 1,
                // args.length));
                // return manager.getJavascriptObject(raw, scope);
                // } else {
                val raw = methodAndArguments.invoke(thisObj)
                return manager.getJavascriptObject(raw, scope)

                // }

                // Based on http://stackoverflow.com/a/16479685/161257
                // return call(cx, scope, getParentScope(), args);
            }
        } catch (iae: IllegalAccessException) {
            throw IllegalStateException("Unable to call " + this.methodName + ".", iae)
        } catch (ite: InvocationTargetException) {
            if (ite.cause is DOMException) {
                throw WrappedException(ite.cause)
            }
            throw WrappedException(
                InvocationTargetException(
                    ite.cause,
                    "Unable to call " + this.methodName + " on " + thisObj + "."
                )
            )
        } catch (iae: IllegalArgumentException) {
            val argTypes = StringBuffer()
            var i = 0
            while (i < methodAndArguments.args.size) {
                if (i > 0) {
                    argTypes.append(", ")
                }
                argTypes.append(if (methodAndArguments.args[i] == null) "<null>" else methodAndArguments.args[i]!!.javaClass.name)
                i++
            }
            throw WrappedException(
                IllegalArgumentException(
                    ("Unable to call " + this.methodName + " in " + className
                            + ". Argument types: " + argTypes + "." + "\n  on method: " + methodAndArguments.method),
                    iae
                )
            )
        }
    }

    override fun getDefaultValue(hint: Class<*>?): Any? {
        if (loggableInfo) {
            logger.info("getDefaultValue(): hint=" + hint + ",this=" + this)
        }
        if ((hint == null) || String::class.java == hint) {
            return "function " + this.methodName
        } else {
            return super.getDefaultValue(hint)
        }
    }

    override fun construct(
        cx: Context?,
        scope: Scriptable?,
        args: kotlin.Array<Any?>?
    ): Scriptable? {
        throw UnsupportedOperationException()
    }

    private class MethodAndArguments(
        val method: Method,
        val args: kotlin.Array<Any>
    ) {
        @Throws(
            IllegalAccessException::class,
            IllegalArgumentException::class,
            InvocationTargetException::class
        )
        fun invoke(javaObject: Any?): Any? {
            return method.invoke(javaObject, *args)
        }

        override fun toString(): String {
            return "MethodAndArguments [method=" + method + ", args=" + args.contentToString() + "]"
        }
    }

    companion object {
        private const val serialVersionUID = 3716471130167741876L
        private val logger: Logger = Logger.getLogger(JavaFunctionObject::class.java.name)
        private val loggableInfo: Boolean = logger.isLoggable(Level.INFO)
        private fun convertArgs(
            args: kotlin.Array<Any?>?,
            numConvert: Int,
            actualArgTypes: kotlin.Array<Class<*>?>
        ): kotlin.Array<Any?> {
            val manager: JavaScript = JavaScript.Companion.instance
            val actualArgs =
                if (args == null) arrayOfNulls<Any>(0) else arrayOfNulls<Any>(numConvert)
            if (args != null) {
                for (i in 0..<numConvert) {
                    val arg = args[i]
                    actualArgs[i] = manager.getJavaObject(arg, actualArgTypes[i])
                }
            }
            return actualArgs
        }
    }
}
