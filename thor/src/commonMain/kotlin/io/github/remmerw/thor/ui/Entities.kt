package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.model.StateModel
import io.github.remmerw.thor.model.Type


@Composable
fun ColumnEntities(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    textDecoration: TextDecoration? = null,
    style: TextStyle = LocalTextStyle.current,
) {

    val entities by stateModel.children(entity).collectAsState()

    if (entities.isNotEmpty()) {

        entities.forEach { entity ->
            Column(modifier = modifier.fillMaxWidth()) {
                Entity(entity, stateModel, modifier,
                    color = color,
                    textDecoration = textDecoration,
                    style = style)
            }
        }
    }
}


@Composable
fun RowEntities(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    textDecoration: TextDecoration? = null,
    style: TextStyle = LocalTextStyle.current,
) {

    val entities by stateModel.children(entity).collectAsState()

    if (entities.isNotEmpty()) {

        entities.forEach { entity ->
            Entity(entity, stateModel, modifier,
                color = color,
                textDecoration = textDecoration,
                style = style)
        }
    }

}

@Composable
fun Entity(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textDecoration: TextDecoration? = null,
    style: TextStyle = LocalTextStyle.current
) {
    when (entity.name) {
        "#text" -> {
            Chars(entity, stateModel, modifier,
                color = color,
                textDecoration = textDecoration,
                style = style)
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

        Type.LINK.name, Type.META.name, Type.HEAD.name -> {
            // not visible (not yet supported)
        }
        else -> {
            Dummy(entity, stateModel, modifier)
        }
    }
}