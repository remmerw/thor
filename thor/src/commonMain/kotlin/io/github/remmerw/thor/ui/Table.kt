package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.model.StateModel


@Composable
fun Table(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        ColumnEntities(entity, stateModel, modifier)
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
fun Th(
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
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Entities(entity, stateModel, modifier)
    }
}


@Composable
fun Td(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Entities(entity, stateModel, modifier)
}