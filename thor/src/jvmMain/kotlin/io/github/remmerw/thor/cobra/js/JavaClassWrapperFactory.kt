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

import java.lang.ref.WeakReference
import java.util.WeakHashMap

class JavaClassWrapperFactory private constructor() {
    private val classWrappers: MutableMap<Class<*>?, WeakReference<JavaClassWrapper?>?> =
        WeakHashMap<Class<*>?, WeakReference<JavaClassWrapper?>?>()

    fun getClassWrapper(clazz: Class<*>): JavaClassWrapper {
        synchronized(this) {
            // WeakHashMaps where the value refers to
            // the key will retain keys. Must make it
            // refer to the value weakly too.
            val jcwr: WeakReference<*>? = this.classWrappers.get(clazz)
            var jcw: JavaClassWrapper? = null
            if (jcwr != null) {
                jcw = jcwr.get() as JavaClassWrapper?
            }
            if (jcw == null) {
                // TODO: need to check with the class shutter here. GH #136
                jcw = JavaClassWrapper(clazz)
                this.classWrappers.put(clazz, WeakReference<JavaClassWrapper?>(jcw))
            }
            return jcw
        }
    }

    companion object {
        var instance: JavaClassWrapperFactory? = null
            get() {
                if (field == null) {
                    synchronized(JavaClassWrapperFactory::class.java) {
                        if (field == null) {
                            field = JavaClassWrapperFactory()
                        }
                    }
                }
                return field
            }
            private set
    }
}
