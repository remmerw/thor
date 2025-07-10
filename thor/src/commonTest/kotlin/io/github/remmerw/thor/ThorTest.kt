package io.github.remmerw.thor

import io.github.remmerw.thor.core.Bookmark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.kmp.testing.context
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ThorTest {


    @Test
    fun initializeTests(): Unit = runBlocking(Dispatchers.IO) {

        val context = context()

        initializeThor(context)

        val thor = thor()
        assertNotNull(thor)

        val bookmarks = thor.bookmarks()
        // tests bookmarks
        val bookmark = Bookmark(0, "https://test.de", "Moin", null)
        bookmarks.insert(bookmark)

        assertTrue(bookmarks.hasBookmark("https://test.de").first())

        val stored = bookmarks.bookmark("https://test.de")
        assertNotNull(stored)

        bookmarks.delete(stored)

        assertFalse(bookmarks.hasBookmark("https://test.de").first())

        // tests
    }

}