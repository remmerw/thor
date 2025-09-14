package io.github.remmerw.thor.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.remmerw.saga.COMMENT_NODE
import io.github.remmerw.saga.Entity
import io.github.remmerw.saga.TEXT_NODE
import io.github.remmerw.saga.Tag
import io.github.remmerw.thor.debug
import io.github.remmerw.thor.model.HtmlModel
import io.github.remmerw.thor.model.Styler


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
    fontFamily: FontFamily? = null,
    style: TextStyle = LocalTextStyle.current
) {


    when (entity.name) {
        COMMENT_NODE, Tag.SCRIPT.tag(), Tag.NOSCRIPT.tag(),
        Tag.STYLE.tag(), Tag.LABEL.tag() -> {
            // nothing to do here
        }

        TEXT_NODE -> {
            val text by htmlModel.text(entity).collectAsState()

            if (text.isNotEmpty()) {
                Text(
                    text = text,
                    modifier = modifier,
                    color = color,
                    fontStyle = fontStyle,
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    fontFamily = fontFamily,
                    textAlign = textAlign,
                    textDecoration = textDecoration,
                    style = style
                )
            }
        }

        Tag.INPUT.tag() -> {
            val attributes by htmlModel.attributes(entity).collectAsState()
            val type = attributes["type"]

            Text(
                text = "Input type $type",
                color = Color.Red,
                modifier = modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineMedium
            ) // todo
        }

        Tag.BODY.tag() -> {
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
                fontFamily = fontFamily,
                style = style
            )
        }

        Tag.H1.tag() -> {
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
                fontFamily = fontFamily,
                style = MaterialTheme.typography.headlineLarge
            )
        }

        Tag.H2.tag() -> {
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
                fontFamily = fontFamily,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Tag.H3.tag() -> {
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
                fontFamily = fontFamily,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Tag.H4.tag() -> {
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
                fontFamily = fontFamily,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        Tag.H5.tag() -> {
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
                fontFamily = fontFamily,
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Tag.H6.tag() -> {
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
                fontFamily = fontFamily,
                style = MaterialTheme.typography.titleSmall
            )
        }

        Tag.CAPTION.tag() -> {
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
                fontFamily = fontFamily,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Tag.TFOOT.tag(), Tag.THEAD.tag(), Tag.FORM.tag() -> {
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
                fontFamily = fontFamily,
                style = style
            )
        }

        Tag.BUTTON.tag() -> {
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
                    fontFamily = fontFamily,
                    style = style
                )
            }
        }


        Tag.CENTER.tag() -> {
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
                fontFamily = fontFamily,
                style = style
            )
        }

        Tag.SPAN.tag() -> {
            val attributes by htmlModel.attributes(entity).collectAsState()
            val color = attributes["color"]?.let {
                Styler.parseColor(it)
            } ?: color
            val fontFamily = attributes["font-family"]?.let {
                Styler.parseFontFamily(it)
            } ?: fontFamily

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
                fontFamily = fontFamily,
                style = style
            )
        }

        Tag.DIV.tag(), Tag.TABLE.tag(),
        Tag.TBODY.tag(), Tag.P.tag(),
        Tag.SECTION.tag(), Tag.ARTICLE.tag(),
        Tag.FOOTER.tag(), Tag.HEADER.tag(),
        Tag.UL.tag(), Tag.OL.tag(),
        Tag.PICTURE.tag() -> {
            val attributes by htmlModel.attributes(entity).collectAsState()
            val textAlign = attributes["text-align"]?.let {
                Styler.parseTextAlign(it)
            } ?: textAlign

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
                    fontFamily = fontFamily,
                    style = style
                )
            }
        }

        Tag.BIG.tag() -> {
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
                fontFamily = fontFamily,
                style = style
            )
        }

        Tag.FONT.tag() -> {
            val attributes by htmlModel.attributes(entity).collectAsState()

            val color = attributes["color"]?.let {
                Styler.parseColor(it)
            } ?: color

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
                fontFamily = fontFamily,
                style = style
            )
        }

        Tag.A.tag(), Tag.ANCHOR.tag() -> {
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
                fontFamily = fontFamily,
                style = MaterialTheme.typography.labelMedium,
            )
        }

        Tag.LI.tag() -> {

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
                fontFamily = fontFamily,
                style = style
            )

        }

        Tag.NAV.tag() -> {
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
                    fontFamily = fontFamily,
                    style = style
                )
            }
        }

        Tag.BR.tag() -> { // todo
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
                fontFamily = fontFamily,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Tag.TR.tag() -> {
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
                fontFamily = fontFamily,
                style = style
            )

        }

        Tag.TD.tag() -> {
            val attributes by htmlModel.attributes(entity).collectAsState()
            val textAlign = attributes["text-align"]?.let {
                Styler.parseTextAlign(it)
            } ?: textAlign

            val backgroundColor = attributes["background-color"]?.let {
                Styler.parseBackgroundColor(it)
            } ?: Color.Unspecified

            Layout(
                entity = entity,
                htmlModel = htmlModel,
                modifier = modifier.background(color = backgroundColor),
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                textAlign = textAlign,
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Tag.TH.tag() -> {
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
                fontFamily = fontFamily,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Tag.IMG.tag() -> {
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

        Tag.SVG.tag() -> {
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

        Tag.BLOCKQUOTE.tag() -> {
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
                fontFamily = fontFamily,
                style = style
            )
        }

        Tag.B.tag(), Tag.STRONG.tag() -> {
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
                fontFamily = fontFamily,
                style = style
            )
        }

        Tag.EM.tag(), Tag.I.tag() -> {
            val attributes by htmlModel.attributes(entity).collectAsState()
            val fontStyle = attributes["font-style"]?.let {
                Styler.parseFontStyle(it)
            } ?: FontStyle.Italic


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
                fontFamily = fontFamily,
                style = style
            )
        }

        Tag.SMALL.tag() -> {
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
                fontFamily = fontFamily,
                style = style
            )
        }

        Tag.HR.tag() -> {
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