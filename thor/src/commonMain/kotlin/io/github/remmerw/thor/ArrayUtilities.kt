package io.github.remmerw.thor

object ArrayUtilities {

    fun <T> iterator(array: Array<T?>, offset: Int, length: Int): MutableIterator<T?> {
        return ArrayIterator<T?>(array, offset, length)
    }

    fun <T> contains(ts: Array<T?>, t: T?): Boolean {
        for (e in ts) {
            if (e == t) {
                return true
            }
        }
        return false
    }

    private class ArrayIterator<T>(
        private val array: Array<T?>,
        private var offset: Int,
        length: Int
    ) : MutableIterator<T?> {
        private val top: Int

        init {
            this.top = offset + length
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.Iterator#hasNext()
         */
        override fun hasNext(): Boolean {
            return this.offset < this.top
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.Iterator#next()
         */
        override fun next(): T? {
            return this.array[this.offset++]
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.Iterator#remove()
         */
        override fun remove() {
            throw UnsupportedOperationException()
        }
    }
}