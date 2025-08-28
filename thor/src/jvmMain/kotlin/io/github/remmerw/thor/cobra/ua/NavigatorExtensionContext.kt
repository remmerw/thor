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

import io.github.remmerw.thor.cobra.clientlet.ClientletSelector
import java.net.URLStreamHandlerFactory

/**
 * This interface gives extensions access to the platform.
 *
 * @see NavigatorExtension.init
 */
interface NavigatorExtensionContext {
    /**
     * Adds a clientlet selector. This is how platform extensions register
     * additional content handlers.
     */
    fun addClientletSelector(selector: ClientletSelector?)

    fun removeClientletSelector(selector: ClientletSelector?)

    /**
     * Adds an object that can view connections made by the browser and
     * potentially modify their headers and other data.
     *
     * @param processor An connection processor.
     */
    fun addConnectionProcessor(processor: ConnectionProcessor?)

    fun removeConnectionProcessor(processor: ConnectionProcessor?)

    /**
     * Adds a listener of navigator events.
     */
    fun addNavigatorErrorListener(listener: NavigatorErrorListener?)

    /**
     * Removes a listener of navigation events.
     */
    fun removeNavigatorErrorListener(listener: NavigatorErrorListener?)

    /**
     * Adds a global listener of navigation events.
     *
     * @param listener The listener.
     */
    fun addNavigationListener(listener: NavigationListener?)

    fun removeNavigationListener(listener: NavigationListener?)

    /**
     * Gets the [UserAgent] instance associated with this context.
     */
    val userAgent: UserAgent?

    /**
     * Registers a URL stream handler factory which may be used to implement
     * custom protocols. Note that Java platform protocols (HTTP, HTTPS, etc.) or
     * protocols defined by extensions with higher priority cannot be overridden.
     * The factory must return `null` if it does not know how to handle
     * a particular protocol.
     *
     * @param factory An implementation of `java.net.URLStreamHandlerFactory`
     * .
     */
    fun addURLStreamHandlerFactory(factory: URLStreamHandlerFactory?)
}
