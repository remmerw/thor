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
 * Created on Oct 9, 2005
 */
package io.github.remmerw.thor.dom

import org.w3c.dom.DOMConfiguration
import org.w3c.dom.DOMException
import org.w3c.dom.DOMStringList

class DOMConfigurationImpl : DOMConfiguration {
    private val parameters: MutableMap<String, Any> = HashMap()

    @Throws(DOMException::class)
    override fun setParameter(name: String, value: Any) {
        synchronized(this) {
            this.parameters.put(name, value)
        }
    }

    @Throws(DOMException::class)
    override fun getParameter(name: String): Any? {
        synchronized(this) {
            return this.parameters[name]
        }
    }

    override fun canSetParameter(name: String?, value: Any?): Boolean {
        // TODO
        return true
    }

    override fun getParameterNames(): DOMStringList {
        synchronized(this) {
            return DOMStringListImpl(parameters.keys.toList())
        }
    }
}
