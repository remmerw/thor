/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The XAMJ Project

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

import io.github.remmerw.thor.cobra.js.AbstractScriptableDelegate

class History internal constructor(private val window: Window) : AbstractScriptableDelegate() {
    val current: String?
        get() {
            val ctx = this.window.htmlRendererContext
            return if (ctx != null) ctx.currentURL else null
        }

    val next: String?
        get() {
            val ctx = this.window.htmlRendererContext
            return if (ctx != null) ctx.nextURL!!.orElse(null) else null
        }

    val previous: String?
        get() {
            val ctx = this.window.htmlRendererContext
            return if (ctx != null) ctx.previousURL?.orElse(null) else null
        }

    val length: Int
        get() {
            val ctx = this.window.htmlRendererContext
            return if (ctx != null) ctx.historyLength else 0
        }

    fun back() {
        val ctx = this.window.htmlRendererContext
        if (ctx != null) {
            ctx.back()
        }
    }

    fun forward() {
        val ctx = this.window.htmlRendererContext
        if (ctx != null) {
            ctx.forward()
        }
    }

    fun go(offset: Int) {
        val ctx = this.window.htmlRendererContext
        if (ctx != null) {
            ctx.moveInHistory(offset)
        }
    }

    fun go(url: String?) {
        val ctx = this.window.htmlRendererContext
        if (ctx != null) {
            ctx.goToHistoryURL(url)
        }
    }
}
