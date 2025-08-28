/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project

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
package io.github.remmerw.thor.cobra.util

object Timing {
    fun round1(value: Double): Double {
        return Math.round(value * 10.0) / 10.0
    }

    fun getElapsedText(elapsedMillis: Long): String {
        if (elapsedMillis < 60000) {
            val unit = round1(elapsedMillis / 1000.0)
            return unit.toString() + (if (unit == 1.0) " second" else " seconds")
        } else if (elapsedMillis < (60000 * 60)) {
            val unit = round1(elapsedMillis / 60000.0)
            return unit.toString() + (if (unit == 1.0) " minute" else " minutes")
        } else if (elapsedMillis < (60000 * 60 * 24)) {
            val unit = round1(elapsedMillis / (60000.0 * 60))
            return unit.toString() + (if (unit == 1.0) " hour" else " hours")
        } else {
            val unit = round1(elapsedMillis / (60000.0 * 60 * 24))
            return unit.toString() + (if (unit == 1.0) " day" else " days")
        }
    }
}
