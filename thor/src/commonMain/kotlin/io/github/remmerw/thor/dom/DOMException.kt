package io.github.remmerw.thor.dom

open class DOMException(var code: Short, var2: String?) : RuntimeException(var2) {
    companion object {
        const val INDEX_SIZE_ERR: Short = 1
        const val DOMSTRING_SIZE_ERR: Short = 2
        const val HIERARCHY_REQUEST_ERR: Short = 3
        const val WRONG_DOCUMENT_ERR: Short = 4
        const val INVALID_CHARACTER_ERR: Short = 5
        const val NO_DATA_ALLOWED_ERR: Short = 6
        const val NO_MODIFICATION_ALLOWED_ERR: Short = 7
        const val NOT_FOUND_ERR: Short = 8
        const val NOT_SUPPORTED_ERR: Short = 9
        const val INUSE_ATTRIBUTE_ERR: Short = 10
        const val INVALID_STATE_ERR: Short = 11
        const val SYNTAX_ERR: Short = 12
        const val INVALID_MODIFICATION_ERR: Short = 13
        const val NAMESPACE_ERR: Short = 14
        const val INVALID_ACCESS_ERR: Short = 15
        const val VALIDATION_ERR: Short = 16
        const val TYPE_MISMATCH_ERR: Short = 17
    }
}