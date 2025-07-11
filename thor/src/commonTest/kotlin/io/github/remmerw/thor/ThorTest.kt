package io.github.remmerw.thor

import io.github.remmerw.thor.core.Bookmark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.kmp.testing.context
import kotlin.test.Test
import kotlin.test.assertEquals
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


        // tests bookmarks
        val bookmark = Bookmark(0, "https://test.de", "Moin", null)
        thor.storeBookmark(bookmark)

        assertTrue(thor.hasBookmark("https://test.de").first())

        val stored = thor.getBookmark("https://test.de")
        assertNotNull(stored)

        val list = thor.getBookmarks().first()
        assertEquals(list.size, 1)

        thor.deleteBookmark(stored)

        assertFalse(thor.hasBookmark("https://test.de").first())


        // tests homepage
        thor.setHomepage("https://test.de", "Moin", null)
        val uri = thor.getHomepageUri("test").first()
        assertEquals("https://test.de", uri)
        thor.removeHomepage()

        // tests tasks



        // cleanup
        thor.reset()
    }

}