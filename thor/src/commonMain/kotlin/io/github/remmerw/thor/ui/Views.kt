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
import io.github.remmerw.thor.dom.TextImpl
import io.github.remmerw.thor.model.DefaultRendererContext
import io.github.remmerw.thor.model.RendererContext
import io.github.remmerw.thor.model.Utils


@Composable
fun HtmlViewer(
    documentModel: DocumentModel,
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
                            Html(nodeModel, rendererContext, modifier = Modifier)
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
    rendererContext: RendererContext,
    modifier: Modifier
) {

    val nodeModels = remember { nodeModel.nodes() }


    if (nodeModels.isNotEmpty()) {

        nodeModels.forEach { nodeModel ->
            when (nodeModel.nodeName.uppercase()) {
                "BODY" -> {
                    Body(nodeModel, rendererContext, modifier)
                }

                "TABLE" -> {
                    Table(nodeModel, rendererContext, modifier)
                }

                "FORM" -> {
                    Form(nodeModel as HTMLFormElementModel, rendererContext, modifier)
                }

                "CENTER" -> {
                    Center(nodeModel, rendererContext, modifier)
                }

                "DIV" -> {
                    Div(nodeModel, rendererContext, modifier)
                }

                "Big" -> {
                    Big(nodeModel, rendererContext, modifier)
                }

                "FONT" -> {
                    Font(nodeModel, rendererContext, modifier)
                }

                "LINK" -> {
                    Link(nodeModel as HTMLLinkElementModel, rendererContext, modifier)
                }

                "A", "ANCHOR" -> {
                    A(nodeModel as HTMLAnchorElementModel, rendererContext, modifier)
                }

                "LI" -> {
                    Li(nodeModel, rendererContext, modifier)
                }

                "BR" -> {
                    Br(nodeModel, rendererContext, modifier)
                }

                "UL" -> {
                    Ul(nodeModel, rendererContext, modifier)
                }

                "TR" -> {
                    Tr(nodeModel, rendererContext, modifier)
                }

                "TD" -> {
                    Td(nodeModel, rendererContext, modifier)
                }

                "IMG" -> {
                    Img(nodeModel as HTMLImageElementModel, rendererContext, modifier)
                }

                "BLOCKQUOTE" -> {
                    Blockquote(nodeModel, rendererContext, modifier )
                }

                "CHARS" -> {
                    Chars(nodeModel as TextImpl, modifier)
                }

                else -> {
                    println(nodeModel.nodeName)
                    Dummy(nodeModel, rendererContext, modifier )
                }
            }
        }

    }
}
@Composable
fun Chars(text: TextImpl, modifier: Modifier) {


    Text(text.textContent, modifier = modifier)

}

@Composable
fun Html(
    nodeModel: NodeModel,
    rendererContext: RendererContext,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext, modifier)
}


@Composable
fun Dummy(
    nodeModel: NodeModel,
    rendererContext: RendererContext,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext, modifier)
}


@Composable
fun Font(
    nodeModel: NodeModel,
    rendererContext: RendererContext,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext, modifier)
}


@Composable
fun Img(
    nodeModel: HTMLImageElementModel,
    rendererContext: RendererContext,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)

    val src = remember { nodeModel.src!! }

    val url = nodeModel.getFullURL(src)

    if (rendererContext.isImageLoadingEnabled()) {
        AsyncImage(
            model = url.toExternalForm(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
    Nodes(nodeModel, rendererContext, modifier)
    println(nodeModel.baseURI)
    println(nodeModel.src)
}

@Composable
fun Ul(
    nodeModel: NodeModel,
    rendererContext: RendererContext,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext, modifier)
}


@Composable
fun Blockquote(
    nodeModel: NodeModel,
    rendererContext: RendererContext,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext, modifier)
}

@Composable
fun Li(
    nodeModel: NodeModel,
    rendererContext: RendererContext,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext, modifier)
}

@Composable
fun Form(
    nodeModel: HTMLFormElementModel,
    rendererContext: RendererContext,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)

    Utils.submit(nodeModel, rendererContext)

    Nodes(nodeModel, rendererContext, modifier)
}

@Composable
fun Body(
    nodeModel: NodeModel,
    rendererContext: RendererContext,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext, modifier)
}


@Composable
fun Center(
    nodeModel: NodeModel,
    rendererContext: RendererContext,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext, modifier)
}

@Composable
fun Table(
    nodeModel: NodeModel,
    rendererContext: RendererContext,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext, modifier)
}


@Composable
fun Link(
    nodeModel: HTMLLinkElementModel,
    rendererContext: RendererContext,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext, modifier)

    Utils.navigate(nodeModel, rendererContext)
}

@Composable
fun A(
    nodeModel: HTMLAnchorElementModel,
    rendererContext: RendererContext,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext, modifier)

    Utils.navigate(nodeModel, rendererContext)


}


@Composable
fun Tr(
    nodeModel: NodeModel,
    rendererContext: RendererContext,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext, modifier)
}


@Composable
fun Td(
    nodeModel: NodeModel,
    rendererContext: RendererContext,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext, modifier)
}

@Composable
fun Div(
    nodeModel: NodeModel,
    rendererContext: RendererContext,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext, modifier)
}

@Composable
fun Big(
    nodeModel: NodeModel,
    rendererContext: RendererContext,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext, modifier)
}

@Composable
fun Br(
    nodeModel: NodeModel,
    rendererContext: RendererContext,
    modifier: Modifier
) {
    Text(nodeModel.nodeName)
    Nodes(nodeModel, rendererContext, modifier)
}