package io.github.remmerw.thor.cobra.util

import org.w3c.dom.DOMException

class DOMExceptions {
    enum class ExtendedError(val code: Short) {
        SecurityError(18.toShort()),
        NetworkError(19.toShort()),
        AbortError(20.toShort()),
        URLMismatchError(21.toShort()),
        QuotaExceededError(22.toShort()),
        TimeoutError(23.toShort()),
        InvalidNodeTypeError(24.toShort()),
        DataCloneError(25.toShort());

        fun createException(): DOMException {
            return DOMException(code, name)
        }

        fun createException(msg: String?): DOMException {
            return DOMException(code, name + ": " + msg)
        }
    }
}
