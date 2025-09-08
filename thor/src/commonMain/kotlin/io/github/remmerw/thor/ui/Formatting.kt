package io.github.remmerw.thor.ui

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.model.StateModel


@Composable
fun Font(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {

    RowEntities(entity, stateModel, modifier)
}

@Composable
fun B(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    textDecoration: TextDecoration? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current,
) {

    RowEntities(
        entity = entity,
        stateModel = stateModel,
        modifier = modifier,
        color = color,
        textDecoration = textDecoration,
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
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current,
) {

    RowEntities(
        entity = entity,
        stateModel = stateModel,
        modifier = modifier,
        color = color,
        textDecoration = textDecoration,
        fontWeight = FontWeight.Thin,
        style = style
    )
}


@Composable
fun Center(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    RowEntities(entity, stateModel, modifier)
}
