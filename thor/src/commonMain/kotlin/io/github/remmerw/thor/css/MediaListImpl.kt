package io.github.remmerw.thor.css

import cz.vutbr.web.css.MediaQuery
import org.w3c.dom.DOMException
import org.w3c.dom.stylesheets.MediaList
import java.util.Collections

internal class MediaListImpl : MediaList {
    private val containingStyleSheet: StyleSheetWrapper
    private val mediaList: MutableList<String?>

    constructor(mediaListStr: String?, containingStyleSheet: StyleSheetWrapper) {
        this.mediaList = splitMediaList(mediaListStr)
        this.containingStyleSheet = containingStyleSheet
    }

    constructor(mediaQueries: MutableList<MediaQuery>, containingStyleSheet: StyleSheetWrapper) {
        val mediaList: MutableList<String?> = ArrayList<String?>()
        for (mediaQuery in mediaQueries) {
            mediaList.add(mediaQuery.type)
        }
        this.mediaList = mediaList
        this.containingStyleSheet = containingStyleSheet
    }

    override fun getMediaText(): String? {
        return this.mediaList.toString()
    }

    @Throws(DOMException::class)
    override fun setMediaText(mediaText: String?) {
        //TODO send a notification about the change
        /*
    this.mediaList.clear();
    this.mediaList.addAll(splitMediaList(mediaText));
     */
        throw UnsupportedOperationException()
    }

    override fun getLength(): Int {
        return this.mediaList.size
    }

    override fun item(index: Int): String? {
        return this.mediaList.get(index)
    }

    @Throws(DOMException::class)
    override fun deleteMedium(oldMedium: String?) {
        this.mediaList.remove(oldMedium)
        this.containingStyleSheet.informChanged()
    }

    @Throws(DOMException::class)
    override fun appendMedium(newMedium: String?) {
        this.mediaList.add(newMedium)
        this.containingStyleSheet.informChanged()
    }

    private fun splitMediaList(mediaListStr: String?): MutableList<String?> {
        if ((mediaListStr != null) && (mediaListStr.length > 0)) {
            val mediaArray: Array<String?> =
                mediaListStr.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val mediaList: MutableList<String?> = ArrayList<String?>()
            Collections.addAll<String?>(mediaList, *mediaArray)
            return mediaList
        }
        return ArrayList<String?>()
    }

    override fun toString(): String {
        return mediaList.toString()
    }
}
