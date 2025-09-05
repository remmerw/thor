package io.github.remmerw.thor.ui

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import io.github.remmerw.thor.model.ElementModel
import io.github.remmerw.thor.model.NodeModel
import io.github.remmerw.thor.model.StateModel
import io.github.remmerw.thor.model.TextModel
import io.github.remmerw.thor.model.Type


@Composable
fun EvaluateNode(
    nodeModel: NodeModel,
    stateModel: StateModel,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    when (nodeModel) {
        is TextModel -> {
            Chars(nodeModel, modifier, style)
        }

        is ElementModel -> {

            when (nodeModel.name()) {
                Type.HTML.name -> {
                    Html(nodeModel, stateModel, modifier)
                }

                Type.BODY.name -> {
                    Body(nodeModel, stateModel, modifier)
                }

                Type.H1.name -> {
                    H1(nodeModel, stateModel, modifier)
                }

                Type.H2.name -> {
                    H2(nodeModel, stateModel, modifier)
                }

                Type.H3.name -> {
                    H3(nodeModel, stateModel, modifier)
                }

                Type.H4.name -> {
                    H4(nodeModel, stateModel, modifier)
                }

                Type.H5.name -> {
                    H5(nodeModel, stateModel, modifier)
                }

                Type.H6.name -> {
                    H6(nodeModel, stateModel, modifier)
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