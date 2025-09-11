package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
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
fun Li(
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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(8.dp, 2.dp)
            .fillMaxWidth()
    ) {

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
}


@Composable
fun Nav(
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
    FlowRow(modifier = Modifier.fillMaxWidth()) {
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
}