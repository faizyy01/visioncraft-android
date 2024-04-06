package com.ncorti.kotlin.template.app.ai

import android.content.Context
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import java.io.File
import java.net.URL

import kotlin.time.Duration.Companion.seconds

object ImageGenerationUtils {
    var openAI = OpenAI(
        token = "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",
        timeout = Timeout(socket = 60.seconds),
    )
    suspend fun generateImage(context: Context, prompt: String, onSuccess: (File) -> Unit, onError: (String) -> Unit) {

        val images = openAI.imageURL(
            creation = ImageCreation(
                prompt = prompt,
                model = ModelId("dall-e-3"),
                n = 1,
                size = ImageSize.is1024x1024
            )
        )
        // Handle success or error
        // You need to implement this based on the response from OpenAI
        if (images != null) {
            val generatedImageFile = saveImageToFile(context, images[0])
            onSuccess.invoke(generatedImageFile)
        } else {
            onError.invoke("Failed to generate image")
        }
    }

    private fun saveImageToFile(context: Context, imageUrl: ImageURL): File {
        val url = URL(imageUrl.url)
        val connection = url.openConnection()
        connection.connect()
        val inputStream = connection.getInputStream()

        val fileName = "generated_image.jpg" // You can customize the file name as needed
        val file = File(context.filesDir, fileName) // Save to internal storage
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return file
    }

    fun uploadImageToFirebaseStorage(imageFile: File, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        // Implement logic to upload imageFile to Firebase Storage
    }
}