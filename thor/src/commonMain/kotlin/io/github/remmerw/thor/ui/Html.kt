package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.model.StateModel


@Composable
fun HtmlViewer(
    stateModel: StateModel,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current
) {

    val entity by remember { mutableStateOf(stateModel.entity) }
    Scaffold(
        content = { padding ->
            Column(
                modifier = Modifier.padding(padding).fillMaxWidth()
            ) {
                if (entity != null) {
                    Entities(
                        entity = entity!!,
                        stateModel = stateModel,
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
    stateModel: StateModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current
) {

    ColumnEntities(
        entity = entity,
        stateModel = stateModel,
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
fun Br(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Entities(entity, stateModel, modifier)
}