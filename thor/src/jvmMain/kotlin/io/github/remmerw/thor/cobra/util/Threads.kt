/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2014 Uproot Labs India Pvt Ltd

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

 */
package io.github.remmerw.thor.cobra.util

import kotlin.math.min

object Threads {
    private const val STACKS_TO_SKIP_AT_START = 2

    fun dumpStack(maxStacks: Int) {
        val stackTrace = Thread.currentThread().stackTrace
        val stacksToPrint = min(stackTrace.size, maxStacks + STACKS_TO_SKIP_AT_START)
        println("--- 8< ------------[START]------------ >8 ---")
        for (i in STACKS_TO_SKIP_AT_START..<stacksToPrint) {
            println(stackTrace[i])
        }
        if (stacksToPrint < stackTrace.size) {
            println("... skipped " + (stackTrace.size - stacksToPrint) + " traces")
        }
        println("--- 8< ------------[ END ]------------ >8 ---")
    }

    /**
     * Sleep until interrupted
     */
    fun sleep(ms: Int) {
        try {
            Thread.sleep(ms.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}
