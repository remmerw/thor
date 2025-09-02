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
import io.github.remmerw.thor.dom.HTMLImageElementModel
import io.github.remmerw.thor.dom.NodeModel


@Composable
fun HtmlViewer(documentModel: DocumentModel) {
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
                            Html(nodeModel)
                        }
                    }
                }
            }

        }
    )
}

@Composable
fun Nodes(nodeModel: NodeModel) {

    val nodeModels = remember { nodeModel.nodes() }


    if (nodeModels.isNotEmpty()) {

        nodeModels.forEach { nodeModel ->
            when (nodeModel.nodeName.uppercase()) {
                "BODY" -> {
                    Body(nodeModel)
                }

                "TABLE" -> {
                    Table(nodeModel)
                }

                "FORM" -> {
                    Form(nodeModel)
                }

                "CENTER" -> {
                    Center(nodeModel)
                }

                "DIV" -> {
                    Div(nodeModel)
                }

                "Big" -> {
                    Big(nodeModel)
                }

                "FONT" -> {
                    Font(nodeModel)
                }

                "A" -> {
                    A(nodeModel)
                }

                "LI" -> {
                    Li(nodeModel)
                }

                "BR" -> {
                    Br(nodeModel)
                }

                "UL" -> {
                    Ul(nodeModel)
                }

                "TR" -> {
                    Tr(nodeModel)
                }

                "TD" -> {
                    Td(nodeModel)
                }

                "IMG" -> {
                    Img(nodeModel as HTMLImageElementModel)
                }

                "BLOCKQUOTE" -> {
                    Blockquote(nodeModel)
                }

                else -> {
                    println(nodeModel.nodeName)
                    Dummy(nodeModel)
                }
            }
        }

        /*
        Scaffold(
            content = { padding ->
                LazyColumn(modifier = Modifier.padding(padding)) {

                    items(
                        items = nodeModels,
                        /*key = { element ->
                        element.id()
                    }*/
                    ) { nodeModel ->

                        when (nodeModel.nodeName.uppercase()) {
                            "BODY" -> {
                                Body(nodeModel)
                            }

                            "TABLE" -> {
                                Table(nodeModel)
                            }

                            "FORM" -> {
                                Form(nodeModel)
                            }

                            "CENTER" -> {
                                Center(nodeModel)
                            }

                            "DIV" -> {
                                Div(nodeModel)
                            }

                            "A" -> {
                                A(nodeModel)
                            }

                            "BIG" -> {
                                Big(nodeModel)
                            }

                            "LI" -> {
                                Li(nodeModel)
                            }

                            "UL" -> {
                                Ul(nodeModel)
                            }

                            "IMG" -> {
                                Img(nodeModel as HTMLImageElementModel)
                            }

                            else -> {
                                println(nodeModel.nodeName)
                            }
                        }
                    }
                }
            }
        )*/
    }
}

@Composable
fun Html(nodeModel: NodeModel) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel)
}


@Composable
fun Dummy(nodeModel: NodeModel) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel)
}


@Composable
fun Font(nodeModel: NodeModel) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel)
}


@Composable
fun Img(nodeModel: HTMLImageElementModel) {
    Text(nodeModel.nodeName)


    AsyncImage(
        model = nodeModel.baseURI + nodeModel.src,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
    )

    Nodes(nodeModel)
    println(nodeModel.baseURI)
    println(nodeModel.src)
}

@Composable
fun Ul(nodeModel: NodeModel) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel)
}


@Composable
fun Blockquote(nodeModel: NodeModel) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel)
}

@Composable
fun Li(nodeModel: NodeModel) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel)
}

@Composable
fun Form(nodeModel: NodeModel) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel)
}

@Composable
fun Body(nodeModel: NodeModel) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel)
}


@Composable
fun Center(nodeModel: NodeModel) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel)
}

@Composable
fun Table(nodeModel: NodeModel) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel)
}


@Composable
fun A(nodeModel: NodeModel) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel)
}


@Composable
fun Tr(nodeModel: NodeModel) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel)
}


@Composable
fun Td(nodeModel: NodeModel) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel)
}

@Composable
fun Div(nodeModel: NodeModel) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel)
}

@Composable
fun Big(nodeModel: NodeModel) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel)
}

@Composable
fun Br(nodeModel: NodeModel) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel)
}