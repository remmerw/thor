package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun HtmlViewer(
    htmlModel: HtmlModel,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current
) {

    val entities by htmlModel.html().collectAsState(emptyList())
    Scaffold(
        content = { padding ->
            Column(
                modifier = Modifier.padding(padding).fillMaxWidth()
            ) {
                entities.forEach { entity ->
                    println(entity.name)
                    Html(
                        entity = entity,
                        htmlModel = htmlModel,
                        modifier = Modifier,
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
    )
}


@Composable
fun Html(
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

    val entities by htmlModel.body(entity).collectAsState(emptyList())

    entities.forEach { entity ->
        println(entity.name)
        Body(
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


@Composable
fun Br(
    entity: Entity,
    htmlModel: HtmlModel,
    modifier: Modifier
) {
    Entities(entity, htmlModel, modifier)
}