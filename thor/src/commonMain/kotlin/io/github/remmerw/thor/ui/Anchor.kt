package io.github.remmerw.thor.ui

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
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
fun A(
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

    val attributes by htmlModel.attributes(entity).collectAsState()
    val href = attributes["href"]
    // todo this is wrong !!!
    if (href != null) {
        // todo
    }


    Layout(
        entity = entity,
        htmlModel = htmlModel,
        modifier = modifier,
        color = Color.Blue,
        fontSize = fontSize,
        fontStyle = fontStyle,
        textDecoration = TextDecoration.Underline,
        textAlign = textAlign,
        fontWeight = fontWeight,
        style = MaterialTheme.typography.labelMedium,
    )

    //Utils.navigate(entity, stateModel)


}