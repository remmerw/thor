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

import io.github.remmerw.thor.cobra.ua.ParameterInfo
import io.github.remmerw.thor.cobra.ua.RequestType
import io.github.remmerw.thor.cobra.ua.UserAgent
import java.net.URL

/**
 * A URL request such as a HTTP, file or FTP request.
 *
 * @author J. H. S.
 */
interface ClientletRequest {
    /**
     * Gets the request method.
     *
     * @return GET, POST, etc.
     */
    val method: String?

    /**
     * Gets the request URL.
     */
    val requestURL: URL

    /**
     * Gets information about the user agent making the request.
     */
    val userAgent: UserAgent?

    /**
     * Gets the referring URL. It should be `null` if none or unknown.
     */
    val referrer: String?

    /**
     * Gets information about the request parameters.
     */
    val parameterInfo: ParameterInfo?

    /**
     * Gets additional headers used in the request.
     */
    val extraHeaders: Array<Header?>?

    /**
     * Convenience method. Determines if the request method is GET.
     */
    val isGetRequest: Boolean

    /**
     * Convenience method. Determines if the request method is POST.
     */
    val isPostRequest: Boolean

    /**
     * Determines if the request was made in order to open a new browser window.
     */
    val isNewWindowRequest: Boolean

    /**
     * Provides alternative POST data in case no `ParameterInfo` is
     * provied.
     */
    val altPostData: String?

    /**
     * Gets the type of request.
     */
    val requestType: RequestType?
}
