package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.model.StateModel


@Composable
fun Ul(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    ColumnEntities(entity, stateModel, modifier)
}


@Composable
fun Ol(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    ColumnEntities(entity, stateModel, modifier)
}

@Composable
fun Li(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(0.dp, 4.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "\u2022",
            modifier = Modifier.padding(8.dp),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )
        Entities(entity, stateModel, modifier)
    }
}