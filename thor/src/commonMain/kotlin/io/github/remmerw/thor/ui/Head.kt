package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.remmerw.thor.model.NodeModel
import io.github.remmerw.thor.model.StateModel



@Composable
fun H6(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    val text by remember { mutableStateOf(stateModel.text(nodeModel)) }

    Text(text = text, modifier = modifier.fillMaxWidth(), style = MaterialTheme.typography.titleSmall)
}


@Composable
fun H5(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    val text by remember { mutableStateOf(stateModel.text(nodeModel)) }

    Text(text = text, modifier = modifier.fillMaxWidth(), style = MaterialTheme.typography.titleMedium)
}

@Composable
fun H4(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    val text by remember { mutableStateOf(stateModel.text(nodeModel)) }

    Text(text = text, modifier = modifier.fillMaxWidth(), style = MaterialTheme.typography.titleLarge)
}

@Composable
fun H3(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    val text by remember { mutableStateOf(stateModel.text(nodeModel)) }

    Text(text = text, modifier = modifier.fillMaxWidth(), style = MaterialTheme.typography.headlineMedium)
}


@Composable
fun H2(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    val text by remember { mutableStateOf(stateModel.text(nodeModel)) }

    Text(text = text, modifier = modifier.fillMaxWidth(), style = MaterialTheme.typography.headlineMedium)
}


@Composable
fun H1(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    val text by remember { mutableStateOf(stateModel.text(nodeModel)) }

    Text(text = text, modifier = modifier.fillMaxWidth(), style = MaterialTheme.typography.headlineLarge)
}