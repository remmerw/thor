package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    // val tasks by stateModel.tasks(pid).collectAsState(emptyList()) ->  Flow<List<Task>>
    val bodyNode by remember { mutableStateOf(stateModel.bodyNode()) }
    val nodeModels = remember { stateModel.childNodes(bodyNode) }

    Scaffold(
        content = { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxWidth()
            ) {

                items(
                    items = nodeModels,
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
    )
}


@Composable
fun Nodes(
    nodeModel: Entity,
    stateModel: StateModel,
    modifier: Modifier,
    style: TextStyle = LocalTextStyle.current,
) {

    val nodeModels = remember { stateModel.childNodes(nodeModel) }


    if (nodeModels.isNotEmpty()) {

        nodeModels.forEach { nodeModel ->
            EvaluateEntity(nodeModel, stateModel, modifier, style)
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
    val text by remember { mutableStateOf(stateModel.text(entity)) }
    if (text.isNotEmpty()) {
        Text(text = text, modifier = modifier, style = style)
    }

}

@Composable
fun Html(
    nodeModel: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {

    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Dummy(
    nodeModel: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {

    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Font(
    nodeModel: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {

    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Img(
    nodeModel: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    val isImageLoadingEnabled = remember { stateModel.isImageLoadingEnabled }

    val src by remember {
        mutableStateOf(
            stateModel.attribute(nodeModel, "src")
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

    Nodes(nodeModel, stateModel, modifier)

}

@Composable
fun Ul(
    nodeModel: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Blockquote(
    nodeModel: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Li(
    nodeModel: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Form(
    nodeModel: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {

    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Body(
    nodeModel: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Center(
    nodeModel: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Table(
    nodeModel: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Link(
    nodeModel: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)

    //Utils.navigate(nodeModel, stateModel)
}

@Composable
fun A(
    nodeModel: Entity,
    stateModel: StateModel,
    modifier: Modifier,
    style: TextStyle = LocalTextStyle.current
) {

    val href by remember {
        mutableStateOf(
            stateModel.attribute(nodeModel, "href")
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


    Nodes(nodeModel, stateModel, modifier, style)

    //Utils.navigate(nodeModel, stateModel)


}


@Composable
fun Tr(
    nodeModel: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Td(
    nodeModel: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Div(
    nodeModel: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Big(
    nodeModel: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Br(
    nodeModel: Entity,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}