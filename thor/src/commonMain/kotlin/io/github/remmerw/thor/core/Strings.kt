package io.github.remmerw.thor.core

import kotlin.math.max

object Strings {


    fun compareVersions(version1: String?, version2: String?, startsWithDigits: Boolean): Int {
        if (version1 == null) {
            return if (version2 == null) 0 else -1
        } else if (version2 == null) {
            return +1
        }
        if (startsWithDigits) {
            val v1prefix = leadingDigits(version1)
            val v2prefix = leadingDigits(version2)
            if (v1prefix.length == 0) {
                if (v2prefix.length == 0) {
                    return 0
                }
                return -1
            } else if (v2prefix.length == 0) {
                return +1
            }
            var diff: Int
            try {
                diff = v1prefix.toInt() - v2prefix.toInt()
            } catch (nfe: NumberFormatException) {
                diff = 0
            }
            if (diff == 0) {
                return compareVersions(
                    version1.substring(v1prefix.length),
                    version2.substring(v2prefix.length),
                    false
                )
            }
            return diff
        } else {
            val v1prefix = leadingNonDigits(version1)
            val v2prefix = leadingNonDigits(version2)
            if (v1prefix.length == 0) {
                if (v2prefix.length == 0) {
                    return 0
                }
                return -1
            } else if (v2prefix.length == 0) {
                return +1
            }
            val diff = v1prefix.compareTo(v2prefix)
            if (diff == 0) {
                return compareVersions(
                    version1.substring(v1prefix.length),
                    version2.substring(v2prefix.length),
                    true
                )
            }
            return diff
        }
    }

    fun leadingDigits(text: String): String {
        val length = text.length
        var buffer: StringBuffer? = null
        for (i in 0..<length) {
            val ch = text.get(i)
            if (!Character.isDigit(ch)) {
                break
            }
            if (buffer == null) {
                buffer = StringBuffer(3)
            }
            buffer.append(ch)
        }
        return if (buffer == null) "" else buffer.toString()
    }

    fun leadingNonDigits(text: String): String {
        val length = text.length
        var buffer: StringBuffer? = null
        for (i in 0..<length) {
            val ch = text.get(i)
            if (Character.isDigit(ch)) {
                break
            }
            if (buffer == null) {
                buffer = StringBuffer(3)
            }
            buffer.append(ch)
        }
        return if (buffer == null) "" else buffer.toString()
    }

    fun isBlank(text: String?): Boolean {
        return (text == null) || "" == text
    }

    fun countLines(text: String): Int {
        var startIdx = 0
        var lineCount = 1
        while (true) {
            val lbIdx = text.indexOf('\n', startIdx)
            if (lbIdx == -1) {
                break
            }
            lineCount++
            startIdx = lbIdx + 1
        }
        return lineCount
    }

    fun isJavaIdentifier(id: String?): Boolean {
        if (id == null) {
            return false
        }
        val len = id.length
        if (len == 0) {
            return false
        }
        if (!Character.isJavaIdentifierStart(id.get(0))) {
            return false
        }
        for (i in 1..<len) {
            if (!Character.isJavaIdentifierPart(id.get(i))) {
                return false
            }
        }
        return true
    }

    fun getJavaStringLiteral(text: String): String {
        val buf = StringBuffer()
        buf.append('"')
        val len = text.length
        for (i in 0..<len) {
            val ch = text.get(i)
            when (ch) {
                '\\' -> buf.append("\\\\")
                '\n' -> buf.append("\\n")
                '\r' -> buf.append("\\r")
                '\t' -> buf.append("\\t")
                '"' -> buf.append("\\\"")
                else -> buf.append(ch)
            }
        }
        buf.append('"')
        return buf.toString()
    }

    fun getJavaIdentifier(candidateID: String): String {
        val len = candidateID.length
        val buf = StringBuffer()
        for (i in 0..<len) {
            val ch = candidateID.get(i)
            val good =
                if (i == 0) Character.isJavaIdentifierStart(ch) else Character.isJavaIdentifierPart(
                    ch
                )
            if (good) {
                buf.append(ch)
            } else {
                buf.append('_')
            }
        }
        return buf.toString()
    }


    fun countChars(text: String, ch: Char): Int {
        val len = text.length
        var count = 0
        for (i in 0..<len) {
            if (ch == text.get(i)) {
                count++
            }
        }
        return count
    }


    /**
     * Removes the double-quotes at the beginning and end of a string. If input
     * string doesn't have the double-quote character at beginning or end, it is
     * returned unchanged.
     */
    fun unquote(text: String): String {
        val length = text.length
        if (length >= 2) {
            if ((text.get(0) == '"') && (text.get(length - 1) == '"')) {
                return text.substring(1, length - 1)
            }
        }
        return text
    }

    /**
     * Removes the single-quotes at the beginning and end of a string. If input
     * string doesn't have the single-quote character at beginning or end, it is
     * returned unchanged.
     */
    fun unquoteSingle(text: String): String {
        val length = text.length
        if (length >= 2) {
            if ((text.get(0) == '\'') && (text.get(length - 1) == '\'')) {
                return text.substring(1, length - 1)
            }
        }
        return text
    }

    fun split(phrase: String): Array<String?> {
        val length = phrase.length
        val wordList = ArrayList<String?>()
        var word: StringBuffer? = null
        for (i in 0..<length) {
            val ch = phrase.get(i)
            when (ch) {
                ' ', '\t', '\r', '\n' -> if (word != null) {
                    wordList.add(word.toString())
                    word = null
                }

                else -> {
                    if (word == null) {
                        word = StringBuffer()
                    }
                    word.append(ch)
                }
            }
        }
        if (word != null) {
            wordList.add(word.toString())
        }
        return wordList.toTypedArray()
    }

    fun truncate(text: String?, maxLength: Int): String? {
        if (text == null) {
            return null
        }
        if (text.length <= maxLength) {
            return text
        }
        return text.substring(0, max(maxLength - 3, 0)) + "..."
    }

    fun strictHtmlEncode(rawText: String, quotes: Boolean): String {
        val output = StringBuffer()
        val length = rawText.length
        for (i in 0..<length) {
            val ch = rawText.get(i)
            when (ch) {
                '&' -> output.append("&amp;")
                '"' -> if (quotes) {
                    output.append("&quot;")
                } else {
                    output.append(ch)
                }

                '<' -> output.append("&lt;")
                '>' -> output.append("&gt;")
                else -> output.append(ch)
            }
        }
        return output.toString()
    }

    fun trimForAlphaNumDash(rawText: String): String {
        val length = rawText.length
        for (i in 0..<length) {
            val ch = rawText.get(i)
            if (((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z')) || ((ch >= '0') && (ch <= '9')) || (ch == '-')) {
                continue
            }
            return rawText.substring(0, i)
        }
        return rawText
    }

    fun getCRLFString(original: String?): String? {
        if (original == null) {
            return null
        }
        val length = original.length
        val buffer = StringBuffer()
        var lastSlashR = false
        for (i in 0..<length) {
            val ch = original.get(i)
            when (ch) {
                '\r' -> lastSlashR = true
                '\n' -> {
                    lastSlashR = false
                    buffer.append("\r\n")
                }

                else -> {
                    if (lastSlashR) {
                        lastSlashR = false
                        buffer.append("\r\n")
                    }
                    buffer.append(ch)
                }
            }
        }
        return buffer.toString()
    }
}