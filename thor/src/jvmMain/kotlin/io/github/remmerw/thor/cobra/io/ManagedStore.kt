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
import java.io.Serializable

/**
 * Represents client-side storage with quota restrictions. A clientlet engine
 * will typically provide an instance of this interface per host. A manged store
 * manages instances of [ManagedFile]. The path to a
 * managed file is a Unix-style path, using forward slashes, with '/'
 * representing the root directory of the managed store. So each managed store
 * is similar to a small file system accessible only by a particular domain.
 *
 * @see ManagedFile
 *
 * @see ClientletContext.getManagedStore
 */
interface ManagedStore {
    /**
     * Gets a ManagedFile instance for the given managed path. Directories in the
     * path are separated by "/".
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getManagedFile(path: String?): ManagedFile?

    /**
     * Gets a ManagedFile relative to a given parent. Directories in the relative
     * path are separated by "/".
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getManagedFile(parent: ManagedFile?, relativePath: String?): ManagedFile?

    @get:Throws(IOException::class)
    val rootManagedDirectory: ManagedFile?

    /**
     * Gets the managed store quota.
     */
    val quota: Long

    @get:Throws(IOException::class)
    val size: Long

    /**
     * Saves a serializable object at the given managed file path.
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun saveObject(path: String?, `object`: Serializable?)

    /**
     * Retrieves a serializable object. If the file identified by
     * `path` does not exist, this method returns `null`.
     *
     * @param path        Managed path to the object file.
     * @param classLoader A class loader that can load the expected object type.
     * @return An object unserialized from managed file data.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Throws(IOException::class, ClassNotFoundException::class)
    fun retrieveObject(path: String?, classLoader: ClassLoader?): Any?

    @Deprecated("Use {@link #retrieveObject(String, ClassLoader)}.")
    @Throws(IOException::class, ClassNotFoundException::class)
    fun retrieveObject(path: String?): Any?
}
