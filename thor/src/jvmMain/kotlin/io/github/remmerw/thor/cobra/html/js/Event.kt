/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The XAMJ Project

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Contact info: lobochief@users.sourceforge.net
 */
package io.github.remmerw.thor.cobra.html.js

import io.github.remmerw.thor.cobra.js.ScriptableDelegate
import org.w3c.dom.Node
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import org.w3c.dom.html.HTMLElement
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

class Event : ScriptableDelegate, Event {
    private val inputEvent: InputEvent?
    var isCancelBubble: Boolean = false
        set(cancelBubble) {
            println("Event.setCancelBubble()")
            field = cancelBubble
        }
    var fromElement: HTMLElement? = null
    var toElement: HTMLElement? = null
    var leafX: Int = 0
    var leafY: Int = 0
    var isReturnValue: Boolean = false
    var srcElement: Node?
        private set
    private var type: String?

    // TODO: Hide from JS
    var isPropagationStopped: Boolean = false
        private set
    private var currentPhase: Short = 0

    constructor(type: String?, srcElement: Node?, mouseEvent: InputEvent?, leafX: Int, leafY: Int) {
        this.type = type
        this.srcElement = srcElement
        this.leafX = leafX
        this.leafY = leafY
        this.inputEvent = mouseEvent
    }

    constructor(type: String?, srcElement: Node?, keyEvent: KeyEvent?) {
        this.type = type
        this.srcElement = srcElement
        this.inputEvent = keyEvent
    }

    constructor(type: String?, srcElement: Node?) {
        this.type = type
        this.srcElement = srcElement
        this.inputEvent = null
    }

    val altKey: Boolean
        get() {
            val ie = this.inputEvent
            return ie != null && ie.isAltDown
        }

    val shiftKey: Boolean
        get() {
            val ie = this.inputEvent
            return ie != null && ie.isShiftDown
        }

    val ctrlKey: Boolean
        get() {
            val ie = this.inputEvent
            return ie != null && ie.isControlDown
        }

    val button: Int
        get() {
            val ie = this.inputEvent
            if (ie is MouseEvent) {
                // return ((MouseEvent) ie).getButton();
                // range of button is 0 to N in DOM spec, but 1 to N in AWT
                return ie.getButton() - 1
            } else {
                return 0
            }
        }

    override fun getType(): String? {
        return type
    }

    fun setType(type: String?) {
        this.type = type
    }

    val clientX: Int
        get() {
            val ie = this.inputEvent
            if (ie is MouseEvent) {
                return ie.getX()
            } else {
                return 0
            }
        }

    val clientY: Int
        get() {
            val ie = this.inputEvent
            if (ie is MouseEvent) {
                return ie.getY()
            } else {
                return 0
            }
        }

    val keyCode: Int
        // public int getOffsetX() {
        get() {
            val ie = this.inputEvent
            if (ie is KeyEvent) {
                return ie.getKeyCode()
            } else {
                return 0
            }
        }

    fun setSrcElement(srcElement: HTMLElement?) {
        this.srcElement = srcElement
    }

    override fun getTarget(): EventTarget? {
        println("TODO: Event.getTarget()")
        // TODO: Target and source may not be always same. Need to add a constructor param for target.
        return srcElement as EventTarget?
    }

    override fun getCurrentTarget(): EventTarget? {
        println("TODO: Event.getCurrentTarget()")
        return null
    }

    override fun getEventPhase(): Short {
        println("Event.getEventPhase() : " + currentPhase)
        return currentPhase
    }

    fun setPhase(newPhase: Short) {
        currentPhase = newPhase
    }

    override fun getBubbles(): Boolean {
        println("TODO: Event.getBubbles()")
        return false
    }

    override fun getCancelable(): Boolean {
        println("TODO: Event.getCancelable()")
        return false
    }

    override fun getTimeStamp(): Long {
        println("Event.getTimeStamp()")
        return 0
    }

    override fun stopPropagation() {
        this.isPropagationStopped = true
        println("Event.stopPropagation()")
    }

    override fun preventDefault() {
        println("TODO: Event.preventDefault()")
    }

    override fun initEvent(eventTypeArg: String?, canBubbleArg: Boolean, cancelableArg: Boolean) {
        println("TODO: Event.initEvent()")
    }

    override fun toString(): String {
        return "Event [phase=" + currentPhase + ", type=" + type + ", leafX=" + leafX + ", leafY=" + leafY + ", srcElement=" + srcElement + "]"
    }
}
