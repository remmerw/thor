package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import io.github.remmerw.thor.model.Type
import io.github.remmerw.thor.model.ElementModel
import io.github.remmerw.thor.model.NodeModel
import io.github.remmerw.thor.model.StateModel
import io.github.remmerw.thor.model.TextModel


@Composable
fun HtmlViewer(
    stateModel: StateModel
) {
    // val tasks by stateModel.tasks(pid).collectAsState(emptyList()) ->  Flow<List<Task>>
    val bodyNode = remember {  stateModel.bodyNode() }
    val nodeModels = remember { stateModel.childNodes(bodyNode) }

    Scaffold(
        content = { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxWidth()
            ) {

                items(
                    items = nodeModels,
                    key = { element -> element.uid() }
                ) { nodeModel ->

                    EvaluateNode(
                        nodeModel = nodeModel,
                        stateModel =stateModel,
                        modifier = Modifier)
                }
            }

        }
    )
}

@Composable
fun EvaluateNode(nodeModel: NodeModel,
                 stateModel: StateModel,
                 modifier: Modifier){
    when (nodeModel) {
        is TextModel -> {
            Chars(nodeModel, modifier)
        }

        is ElementModel -> {

            when (nodeModel.name()) {
                Type.HTML.name -> {
                    Html(nodeModel, stateModel, modifier)
                }
                Type.BODY.name -> {
                    Body(nodeModel, stateModel, modifier)
                }

                Type.TABLE.name -> {
                    Table(nodeModel, stateModel, modifier)
                }

                Type.FORM.name -> {
                    Form(nodeModel, stateModel, modifier)
                }

                Type.CENTER.name -> {
                    Center(nodeModel, stateModel, modifier)
                }

                Type.DIV.name -> {
                    Div(nodeModel, stateModel, modifier)
                }

                Type.BIG.name -> {
                    Big(nodeModel, stateModel, modifier)
                }

                Type.FONT.name -> {
                    Font(nodeModel, stateModel, modifier)
                }

                Type.LINK.name -> {
                    Link(nodeModel, stateModel, modifier)
                }

                Type.A.name, Type.ANCHOR.name -> {
                    A(nodeModel, stateModel, modifier)
                }

                Type.LI.name -> {
                    Li(nodeModel, stateModel, modifier)
                }

                Type.BR.name -> {
                    Br(nodeModel, stateModel, modifier)
                }

                Type.UL.name -> {
                    Ul(nodeModel, stateModel, modifier)
                }

                Type.TR.name -> {
                    Tr(nodeModel, stateModel, modifier)
                }

                Type.TD.name -> {
                    Td(nodeModel, stateModel, modifier)
                }

                Type.IMG.name -> {
                    Img(nodeModel, stateModel, modifier)
                }

                Type.BLOCKQUOTE.name -> {
                    Blockquote(nodeModel, stateModel, modifier)
                }

                else -> {
                    Dummy(nodeModel, stateModel, modifier)
                }
            }
        }

        else -> {
            println("TODO")
        }
    }
}

@Composable
fun Nodes(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {

    val nodeModels = remember { stateModel.childNodes(nodeModel) }


    if (nodeModels.isNotEmpty()) {

        nodeModels.forEach { nodeModel ->
            EvaluateNode(nodeModel, stateModel, modifier)
        }
    }

}


@Composable
fun Chars(text: TextModel, modifier: Modifier) {
    val text = remember { text.text() }

    Text(text = text, modifier = modifier.fillMaxWidth())

}

@Composable
fun Html(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {

    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Dummy(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {

    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Font(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {

    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Img(
    nodeModel: ElementModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    val isImageLoadingEnabled = remember { stateModel.isImageLoadingEnabled }

    val src = remember { stateModel.attribute(nodeModel,"src") }

    if (isImageLoadingEnabled && !src.isNullOrEmpty()) {
        AsyncImage(
            model = stateModel.fullUri(src),
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
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Blockquote(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Li(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Form(
    nodeModel: ElementModel,
    stateModel: StateModel,
    modifier: Modifier
) {

    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Body(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Center(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Table(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Link(
    nodeModel: ElementModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)

    //Utils.navigate(nodeModel, stateModel)
}

@Composable
fun A(
    nodeModel: ElementModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)

    //Utils.navigate(nodeModel, stateModel)


}


@Composable
fun Tr(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Td(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Div(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Big(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Br(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Nodes(nodeModel, stateModel, modifier)
}