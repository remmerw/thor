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
import io.github.remmerw.thor.dom.DocumentModel
import io.github.remmerw.thor.dom.ElementModel
import io.github.remmerw.thor.dom.ElementType
import io.github.remmerw.thor.dom.NodeModel
import io.github.remmerw.thor.dom.TextModel
import io.github.remmerw.thor.model.StateModel
import io.github.remmerw.thor.model.Utils


@Composable
fun HtmlViewer(
    documentModel: DocumentModel,
    stateModel: StateModel
) {
    val nodeModels = remember { documentModel.nodes() }

    Scaffold(
        content = { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding)
            ) {

                items(
                    items = nodeModels,
                    /*key = { element ->
                    element.id()
                }*/
                ) { nodeModel ->

                    nodeModel.hasChildNodes()


                    when (nodeModel.nodeName.uppercase()) {
                        "HTML" -> {
                            Html(nodeModel, stateModel, modifier = Modifier)
                        }
                    }
                }
            }

        }
    )
}

@Composable
fun Nodes(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {

    val nodeModels = remember { nodeModel.nodes() }


    if (nodeModels.isNotEmpty()) {

        nodeModels.forEach { nodeModel ->

            when (nodeModel) {
                is TextModel -> {
                    Chars(nodeModel, modifier)
                }

                is ElementModel -> {

                    when (nodeModel.elementType()) {
                        ElementType.BODY -> {
                            Body(nodeModel, stateModel, modifier)
                        }

                        ElementType.TABLE -> {
                            Table(nodeModel, stateModel, modifier)
                        }

                        ElementType.FORM -> {
                            Form(nodeModel, stateModel, modifier)
                        }

                        ElementType.CENTER -> {
                            Center(nodeModel, stateModel, modifier)
                        }

                        ElementType.DIV -> {
                            Div(nodeModel, stateModel, modifier)
                        }

                        ElementType.BIG -> {
                            Big(nodeModel, stateModel, modifier)
                        }

                        ElementType.FONT -> {
                            Font(nodeModel, stateModel, modifier)
                        }

                        ElementType.LINK -> {
                            Link(nodeModel, stateModel, modifier)
                        }

                        ElementType.A, ElementType.ANCHOR -> {
                            A(nodeModel, stateModel, modifier)
                        }

                        ElementType.LI -> {
                            Li(nodeModel, stateModel, modifier)
                        }

                        ElementType.BR -> {
                            Br(nodeModel, stateModel, modifier)
                        }

                        ElementType.UL -> {
                            Ul(nodeModel, stateModel, modifier)
                        }

                        ElementType.TR -> {
                            Tr(nodeModel, stateModel, modifier)
                        }

                        ElementType.TD -> {
                            Td(nodeModel, stateModel, modifier)
                        }

                        ElementType.IMG -> {
                            Img(nodeModel, stateModel, modifier)
                        }

                        ElementType.BLOCKQUOTE -> {
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
    }

}


@Composable
fun Chars(text: TextModel, modifier: Modifier) {
    val text = remember { text.text() }

    Text(text = text, modifier = modifier)

}

@Composable
fun Html(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Dummy(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Font(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Img(
    nodeModel: ElementModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    val isImageLoadingEnabled = remember { stateModel.isImageLoadingEnabled }
    Text(nodeModel.nodeName)

    val attributes = remember { nodeModel.attributes() }
    val src = remember { attributes.getValue("src") }


    println("redraw img $src")
    if (isImageLoadingEnabled) {
        AsyncImage(
            model = Utils.getFullURL(nodeModel, src).toExternalForm(),
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
    Text(nodeModel.nodeName)
    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Blockquote(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Li(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Form(
    nodeModel: ElementModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)



    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Body(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Center(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Table(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Link(
    nodeModel: ElementModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, stateModel, modifier)

    Utils.navigate(nodeModel, stateModel)
}

@Composable
fun A(
    nodeModel: ElementModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, stateModel, modifier)

    Utils.navigate(nodeModel, stateModel)


}


@Composable
fun Tr(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, stateModel, modifier)
}


@Composable
fun Td(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Div(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Big(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, stateModel, modifier)
}

@Composable
fun Br(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, stateModel, modifier)
}