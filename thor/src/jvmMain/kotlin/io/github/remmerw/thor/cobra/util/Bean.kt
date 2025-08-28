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
 * Created on Mar 20, 2005
 */
package io.github.remmerw.thor.cobra.util

import java.beans.IntrospectionException
import java.beans.Introspector
import java.beans.PropertyDescriptor

//import java.util.logging.*;

/**
 * @author J. H. S.
 */
class Bean(// private static final java.util.logging.Logger logger =
    // Logger.getLogger(Bean.class);
    private val clazz: Class<*>
) {
    private var propertyDescriptors: MutableMap<String?, PropertyDescriptor>? = null

    @Throws(IntrospectionException::class)
    private fun populateDescriptors(map: MutableMap<String?, PropertyDescriptor>, clazz: Class<*>) {
        val beanInfo = Introspector.getBeanInfo(clazz)
        val pds = beanInfo.propertyDescriptors
        for (pd in pds) {
            map.put(pd.name, pd)
        }
        if (clazz.isInterface) {
            val interfaces = clazz.genericInterfaces
            for (interface1 in interfaces) {
                this.populateDescriptors(map, (interface1 as Class<*>?)!!)
            }
        }
    }

    @Throws(IntrospectionException::class)
    fun getPropertyDescriptor(propertyName: String?): PropertyDescriptor {
        synchronized(this) {
            if (this.propertyDescriptors == null) {
                this.propertyDescriptors = HashMap<String?, PropertyDescriptor>()
                this.populateDescriptors(this.propertyDescriptors!!, this.clazz)
            }
            return this.propertyDescriptors!!.get(propertyName)!!
        }
    }

    @get:Throws(IntrospectionException::class)
    val propertyDescriptorsMap: MutableMap<String?, PropertyDescriptor>?
        get() {
            synchronized(this) {
                if (this.propertyDescriptors == null) {
                    this.propertyDescriptors =
                        HashMap<String?, PropertyDescriptor>()
                    this.populateDescriptors(this.propertyDescriptors!!, this.clazz)
                }
                return this.propertyDescriptors
            }
        }

    @Throws(IntrospectionException::class)
    fun getPropertyDescriptors(): Array<PropertyDescriptor?> {
        synchronized(this) {
            return this.propertyDescriptorsMap!!.values.toTypedArray<PropertyDescriptor?>()
        }
    }

    @Throws(Exception::class)
    fun setPropertyForFQN(receiver: Any?, fullyQualifiedPropertyName: String, value: Any?) {
        val idx = fullyQualifiedPropertyName.indexOf('.')
        if (idx == -1) {
            val pd = this.getPropertyDescriptor(fullyQualifiedPropertyName)
            checkNotNull(pd) { "Property '" + fullyQualifiedPropertyName + "' unknown" }
            val method = pd.writeMethod
            checkNotNull(method) { "Property '" + fullyQualifiedPropertyName + "' not settable" }
            val actualValue: Any? = convertValue(value, pd.propertyType)
            method.invoke(receiver, actualValue)
        } else {
            val prefix = fullyQualifiedPropertyName.substring(0, idx)
            val pinfo = this.getPropertyDescriptor(prefix)
            checkNotNull(pinfo) { "Property '" + prefix + "' unknown" }
            val readMethod = pinfo.readMethod
            checkNotNull(readMethod) { "Property '" + prefix + "' not readable" }
            val newReceiver = readMethod.invoke(receiver)
            // Class newClass = pinfo.getPropertyType();
            val nameRest = fullyQualifiedPropertyName.substring(idx + 1)
            this.setPropertyForFQN(newReceiver, nameRest, value)
        }
    }

    companion object {
        private fun convertValue(value: Any?, targetType: Class<*>): Any? {
            var value = value
            val targetString = targetType.isAssignableFrom(String::class.java)
            if ((value is String) && targetString) {
                // ignore
            } else if (targetString) {
                value = value.toString()
            } else if (value !is Byte && ((targetType == Byte::class.java) || (targetType == Byte::class.javaPrimitiveType))) {
                value = value.toString().toByte()
            } else if (value !is Boolean && ((targetType == Boolean::class.java) || (targetType == Boolean::class.javaPrimitiveType))) {
                value = value.toString().toBoolean()
            } else if (value !is Short && ((targetType == Short::class.java) || (targetType == Short::class.javaPrimitiveType))) {
                value = value.toString().toShort()
            } else if (value !is Int && ((targetType == Int::class.java) || (targetType == Int::class.javaPrimitiveType))) {
                value = value.toString().toInt()
            } else if (value !is Long && ((targetType == Long::class.java) || (targetType == Long::class.javaPrimitiveType))) {
                value = value.toString().toLong()
            }
            return value
        }
    }
}
