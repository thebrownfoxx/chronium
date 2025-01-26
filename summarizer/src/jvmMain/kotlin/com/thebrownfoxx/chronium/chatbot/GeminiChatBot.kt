package com.thebrownfoxx.chronium.chatbot

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

class GeminiChatBot(
    private val httpClient: HttpClient,
    apiKey: String,
) : ChatBot {
    private val uri =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent?key=$apiKey"

    override suspend fun prompt(conversation: Conversation): String {
        val response = httpClient.post(uri) {
            contentType(ContentType.Application.Json)
            setBody(conversation.toPrompt())
        }
        return response.body<Response>().candidates.first().content.parts.joinToString(separator = "\n") { it.text }
    }

    private fun Conversation.toPrompt() = Prompt(
        contents = messages.map { it.toPromptContent() },
        generationConfig = GenerationConfig.Default,
    )

    private fun Message.toPromptContent() = Content(
        role = role.toGeminiRole(),
        parts = listOf(Part(text = content)),
    )

    private fun Role.toGeminiRole() = when (this) {
        User -> "user"
        Model -> "model"
    }

    @Serializable
    data class Prompt(
        val contents: List<Content>,
        val generationConfig: GenerationConfig,
    )

    @Serializable
    data class Content(
        val role: String,
        val parts: List<Part>,
    )

    @Serializable
    data class Part(val text: String)

    @Serializable
    data class GenerationConfig(
        val temperature: Float,
        val topK: Int,
        val topP: Float,
        val maxOutputTokens: Int,
        val responseMimeType: String,
    ) {
        companion object {
            val Default = GenerationConfig(
                temperature = 1f,
                topK = 40,
                topP = 0.95f,
                maxOutputTokens = 8192,
                responseMimeType = "text/plain",
            )
        }
    }

    @Serializable
    data class Response(
        val candidates: List<Candidate>,
    )

    @Serializable
    data class Candidate(val content: Content)
}