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
package io.github.remmerw.thor.cobra.util.io

import java.io.File

object Files {
    /**
     * Guesses the right content-type for a local file, and includes a charset if
     * appropriate.
     */
    fun getContentType(file: File): String {
        // Not very complete at the moment :)
        val name = file.name
        val dotIdx = name.lastIndexOf('.')
        val extension = if (dotIdx == -1) null else name.substring(dotIdx + 1)
        if ("txt".equals(extension, ignoreCase = true)) {
            return "text/plain; charset=\"" + System.getProperty("file.encoding") + "\""
        }
        if ("html".equals(extension, ignoreCase = true) || "htm".equals(
                extension,
                ignoreCase = true
            )
        ) {
            return "text/html; charset=\"" + System.getProperty("file.encoding") + "\""
        } else {
            return "application/octet-stream"
        }
    }

    /**
     * Appends path components to a given directory
     */
    fun joinPaths(start: File?, vararg components: String): File? {
        var result = start
        for (component in components) {
            result = File(result, component)
        }
        return result
    }
}
