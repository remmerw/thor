package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.remmerw.thor.dom.Entity
import io.github.remmerw.thor.model.StateModel


@Composable
fun Body(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    val entities = remember { stateModel.children(entity) }
    LazyColumn(modifier = modifier.fillMaxWidth()) {

        items(
            items = entities,
            key = { element -> element.uid }
        ) { nodeModel ->
            FlowRow {
                EvaluateEntity(
                    entity = nodeModel,
                    stateModel = stateModel,
                    modifier = Modifier
                )
            }
        }
    }
}