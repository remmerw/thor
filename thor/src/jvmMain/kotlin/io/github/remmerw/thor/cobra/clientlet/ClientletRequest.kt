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
package io.github.remmerw.thor.cobra.clientlet;

import org.eclipse.jdt.annotation.NonNull;

import java.net.URL;

import io.github.remmerw.thor.cobra.ua.ParameterInfo;
import io.github.remmerw.thor.cobra.ua.RequestType;
import io.github.remmerw.thor.cobra.ua.UserAgent;

/**
 * A URL request such as a HTTP, file or FTP request.
 *
 * @author J. H. S.
 */
public interface ClientletRequest {
    /**
     * Gets the request method.
     *
     * @return GET, POST, etc.
     */
    String getMethod();

    /**
     * Gets the request URL.
     */
    @NonNull
    URL getRequestURL();

    /**
     * Gets information about the user agent making the request.
     */
    UserAgent getUserAgent();

    /**
     * Gets the referring URL. It should be <code>null</code> if none or unknown.
     */
    String getReferrer();

    /**
     * Gets information about the request parameters.
     */
    ParameterInfo getParameterInfo();

    /**
     * Gets additional headers used in the request.
     */
    Header[] getExtraHeaders();

    /**
     * Convenience method. Determines if the request method is GET.
     */
    boolean isGetRequest();

    /**
     * Convenience method. Determines if the request method is POST.
     */
    boolean isPostRequest();

    /**
     * Determines if the request was made in order to open a new browser window.
     */
    boolean isNewWindowRequest();

    /**
     * Provides alternative POST data in case no <code>ParameterInfo</code> is
     * provied.
     */
    String getAltPostData();

    /**
     * Gets the type of request.
     */
    RequestType getRequestType();
}
