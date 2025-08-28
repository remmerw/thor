package io.github.remmerw.thor.cobra.util

object HexDump {
    private val HEX_DIGITS =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    private val HEX_LOWER_CASE_DIGITS =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    fun dumpHexString(array: ByteArray?): String {
        if (array == null) return "(null)"
        return dumpHexString(array, 0, array.size)
    }

    fun dumpHexString(array: ByteArray?, offset: Int, length: Int): String {
        if (array == null) return "(null)"
        val result = StringBuilder()

        val line = ByteArray(16)
        var lineIndex = 0

        result.append("\n0x")
        result.append(toHexString(offset))

        for (i in offset..<offset + length) {
            if (lineIndex == 16) {
                result.append(" ")

                for (j in 0..15) {
                    if (line[j] > ' '.code.toByte() && line[j] < '~'.code.toByte()) {
                        result.append(String(line, j, 1))
                    } else {
                        result.append(".")
                    }
                }

                result.append("\n0x")
                result.append(toHexString(i))
                lineIndex = 0
            }

            val b = array[i]
            result.append(" ")
            result.append(HEX_DIGITS[(b.toInt() ushr 4) and 0x0F])
            result.append(HEX_DIGITS[b.toInt() and 0x0F])

            line[lineIndex++] = b
        }

        if (lineIndex != 16) {
            var count = (16 - lineIndex) * 3
            count++
            for (i in 0..<count) {
                result.append(" ")
            }

            for (i in 0..<lineIndex) {
                if (line[i] > ' '.code.toByte() && line[i] < '~'.code.toByte()) {
                    result.append(String(line, i, 1))
                } else {
                    result.append(".")
                }
            }
        }

        return result.toString()
    }

    fun toHexString(b: Byte): String {
        return toHexString(toByteArray(b))
    }

    fun toHexString(array: ByteArray, upperCase: Boolean): String {
        return toHexString(array, 0, array.size, upperCase)
    }

    @JvmOverloads
    fun toHexString(
        array: ByteArray,
        offset: Int = 0,
        length: Int = array.length,
        upperCase: Boolean = true
    ): String {
        val digits = if (upperCase) HEX_DIGITS else HEX_LOWER_CASE_DIGITS
        val buf = CharArray(length * 2)

        var bufIndex = 0
        for (i in offset..<offset + length) {
            val b = array[i]
            buf[bufIndex++] = digits[(b.toInt() ushr 4) and 0x0F]
            buf[bufIndex++] = digits[b.toInt() and 0x0F]
        }

        return String(buf)
    }

    fun toHexString(i: Int): String {
        return toHexString(toByteArray(i))
    }

    fun toByteArray(b: Byte): ByteArray {
        val array = ByteArray(1)
        array[0] = b
        return array
    }

    fun toByteArray(i: Int): ByteArray {
        val array = ByteArray(4)

        array[3] = (i and 0xFF).toByte()
        array[2] = ((i shr 8) and 0xFF).toByte()
        array[1] = ((i shr 16) and 0xFF).toByte()
        array[0] = ((i shr 24) and 0xFF).toByte()

        return array
    }

    private fun toByte(c: Char): Int {
        if (c >= '0' && c <= '9') return (c.code - '0'.code)
        if (c >= 'A' && c <= 'F') return (c.code - 'A'.code + 10)
        if (c >= 'a' && c <= 'f') return (c.code - 'a'.code + 10)

        throw RuntimeException("Invalid hex char '" + c + "'")
    }


    fun hexStringToByteArray(hexString: String): ByteArray {
        val length = hexString.length
        val buffer = ByteArray(length / 2)

        var i = 0
        while (i < length) {
            buffer[i / 2] =
                ((toByte(hexString.get(i)) shl 4) or toByte(hexString.get(i + 1))).toByte()
            i += 2
        }

        return buffer
    }

    fun appendByteAsHex(sb: StringBuilder, b: Byte, upperCase: Boolean): StringBuilder {
        val digits = if (upperCase) HEX_DIGITS else HEX_LOWER_CASE_DIGITS
        sb.append(digits[(b.toInt() shr 4) and 0xf])
        sb.append(digits[b.toInt() and 0xf])
        return sb
    }
}
