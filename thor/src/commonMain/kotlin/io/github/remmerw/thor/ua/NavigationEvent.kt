/*
    GNU GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Contact info: lobochief@users.sourceforge.net
 */
package io.github.remmerw.thor.ua

import java.net.URL
import java.util.EventObject

/**
 * A navigation event.
 *
 * @see NavigationListener
 *
 * @see NavigatorExtensionContext.addNavigationListener
 */
class NavigationEvent(
    source: Any, url: URL, method: String?, paramInfo: ParameterInfo?,
    targetType: TargetType?, requestType: RequestType?,
    fromClick: Boolean, linkObject: Any?, originatingFrame: NavigatorFrame?
) : EventObject(source) {
    val uRL: URL
    val method: String?
    val paramInfo: ParameterInfo?
    val targetType: TargetType?
    val requestType: RequestType?
    val isFromClick: Boolean
    val linkObject: Any?
    val originatingFrame: NavigatorFrame?

    init {
        this.uRL = url
        this.method = method
        this.paramInfo = paramInfo
        this.targetType = targetType
        this.requestType = requestType
        this.isFromClick = fromClick
        this.linkObject = linkObject
        this.originatingFrame = originatingFrame
    }

    constructor(
        source: Any, url: URL, method: String?, paramInfo: ParameterInfo?,
        targetType: TargetType?, requestType: RequestType?,
        originatingFrame: NavigatorFrame?
    ) : this(source, url, method, paramInfo, targetType, requestType, false, null, originatingFrame)

    constructor(
        source: Any, url: URL, targetType: TargetType?, requestType: RequestType?,
        linkObject: Any?,
        originatingFrame: NavigatorFrame?
    ) : this(source, url, "GET", null, targetType, requestType, true, linkObject, originatingFrame)

    constructor(
        source: Any, url: URL, method: String?, requestType: RequestType?,
        originatingFrame: NavigatorFrame?
    ) : this(source, url, method, null, TargetType.SELF, requestType, false, null, originatingFrame)

    companion object {
        private val serialVersionUID = -3655001617854084211L
    }
}
