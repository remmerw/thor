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
package io.github.remmerw.thor.cobra.clientlet

/**
 * Provides utility methods to access the current clientlet context.
 */
object ClientletAccess {
    var currentClientletContext: ThreadLocal<ClientletContext?> = ThreadLocal<ClientletContext?>()
        /**
         * Gets the [ClientletContext] of the current thread, if any.
         */
        get() {
            val ctx = field.get()
            if (ctx != null) {
                return ctx
            } else {
                val td = Thread.currentThread().threadGroup
                if (td is ClientletThreadGroup) {
                    return (td as ClientletThreadGroup).clientletContext
                } else {
                    return null
                }
            }
        }
        /**
         * This method should be invoked by the clientlet platform to publish the
         * [ClientletContext] of the current thread.
         *
         * @param context A [ClientletContext] instance.
         */
        set(context) {
            field.set(context as ClientletContext?)
        }
}
