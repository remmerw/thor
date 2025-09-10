package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.model.StateModel


@Composable
fun Table(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Column {
        Entities(entity, stateModel, modifier)
    }
}


@Composable
fun Caption(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Row(modifier = Modifier.padding(4.dp).fillMaxWidth()) {
        Entities(
            entity = entity,
            stateModel = stateModel,
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
    stateModel: StateModel,
    modifier: Modifier
) {
    Entities(entity, stateModel, modifier)
}


@Composable
fun TFoot(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Entities(entity, stateModel, modifier)
}


@Composable
fun THead(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Entities(entity, stateModel, modifier)
}


@Composable
fun Tr(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {

    val entities by stateModel.children(entity).collectAsState(emptyList())

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
                        stateModel = stateModel,
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
    stateModel: StateModel,
    modifier: Modifier
) {
    Entities(
        entity = entity,
        stateModel = stateModel,
        modifier = modifier.padding(4.dp),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelSmall
    )
}

@Composable
fun Td(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Entities(
        entity = entity,
        stateModel = stateModel,
        modifier = modifier.padding(4.dp),
        textAlign = TextAlign.Left
    )
}