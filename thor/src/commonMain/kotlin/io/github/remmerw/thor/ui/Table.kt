package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.model.HtmlModel


@Composable
fun Table(
    entity: Entity,
    htmlModel: HtmlModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current
) {
    val entities by htmlModel.children(entity).collectAsState(emptyList())

    if (entities.isNotEmpty()) {
        Column {
            entities.forEach { entity ->

                EntityComposable(
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
        }
    }
}


@Composable
fun Caption(
    entity: Entity,
    htmlModel: HtmlModel,
    modifier: Modifier
) {
    Row(modifier = Modifier.padding(4.dp).fillMaxWidth()) {
        Entities(
            entity = entity,
            htmlModel = htmlModel,
            modifier = modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun TBody(
    entity: Entity,
    htmlModel: HtmlModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current
) {
    val entities by htmlModel.children(entity).collectAsState(emptyList())

    if (entities.isNotEmpty()) {
        Column {
            entities.forEach { entity ->

                EntityComposable(
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
        }
    }
}


@Composable
fun TFoot(
    entity: Entity,
    htmlModel: HtmlModel,
    modifier: Modifier
) {
    Entities(entity, htmlModel, modifier)
}


@Composable
fun THead(
    entity: Entity,
    htmlModel: HtmlModel,
    modifier: Modifier
) {
    Entities(entity, htmlModel, modifier)
}


@Composable
fun Tr(
    entity: Entity,
    htmlModel: HtmlModel,
    modifier: Modifier
) {

    val entities by htmlModel.children(entity).collectAsState(emptyList())

    if (entities.isNotEmpty()) {

        val weight = 1F

        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {


            entities.forEach { entity ->
                Column(modifier = modifier.weight(weight, true)) {
                    EntityComposable(
                        entity = entity,
                        htmlModel = htmlModel,
                        modifier = modifier,
                        /* color = color,
                         fontSize = fontSize,
                         fontStyle = fontStyle,
                         textDecoration = textDecoration,
                         textAlign = textAlign,
                         fontWeight = fontWeight,
                         style = style*/
                    )
                }
            }
        }
    }
}


@Composable
fun Th(
    entity: Entity,
    htmlModel: HtmlModel,
    modifier: Modifier
) {
    Entities(
        entity = entity,
        htmlModel = htmlModel,
        modifier = modifier.padding(4.dp),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelSmall
    )
}

@Composable
fun Td(
    entity: Entity,
    htmlModel: HtmlModel,
    modifier: Modifier
) {
    Entities(
        entity = entity,
        htmlModel = htmlModel,
        modifier = modifier.padding(4.dp),
        textAlign = TextAlign.Left
    )
}