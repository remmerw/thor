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
 * Created on Jun 22, 2005
 */
package io.github.remmerw.thor.cobra.util

import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLConnection

/**
 * @author J. H. S.
 */
class GenericURLConnection
/**
 *
 */(url: URL?, private val inputStream: InputStream?) : URLConnection(url) {
    /*
         * (non-Javadoc)
         *
         * @see java.net.URLConnection#connect()
         */
    @Throws(IOException::class)
    override fun connect() {
    }

    /*
     * (non-Javadoc)
     *
     * @see java.net.URLConnection#getInputStream()
     */
    @Throws(IOException::class)
    override fun getInputStream(): InputStream? {
        return this.inputStream
    }

    /*
     * (non-Javadoc)
     *
     * @see java.net.URLConnection#getHeaderField(int)
     */
    override fun getHeaderField(n: Int): String? {
        return null
    }

    /*
     * (non-Javadoc)
     *
     * @see java.net.URLConnection#getHeaderField(java.lang.String)
     */
    override fun getHeaderField(name: String?): String? {
        return null
    }

    /*
     * (non-Javadoc)
     *
     * @see java.net.URLConnection#getHeaderFieldKey(int)
     */
    override fun getHeaderFieldKey(n: Int): String? {
        return null
    }
}
