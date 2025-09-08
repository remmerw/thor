package io.github.remmerw.thor.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.remmerw.saga.Entity
import io.github.remmerw.saga.Model
import io.ktor.http.Url
import kotlinx.coroutines.flow.StateFlow

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

    fun children(entity: Entity): StateFlow<List<Entity>> {
        return model!!.children(entity)
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

}