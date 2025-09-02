package io.github.remmerw.thor.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.remmerw.thor.dom.DocumentModel


@Composable
fun Html(documentModel: DocumentModel) {
    val nodes = remember { documentModel.nodes() }

    Scaffold(
         content = { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding)
            ) {
                items(
                    items = nodes,
                    /*key = { element ->
                        element.id()
                    }*/) { element ->


                }
            }
        }
    )
}