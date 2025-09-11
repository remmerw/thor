package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun Element(
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


        Type.PICTURE.name -> {
            ColumnLayout(
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
            FlowLayout(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier.fillMaxWidth(),
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = MaterialTheme.typography.headlineLarge
            )
        }

        Type.H2.name -> {
            FlowLayout(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier.fillMaxWidth(),
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Type.H3.name -> {
            FlowLayout(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier.fillMaxWidth(),
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Type.H4.name -> {
            FlowLayout(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier.fillMaxWidth(),
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        Type.H5.name -> {
            FlowLayout(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Type.H6.name -> {
            FlowLayout(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier.fillMaxWidth(),
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = MaterialTheme.typography.titleSmall
            )
        }

        Type.CAPTION.name -> {
            Caption(entity, htmlModel, modifier)
        }

        Type.TFOOT.name -> {
            TFoot(entity, htmlModel, modifier)
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


        Type.CENTER.name -> {
            FlowLayout(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = TextAlign.Center,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.SPAN.name -> {
            FlowLayout(
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

        Type.DIV.name, Type.TABLE.name,
        Type.TBODY.name, Type.P.name,
        Type.SECTION.name, Type.ARTICLE.name,
        Type.FOOTER.name, Type.HEADER.name,
        Type.UL.name, Type.OL.name -> {
            ColumnLayout(
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
            FlowLayout(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = FontWeight.Bold,
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

        Type.TR.name -> {
            RowLayout(entity, htmlModel, modifier)
        }

        Type.TD.name -> {
            FlowLayout(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier.padding(4.dp),
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Type.TH.name -> {
            FlowLayout(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier.padding(4.dp),
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall
            )
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
            FlowLayout(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier.padding(16.dp, 0.dp, 0.dp, 0.dp),
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
            FlowLayout(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = FontWeight.Bold,
                style = style
            )
        }

        Type.EM.name -> {
            FlowLayout(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = FontStyle.Italic,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.SMALL.name -> {
            FlowLayout(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = FontWeight.Thin,
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