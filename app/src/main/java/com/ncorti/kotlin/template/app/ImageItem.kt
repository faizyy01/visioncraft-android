package com.ncorti.kotlin.template.app

data class ImageItem(
    val url: String = "",
    val prompt: String = "",
    val userId: String = "",
    var documentId: String = "", // New field for document ID
    var path: String = ""        // New field for document path
)
