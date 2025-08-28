/*
   Copyright 2014 Uproot Labs India Pvt Ltd

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package io.github.remmerw.thor.cobra.css.domimpl

import cz.vutbr.web.css.MediaQuery
import org.w3c.dom.DOMException
import org.w3c.dom.stylesheets.MediaList
import java.util.Collections

internal class MediaListImpl : MediaList {
    private val containingStyleSheet: JStyleSheetWrapper
    private val mediaList: MutableList<String?>

    constructor(mediaListStr: String?, containingStyleSheet: JStyleSheetWrapper) {
        this.mediaList = splitMediaList(mediaListStr)
        this.containingStyleSheet = containingStyleSheet
    }

    constructor(mediaQueries: MutableList<MediaQuery>, containingStyleSheet: JStyleSheetWrapper) {
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
