package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.model.HtmlModel

@Composable
fun Caption(
    entity: Entity,
    htmlModel: HtmlModel,
    modifier: Modifier
) {
    Row(modifier = Modifier.padding(4.dp).fillMaxWidth()) {
        Layout(
            entity = entity,
            htmlModel = htmlModel,
            modifier = modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun TFoot(
    entity: Entity,
    htmlModel: HtmlModel,
    modifier: Modifier
) {
    Layout(entity, htmlModel, modifier)
}


@Composable
fun THead(
    entity: Entity,
    htmlModel: HtmlModel,
    modifier: Modifier
) {
    Layout(entity, htmlModel, modifier)
}

