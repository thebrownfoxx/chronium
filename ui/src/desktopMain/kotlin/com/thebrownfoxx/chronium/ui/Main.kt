package com.thebrownfoxx.chronium.ui

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() {
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Neon",
        ) {
            App()
        }
    }
}