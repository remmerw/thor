package io.github.remmerw.thor.ui

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import io.github.remmerw.thor.dom.Entity
import io.github.remmerw.thor.model.StateModel
import io.github.remmerw.thor.model.Type


@Composable
fun EvaluateEntity(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    when (entity.name) {
        "#text" -> {
            Chars(entity, stateModel, modifier, style)
        }

        Type.HTML.name -> {
            Html(entity, stateModel, modifier)
        }

        Type.BODY.name -> {
            Body(entity, stateModel, modifier)
        }

        Type.H1.name -> {
            H1(entity, stateModel, modifier)
        }

        Type.H2.name -> {
            H2(entity, stateModel, modifier)
        }

        Type.H3.name -> {
            H3(entity, stateModel, modifier)
        }

        Type.H4.name -> {
            H4(entity, stateModel, modifier)
        }

        Type.H5.name -> {
            H5(entity, stateModel, modifier)
        }

        Type.H6.name -> {
            H6(entity, stateModel, modifier)
        }

        Type.TABLE.name -> {
            Table(entity, stateModel, modifier)
        }

        Type.FORM.name -> {
            Form(entity, stateModel, modifier)
        }

        Type.CENTER.name -> {
            Center(entity, stateModel, modifier)
        }

        Type.DIV.name -> {
            Div(entity, stateModel, modifier)
        }

        Type.BIG.name -> {
            Big(entity, stateModel, modifier)
        }

        Type.FONT.name -> {
            Font(entity, stateModel, modifier)
        }

        Type.LINK.name -> {
            Link(entity, stateModel, modifier)
        }

        Type.A.name, Type.ANCHOR.name -> {
            A(entity, stateModel, modifier)
        }

        Type.LI.name -> {
            Li(entity, stateModel, modifier)
        }

        Type.BR.name -> {
            Br(entity, stateModel, modifier)
        }

        Type.UL.name -> {
            Ul(entity, stateModel, modifier)
        }

        Type.TR.name -> {
            Tr(entity, stateModel, modifier)
        }

        Type.TD.name -> {
            Td(entity, stateModel, modifier)
        }

        Type.IMG.name -> {
            Img(entity, stateModel, modifier)
        }

        Type.BLOCKQUOTE.name -> {
            Blockquote(entity, stateModel, modifier)
        }

        else -> {
            Dummy(entity, stateModel, modifier)
        }
    }
}