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
package io.github.remmerw.thor.ua


/**
 * This interface represents a navigator window.
 */
interface NavigatorWindow {


    /**
     * Adds a listener of window events.
     *
     * @param listener A listener instance.
     */
    fun addNavigatorWindowListener(listener: NavigatorWindowListener?)

    /**
     * Removes a listener previously added with
     * [.addNavigatorWindowListener]
     *
     * @param listener
     */
    fun removeNavigatorWindowListener(listener: NavigatorWindowListener?)

    /**
     * Gets the top frame of this window.
     */
    val topFrame: NavigatorFrame?


    /**
     * Closes the window.
     */
    fun dispose()

    /**
     * Gets the navigator for the window.
     */
    val userAgent: UserAgent?

    fun canBack(): Boolean

    fun canForward(): Boolean

    fun back(): Boolean

    fun forward(): Boolean

    fun canReload(): Boolean

    fun reload(): Boolean

    fun stop(): Boolean

    fun canCopy(): Boolean

    fun copy(): Boolean

    fun hasSource(): Boolean

    /**
     * Navigates to a [NavigationEntry] belonging to navigation history in
     * the current session. without generating a new entry, in much the same way
     * that [.back] and [.forward] work.
     *
     * @param entry A existing `NavigationEntry`.
     * @return True if the operation succeeded.
     */
    fun goTo(entry: NavigationEntry?): Boolean

    val backNavigationEntries: MutableList<NavigationEntry?>?

    val forwardNavigationEntries: MutableList<NavigationEntry?>?

    val currentNavigationEntry: NavigationEntry?


}
