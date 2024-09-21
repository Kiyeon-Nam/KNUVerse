package com.example.knuverse

data class DocumentData(
    val documentId: String,
    val title: String,
    val content: String,
    val startDate: String,
    val endDate: String,
    val isPartnership: Boolean,
    val imageUrls: List<String>
)