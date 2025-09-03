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
import io.github.remmerw.thor.dom.HTMLAnchorElementModel
import io.github.remmerw.thor.dom.HTMLFormElementModel
import io.github.remmerw.thor.dom.HTMLImageElementModel
import io.github.remmerw.thor.dom.HTMLLinkElementModel
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
                    println(nodeModel.nodeName)

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
            when (nodeModel.nodeName.uppercase()) {
                "BODY" -> {
                    Body(nodeModel, stateModel, modifier)
                }

                "TABLE" -> {
                    Table(nodeModel, stateModel, modifier)
                }

                "FORM" -> {
                    Form(nodeModel as HTMLFormElementModel, stateModel, modifier)
                }

                "CENTER" -> {
                    Center(nodeModel, stateModel, modifier)
                }

                "DIV" -> {
                    Div(nodeModel, stateModel, modifier)
                }

                "Big" -> {
                    Big(nodeModel, stateModel, modifier)
                }

                "FONT" -> {
                    Font(nodeModel, stateModel, modifier)
                }

                "LINK" -> {
                    Link(nodeModel as HTMLLinkElementModel, stateModel, modifier)
                }

                "A", "ANCHOR" -> {
                    A(nodeModel as HTMLAnchorElementModel, stateModel, modifier)
                }

                "LI" -> {
                    Li(nodeModel, stateModel, modifier)
                }

                "BR" -> {
                    Br(nodeModel, stateModel, modifier)
                }

                "UL" -> {
                    Ul(nodeModel, stateModel, modifier)
                }

                "TR" -> {
                    Tr(nodeModel, stateModel, modifier)
                }

                "TD" -> {
                    Td(nodeModel, stateModel, modifier)
                }

                "IMG" -> {
                    Img(nodeModel as HTMLImageElementModel, stateModel, modifier)
                }

                "BLOCKQUOTE" -> {
                    Blockquote(nodeModel, stateModel, modifier)
                }

                "CHARS" -> {
                    Chars(nodeModel as TextModel, modifier)
                }

                else -> {
                    println(nodeModel.nodeName)
                    Dummy(nodeModel, stateModel, modifier)
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
    nodeModel: HTMLImageElementModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    val isImageLoadingEnabled = remember { stateModel.isImageLoadingEnabled }
    Text(nodeModel.nodeName)

    val attributes = remember { nodeModel.attributes() }
    val src = remember { attributes.getValue("src") }
    val url = remember { nodeModel.getFullURL(src) }


    println("redraw img")
    if (isImageLoadingEnabled) {
        AsyncImage(
            model = url.toExternalForm(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
    Nodes(nodeModel, stateModel, modifier)
    println(nodeModel.baseURI)
    println(nodeModel.src)
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
    nodeModel: HTMLFormElementModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)

    Utils.submit(nodeModel, stateModel)

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
    nodeModel: HTMLLinkElementModel,
    stateModel: StateModel,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, stateModel, modifier)

    Utils.navigate(nodeModel, stateModel)
}

@Composable
fun A(
    nodeModel: HTMLAnchorElementModel,
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