package io.github.remmerw.thor.ui

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.model.StateModel


@Composable
fun A(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier,
    style: TextStyle = LocalTextStyle.current
) {

    val attributes by stateModel.attributes(entity).collectAsState()
    val href = attributes["href"]
    // todo this is wrong !!!
    if (href != null) {
        // todo
    }


    Entities(
        entity = entity,
        stateModel = stateModel,
        modifier = modifier,
        color = Color.Blue,
        textDecoration = TextDecoration.Underline,
        style = MaterialTheme.typography.labelMedium
    )

    //Utils.navigate(entity, stateModel)


}