package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import coil3.compose.AsyncImage
import io.github.remmerw.thor.dom.Entity
import io.github.remmerw.thor.model.StateModel


@Composable
fun HtmlViewer(
    stateModel: StateModel
) {

    val entity by stateModel.entity.collectAsState()
    Scaffold(
        content = { padding ->
            Box(
                modifier = Modifier.padding(padding).fillMaxWidth()
            ) {
                if (entity != null) {
                    Nodes(
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
fun Nodes(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier,
    style: TextStyle = LocalTextStyle.current,
) {

    val entities = remember { stateModel.children(entity) }

    if (entities.isNotEmpty()) {

        entities.forEach { entity ->
            EvaluateEntity(entity, stateModel, modifier, style)
        }
    }

}


@Composable
fun Chars(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    val text by stateModel.wurst(entity).collectAsState()

    if (text.isNotEmpty()) {
        Text(text = text, modifier = modifier, style = style)
    }

}

@Composable
fun Html(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {

    Nodes(entity, stateModel, modifier)
}


@Composable
fun Dummy(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {

    Nodes(entity, stateModel, modifier)
}


@Composable
fun Font(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {

    Nodes(entity, stateModel, modifier)
}


@Composable
fun Img(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    val isImageLoadingEnabled = remember { stateModel.isImageLoadingEnabled }

    val src by remember {
        mutableStateOf(
            stateModel.attribute(entity, "src")
        )
    }

    if (isImageLoadingEnabled && !src.isNullOrEmpty()) {
        AsyncImage(
            model = stateModel.fullUri(src!!),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
        )
    }

    Nodes(entity, stateModel, modifier)

}

@Composable
fun Ul(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(entity, stateModel, modifier)
}


@Composable
fun Blockquote(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(entity, stateModel, modifier)
}

@Composable
fun Li(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(entity, stateModel, modifier)
}

@Composable
fun Form(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {

    Nodes(entity, stateModel, modifier)
}


@Composable
fun Center(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(entity, stateModel, modifier)
}

@Composable
fun Table(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(entity, stateModel, modifier)
}


@Composable
fun Link(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(entity, stateModel, modifier)

    //Utils.navigate(entity, stateModel)
}

@Composable
fun A(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier,
    style: TextStyle = LocalTextStyle.current
) {

    val href by remember {
        mutableStateOf(
            stateModel.attribute(entity, "href")
        )
    }

    // todo this is wrong !!!
    if (href != null) {
        Text(
            text = href!!, modifier = modifier,
            color = Color.Blue,
            textDecoration = TextDecoration.Underline
        )
    }


    Nodes(entity, stateModel, modifier, style)

    //Utils.navigate(entity, stateModel)


}


@Composable
fun Tr(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(entity, stateModel, modifier)
}


@Composable
fun Td(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(entity, stateModel, modifier)
}

@Composable
fun Div(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(entity, stateModel, modifier)
}

@Composable
fun Big(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(entity, stateModel, modifier)
}

@Composable
fun Br(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(entity, stateModel, modifier)
}