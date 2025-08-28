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
import org.mozilla.javascript.ExternalArrayData
import org.mozilla.javascript.Function
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.Undefined
import org.mozilla.javascript.WrappedException
import java.lang.reflect.Field
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
        val integerIndexer = classWrapper.getIntegerIndexer()
        if (integerIndexer != null) {
            setExternalArrayData(object : ExternalArrayData {
                override fun getArrayLength(): Int {
                    try {
                        // TODO: Some length() methods are returning integer while others return length. A good test case is http://web-platform.test:8000/dom/nodes/Element-classlist.html
                        //       Check if length() methods can be converted to return a single type.
                        val lengthObj = classWrapper.getProperty("length").getGetter().invoke(
                            this.javaObject, *null as Array<Any?>?
                        )
                        if (lengthObj is Long) {
                            val lengthLong = lengthObj
                            val lengthInt = lengthLong.toInt()
                            // TODO: Check for overflow when casting to int and throw an exception
                            return lengthInt
                        } else if (lengthObj is Int) {
                            return lengthObj
                        } else {
                            // TODO: Throw exception
                            throw RuntimeException("Can't represent length as an integer type")
                        }
                    } catch (e: IllegalAccessException) {
                        // TODO Auto-generated catch block
                        e.printStackTrace()
                        return 0
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                        return 0
                    } catch (e: InvocationTargetException) {
                        e.printStackTrace()
                        return 0
                    }
                }

                override fun getArrayElement(index: Int): Any? {
                    if (index < 0) {
                        // TODO: The interface's javadoc says that this method is only called for indices are within range.
                        //       Need to check if negative values are considered in range. Negative indices are being used in
                        //       one of the web-platform-tests
                        return Undefined.instance
                    }
                    try {
                        val result: Any? = JavaScript.Companion.getInstance().getJavascriptObject(
                            integerIndexer.getGetter().invoke(this.javaObject, index), null
                        )
                        return result
                    } catch (e: IllegalAccessException) {
                        throw RuntimeException("Error accessing a indexed element")
                    } catch (e: IllegalArgumentException) {
                        throw RuntimeException("Error accessing a indexed element")
                    } catch (e: InvocationTargetException) {
                        throw RuntimeException("Error accessing a indexed element")
                    }
                }

                override fun setArrayElement(index: Int, value: Any?) {
                    // TODO: Can this be supported? Needs a setter.
                    throw UnsupportedOperationException("Writing to an indexed object")
                }
            })
        }
        classWrapper.properties.forEach { (name: String?, property: PropertyInfo?) ->
            // TODO: Don't setup properties if getter is null? Are write-only properties supported in JS?
            defineProperty(name, null, property!!.getter, property.setter, 0)
        }
        classWrapper.staticFinalProperties.forEach { (name: String?, field: Field?) ->
            try {
                defineProperty(name, field!!.get(null), READONLY)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
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
        return this.classWrapper.getClassName()
    }

    override fun get(name: String?, start: Scriptable): Any? {
        val pinfo = this.classWrapper.getProperty(name)
        if (pinfo != null) {
            val getter = pinfo.getGetter()
            if (getter == null) {
                throw EvaluatorException("Property '" + name + "' is not readable")
            }
            // Cannot retain delegate with a strong reference.
            val javaObject = this.javaObject
            checkNotNull(javaObject) { "Java object (class=" + this.classWrapper + ") is null." }
            val `val`: Any?
            try {
                `val` = getter.invoke(javaObject, *null as Array<Any?>?)
            } catch (e: IllegalAccessException) {
                throw RuntimeException(e)
            } catch (e: InvocationTargetException) {
                throw RuntimeException(e)
            }

            return JavaScript.Companion.getInstance()
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
                val ni = this.classWrapper.getNameIndexer()
                if (ni != null) {
                    val getter = ni.getGetter()
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
                                return JavaScript.Companion.getInstance()
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
        val pinfo = this.classWrapper.getIntegerIndexer()
        if (pinfo == null) {
            super.put(index, start, value)
        } else {
            try {
                val setter = pinfo.getSetter()
                if (setter == null) {
                    throw EvaluatorException("Indexer is read-only")
                }
                val actualValue: Any?
                actualValue =
                    JavaScript.Companion.getInstance().getJavaObject(value, pinfo.getPropertyType())
                setter.invoke(this.javaObject, (index), actualValue)
            } catch (err: Exception) {
                throw WrappedException(err)
            }
        }
    }

    override fun put(name: String?, start: Scriptable?, value: Any?) {
        if (value is Undefined) {
            super.put(name, start, value)
        } else {
            val pinfo = this.classWrapper.getProperty(name)
            if (pinfo != null) {
                val setter = pinfo.getSetter()
                if (setter == null) {
                    throw EvaluatorException("Property '" + name + "' is not settable in " + this.classWrapper.getClassName() + ".")
                }
                try {
                    val actualValue: Any? = JavaScript.Companion.getInstance()
                        .getJavaObject(value, pinfo.getPropertyType())
                    setter.invoke(this.javaObject, actualValue)
                } catch (iae: IllegalArgumentException) {
                    val newException: Exception = IllegalArgumentException(
                        ("Property named '" + name + "' could not be set with value " + value
                                + "."),
                        iae
                    )
                    throw WrappedException(newException)
                } catch (err: Exception) {
                    throw WrappedException(err)
                }
            } else {
                val ni = this.classWrapper.getNameIndexer()
                if (ni != null) {
                    val setter = ni.getSetter()
                    if (setter != null) {
                        try {
                            val actualValue: Any?
                            actualValue = JavaScript.Companion.getInstance()
                                .getJavaObject(value, ni.getPropertyType())
                            setter.invoke(this.javaObject, name, actualValue)
                        } catch (err: Exception) {
                            throw WrappedException(err)
                        }
                    } else {
                        super.put(name, start, value)
                    }
                } else {
                    super.put(name, start, value)
                }
            }
        }
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
        if ((instance is JavaObjectWrapper) && (this.javaObject is Class<*>)) {
            return myClass.isInstance(instance.javaObject)
        } else {
            return super.hasInstance(instance)
        }
    }

    // TODO: Override has(int index) also
    override fun has(name: String?, start: Scriptable?): Boolean {
        // TODO: should the start parameter be considered here?
        if (classWrapper.getProperties()
                .containsKey(name) || classWrapper.getStaticFinalProperties().containsKey(name)
        ) {
            return true
        }
        return super.has(name, start)
    }

    companion object {
        private val serialVersionUID = -2669458528000105312L
        private val logger: Logger = Logger.getLogger(JavaObjectWrapper::class.java.name)
        private val loggableInfo: Boolean = logger.isLoggable(Level.INFO)
        fun getConstructor(
            className: String?,
            classWrapper: JavaClassWrapper?,
            scope: Scriptable?
        ): Function {
            return JavaConstructorObject(className, classWrapper)
        }

        fun getConstructor(
            className: String?, classWrapper: JavaClassWrapper?, scope: Scriptable?,
            instantiator: JavaInstantiator?
        ): Function {
            return JavaConstructorObject(className, classWrapper, instantiator)
        }
    }
}
