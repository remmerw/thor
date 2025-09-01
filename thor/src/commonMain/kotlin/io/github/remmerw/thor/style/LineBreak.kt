package io.github.remmerw.thor.style

class LineBreak(val breakType: Int) {
    companion object {
        const val NONE: Int = 0
        const val LEFT: Int = 1
        const val RIGHT: Int = 2
        const val ALL: Int = 3

        fun getBreakType(clearAttr: String?): Int {
            if (clearAttr == null) {
                return NONE
            } else if ("all".equals(clearAttr, ignoreCase = true)) {
                return ALL
            } else if ("left".equals(clearAttr, ignoreCase = true)) {
                return LEFT
            } else if ("right".equals(clearAttr, ignoreCase = true)) {
                return RIGHT
            } else {
                return NONE
            }
        }
    }
}