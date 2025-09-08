package io.github.remmerw.thor.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.model.StateModel


@Composable
fun Img(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    val isImageLoadingEnabled = remember { stateModel.isImageLoadingEnabled }

    val attributes by stateModel.attributes(entity).collectAsState()
    val src = attributes["src"]

    if (isImageLoadingEnabled && !src.isNullOrEmpty()) {
        AsyncImage(
            model = stateModel.fullUri(src),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }

    Entities(entity, stateModel, modifier)

}