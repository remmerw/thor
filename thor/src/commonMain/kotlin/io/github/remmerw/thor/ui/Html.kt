package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.model.StateModel


@Composable
fun HtmlViewer(
    stateModel: StateModel
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
    modifier: Modifier
) {

    ColumnEntities(entity, stateModel, modifier)
}



@Composable
fun Br(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Entities(entity, stateModel, modifier)
}