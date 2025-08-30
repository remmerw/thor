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

import io.github.remmerw.thor.cobra.js.ScriptableDelegate
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import org.mozilla.javascript.Scriptable

class Navigator internal constructor(private val context: UserAgentContext) :
    ScriptableDelegate {
    var scriptable: Scriptable? = null

    override fun scriptable(): Scriptable? {
        return scriptable
    }
    var mimeTypes: MimeTypesCollection? = null
        get() {
            synchronized(this) {
                var mt = field
                if (mt == null) {
                    mt = MimeTypesCollection()
                    field = mt
                }
                return mt
            }
        }
        private set

    val appCodeName: String?
        get() = this.context.getAppCodeName()

    val appName: String?
        get() = this.context.getAppName()

    val appVersion: String?
        get() = this.context.getAppVersion()

    val appMinorVersion: String?
        get() = this.context.getAppMinorVersion()

    val platform: String?
        get() = this.context.getPlatform()

    val userAgent: String?
        get() = this.context.getUserAgent()

    val vendor: String?
        get() = this.context.getVendor()

    val product: String?
        get() = this.context.getProduct()

    fun javaEnabled(): Boolean {
        // True always?
        return true
    }

    inner class MimeTypesCollection {
        val length: Int
            // Class must be public to allow JavaScript access
            get() = 0

        fun item(index: Int): Any? {
            return null
        }

        fun namedItem(name: String?): Any? {
            return null
        }
    }
}