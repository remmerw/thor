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
 * Created on Oct 22, 2005
 */
package io.github.remmerw.thor.cobra.html.parser

import org.xml.sax.InputSource
import java.io.InputStream
import java.io.Reader

/**
 * The `InputSourceImpl` class implements the
 * `InputSource` interface.
 *
 * @author J. H. S.
 */
class InputSourceImpl : InputSource {
    /**
     * Constructs an `InputSourceImpl`.
     *
     */
    @Deprecated("Use a constructor that takes either a stream or a reader.")
    constructor() : super()

    /**
     * Constructs an `InputSourceImpl`.
     *
     * @param byteStream The input stream where content can be read.
     */
    @Deprecated("Use constructor with <code>uri</code> parameter.")
    constructor(byteStream: InputStream?) : super(byteStream)

    /**
     * Constructs an `InputSourceImpl`.
     *
     * @param characterStream The `Reader` where characters can be read.
     */
    @Deprecated("Use constructor with <code>uri</code> parameter.")
    constructor(characterStream: Reader?) : super(characterStream)

    /**
     * Constructs an `InputSourceImpl`.
     *
     * @param characterStream The `Reader` where characters can be read.
     * @param uri             The URI of the document.
     */
    constructor(characterStream: Reader?, uri: String?) : super(characterStream) {
        this.systemId = uri
    }

    /**
     * Constructs an `InputSourceImpl`.
     *
     * @param byteStream The input stream where content can be read.
     * @param uri        The URI that identifies the content.
     * @param charset    The character set of the input stream.
     */
    constructor(byteStream: InputStream?, uri: String?, charset: String?) : super(byteStream) {
        this.encoding = charset
        this.systemId = uri
        this.publicId = uri
    }
}
