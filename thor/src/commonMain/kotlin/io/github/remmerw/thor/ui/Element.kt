package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.remmerw.saga.Entity
import io.github.remmerw.thor.debug
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
        "#comment", Type.SCRIPT.name, Type.NOSCRIPT.name,
        Type.STYLE.name, Type.LABEL.name -> {
            // nothing to do here
        }

        "#text" -> {
            val text by htmlModel.text(entity).collectAsState()

            if (text.isNotEmpty()) {
                Text(
                    text = text,
                    modifier = modifier,
                    color = color,
                    fontStyle = fontStyle,
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    textAlign = textAlign,
                    textDecoration = textDecoration,
                    style = style
                )
            }
        }

        Type.INPUT.name -> {
            val attributes by htmlModel.attributes(entity).collectAsState()
            val type = attributes["type"]

            Text(
                text = "Input type $type",
                color = Color.Red,
                modifier = modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineMedium
            ) // todo
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
            Layout(
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
            Layout(
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
            Layout(
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
            Layout(
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
            Layout(
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
            Layout(
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
            Layout(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier.fillMaxWidth().padding(4.dp),
                layout = Layout.LINEAR,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Type.TFOOT.name, Type.THEAD.name, Type.FORM.name -> {
            Layout(
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
            Button(
                onClick = {},
                enabled = false,
            ) {
                Layout(
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


        Type.CENTER.name -> {
            Layout(
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
            Layout(
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
        Type.UL.name, Type.OL.name,
        Type.PICTURE.name -> {
            Column {
                Layout(
                    entity = entity,
                    htmlModel = htmlModel,
                    modifier = modifier,
                    layout = Layout.HORIZONTAL,
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

        Type.BIG.name -> {
            Layout(
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
            val attributes by htmlModel.attributes(entity).collectAsState()
            var colorOverwrite: Color = color
            val colorAttribute = attributes["color"]
            if (!colorAttribute.isNullOrEmpty()) {
                colorOverwrite = htmlModel.color(colorAttribute) ?: color
            }
            Layout(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = colorOverwrite,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )
        }

        Type.A.name, Type.ANCHOR.name -> {
            val attributes by htmlModel.attributes(entity).collectAsState()
            attributes["href"]
            // todo href
            Layout(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                color = Color.Blue,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = TextDecoration.Underline,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = MaterialTheme.typography.labelMedium,
            )
        }

        Type.LI.name -> { // todo test again

            Layout(
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
            FlowRow { // todo check
                Layout(
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

        Type.BR.name -> { // todo
            Layout(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                layout = Layout.LINEAR,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Type.TR.name -> {
            Layout(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier,
                layout = Layout.VERTICAL,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = fontWeight,
                style = style
            )

        }

        Type.TD.name -> {
            Layout(
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
            Layout(
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
            val attributes by htmlModel.attributes(entity).collectAsState()
            val isImageLoadingEnabled = remember { htmlModel.isImageLoadingEnabled }
            val src = attributes["src"]
            val alt = attributes["alt"]


            if (isImageLoadingEnabled && !src.isNullOrEmpty()) {
                println(src)
                AsyncImage(
                    model = htmlModel.fullUri(src),
                    contentDescription = alt,
                    contentScale = ContentScale.Crop,
                    modifier = modifier
                )
            }
        }

        Type.SVG.name -> {
            val attributes by htmlModel.attributes(entity).collectAsState()
            var width = 24
            var height = 24
            try {
                width = attributes["width"]?.toInt() ?: 24
                height = attributes["height"]?.toInt() ?: 24
            } catch (throwable: Throwable) {
                debug(throwable)
            }
            val svg = remember { htmlModel.content(entity) }

            if (svg.isNotEmpty()) {
                AsyncImage(
                    model = svg.encodeToByteArray(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = modifier.size(width.dp, height.dp)
                )
            }
        }

        Type.BLOCKQUOTE.name -> {
            Layout(
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
            Layout(
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

        Type.EM.name, Type.I.name -> {
            Layout(
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
            Layout(
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