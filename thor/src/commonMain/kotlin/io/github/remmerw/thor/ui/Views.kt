package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.remmerw.thor.dom.DocumentModel
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

                    when(nodeModel.nodeName.uppercase()){
                        "HTML" -> {
                            Html(nodeModel)
                        }
                        else -> {
                            println(nodeModel.nodeName)
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
    LazyColumn(
    ) {
        items(
            items = nodeModels,
            /*key = { element ->
                element.id()
            }*/
        ) { nodeModel ->

            nodeModel.hasChildNodes()
            println(nodeModel.nodeName)

            when(nodeModel.nodeName.uppercase()){
                "BODY" -> {
                    Body(nodeModel)
                }
                "TABLE" -> {
                    Table(nodeModel)
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
                else -> {
                    println(nodeModel.nodeName)
                }
            }
        }
    }
}
@Composable
fun Html(nodeModel: NodeModel) {
    Nodes(nodeModel)
}


@Composable
fun Body(nodeModel: NodeModel) {
    Nodes(nodeModel)
}


@Composable
fun Center(nodeModel: NodeModel) {
    Nodes(nodeModel)
}

@Composable
fun Table(nodeModel: NodeModel) {
    Nodes(nodeModel)
}


@Composable
fun A(nodeModel: NodeModel) {
    Nodes(nodeModel)
}


@Composable
fun Big(nodeModel: NodeModel) {
    Nodes(nodeModel)
}

@Composable
fun Div(nodeModel: NodeModel) {
    Nodes(nodeModel)
}