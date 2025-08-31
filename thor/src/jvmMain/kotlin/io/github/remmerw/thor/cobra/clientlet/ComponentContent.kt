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
 * Content set by a [Clientlet]. To ensure backward compatibility, it is
 * recommended that [AbstractComponentContent] be extended instead of
 * implementing this interface whenever possible.
 *
 * @see ClientletContext.setResultingContent
 */
interface ComponentContent {


    var component: Component?

    var title: String?

    val description: String?

    /**
     * Determines whether it's possible to copy content to the clipboard. This
     * method can be used by the platform to determine if a menu item should be
     * enabled.
     */
    fun canCopy(): Boolean

    /**
     * Copies content to the clipboard.
     *
     * @return True if the operation succeeded.
     */
    fun copy(): Boolean

    /**
     * Gets the source code associated with the content.
     */
    var sourceCode: String?

    /**
     * Called after the content has been added to a container for display.
     */
    fun addNotify()

    /**
     * Called after the addNotify and navigation updated
     */
    fun navigatedNotify()

    /**
     * Called after the content has been removed from the display container. This
     * method may be used to dispose associated resources.
     */
    fun removeNotify()

    /**
     * Gets an implementation-dependent object that represents the content. For
     * example, if the content is HTML, the object returned by this method may be
     * of type HTMLDocument.
     */
    val contentObject: Any?

    /**
     * Gets a mime-type that can be said to be associated with the object returned
     * by [.getContentObject]. This may differ from the mime-type of the
     * response that produced the content, and it may also be `null`.
     */
    val mimeType: String?

    /**
     * Sets a property of the content. Property names are
     * implementation-dependent.
     *
     * @param name
     * @param value
     */
    fun setProperty(name: String?, value: Any?)

    val isReadyToPaint: Boolean
        get() = true
}
