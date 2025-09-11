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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.model.HtmlModel


@Composable
fun RowLayout(
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

    val weight = 1F

    Row {
        entities.forEach { entity ->
            Column(modifier = modifier.weight(weight, true)) {
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
                    style = style
                )
            }
        }
    }


}