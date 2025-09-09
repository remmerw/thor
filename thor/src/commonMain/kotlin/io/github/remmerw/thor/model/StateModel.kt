package io.github.remmerw.thor.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import io.github.remmerw.saga.Entity
import io.github.remmerw.saga.Model
import io.ktor.http.Url
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transform

class StateModel() : ViewModel() {
    var isImageLoadingEnabled: Boolean by mutableStateOf(true)
    var documentUri: String? = null
    private var model: Model? = null

    var entity: Entity? = null

    fun setModel(model: Model) {
        println(model.entity())
        this.model = model
        this.entity = model.entity()

    }

    fun navigate(url: Url, target: String?) {
        TODO("Not yet implemented")
    }

    fun warn(message: String, err: Throwable?) {
        println(message)
        err?.printStackTrace()
    }

    fun attributes(entity: Entity): StateFlow<Map<String, String>> {
        return model!!.attributes(entity)
    }

    fun text(entity: Entity): StateFlow<String> {
        return model!!.text(entity)
    }

    fun children(entity: Entity): Flow<List<Entity>> {
        return model!!.children(entity).transform { value ->
            val list = mutableListOf<Entity>()
            value.forEach { entity ->
                if(entity.name != Type.IMG.name &&
                    entity.name != Type.HEAD.name){
                    list.add(entity)
                }
            }
            emit(list)
        }
    }

    fun fullUri(spec: String): String {

        val cleanSpec = Urls.encodeIllegalCharacters(spec)
        return if (model != null) {
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

    fun color(colorAttribute:String) : Color? {
        try {
            return parseColor(colorAttribute)
        } catch (throwable:Throwable){
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