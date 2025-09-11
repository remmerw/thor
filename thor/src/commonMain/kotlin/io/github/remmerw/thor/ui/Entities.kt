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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.model.HtmlModel
import io.github.remmerw.thor.model.Type


@Composable
fun ColumnEntities(
    entity: Entity,
    htmlModel: HtmlModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current,
) {

    val entities by htmlModel.children(entity).collectAsState(emptyList())

    if (entities.isNotEmpty()) {

        entities.forEach { entity ->
            Column {
                EntityComposable(
                    entity = entity,
                    htmlModel = htmlModel,
                    modifier = modifier,
                    color = color,
                    fontSize = fontSize,
                    fontStyle = fontStyle,
                    textDecoration = textDecoration,
                    textAlign = textAlign,
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
    htmlModel: HtmlModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current
) {

    val entities by htmlModel.children(entity).collectAsState(emptyList())

    if (entities.isNotEmpty()) {

        entities.forEach { entity ->
            EntityComposable(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
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
    htmlModel: HtmlModel,
    modifier: Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current
) {
    when (entity.name) {
        "#text" -> {
            Chars(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.HTML.name -> {
            Html(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.MAIN.name -> {
            ColumnEntities(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.PICTURE.name -> {
            Entities(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.BODY.name -> {
            Body(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.H1.name -> {
            H1(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.H2.name -> {
            H2(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.H3.name -> {
            H3(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.H4.name -> {
            H4(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.H5.name -> {
            H5(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.H6.name -> {
            H6(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.TABLE.name -> {
            Table(entity, htmlModel, modifier)
        }

        Type.CAPTION.name -> {
            Caption(entity, htmlModel, modifier)
        }

        Type.TFOOT.name -> {
            TFoot(entity, htmlModel, modifier)
        }

        Type.TBODY.name -> {
            TBody(entity, htmlModel, modifier)
        }

        Type.THEAD.name -> {
            THead(entity, htmlModel, modifier)
        }

        Type.FORM.name -> {
            Form(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.BUTTON.name -> {
            InputButton(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.HEADER.name -> {
            Header(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.SECTION.name, Type.ARTICLE.name, Type.FOOTER.name -> { // maybe todo article
            Section(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.CENTER.name -> {
            Center(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.SPAN.name -> {
            Span(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.DIV.name -> {
            Div(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.BIG.name -> {
            Big(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.FONT.name -> {
            Font(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.A.name, Type.ANCHOR.name -> {
            A(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.LI.name -> {
            Li(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.NAV.name -> {
            Nav(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.BR.name -> {
            Br(entity, htmlModel, modifier)
        }

        Type.UL.name -> {
            Ul(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.OL.name -> {
            Ol(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.TR.name -> {
            Tr(entity, htmlModel, modifier)
        }

        Type.TD.name -> {
            Td(entity, htmlModel, modifier)
        }

        Type.TH.name -> {
            Th(entity, htmlModel, modifier)
        }

        Type.IMG.name -> {
            Img(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.BLOCKQUOTE.name -> {
            Blockquote(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.B.name, Type.STRONG.name -> {
            B(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.EM.name -> {
            Em(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.P.name -> {
            P(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.SMALL.name -> {
            Small(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.HR.name -> {
            HorizontalDivider(thickness = 2.dp)
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