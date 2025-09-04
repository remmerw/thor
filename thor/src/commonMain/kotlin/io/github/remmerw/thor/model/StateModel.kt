package io.github.remmerw.thor.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.remmerw.thor.dom.ElementModel
import java.net.URL
import java.util.Optional

class StateModel() : ViewModel() {
    var isImageLoadingEnabled: Boolean by mutableStateOf(true)


    fun navigate(url: URL, target: String?) {
        TODO("Not yet implemented")
    }

    fun warn(message: String, err: Throwable?) {
        println(message)
        err?.printStackTrace()
    }


    fun getAttribute(modelElement: ElementModel, attribute:String) : String {
        return ""
    }

    fun linkClicked(
        url: URL,
        target: String?
    ) {
        println("TODO linkClicked $url")
    }


    fun alert(message: String?) {
        TODO("Not yet implemented")
    }

    fun back() {
        TODO("Not yet implemented")
    }

    fun blur() {
        TODO("Not yet implemented")
    }

    fun close() {
        TODO("Not yet implemented")
    }

    fun confirm(message: String?): Boolean {
        TODO("Not yet implemented")
    }

    fun focus() {
        TODO("Not yet implemented")
    }

    fun open(
        absoluteUrl: String?,
        windowName: String?,
        windowFeatures: String?,
        replace: Boolean
    ) {
        TODO("Not yet implemented")
    }

    fun open(
        url: URL,
        windowName: String?,
        windowFeatures: String?,
        replace: Boolean
    ) {
        TODO("Not yet implemented")
    }

    fun prompt(message: String?, inputDefault: String?): String? {
        TODO("Not yet implemented")
    }

    fun scroll(x: Int, y: Int) {
        TODO("Not yet implemented")
    }

    fun scrollBy(x: Int, y: Int) {
        TODO("Not yet implemented")
    }

    fun resizeTo(width: Int, height: Int) {
        TODO("Not yet implemented")
    }

    fun resizeBy(byWidth: Int, byHeight: Int) {
        TODO("Not yet implemented")
    }

    fun isClosed(): Boolean {
        TODO("Not yet implemented")
    }

    fun defaultStatus(): String? {
        TODO("Not yet implemented")
    }

    fun name(): String? {
        TODO("Not yet implemented")
    }


    fun status(): String? {
        TODO("Not yet implemented")
    }


    fun reload() {
        TODO("Not yet implemented")
    }

    fun historyLength(): Int {
        TODO("Not yet implemented")
    }

    fun currentURL(): String? {
        TODO("Not yet implemented")
    }

    fun nextURL(): Optional<String>? {
        TODO("Not yet implemented")
    }

    fun previousURL(): Optional<String>? {
        TODO("Not yet implemented")
    }

    fun forward() {
        TODO("Not yet implemented")
    }

    fun moveInHistory(offset: Int) {
        TODO("Not yet implemented")
    }

    fun goToHistoryURL(url: String?) {
        TODO("Not yet implemented")
    }
}