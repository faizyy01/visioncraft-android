import android.content.Context
import androidx.core.net.toUri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.FirebaseFirestore
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

object ImageGenerationUtils {
    private var openAI = OpenAI(
        token = "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",
        timeout = Timeout(socket = 60.seconds),
    )

    suspend fun generateImage(context: Context, prompt: String, userId: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        try {
            val images = openAI.imageURL(
                creation = ImageCreation(
                    prompt = prompt,
                    model = ModelId("dall-e-3"),
                    n = 1,
                    size = ImageSize.is1024x1024
                )
            )
            if (images != null) {
                val generatedImageFile = saveImageToFile(context, images[0])
                // Now call upload after successfully saving the image locally
                uploadImageToFirebaseStorage(generatedImageFile, prompt, userId, onSuccess, onError)
            } else {
                onError.invoke("Failed to generate image")
            }
        } catch (e: InvalidRequestException) {
            onError.invoke("Error: ${e.message}")
        } catch (e: Exception) {
            onError.invoke("An unexpected error occurred: ${e.message}")
        }
    }

    private suspend fun saveImageToFile(context: Context, imageUrl: ImageURL): File = withContext(Dispatchers.IO) {
        val url = URL(imageUrl.url)
        val connection = url.openConnection()
        connection.connect()
        val inputStream = connection.getInputStream()

        val fileName = "generated_image_${UUID.randomUUID()}.jpg"
        val file = File(context.filesDir, fileName)
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        file
    }

    private fun uploadImageToFirebaseStorage(imageFile: File, prompt: String, userId: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/${imageFile.name}")

        val uploadTask = imageRef.putFile(imageFile.toUri())

        uploadTask.addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                updateFirestore(imageUrl, userId, prompt, onSuccess, onError)
            }
        }.addOnFailureListener {
            onError.invoke("Upload failed: ${it.message}")
        }
    }

    private fun updateFirestore(imageUrl: String, userId: String, prompt: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val imageInfo = hashMapOf(
            "url" to imageUrl,
            "userId" to userId,
            "prompt" to prompt // Including the prompt here.
        )
        db.collection("images").add(imageInfo).addOnSuccessListener {
            onSuccess.invoke(imageUrl)
        }.addOnFailureListener {
            onError.invoke("Firestore update failed: ${it.message}")
        }
    }
}
