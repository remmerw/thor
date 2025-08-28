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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Iterator;

import io.github.remmerw.thor.cobra.ua.RequestType;

/**
 * Represents a URL response such as an HTTP or file protocol response.
 */
public interface ClientletResponse {
    /**
     * Gets the response URL. This may be different to the request URL in the case
     * of a redirect.
     */
    @NonNull
    URL getResponseURL();

    /**
     * Gets the request method for the response URL. This may be different to the
     * original request method in case of a redirect.
     */
    String getLastRequestMethod();

    /**
     * Gets a response header.
     *
     * @param name The header name.
     */
    String getHeader(String name);

    /**
     * Gets all values for a particular header.
     *
     * @param name The header name.
     */
    String[] getHeaders(String name);

    /**
     * Gets an iterator of response header names.
     */
    Iterator<String> getHeaderNames();

    /**
     * Gets the response stream.
     *
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;

    /**
     * Gets the response content type. This can also contain a character encoding,
     * e.g. <em>text/html; charset=ISO-8859-1</em>.
     *
     * @see #getMimeType()
     */
    String getContentType();

    /**
     * Gets only the mime-type part of the content type, e.g. <em>text/html</em>.
     *
     * @see #getContentType()
     */
    String getMimeType();

    /**
     * A convenience method used to match parameters provided against the response
     * mime-type or the "file extension" of the response URL's file path. The file
     * extension is matched only when the mime type of the response is either
     * <code>application/octet-stream</code>, <code>content/unknown</code>, or not
     * provided.
     *
     * @param mimeType      A mime type, e.g. <em>application/x-acme</em>.
     * @param fileExtension A collection of file extensions, each starting with a dot, e.g.
     *                      <em>new String[] { ".acme", ".acm" }</em>.
     * @return True if the navigator considers there is a match.
     */
    boolean matches(String mimeType, String[] fileExtension);

    /**
     * Gets the content length of the reponse. This may be -1 if the content
     * length is not known.
     */
    int getContentLength();

    /**
     * Returns true only if the response comes from a local cache.
     */
    boolean isFromCache();

    /**
     * Gets the charset specified with the content type. If no such charset has
     * been provided, the implementation may recommend a default.
     */
    String getCharset();

    /**
     * Determines whether a charset has been provided with the Content-Type
     * header.
     */
    boolean isCharsetProvided();

    /**
     * Gets the HTTP response or status code.
     */
    int getResponseCode() throws IOException;

    /**
     * Gets the HTTP response message.
     */
    String getResponseMessage() throws IOException;

    /**
     * Returns true only if the response is allowed to be cached.
     */
    boolean isCacheable();

    /**
     * Returns true only if the response does not result from a reload, forward or
     * back. Generally, this method indicates that a response is not related to an
     * entry already in the navigation history.
     */
    boolean isNewNavigationAction();

    /**
     * If available, gets an object previously persisted along with the cached
     * document.
     *
     * @param classLoader A class loader that can load an object of the type expected.
     * @see #setNewPersistentCachedObject(Serializable)
     */
    Object getPersistentCachedObject(ClassLoader classLoader);

    /**
     * Caches the object provided in persistent memory and associates it with the
     * reponse URL, if caching is allowed.
     *
     * @param object A <code>Serializable</code> object.
     */
    void setNewPersistentCachedObject(Serializable object);

    /**
     * If available, gets an object previously cached in main memory associated
     * with the response URL.
     * <p>
     * <b>Note</b>: Most callers should only use the persistent cached object if
     * {@link #isFromCache()} returns true.
     *
     * @see #setNewTransientCachedObject(Object, int)
     */
    Object getTransientCachedObject();

    /**
     * Caches an object in main memory, provided caching is allowed and there's
     * enough memory to do so. The object is associated with the current response
     * URL.
     *
     * @param object     An object.
     * @param approxSize The approximate byte size the object occupies in memory. Note that
     *                   values less than the size of the response in bytes are assumed to
     *                   be in error.
     */
    void setNewTransientCachedObject(Object object, int approxSize);

    /**
     * Gets the approximate size in bytes of the transient cached object
     * previously associated with the response.
     * <p>
     * <b>Note</b>: Most callers should only use the transient cached object if
     * {@link #isFromCache()} returns true.
     */
  /* Commented because nothing is using it.
  public int getTransientCachedObjectSize();
  */

    /**
     * Gets the value of the "Date" header. This method returns <code>null</code>
     * if the header is not available.
     */
    java.util.Date getDate();

    /**
     * Gets the type of request.
     */
    RequestType getRequestType();
}
