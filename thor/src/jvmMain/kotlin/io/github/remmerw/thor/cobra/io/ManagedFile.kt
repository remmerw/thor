/*
Copyright 1994-2006 The Lobo Project. All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer. Redistributions in binary form must
reproduce the above copyright notice, this list of conditions and the following
disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE LOBO PROJECT ``AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
EVENT SHALL THE FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.github.remmerw.thor.cobra.io

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Represents a file location in a managed store.
 *
 * @author J. H. S.
 */
interface ManagedFile {
    val path: String?

    val isDirectory: Boolean

    fun exists(): Boolean

    fun mkdir(): Boolean

    fun mkdirs(): Boolean

    @Throws(IOException::class)
    fun listFiles(): Array<ManagedFile?>?

    @Throws(IOException::class)
    fun listFiles(filter: ManagedFileFilter?): Array<ManagedFile?>?

    /**
     * Atomically creates a new file.
     *
     * @return True if and only if the file did not already exist and was
     * successfully created.
     * @throws IOException
     */
    @Throws(IOException::class)
    fun createNewFile(): Boolean

    @get:Throws(IOException::class)
    val parent: ManagedFile?

    /**
     * Creates an output stream for the managed file. If the managed file already
     * exists, it is overwritten.
     *
     *
     * The number of bytes that can be written to the stream may be restricted by
     * a quota.
     *
     * @see QuotaExceededException
     */
    @Throws(IOException::class)
    fun openOutputStream(): OutputStream?

    /**
     * Creates an input stream for reading from the managed file.
     *
     * @throws java.io.FileNotFoundException If the file does not exist.
     */
    @Throws(IOException::class)
    fun openInputStream(): InputStream?

    @Throws(IOException::class)
    fun delete(): Boolean
}
