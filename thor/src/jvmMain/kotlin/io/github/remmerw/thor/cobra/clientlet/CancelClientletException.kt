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
 * This exception may be thrown from the
 * [Clientlet.process] method of a clientlet to cancel
 * the request. Doing so will not result in an error page if the clientlet is
 * still processing the web response. Preferably, it should be thrown before
 * reading any input.
 *
 *
 * This exception may be thrown right after navigation is redirected to another
 * document.
 *
 * @author J. H. S.
 */
class CancelClientletException : ClientletException {
    /**
     * @param message
     */
    constructor(message: String?) : super(message)

    /**
     *
     */
    constructor() : super("")

    /**
     * @param message
     * @param rootCause
     */
    constructor(message: String?, rootCause: Throwable?) : super(message, rootCause)

    /**
     * @param rootCause
     */
    constructor(rootCause: Throwable?) : super(rootCause)

    companion object {
        private val serialVersionUID = -2733027023927994062L
    }
}
