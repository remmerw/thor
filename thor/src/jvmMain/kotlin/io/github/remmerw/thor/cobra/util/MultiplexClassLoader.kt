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
 * Created on Jun 19, 2005
 */
package io.github.remmerw.thor.cobra.util

import java.net.URL

/**
 * @author J. H. S.
 */
class MultiplexClassLoader(classLoaders: MutableCollection<ClassLoader?>) : BaseClassLoader(null) {
    private val parentLoaders: Array<ClassLoader>

    /**
     * @param parent
     */
    init {
        // TODO: Check why input parameter is not being used
        this.parentLoaders = classLoaders.toArray<ClassLoader?>(EMPTY_CLASS_LOADERS)
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
     */
    @Synchronized
    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String?, resolve: Boolean): Class<*>? {
        // First, check if the class has already been loaded
        var c = findLoadedClass(name)
        if (c == null) {
            try {
                val len = this.parentLoaders.size
                if (len == 0) {
                    c = findSystemClass(name)
                } else {
                    for (i in 0..<len) {
                        val parent = this.parentLoaders[i]
                        try {
                            c = parent.loadClass(name)
                            if (c != null) {
                                return c
                            }
                        } catch (cnfe: ClassNotFoundException) {
                            // ignore
                        }
                    }
                }
            } catch (e: ClassNotFoundException) {
                // If still not found, then invoke findClass in order
                // to find the class.
                c = findClass(name)
            }
            if (c == null) {
                c = findClass(name)
            }
        }
        if (resolve) {
            resolveClass(c)
        }
        return c
    }

    override fun getResource(name: String?): URL? {
        for (loader in parentLoaders) {
            val url = loader.getResource(name)
            if (url != null) {
                return url
            }
        }
        return null
    }

    companion object {
        private val EMPTY_CLASS_LOADERS = arrayOfNulls<ClassLoader>(0)
    }
}
