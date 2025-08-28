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
 * Created on Mar 19, 2005
 */
package io.github.remmerw.thor.cobra.util.io

import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.URLConnection
import java.util.function.Consumer
import java.util.stream.Collectors
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream

/**
 * @author J. H. S.
 */
object IORoutines {
    val LINE_BREAK_BYTES: ByteArray = byteArrayOf(13.toByte(), 10.toByte())

    @JvmOverloads
    @Throws(IOException::class)
    fun loadAsText(`in`: InputStream, encoding: String, bufferSize: Int = 4096): String {
        val reader = InputStreamReader(`in`, encoding)
        var buffer = CharArray(bufferSize)
        var offset = 0
        while (true) {
            var remain = buffer.size - offset
            if (remain <= 0) {
                val newBuffer = CharArray(buffer.size * 2)
                System.arraycopy(buffer, 0, newBuffer, 0, offset)
                buffer = newBuffer
                remain = buffer.size - offset
            }
            val numRead = reader.read(buffer, offset, remain)
            if (numRead == -1) {
                break
            }
            offset += numRead
        }
        return String(buffer, 0, offset)
    }

    @Throws(IOException::class)
    fun load(file: File): ByteArray {
        val fileLength = file.length()
        if (fileLength > Int.Companion.MAX_VALUE) {
            throw IOException("File '" + file.name + "' too big")
        }
        FileInputStream(file).use { `in` ->
            return loadExact(`in`, fileLength.toInt())
        }
    }

    @JvmOverloads
    @Throws(IOException::class)
    fun load(`in`: InputStream, initialBufferSize: Int = 4096): ByteArray {
        var initialBufferSize = initialBufferSize
        if (initialBufferSize == 0) {
            initialBufferSize = 1
        }
        var buffer = ByteArray(initialBufferSize)
        var offset = 0
        while (true) {
            var remain = buffer.size - offset
            if (remain <= 0) {
                val newSize = buffer.size * 2
                val newBuffer = ByteArray(newSize)
                System.arraycopy(buffer, 0, newBuffer, 0, offset)
                buffer = newBuffer
                remain = buffer.size - offset
            }
            val numRead = `in`.read(buffer, offset, remain)
            if (numRead == -1) {
                break
            }
            offset += numRead
        }
        if (offset < buffer.size) {
            val newBuffer = ByteArray(offset)
            System.arraycopy(buffer, 0, newBuffer, 0, offset)
            buffer = newBuffer
        }
        return buffer
    }

    @Throws(IOException::class)
    fun loadExact(`in`: InputStream, length: Int): ByteArray {
        val buffer = ByteArray(length)
        var offset = 0
        while (true) {
            val remain = length - offset
            if (remain <= 0) {
                break
            }
            val numRead = `in`.read(buffer, offset, remain)
            if (numRead == -1) {
                throw IOException("Reached EOF, read " + offset + " expecting " + length)
            }
            offset += numRead
        }
        return buffer
    }

    @Throws(IOException::class)
    fun equalContent(file: File, content: ByteArray?): Boolean {
        val length = file.length()
        if (length > Int.Companion.MAX_VALUE) {
            throw IOException("File '" + file + "' too big")
        }

        FileInputStream(file).use { `in` ->
            val fileContent = loadExact(`in`, length.toInt())
            return content.contentEquals(fileContent)
        }
    }

    @Throws(IOException::class)
    fun save(file: File, content: ByteArray) {
        FileOutputStream(file).use { out ->
            out.write(content)
        }
    }

    /**
     * Reads line without buffering.
     */
    @Throws(IOException::class)
    fun readLine(`in`: InputStream): String? {
        var b: Int
        var sb: StringBuffer? = null
        OUTER@ while ((`in`.read().also { b = it }) != -1) {
            if (sb == null) {
                sb = StringBuffer()
            }
            when (b) {
                '\n'.code.toByte() -> break@OUTER
                '\r'.code.toByte() -> {}
                else -> sb.append(b.toChar())
            }
        }
        return if (sb == null) null else sb.toString()
    }

    fun touch(file: File) {
        file.setLastModified(System.currentTimeMillis())
    }

    @Throws(IOException::class)
    fun saveStrings(file: File, list: MutableCollection<String?>) {
        FileOutputStream(file).use { fout ->
            BufferedOutputStream(fout).use { bout ->
                PrintWriter(bout).use { writer ->
                    list.forEach(Consumer { text: String? -> writer.println(text) })
                    writer.flush()
                }
            }
        }
    }

    @Throws(IOException::class)
    fun loadStrings(file: File): MutableList<String?> {
        FileInputStream(file).use { `in` ->
            BufferedReader(InputStreamReader(`in`)).use { reader ->
                return reader.lines().collect(
                    Collectors.toList()
                )
            }
        }
    }

    @Throws(IOException::class)
    fun getDecodedStream(connection: URLConnection): InputStream? {
        val cis = connection.getInputStream()
        if ("gzip" == connection.contentEncoding) {
            return GZIPInputStream(cis)
        } else if ("deflate" == connection.contentEncoding) {
            return InflaterInputStream(cis)
        } else {
            return cis
        }
    }

    @Throws(IOException::class)
    fun getDecodedErrorStream(connection: HttpURLConnection): InputStream? {
        val cis = connection.errorStream
        if (cis != null) {
            if ("gzip" == connection.contentEncoding) {
                return GZIPInputStream(cis)
            } else if ("deflate" == connection.contentEncoding) {
                return InflaterInputStream(cis)
            } else {
                return cis
            }
        } else {
            return null
        }
    }
}
