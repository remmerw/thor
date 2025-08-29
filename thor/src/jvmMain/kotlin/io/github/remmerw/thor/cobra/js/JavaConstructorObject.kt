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

import org.mozilla.javascript.Context
import org.mozilla.javascript.EcmaError
import org.mozilla.javascript.Function
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.WrappedException
import org.w3c.dom.DOMException

class JavaConstructorObject : ScriptableObject, Function {
    private val classWrapper: JavaClassWrapper
    private val instantiator: JavaInstantiator
    private val name: String?

    constructor(name: String?, classWrapper: JavaClassWrapper) {
        this.name = name
        this.classWrapper = classWrapper
        this.instantiator = SimpleInstantiator(classWrapper)
    }

    constructor(name: String?, classWrapper: JavaClassWrapper, instantiator: JavaInstantiator) {
        this.name = name
        this.classWrapper = classWrapper
        this.instantiator = instantiator
    }

    override fun getClassName(): String? {
        return this.name
    }

    override fun call(
        cx: Context?,
        scope: Scriptable?,
        thisObj: Scriptable?,
        args: Array<Any?>?
    ): Any? {
        // TODO: Implement this, or atleast remove the wrapped exception.
        //       The exception is being wrapped so that web-platform-tests don't timeout; timeouts are slowing down testing.
        throw WrappedException(UnsupportedOperationException())
    }

    override fun construct(cx: Context?, scope: Scriptable?, args: Array<Any?>?): Scriptable {
        try {
            val javaObject = this.instantiator.newInstance(args as Array<Any>)
            val newObject: Scriptable = JavaObjectWrapper(this.classWrapper, javaObject)
            newObject.parentScope = scope
            return newObject
        } catch (err: DOMException) {
            throw WrappedException(err)
        } catch (err: EcmaError) {
            throw err
        } catch (err: Exception) {
            throw IllegalStateException(err)
        }
    }

    override fun getDefaultValue(hint: Class<*>?): Any? {
        // null is passed as hint when converting to string, hence adding it as an extra condition.
        if (String::class.java == hint || (hint == null)) {
            return "function " + this.name
        } else {
            return super.getDefaultValue(hint)
        }
    }

    override fun hasInstance(instance: Scriptable?): Boolean {
        return classWrapper.hasInstance(instance)
    }

    class SimpleInstantiator(private val classWrapper: JavaClassWrapper) : JavaInstantiator {
        @Throws(InstantiationException::class, IllegalAccessException::class)
        override fun newInstance(args: Array<Any>): Any {
            return this.classWrapper.newInstance()
        }

    }

}
