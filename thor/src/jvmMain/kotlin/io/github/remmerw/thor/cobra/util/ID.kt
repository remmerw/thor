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
/*
 * Created on Jun 7, 2005
 */
package io.github.remmerw.thor.cobra.util

import java.math.BigInteger
import java.net.InetAddress
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale
import java.util.Random
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.math.abs

/**
 * @author J. H. S.
 */
object ID {
    private val RANDOM1: Random
    private val RANDOM2: Random
    private val RANDOM3: Random

    /**
     * Gets a process ID that is nearly guaranteed to be globally unique.
     */
    // Disabling. Don't like the feel of this:
    val globalProcessID: Long

    private val logger: Logger = Logger.getLogger(ID::class.java.name)

    init {
        val time = System.currentTimeMillis()
        val nanoTime = System.nanoTime()
        val freeMemory = Runtime.getRuntime().freeMemory()
        var addressHashCode: Long
        try {
            val inetAddress: InetAddress
            inetAddress = InetAddress.getLoopbackAddress()
            // inetAddress = InetAddress.getLocalHost();
            addressHashCode = (inetAddress.hostName.hashCode() xor inetAddress.hostAddress
                .hashCode()).toLong()
        } catch (err: Exception) {
            logger.log(Level.WARNING, "Unable to get local host information.", err)
            addressHashCode = ID::class.java.hashCode().toLong()
        }

        globalProcessID = time xor nanoTime xor freeMemory xor addressHashCode
        RANDOM1 = Random(time)
        RANDOM2 = Random(nanoTime)
        RANDOM3 = Random(addressHashCode xor freeMemory)
    }

    fun generateLong(): Long {
        return abs(RANDOM1.nextLong() xor RANDOM2.nextLong() xor RANDOM3.nextLong())
    }

    fun generateInt(): Int {
        return generateLong().toInt()
    }

    fun getMD5Bytes(content: String): ByteArray? {
        try {
            val digest = MessageDigest.getInstance("MD5")
            return digest.digest(content.toByteArray(StandardCharsets.UTF_8))
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalStateException(e)
        }
    }

    fun getHexString(bytes: ByteArray): String {
        // This method cannot change even if it's wrong.
        var bigInteger = BigInteger.ZERO
        var shift = 0
        var i = bytes.size
        while (--i >= 0) {
            var contrib = BigInteger.valueOf((bytes[i].toInt() and 0xFF).toLong())
            contrib = contrib.shiftLeft(shift)
            bigInteger = bigInteger.add(contrib)
            shift += 8
        }
        return bigInteger.toString(16).uppercase(Locale.getDefault())
    }

    fun random(min: Int, max: Int): Int {
        if (max <= min) {
            return min
        }
        return (abs(RANDOM1.nextInt()) % (max - min)) + min
    }
}
