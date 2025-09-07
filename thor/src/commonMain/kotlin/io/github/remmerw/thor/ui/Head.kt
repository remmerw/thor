package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.remmerw.thor.dom.Entity
import io.github.remmerw.thor.model.StateModel


@Composable
fun H6(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(
        entity, stateModel,
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.titleSmall
    )

}


@Composable
fun H5(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(
        entity, stateModel,
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
fun H4(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(
        entity, stateModel,
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
fun H3(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(
        entity, stateModel,
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.headlineLarge
    )
}


@Composable
fun H2(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(
        entity, stateModel,
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.headlineMedium
    )


}


@Composable
fun H1(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(
        entity, stateModel,
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.headlineSmall
    )
}