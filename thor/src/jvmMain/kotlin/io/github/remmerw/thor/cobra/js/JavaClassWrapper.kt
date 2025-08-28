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

import io.github.remmerw.thor.cobra.html.js.NotGetterSetter
import io.github.remmerw.thor.cobra.html.js.PropertyName
import org.mozilla.javascript.Function
import org.mozilla.javascript.Scriptable
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class JavaClassWrapper(private val javaClass: Class<*>) {
    private val functions: MutableMap<String?, JavaFunctionObject?> =
        HashMap<String?, JavaFunctionObject?>()
    val properties: MutableMap<String?, PropertyInfo?> = HashMap<String?, PropertyInfo?>()

    val staticFinalProperties: MutableMap<String?, Field?> = HashMap<String?, Field?>()

    var nameIndexer: PropertyInfo? = null
        private set
    var integerIndexer: PropertyInfo? = null
        private set

    init {
        this.scanMethods()
    }

    @Throws(InstantiationException::class, IllegalAccessException::class)
    fun newInstance(): Any {
        return this.javaClass.newInstance()
    }

    val className: String
        get() {
            val className = this.javaClass.name
            val lastDotIdx = className.lastIndexOf('.')
            return if (lastDotIdx == -1) className else className.substring(lastDotIdx + 1)
        }

    val canonicalClassName: String?
        get() = this.javaClass.canonicalName

    fun getFunction(name: String?): Function? {
        return this.functions.get(name)
    }

    fun getProperty(name: String?): PropertyInfo? {
        return this.properties.get(name)
    }

    private fun scanMethods() {
        val fields: Array<Field> = extractFields(javaClass)
        for (f in fields) {
            val modifiers = f.modifiers
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(
                    modifiers
                )
            ) {
                staticFinalProperties.put(f.name, f)
            }
        }

        val methods: Array<Method> = extractMethods(javaClass)
        val len = methods.size
        for (i in 0..<len) {
            val method = methods[i]
            // TODO: Need a more robust blocking mechanism. GH #125
            val blocked = method.declaringClass.canonicalName.startsWith("java")
            if (!(blocked || method.isAnnotationPresent(HideFromJS::class.java))) {
                val name = method.name
                if (isPropertyMethod(name, method)) {
                    this.ensurePropertyKnown(name, method)
                } else {
                    if (isNameIndexer(name, method)) {
                        this.updateNameIndexer(name, method)
                    } else if (isIntegerIndexer(name, method)) {
                        this.updateIntegerIndexer(name, method)
                    }
                    var f = this.functions.get(name)
                    if (f == null) {
                        f = JavaFunctionObject(name, javaClass.name)
                        this.functions.put(name, f)
                    }
                    f.addMethod(method)
                }
            }
        }
    }

    private fun updateNameIndexer(methodName: String, method: Method?) {
        val getter = !methodName.startsWith("set")
        var indexer = this.nameIndexer
        if (indexer == null) {
            indexer = PropertyInfo("\$item", Any::class.java)
            this.nameIndexer = indexer
        }
        if (getter) {
            indexer.setGetter(method)
        } else {
            indexer.setSetter(method)
        }
    }

    private fun updateIntegerIndexer(methodName: String, method: Method) {
        val getter = !methodName.startsWith("set")
        var indexer = this.integerIndexer
        if (indexer == null) {
            val pt = if (getter) method.returnType else method.parameterTypes[1]
            indexer = PropertyInfo("\$item", pt)
            this.integerIndexer = indexer
        }
        if (getter) {
            indexer.setGetter(method)
        } else {
            indexer.setSetter(method)
        }
    }

    private fun ensurePropertyKnown(methodName: String, method: Method) {
        val capPropertyName: String?
        var getter = false
        var setter = false
        if (methodName.startsWith("get")) {
            capPropertyName = methodName.substring(3)
            getter = true
        } else if (methodName.startsWith("set")) {
            capPropertyName = methodName.substring(3)
            setter = method.returnType == Void.TYPE
        } else if (methodName.startsWith("is")) {
            capPropertyName = methodName.substring(2)
            getter = true
        } else {
            throw IllegalArgumentException("methodName=" + methodName)
        }

        val propertyNameAnnotation = method.getAnnotation<PropertyName?>(PropertyName::class.java)
        val propertyName =
            if (propertyNameAnnotation != null) propertyNameAnnotation.value else propertyUncapitalize(
                capPropertyName
            )

        var pinfo = this.properties.get(propertyName)
        if (pinfo == null) {
            val pt = if (getter) method.returnType else method.parameterTypes[0]
            pinfo = PropertyInfo(propertyName, pt)
            this.properties.put(propertyName, pinfo)
        }
        if (getter) {
            pinfo.setGetter(method)
        }
        if (setter) {
            pinfo.setSetter(method)
        }
    }

    override fun toString(): String {
        return this.javaClass.name
    }

    fun hasInstance(instance: Scriptable?): Boolean {
        if (instance is JavaObjectWrapper) {
            return javaClass.isInstance(instance.getJavaObject())
        }
        return javaClass.isInstance(instance)
    }

    companion object {
        private fun extractFields(jClass: Class<*>): Array<Field> {
            try {
                return jClass.fields
            } catch (ace: Throwable) {
                // TODO: Try looking at individual interfaces implemented by the class
                //return new Field[0];
                throw RuntimeException("Couldn't access fields of a class")
            }
        }

        private fun extractMethods(jClass: Class<*>): Array<Method> {
            try {
                return jClass.methods
            } catch (ace: Throwable) {
                // TODO: Try looking at individual interfaces implemented by the class
                // return new Method[0];
                throw RuntimeException("Couldn't access methods of a class")
            }
        }

        private fun isNameIndexer(name: String?, method: Method): Boolean {
            return ("namedItem" == name && (method.parameterTypes.size == 1))
                    || ("setNamedItem" == name && (method.parameterTypes.size == 2))
        }

        private fun isIntegerIndexer(name: String?, method: Method): Boolean {
            return ("item" == name && (method.parameterTypes.size == 1))
                    || ("setItem" == name && (method.parameterTypes.size == 2))
        }

        private fun isPropertyMethod(name: String, method: Method): Boolean {
            if (method.isAnnotationPresent(NotGetterSetter::class.java)) {
                return false
            } else {
                if (name.startsWith("get") || name.startsWith("is")) {
                    return method.parameterTypes.size == 0
                } else if (name.startsWith("set")) {
                    return method.parameterTypes.size == 1
                } else {
                    return false
                }
            }
        }

        private fun propertyUncapitalize(text: String): String {
            try {
                if ((text.length > 1) && Character.isUpperCase(text.get(1))) {
                    // If second letter is capitalized, don't uncapitalize,
                    // e.g. getURL.
                    return text
                }
                return text.get(0).lowercaseChar().toString() + text.substring(1)
            } catch (iob: IndexOutOfBoundsException) {
                return text
            }
        }
    }
}
