package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.model.StateModel


@Composable
fun Img(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current,
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

    Entities( entity = entity,
        stateModel = stateModel,
        modifier = modifier,
        color = color,
        textDecoration = textDecoration,
        textAlign = textAlign,
        fontWeight = fontWeight,
        style = style)

}