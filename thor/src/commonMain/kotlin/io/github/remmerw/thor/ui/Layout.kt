package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.model.HtmlModel

enum class Layout {
    LINEAR, VERTICAL, HORIZONTAL
}

@Composable
fun Layout(
    entity: Entity,
    htmlModel: HtmlModel,
    modifier: Modifier,
    layout: Layout = Layout.LINEAR,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    style: TextStyle = LocalTextStyle.current
) {

    val entities by htmlModel.children(entity).collectAsState(emptyList())

    if (entities.isNotEmpty()) {

        entities.forEach { entity ->
            when (layout) {
                Layout.LINEAR -> {
                    Element(
                        entity = entity,
                        htmlModel = htmlModel,
                        modifier = modifier,
                        color = color,
                        fontSize = fontSize,
                        fontStyle = fontStyle,
                        textDecoration = textDecoration,
                        textAlign = textAlign,
                        fontWeight = fontWeight,
                        fontFamily = fontFamily,
                        style = style
                    )
                }

                Layout.VERTICAL -> {
                    Column {
                        Element(
                            entity = entity,
                            htmlModel = htmlModel,
                            modifier = modifier,
                            color = color,
                            fontSize = fontSize,
                            fontStyle = fontStyle,
                            textDecoration = textDecoration,
                            textAlign = textAlign,
                            fontWeight = fontWeight,
                            fontFamily = fontFamily,
                            style = style
                        )
                    }
                }

                Layout.HORIZONTAL -> {
                    Row {
                        Element(
                            entity = entity,
                            htmlModel = htmlModel,
                            modifier = modifier,
                            color = color,
                            fontSize = fontSize,
                            fontStyle = fontStyle,
                            textDecoration = textDecoration,
                            textAlign = textAlign,
                            fontWeight = fontWeight,
                            fontFamily = fontFamily,
                            style = style
                        )
                    }
                }
            }
        }
    }
}