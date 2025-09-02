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
import io.github.remmerw.thor.model.RendererContext
import io.github.remmerw.thor.dom.NodeModel
import io.github.remmerw.thor.model.DefaultRendererContext
import io.github.remmerw.thor.model.Utils


@Composable
fun HtmlViewer(documentModel: DocumentModel,
               rendererContext: RendererContext = DefaultRendererContext()
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
                            Html(nodeModel, rendererContext)
                        }
                    }
                }
            }

        }
    )
}

@Composable
fun Nodes(nodeModel: NodeModel,
          rendererContext: RendererContext) {

    val nodeModels = remember { nodeModel.nodes() }


    if (nodeModels.isNotEmpty()) {

        nodeModels.forEach { nodeModel ->
            when (nodeModel.nodeName.uppercase()) {
                "BODY" -> {
                    Body(nodeModel, rendererContext)
                }

                "TABLE" -> {
                    Table(nodeModel, rendererContext)
                }

                "FORM" -> {
                    Form(nodeModel as HTMLFormElementModel, rendererContext)
                }

                "CENTER" -> {
                    Center(nodeModel, rendererContext)
                }

                "DIV" -> {
                    Div(nodeModel, rendererContext)
                }

                "Big" -> {
                    Big(nodeModel, rendererContext)
                }

                "FONT" -> {
                    Font(nodeModel, rendererContext)
                }

                "LINK" -> {
                    Link(nodeModel as HTMLLinkElementModel, rendererContext)
                }
                "A", "ANCHOR" -> {
                    A(nodeModel as HTMLAnchorElementModel, rendererContext)
                }

                "LI" -> {
                    Li(nodeModel, rendererContext)
                }

                "BR" -> {
                    Br(nodeModel, rendererContext)
                }

                "UL" -> {
                    Ul(nodeModel, rendererContext)
                }

                "TR" -> {
                    Tr(nodeModel, rendererContext)
                }

                "TD" -> {
                    Td(nodeModel, rendererContext)
                }

                "IMG" -> {
                    Img(nodeModel as HTMLImageElementModel, rendererContext)
                }

                "BLOCKQUOTE" -> {
                    Blockquote(nodeModel, rendererContext)
                }

                else -> {
                    println(nodeModel.nodeName)
                    Dummy(nodeModel, rendererContext)
                }
            }
        }

    }
}

@Composable
fun Html(nodeModel: NodeModel,
         rendererContext: RendererContext) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext)
}


@Composable
fun Dummy(nodeModel: NodeModel,
          rendererContext: RendererContext) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext)
}


@Composable
fun Font(nodeModel: NodeModel,
         rendererContext: RendererContext) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext)
}


@Composable
fun Img(nodeModel: HTMLImageElementModel,
        rendererContext: RendererContext) {
    Text(nodeModel.nodeName)

    val src = remember { nodeModel.src!! }

    val url = nodeModel.getFullURL(src)

    if(rendererContext.isImageLoadingEnabled()) {
        AsyncImage(
            model = url.toExternalForm(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
    Nodes(nodeModel, rendererContext)
    println(nodeModel.baseURI)
    println(nodeModel.src)
}

@Composable
fun Ul(nodeModel: NodeModel,
       rendererContext: RendererContext) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext)
}


@Composable
fun Blockquote(nodeModel: NodeModel,
               rendererContext: RendererContext) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext)
}

@Composable
fun Li(nodeModel: NodeModel,
       rendererContext: RendererContext) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext)
}

@Composable
fun Form(nodeModel: HTMLFormElementModel,
         rendererContext: RendererContext) {
    Text(nodeModel.nodeName)

    Utils.submit(nodeModel, rendererContext)

    Nodes(nodeModel, rendererContext)
}

@Composable
fun Body(nodeModel: NodeModel,
         rendererContext: RendererContext) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext)
}


@Composable
fun Center(nodeModel: NodeModel,
           rendererContext: RendererContext) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext)
}

@Composable
fun Table(nodeModel: NodeModel,
          rendererContext: RendererContext) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext)
}


@Composable
fun Link(nodeModel: HTMLLinkElementModel,
      rendererContext: RendererContext) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext)

    Utils.navigate(nodeModel, rendererContext)
}

@Composable
fun A(nodeModel: HTMLAnchorElementModel,
      rendererContext: RendererContext) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext)

    Utils.navigate(nodeModel, rendererContext)


}


@Composable
fun Tr(nodeModel: NodeModel,
       rendererContext: RendererContext) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext)
}


@Composable
fun Td(nodeModel: NodeModel,
       rendererContext: RendererContext) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext)
}

@Composable
fun Div(nodeModel: NodeModel,
        rendererContext: RendererContext) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext)
}

@Composable
fun Big(nodeModel: NodeModel,
        rendererContext: RendererContext) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext)
}

@Composable
fun Br(nodeModel: NodeModel,
       rendererContext: RendererContext) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext)
}