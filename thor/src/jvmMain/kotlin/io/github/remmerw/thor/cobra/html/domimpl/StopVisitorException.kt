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
 * Created on Dec 3, 2005
 */
package io.github.remmerw.thor.cobra.html.domimpl

internal class StopVisitorException : RuntimeException {
    val tag: Any?

    constructor() : super() {
        this.tag = null
    }

    constructor(message: String?) : super(message) {
        this.tag = null
    }

    constructor(message: String?, cause: Throwable?) : super(message, cause) {
        this.tag = null
    }

    constructor(cause: Throwable?) : super(cause) {
        this.tag = null
    }

    constructor(tag: Any?) {
        this.tag = tag
    }

    companion object {
        private val serialVersionUID = -2910258563737355879L
    }
}
