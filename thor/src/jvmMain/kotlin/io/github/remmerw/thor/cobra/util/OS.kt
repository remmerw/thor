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
 * Created on May 14, 2005
 */
package io.github.remmerw.thor.cobra.util

import java.io.IOException

/**
 * @author J. H. S.
 */
object OS {
    val isWindows: Boolean
        get() {
            val osName = System.getProperty("os.name")
            return osName.indexOf("Windows") != -1
        }

    @Throws(IOException::class)
    fun launchBrowser(url: String?) {
        val cmdLine: String?
        if (isWindows) {
            cmdLine = "rundll32 url.dll,FileProtocolHandler " + url
        } else {
            cmdLine = "firefox " + url
        }
        try {
            Runtime.getRuntime().exec(cmdLine)
        } catch (ioe: IOException) {
            Runtime.getRuntime().exec("netscape " + url)
        }
    }

    /**
     * Opens a file a directory with an appropriate program.
     */
    @Throws(IOException::class)
    fun launchPath(path: String?) {
        if (isWindows) {
            Runtime.getRuntime().exec(arrayOf<String?>("cmd.exe", "/c", "start", "\"title\"", path))
        } else {
            throw UnsupportedOperationException("Unsupported")
        }
    }

    fun supportsLaunchPath(): Boolean {
        return isWindows
    }
}
