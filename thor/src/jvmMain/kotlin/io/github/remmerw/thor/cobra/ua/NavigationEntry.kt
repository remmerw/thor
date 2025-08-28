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
package io.github.remmerw.thor.cobra.ua

import io.github.remmerw.thor.cobra.clientlet.ClientletResponse
import java.net.URL

/**
 * Represents one item in the navigation history.
 */
class NavigationEntry(
    val navigatorFrame: NavigatorFrame?, // Note: Do not retain request context here.
    var url: URL, method: String?, title: String?, description: String?
) {
    /**
     * Gets the uppercase request method that resulted in this navigation entry.
     */
    val method: String?
    val title: String?
    val description: String?

    init {
        this.url = url
        this.method = method
        this.title = title
        this.description = description
    }

    override fun toString(): String {
        return "NavigationEntry[url=" + this.url + ",method=" + this.method + ",title=" + title + "]"
    }

    companion object {
        fun fromResponse(
            frame: NavigatorFrame?, response: ClientletResponse, title: String?,
            description: String?
        ): NavigationEntry {
            return NavigationEntry(
                frame,
                response.responseURL,
                response.lastRequestMethod,
                title,
                description
            )
        }
    }
}
