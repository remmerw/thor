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
package io.github.remmerw.thor.cobra.html.style

import cz.vutbr.web.css.CSSProperty
import io.github.remmerw.thor.cobra.html.domimpl.HTMLDocumentImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLElementImpl
import java.awt.Color
import java.awt.Cursor
import java.awt.Font
import java.awt.FontMetrics
import java.util.Optional

/**
 * Render state for elements that are displayed as blocks by default.
 */
class BlockRenderState : StyleSheetRenderState {
    constructor(prevRenderState: RenderState?, element: HTMLElementImpl) : super(
        prevRenderState,
        element
    )

    constructor(document: HTMLDocumentImpl?) : super(document)

    public fun getDefaultDisplay(): Int {
        return RenderState.Companion.DISPLAY_BLOCK
    }

    override val position: Int
        get() = TODO("Not yet implemented")
    override val float: Int
        get() = TODO("Not yet implemented")
    override val clear: Int
        get() = TODO("Not yet implemented")
    override val visibility: Int
        get() = TODO("Not yet implemented")
    override val font: Font?
        get() = TODO("Not yet implemented")
    override val fontBase: Int
        get() = TODO("Not yet implemented")
    override val color: Color?
        get() = TODO("Not yet implemented")
    override val backgroundColor: Color?
        get() = TODO("Not yet implemented")
    override val textBackgroundColor: Color?
        get() = TODO("Not yet implemented")
    override val backgroundInfo: BackgroundInfo?
        get() = TODO("Not yet implemented")
    override val overlayColor: Color?
        get() = TODO("Not yet implemented")
    override val textTransform: Int
        get() = TODO("Not yet implemented")
    override val textDecorationMask: Int
        get() = TODO("Not yet implemented")
    override val fontMetrics: FontMetrics?
        get() = TODO("Not yet implemented")
    override val fontXHeight: Double
        get() = TODO("Not yet implemented")
    override val blankWidth: Int
        get() = TODO("Not yet implemented")
    override var isHighlight: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override val alignYPercent: Int
        get() = TODO("Not yet implemented")
    override val display: Int
        get() = TODO("Not yet implemented")
    override val textIndentText: String?
        get() = TODO("Not yet implemented")
    override val whiteSpace: Int
        get() = TODO("Not yet implemented")
    override val cursor: Optional<Cursor>?
        get() = TODO("Not yet implemented")
    override val left: String?
        get() = TODO("Not yet implemented")
    override val top: String?
        get() = TODO("Not yet implemented")
    override val right: String?
        get() = TODO("Not yet implemented")
    override val bottom: String?
        get() = TODO("Not yet implemented")
    override val verticalAlign: CSSProperty.VerticalAlign?
        get() = TODO("Not yet implemented")
}
