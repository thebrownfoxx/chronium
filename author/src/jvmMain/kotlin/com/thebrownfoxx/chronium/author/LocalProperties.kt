package com.thebrownfoxx.chronium.author

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.util.*

suspend fun getLocalProperties(): Properties {
    return withContext(Dispatchers.IO) {
        Properties().apply {
            load(FileInputStream("../local.properties"))
        }
    }
}