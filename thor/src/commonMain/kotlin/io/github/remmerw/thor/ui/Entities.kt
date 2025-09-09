package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
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
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current,
) {

    val entities by stateModel.children(entity).collectAsState(emptyList())

    if (entities.isNotEmpty()) {

        entities.forEach { entity ->
            Column {
                EntityComposable(
                    entity = entity,
                    stateModel = stateModel,
                    modifier = modifier,
                    color = color,
                    textDecoration = textDecoration,
                    fontWeight = fontWeight,
                    style = style
                )
            }
        }
    }
}


@Composable
fun Entities(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current
) {

    val entities by stateModel.children(entity).collectAsState(emptyList())

    if (entities.isNotEmpty()) {

        entities.forEach { entity ->
            EntityComposable(
                entity = entity,
                stateModel = stateModel,
                modifier = modifier,
                color = color,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }
    }

}

@Composable
fun EntityComposable(
    entity: Entity,
    stateModel: StateModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current
) {
    when (entity.name) {
        "#text" -> {
            Chars(
                entity = entity,
                stateModel = stateModel,
                modifier = modifier,
                color = color,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.HTML.name -> {
            Html(
                entity = entity,
                stateModel = stateModel,
                modifier = modifier
            )
        }

        Type.BODY.name -> {
            Body(
                entity = entity,
                stateModel = stateModel,
                modifier = modifier
            )
        }

        Type.H1.name -> {
            H1(
                entity = entity,
                stateModel = stateModel,
                modifier = modifier
            )
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

        Type.CAPTION.name -> {
            Caption(entity, stateModel, modifier)
        }

        Type.TFOOT.name -> {
            TFoot(entity, stateModel, modifier)
        }

        Type.TBODY.name -> {
            TBody(entity, stateModel, modifier)
        }

        Type.THEAD.name -> {
            THead(entity, stateModel, modifier)
        }

        Type.FORM.name -> {
            Form(entity, stateModel, modifier)
        }

        Type.CENTER.name -> {
            Center(
                entity = entity,
                stateModel = stateModel,
                modifier = modifier,
                color = color,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.SPAN.name -> {
            Span(
                entity = entity,
                stateModel = stateModel,
                modifier = modifier,
                color = color,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.DIV.name -> {
            Div(entity, stateModel, modifier)
        }

        Type.BIG.name -> {
            Big(entity, stateModel, modifier)
        }

        Type.FONT.name -> {
            Font(
                entity = entity,
                stateModel = stateModel,
                modifier = modifier,
                color = color,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
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

        Type.OL.name -> {
            Ol(entity, stateModel, modifier)
        }

        Type.TR.name -> {
            Tr(entity, stateModel, modifier)
        }

        Type.TD.name -> {
            Td(entity, stateModel, modifier)
        }

        Type.TH.name -> {
            Th(entity, stateModel, modifier)
        }

        Type.IMG.name -> {
            Img(entity, stateModel, modifier)
        }

        Type.BLOCKQUOTE.name -> {
            Blockquote(entity, stateModel, modifier)
        }

        Type.B.name -> {
            B(
                entity = entity,
                stateModel = stateModel,
                modifier = modifier,
                color = color,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.P.name -> {
            P(
                entity = entity,
                stateModel = stateModel,
                modifier = modifier,
                color = color,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.SMALL.name -> {
            Small(
                entity = entity,
                stateModel = stateModel,
                modifier = modifier,
                color = color,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.HR.name -> {
            HorizontalDivider(thickness = 2.dp)
        }

        Type.TITLE.name, Type.LINK.name, Type.META.name, Type.HEAD.name -> {
            // not visible (not yet supported)
        }

        else -> {
            Text(
                text = entity.name,
                color = Color.Red,
                modifier = modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}