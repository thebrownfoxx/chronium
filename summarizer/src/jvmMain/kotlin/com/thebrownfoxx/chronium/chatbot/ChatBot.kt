package com.thebrownfoxx.chronium.chatbot

interface ChatBot {
    suspend fun prompt(conversation: Conversation): String
}

fun conversation(block: ConversationBuilder.() -> Unit): Conversation {
    return ConversationBuilder().apply(block).build()
}

class ConversationBuilder {
    private val _messages = mutableListOf<Message>()

    fun Role.said(message: String) {
        _messages.add(Message(this, message))
    }

    fun build(): Conversation = Conversation(_messages.toList())
}

data class Conversation(val messages: List<Message>)

data class Message(
    val role: Role,
    val content: String,
)

sealed interface Role
data object User : Role
data object Model : Role