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

import io.github.remmerw.thor.cobra.html.js.Window
import org.mozilla.javascript.EvaluatorException
import org.mozilla.javascript.Function
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.WrappedException
import java.lang.reflect.InvocationTargetException
import java.util.logging.Level
import java.util.logging.Logger

class JavaObjectWrapper : ScriptableObject {// Cannot retain delegate with a strong reference.
    /**
     * Returns the Java object.
     *
     * @return An object or `null` if garbage collected.
     */
    val javaObject: Any
    private val classWrapper: JavaClassWrapper

    constructor(classWrapper: JavaClassWrapper) {
        this.classWrapper = classWrapper
        // Retaining a strong reference, but note
        // that the object wrapper map uses weak keys
        // and weak values.
        val delegate = this.classWrapper.newInstance()
        this.javaObject = delegate
        setupProperties()
    }

    constructor(classWrapper: JavaClassWrapper, delegate: Any) {
        requireNotNull(delegate) { "Argument delegate cannot be null." }
        this.classWrapper = classWrapper
        // Retaining a strong reference, but note
        // that the object wrapper map uses weak keys
        // and weak values.
        this.javaObject = delegate
        setupProperties()
    }

    override fun setParentScope(m: Scriptable?) {
        // Don't allow Window's parent scope to be changed. Fixes GH #29
        if (classWrapper.canonicalClassName == Window::class.java.canonicalName) {
            return
        }

        if (m === this) {
            // TODO: This happens when running jQuery 2
            super.setParentScope(null)
        } else {
            super.setParentScope(m)
        }
    }

    private fun setupProperties() {

    }

    /*
  @Override
  public Object get(final int index, final Scriptable start) {
    final PropertyInfo pinfo = this.classWrapper.getIntegerIndexer();
    if (pinfo == null) {
      return super.get(index, start);
    } else {
      try {
        final Method getter = pinfo.getGetter();
        if (getter == null) {
          throw new EvaluatorException("Indexer is write-only");
        }
        // Cannot retain delegate with a strong reference.
        final Object javaObject = this.getJavaObject();
        if (javaObject == null) {
          throw new IllegalStateException("Java object (class=" + this.classWrapper + ") is null.");
        }
        final Object raw = getter.invoke(javaObject, new Object[] { new Integer(index) });
        if (raw == null) {
          // Return this instead of null.
          return Scriptable.NOT_FOUND;
        }
        return JavaScript.getInstance().getJavascriptObject(raw, this.getParentScope());
      } catch (final Exception err) {
        throw new WrappedException(err);
      }
    }
  }*/

    override fun getClassName(): String? {
        return this.classWrapper.className
    }

    override fun get(name: String?, start: Scriptable): Any? {
        val pinfo = this.classWrapper.getProperty(name)
        if (pinfo != null) {
            val getter = pinfo.getter
            if (getter == null) {
                throw EvaluatorException("Property '" + name + "' is not readable")
            }
            // Cannot retain delegate with a strong reference.
            val javaObject = this.javaObject
            checkNotNull(javaObject) { "Java object (class=" + this.classWrapper + ") is null." }
            val `val`: Any?
            try {
                `val` = getter.invoke(javaObject, null)
            } catch (e: IllegalAccessException) {
                throw RuntimeException(e)
            } catch (e: InvocationTargetException) {
                throw RuntimeException(e)
            }

            return JavaScript.Companion.instance
                .getJavascriptObject(`val`, start.parentScope)
        } else {
            val f = this.classWrapper.getFunction(name)
            if (f != null) {
                return f
            } else {
                // Should check properties set in context
                // first. Consider element IDs should not
                // override Window variables set by user.
                val result = super.get(name, start)
                if (result !== NOT_FOUND) {
                    return result
                }
                val ni = this.classWrapper.nameIndexer
                if (ni != null) {
                    val getter = ni.getter
                    if (getter != null) {
                        // Cannot retain delegate with a strong reference.
                        val javaObject = this.javaObject
                        checkNotNull(javaObject) { "Java object (class=" + this.classWrapper + ") is null." }
                        try {
                            val `val` = getter.invoke(javaObject, name)
                            if (`val` == null) {
                                // There might not be an indexer setter.
                                return super.get(name, start)
                            } else {
                                return JavaScript.Companion.instance
                                    .getJavascriptObject(`val`, start.parentScope)
                            }
                        } catch (err: Exception) {
                            throw WrappedException(err)
                        }
                    }
                }
                return NOT_FOUND
            }
        }
    }

    override fun put(index: Int, start: Scriptable?, value: Any?) {
        val pinfo = this.classWrapper.integerIndexer
        if (pinfo == null) {
            super.put(index, start, value)
        } else {
            try {
                val setter = pinfo.setter
                if (setter == null) {
                    throw EvaluatorException("Indexer is read-only")
                }
                val actualValue: Any?
                actualValue =
                    JavaScript.Companion.instance.getJavaObject(value, pinfo.propertyType)
                setter.invoke(this.javaObject, (index), actualValue)
            } catch (err: Exception) {
                throw WrappedException(err)
            }
        }
    }

    override fun put(name: String?, start: Scriptable?, value: Any?) {

    }

    override fun getDefaultValue(hint: Class<*>?): Any? {
        if (loggableInfo) {
            logger.info("getDefaultValue(): hint=" + hint + ",this=" + this.javaObject)
        }
        if ((hint == null) || String::class.java == hint) {
            val javaObject = this.javaObject
            checkNotNull(javaObject) { "Java object (class=" + this.classWrapper + ") is null." }
            return javaObject.toString()
        } else if (Number::class.java.isAssignableFrom(hint)) {
            val javaObject = this.javaObject
            if (javaObject is Number) {
                return javaObject
            } else if (javaObject is String) {
                return javaObject.toDouble()
            } else {
                return super.getDefaultValue(hint)
            }
        } else {
            return super.getDefaultValue(hint)
        }
    }

    override fun toString(): String {
        val javaObject = this.javaObject
        val type = if (javaObject == null) "<null>" else javaObject.javaClass.name
        return "JavaObjectWrapper[object=" + this.javaObject + ",type=" + type + "]"
    }

    override fun hasInstance(instance: Scriptable?): Boolean {
        return false
    }

    // TODO: Override has(int index) also
    override fun has(name: String?, start: Scriptable?): Boolean {
        // TODO: should the start parameter be considered here?
        if (classWrapper.properties
                .containsKey(name) || classWrapper.staticFinalProperties.containsKey(name)
        ) {
            return true
        }
        return super.has(name, start)
    }

    companion object {

        private val logger: Logger = Logger.getLogger(JavaObjectWrapper::class.java.name)
        private val loggableInfo: Boolean = logger.isLoggable(Level.INFO)
        fun getConstructor(
            className: String?,
            classWrapper: JavaClassWrapper,
            scope: Scriptable?
        ): Function {
            return JavaConstructorObject(className, classWrapper)
        }

        fun getConstructor(
            className: String?, classWrapper: JavaClassWrapper, scope: Scriptable?,
            instantiator: JavaInstantiator
        ): Function {
            return JavaConstructorObject(className, classWrapper, instantiator)
        }
    }
}
