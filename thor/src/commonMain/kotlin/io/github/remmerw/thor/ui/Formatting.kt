package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.model.StateModel


@Composable
fun Font(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current,
) {
    var colorOverwrite: Color = color
    val attributes by stateModel.attributes(entity).collectAsState()
    val colorAttribute = attributes["color"]
    if (!colorAttribute.isNullOrEmpty()) {
        colorOverwrite = stateModel.color(colorAttribute) ?: color
    }
    Entities(
        entity = entity,
        stateModel = stateModel,
        modifier = modifier,
        color = colorOverwrite,
        textDecoration = textDecoration,
        textAlign = textAlign,
        fontWeight = fontWeight,
        style = style
    )
}

@Composable
fun B(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current,
) {

    Entities(
        entity = entity,
        stateModel = stateModel,
        modifier = modifier,
        color = color,
        textDecoration = textDecoration,
        textAlign = textAlign,
        fontWeight = FontWeight.Bold,
        style = style
    )
}

@Composable
fun Small(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current,
) {

    Entities(
        entity = entity,
        stateModel = stateModel,
        modifier = modifier,
        color = color,
        textDecoration = textDecoration,
        textAlign = textAlign,
        fontWeight = FontWeight.Thin,
        style = style
    )
}


@Composable
fun Span(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current,
) {

    // right now nothing to do here
    Entities(
        entity = entity,
        stateModel = stateModel,
        modifier = modifier,
        color = color,
        textDecoration = textDecoration,
        textAlign = textAlign,
        fontWeight = fontWeight,
        style = style
    )
}

@Composable
fun Center(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current,
) {
    Entities(
        entity = entity,
        stateModel = stateModel,
        modifier = modifier,
        color = color,
        textDecoration = textDecoration,
        textAlign = TextAlign.Center,
        fontWeight = fontWeight,
        style = style
    )
}

@Composable
fun Big(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current,
) {
    Entities(
        entity = entity,
        stateModel = stateModel,
        modifier = modifier,
        color = color,
        textDecoration = textDecoration,
        textAlign = textAlign,
        fontWeight = FontWeight.Bold,
        style = style
    )
}


@Composable
fun Div(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current,
) {

    var textAlignOverwrite = textAlign

    val attributes by stateModel.attributes(entity).collectAsState()
    val alignAttribute = attributes["align"]
    if (!alignAttribute.isNullOrEmpty()) {
        if (alignAttribute == "left") {
            textAlignOverwrite = TextAlign.Left
        }
    }

    Entities(
        entity = entity,
        stateModel = stateModel,
        modifier = modifier,
        color = color,
        textDecoration = textDecoration,
        textAlign = textAlignOverwrite,
        fontWeight = fontWeight,
        style = style
    )
}



@Composable
fun Blockquote(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current,
) {
    Entities(
        entity = entity,
        stateModel = stateModel,
        modifier = modifier.padding(16.dp, 0.dp, 0.dp, 0.dp),
        color = color,
        textDecoration = textDecoration,
        textAlign = textAlign,
        fontWeight = fontWeight,
        style = style
    )
}