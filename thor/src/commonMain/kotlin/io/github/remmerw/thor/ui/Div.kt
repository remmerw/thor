package io.github.remmerw.thor.ui

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
fun Div(
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

    var textAlignOverwrite = textAlign

    val attributes by htmlModel.attributes(entity).collectAsState()
    val alignAttribute = attributes["align"]
    if (!alignAttribute.isNullOrEmpty()) {
        if (alignAttribute == "left") {
            textAlignOverwrite = TextAlign.Left
        }
    }

    val entities by htmlModel.children(entity).collectAsState(emptyList())

    if (entities.isNotEmpty()) {

        entities.forEach { entity ->

            EntityComposable(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlignOverwrite,
                fontWeight = fontWeight,
                style = style
            )
        }

    }
}