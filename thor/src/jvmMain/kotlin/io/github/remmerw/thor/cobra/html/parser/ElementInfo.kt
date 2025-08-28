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
 * Created on Oct 23, 2005
 */
package io.github.remmerw.thor.cobra.html.parser

internal class ElementInfo {
    val endElementType: Int
    val childElementOk: Boolean
    val stopTags: MutableSet<String?>?
    val noScriptElement: Boolean
    val decodeEntities: Boolean

    /**
     * @param ok
     * @param type
     */
    constructor(ok: Boolean, type: Int) {
        this.childElementOk = ok
        this.endElementType = type
        this.stopTags = null
        this.noScriptElement = false
        this.decodeEntities = true
    }

    /**
     * @param ok
     * @param type
     */
    constructor(ok: Boolean, type: Int, stopTags: MutableSet<String?>?) {
        this.childElementOk = ok
        this.endElementType = type
        this.stopTags = stopTags
        this.noScriptElement = false
        this.decodeEntities = true
    }

    constructor(ok: Boolean, type: Int, stopTags: MutableSet<String?>?, noScriptElement: Boolean) {
        this.childElementOk = ok
        this.endElementType = type
        this.stopTags = stopTags
        this.noScriptElement = noScriptElement
        this.decodeEntities = true
    }

    constructor(ok: Boolean, type: Int, decodeEntities: Boolean) {
        this.childElementOk = ok
        this.endElementType = type
        this.stopTags = null
        this.noScriptElement = false
        this.decodeEntities = decodeEntities
    }

    companion object {
        const val END_ELEMENT_FORBIDDEN: Int = 0
        const val END_ELEMENT_OPTIONAL: Int = 1
        const val END_ELEMENT_REQUIRED: Int = 2
    }
}
