package com.example.notez.chatBot

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notez.BuildConfig

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }
    private val generativeModel : GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.API_KEY
    )


    fun sendMessage(question: String) {
        viewModelScope.launch {
            try {
                // Initialize chat with history
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role) {
                            text(it.message)
                        }
                    }.toList()
                )

                // Add the user message to the message list
                messageList.add(MessageModel(question, role = "user"))

                // Add the "typing..." message from the model
                messageList.add(MessageModel("typing...", role = "model"))

                // Introduce a small delay to allow the UI to display the "typing..." animation
                delay(1000L) // 1 second delay to simulate the typing animation

                // Get the response from the model
                val response = chat.sendMessage(question)

                // Remove the "typing..." message
                messageList.removeLast()

                // Add the actual response from the model to the message list
                messageList.add(MessageModel(response.text.toString(), role = "model"))
            } catch (e: Exception) {
                // Handle error case, remove typing message, and show error
                messageList.removeLast()
                messageList.add(MessageModel("Error: ${e.message}", role = "model"))
            }
        }
    }

}