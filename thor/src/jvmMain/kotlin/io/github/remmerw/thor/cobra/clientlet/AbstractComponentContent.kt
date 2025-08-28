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

import java.awt.Component

/**
 * Abstract implementation of [ComponentContent]. It is recommended that
 * `ComponentContent` implementations extend this class for forward
 * compatibility.
 */
abstract class AbstractComponentContent : ComponentContent {
    override fun canCopy(): Boolean {
        return false
    }

    override fun copy(): Boolean {
        return false
    }

    abstract override fun getComponent(): Component?

    abstract override fun getSourceCode(): String?

    abstract override fun getTitle(): String?

    override fun getDescription(): String {
        return ""
    }

    override fun addNotify() {
    }

    override fun removeNotify() {
    }

    override fun getContentObject(): Any? {
        return null
    }

    override fun getMimeType(): String? {
        return null
    }

    override fun setProperty(name: String?, value: Any?) {
        // NOP
    } // Backward compatibility note: Additional methods should provide an empty
    // body.
}
