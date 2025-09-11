package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import coil3.PlatformContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.model.HtmlModel


@Composable
fun Img(
    entity: Entity,
    htmlModel: HtmlModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current,
) {
    val isImageLoadingEnabled = remember { htmlModel.isImageLoadingEnabled }

    val attributes by htmlModel.attributes(entity).collectAsState()
    val src = attributes["src"]
    val alt = attributes["alt"]


    if (isImageLoadingEnabled && !src.isNullOrEmpty()) {
        println(src)
        AsyncImage(
            model = htmlModel.fullUri(src),
            contentDescription = alt,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }

    Layout(
        entity = entity,
        htmlModel = htmlModel,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        textDecoration = textDecoration,
        textAlign = textAlign,
        fontWeight = fontWeight,
        style = style
    )

}


@Composable
fun Svg(
    entity: Entity,
    htmlModel: HtmlModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current,
) {

    val svg = remember { htmlModel.content(entity) }

    Card {
        Text(
            text = "SVG", color = Color.Red,
            style = MaterialTheme.typography.headlineMedium
        )

        if (!svg.isNotEmpty()) {
            AsyncImage(
                model = svg.encodeToByteArray(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = modifier
            )
        }
    }
    /*
    Layout(
        entity = entity,
        htmlModel = htmlModel,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        textDecoration = textDecoration,
        textAlign = textAlign,
        fontWeight = fontWeight,
        style = style
    )*/
}