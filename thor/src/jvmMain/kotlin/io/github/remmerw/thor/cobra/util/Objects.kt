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
 * Created on Apr 10, 2005
 */
package io.github.remmerw.thor.cobra.util

/**
 * @author J. H. S.
 */
object Objects {
    fun isBoxClass(clazz: Class<*>?): Boolean {
        return (clazz == Int::class.java) || (clazz == Boolean::class.java) || (clazz == Double::class.java) || (clazz == Float::class.java)
                || (clazz == Long::class.java)
                || (clazz == Byte::class.java) || (clazz == Short::class.java) || (clazz == Char::class.java)
    }

    /* Checks whether the arguments are an exact match to the parameter types */
    fun areSameTo(objects: Array<out Any?>, types: Array<Class<*>?>): Boolean {
        val length = objects.size
        if (length != types.size) {
            return false
        }
        for (i in 0..<length) {
            if (!isSameOrBox(objects[i], types[i]!!)) {
                return false
            }
        }
        return true
    }

    /* Checks whether a value is an exact match to the clazz */
    fun isSameOrBox(value: Any?, clazz: Class<out Any?>): Boolean {
        if (clazz.isInstance(value)) {
            return true
        }
        if (clazz.isPrimitive) {
            return ((clazz == Double::class.javaPrimitiveType) && (value is Double)) || ((clazz == Int::class.javaPrimitiveType) && (value is Int))
                    || ((clazz == Long::class.javaPrimitiveType) && (value is Long)) || ((clazz == Boolean::class.javaPrimitiveType) && (value is Boolean))
                    || ((clazz == Byte::class.javaPrimitiveType) && (value is Byte)) || ((clazz == Char::class.javaPrimitiveType) && (value is Char))
                    || ((clazz == Short::class.javaPrimitiveType) && (value is Short)) || ((clazz == Float::class.javaPrimitiveType) && (value is Float))
        }

        return false
    }

    fun areAssignableTo(objects: Array<Any?>, types: Array<Class<*>?>): Boolean {
        val length = objects.size
        if (length != types.size) {
            return false
        }
        for (i in 0..<length) {
            if (!isAssignableOrBox(objects[i], types[i]!!)) {
                return false
            }
        }
        return true
    }

    fun isAssignableOrBox(value: Any?, clazz: Class<out Any?>): Boolean {
        if (clazz.isInstance(value)) {
            return true
        }
        if (clazz.isPrimitive) {
            if (((clazz == Double::class.javaPrimitiveType) && (value is Double)) || ((clazz == Int::class.javaPrimitiveType) && (value is Int))
                || ((clazz == Long::class.javaPrimitiveType) && (value is Long)) || ((clazz == Boolean::class.javaPrimitiveType) && (value is Boolean))
                || ((clazz == Byte::class.javaPrimitiveType) && (value is Byte)) || ((clazz == Char::class.javaPrimitiveType) && (value is Char))
                || ((clazz == Short::class.javaPrimitiveType) && (value is Short)) || ((clazz == Float::class.javaPrimitiveType) && (value is Float))
            ) {
                return true
            }
        }
        if (Objects.isNumeric(clazz) && isNumeric(value)) {
            return true
        }
        if (clazz.isAssignableFrom(String::class.java)) {
            return (value == null) || !value.javaClass.isPrimitive
        }
        return false
    }

    private fun isNumeric(clazz: Class<out Any?>): Boolean {
        return Number::class.java.isAssignableFrom(clazz)
                || (clazz.isPrimitive && ((clazz == Int::class.javaPrimitiveType) || (clazz == Double::class.javaPrimitiveType) || (clazz == Byte::class.javaPrimitiveType) || (clazz == Short::class.javaPrimitiveType)
                || (clazz == Float::class.javaPrimitiveType) || (clazz == Long::class.javaPrimitiveType)))
    }

    private fun isNumeric(value: Any?): Boolean {
        if (value == null) {
            return false
        }
        return Objects.isNumeric(value.javaClass)
    }
}
