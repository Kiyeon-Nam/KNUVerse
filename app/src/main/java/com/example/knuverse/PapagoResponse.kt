package com.example.knuverse

data class PapagoResponse(
    val message: Message
) {
    data class Message(
        val result: Result
    ) {
        data class Result(
            val translatedText: String
        )
    }
}