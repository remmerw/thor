package io.github.remmerw.thor.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.model.StateModel


@Composable
fun Table(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    RowEntities(entity, stateModel, modifier)
}