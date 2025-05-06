package uk.dominikdias.manga

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import uk.dominikdias.manga.di.initKoin
import uk.dominikdias.manga.ui.MainContent

fun main() = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "manga-tracker",
    ) {
        MainContent()
    }
}