package io.github.remmerw.thor.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.toUri
import io.github.remmerw.saga.Entity
import io.github.remmerw.saga.Model
import io.github.remmerw.saga.Tag
import io.github.remmerw.saga.createModel
import io.github.remmerw.thor.debug
import io.ktor.http.Url
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.io.Source

class HtmlModel() : ViewModel() {
    var isImageLoadingEnabled: Boolean by mutableStateOf(true)
    var documentUri: String? = null
    private val model: Model = createModel()

    val entity: Entity = model.entity()

    fun model(): Model { // todo private
        return model
    }


    fun parse(source: Source) {
        try {
            model.parse(source)
            model.normalize()
        } catch (throwable: Throwable) {
            // todo give message
            debug(throwable)
        }
    }

    fun navigate(url: Url, target: String?) {
        TODO("Not yet implemented")
    }

    fun warn(message: String, err: Throwable?) {
        println(message)
        err?.printStackTrace()
    }

    fun attributes(entity: Entity): StateFlow<Map<String, String>> {
        return model.attributes(entity).stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyMap()
        )
    }

    fun text(entity: Entity): StateFlow<String> {
        return model.text(entity).stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ""
        )
    }

    fun children(entity: Entity): StateFlow<List<Entity>> {
        return model.children(entity).stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    }

    fun body(entity: Entity): StateFlow<List<Entity>> {
        require(entity.name == Tag.HTML.tag())
        return model.children(entity).transform { value ->
            val list = mutableListOf<Entity>()
            value.forEach { entity ->
                if (entity.name == Tag.BODY.tag()) {
                    list.add(entity)
                }
            }
            emit(list)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    }

    fun content(entity: Entity): String {
        return model.content(entity)
    }

    fun content(): String {
        return model.content(entity)
    }

    fun html(): StateFlow<List<Entity>> {
        return model.children(model.entity()).transform { value ->
            val list = mutableListOf<Entity>()
            value.forEach { entity ->
                if (entity.name == Tag.HTML.tag()) {
                    list.add(entity)
                }
            }
            emit(list)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    }

    fun fullUri(uri: String): String {

        val cleanUri = Urls.encodeIllegalCharacters(uri)

        try {
            val testUri = cleanUri.toUri()
            if (!testUri.scheme.isNullOrEmpty()) {
                return testUri.toString()
            }
        } catch (throwable: Throwable) {
            debug(throwable)
        }
        return if (documentUri != null) { // todo might be wrong
            Utils.getFullURL(documentUri!!, cleanUri).toString()
        } else {
            Url(cleanUri).toString()
        }
    }


    fun linkClicked(
        url: Url,
        target: String?
    ) {
        println("TODO linkClicked $url")
    }

}