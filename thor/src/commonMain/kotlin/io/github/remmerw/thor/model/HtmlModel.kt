package io.github.remmerw.thor.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.remmerw.saga.Entity
import io.github.remmerw.saga.Model
import io.github.remmerw.saga.attachToModel
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
            attachToModel(source, model) // todo rename
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
        return model.children(entity).transform { value ->
            val list = mutableListOf<Entity>()
            value.forEach { entity ->
                if (entity.name != Type.HEAD.name &&
                    entity.name != Type.SVG.name &&
                    entity.name != Type.SCRIPT.name &&
                    entity.name != Type.SOURCE.name &&
                    entity.name != Type.NOSCRIPT.name
                ) {
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

    fun body(entity: Entity): StateFlow<List<Entity>> {
        require(entity.name == Type.HTML.name)
        return model.children(entity).transform { value ->
            val list = mutableListOf<Entity>()
            value.forEach { entity ->
                if (entity.name != Type.BODY.name) {
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

    fun html(): StateFlow<List<Entity>> {
        return model.children(model.entity()).transform { value ->
            val list = mutableListOf<Entity>()
            value.forEach { entity ->
                if (entity.name == Type.HTML.name) {
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

    fun fullUri(spec: String): String {

        val cleanSpec = Urls.encodeIllegalCharacters(spec)
        return if (documentUri != null) { // todo might be wrong
            Utils.getFullURL(documentUri!!, cleanSpec).toString()
        } else {
            Url(cleanSpec).toString()
        }
    }





    fun linkClicked(
        url: Url,
        target: String?
    ) {
        println("TODO linkClicked $url")
    }

    fun color(colorAttribute: String): Color? {
        try {
            return parseColor(colorAttribute)
        } catch (throwable: Throwable) {
            throwable.printStackTrace() // todo
            return null
        }
    }


    @Suppress("MagicNumber")
    fun parseColor(colorHEX: String): Color {
        val clean = colorHEX.removePrefix("#").uppercase()

        return when (clean.length) {
            3 -> {
                // RGB -> RRGGBB
                val r = clean[0].digitToInt(16) * 17
                val g = clean[1].digitToInt(16) * 17
                val b = clean[2].digitToInt(16) * 17
                Color(red = r, green = g, blue = b, alpha = 255)
            }

            4 -> {
                // ARGB
                val a = clean[0].digitToInt(radix = 16) * 17
                val r = clean[1].digitToInt(16) * 17
                val g = clean[2].digitToInt(16) * 17
                val b = clean[3].digitToInt(16) * 17
                Color(alpha = a, red = r, green = g, blue = b)
            }

            6 -> {
                // RRGGBB
                val r = clean.substring(0, 2).toInt(16)
                val g = clean.substring(2, 4).toInt(16)
                val b = clean.substring(4, 6).toInt(16)
                Color(red = r, green = g, blue = b, alpha = 255)
            }

            8 -> {
                // AARRGGBB
                val a = clean.substring(0, 2).toInt(16)
                val r = clean.substring(2, 4).toInt(16)
                val g = clean.substring(4, 6).toInt(16)
                val b = clean.substring(6, 8).toInt(16)
                Color(alpha = a, red = r, green = g, blue = b)
            }

            else -> throw IllegalArgumentException("Invalid Hex color: $colorHEX")
        }
    }
}