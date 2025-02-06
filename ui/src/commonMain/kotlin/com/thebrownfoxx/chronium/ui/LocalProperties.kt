package com.thebrownfoxx.chronium.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.util.*

val localProperties = runBlocking { getLocalProperties() }

private suspend fun getLocalProperties(): Properties {
    return withContext(Dispatchers.IO) {
        Properties().apply {
            load(FileInputStream("../local.properties"))
        }
    }
}