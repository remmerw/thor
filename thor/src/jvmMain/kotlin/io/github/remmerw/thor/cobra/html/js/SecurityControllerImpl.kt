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

import org.mozilla.javascript.Callable
import org.mozilla.javascript.Context
import org.mozilla.javascript.GeneratedClassLoader
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.SecurityController
import java.net.URL
import java.security.CodeSource
import java.security.PrivilegedAction
import java.security.ProtectionDomain
import java.security.SecureClassLoader
import java.security.cert.Certificate

class SecurityControllerImpl(private val url: URL?) :
    SecurityController() {
    private val codesource: CodeSource

    init {
        this.codesource = CodeSource(this.url, null as Array<Certificate?>?)
    }

    override fun callWithDomain(
        securityDomain: Any?, ctx: Context?, callable: Callable, scope: Scriptable?,
        thisObj: Scriptable?, args: Array<Any?>?
    ): Any? {
        if (securityDomain == null) {
            // TODO: Investigate
            return callable.call(ctx, scope, thisObj, args)
        } else {
            PrivilegedAction { callable.call(ctx, scope, thisObj, args) }
            securityDomain as ProtectionDomain
            return null // todo
        }
    }

    override fun createClassLoader(parent: ClassLoader?, staticDomain: Any?): GeneratedClassLoader {
        return LocalSecureClassLoader(parent)
    }

    override fun getDynamicSecurityDomain(securityDomain: Any?): Any {

        return securityDomain!!

    }

    private inner class LocalSecureClassLoader(parent: ClassLoader?) : SecureClassLoader(parent),
        GeneratedClassLoader {
        override fun defineClass(name: String?, b: ByteArray): Class<*>? {
            return this.defineClass(name, b, 0, b.size, codesource)
        }

        override fun linkClass(clazz: Class<*>?) {
            super.resolveClass(clazz)
        }
    }
}
