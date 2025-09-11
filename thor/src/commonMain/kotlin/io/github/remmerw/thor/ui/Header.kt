package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.model.HtmlModel


@Composable
fun H6(
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

    Entities(
        entity = entity,
        htmlModel = htmlModel,
        modifier = modifier.fillMaxWidth(),
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        textDecoration = textDecoration,
        textAlign = textAlign,
        fontWeight = fontWeight,
        style = MaterialTheme.typography.titleSmall
    )


}


@Composable
fun H5(
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

    Entities(
        entity = entity,
        htmlModel = htmlModel,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        textDecoration = textDecoration,
        textAlign = textAlign,
        fontWeight = fontWeight,
        style = MaterialTheme.typography.titleMedium,
    )

}

@Composable
fun H4(
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

    Entities(
        entity = entity,
        htmlModel = htmlModel,
        modifier = modifier.fillMaxWidth(),
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        textDecoration = textDecoration,
        textAlign = textAlign,
        fontWeight = fontWeight,
        style = MaterialTheme.typography.titleLarge,
    )


}

@Composable
fun H3(
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

    Entities(
        entity = entity,
        htmlModel = htmlModel,
        modifier = modifier.fillMaxWidth(),
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        textDecoration = textDecoration,
        textAlign = textAlign,
        fontWeight = fontWeight,
        style = MaterialTheme.typography.headlineSmall
    )


}


@Composable
fun H2(
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

    Entities(
        entity = entity,
        htmlModel = htmlModel,
        modifier = modifier.fillMaxWidth(),
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        textDecoration = textDecoration,
        textAlign = textAlign,
        fontWeight = fontWeight,
        style = MaterialTheme.typography.headlineMedium
    )

}


@Composable
fun H1(
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

    Entities(
        entity = entity,
        htmlModel = htmlModel,
        modifier = modifier.fillMaxWidth(),
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        textDecoration = textDecoration,
        textAlign = textAlign,
        fontWeight = fontWeight,
        style = MaterialTheme.typography.headlineLarge
    )

}