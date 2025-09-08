package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.model.StateModel


@Composable
fun Body(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    val entities by stateModel.children(entity).collectAsState()
    LazyColumn(modifier = modifier.fillMaxWidth()) {

        items(
            items = entities,
            key = { element -> element.uid }
        ) { entity ->
            FlowRow {
                Entity(
                    entity = entity,
                    stateModel = stateModel,
                    modifier = Modifier
                )
            }
        }
    }
}