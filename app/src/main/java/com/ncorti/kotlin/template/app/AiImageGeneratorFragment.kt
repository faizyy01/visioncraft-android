package com.ncorti.kotlin.template.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.ncorti.kotlin.template.app.ai.ImageGenerationUtils
import kotlinx.coroutines.runBlocking

class AiImageGeneratorFragment : Fragment() {

    private lateinit var promptEditText: EditText
    private lateinit var generateButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var statusTextView: TextView
    private lateinit var generatedImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ai_image_generator, container, false)

        promptEditText = view.findViewById(R.id.etPrompt)
        generateButton = view.findViewById(R.id.btnGenerate)
        progressBar = view.findViewById(R.id.progressBar)
        statusTextView = view.findViewById(R.id.tvStatus)
        generatedImageView = view.findViewById(R.id.ivGeneratedImage)

        generateButton.setOnClickListener {
            val prompt = promptEditText.text.toString()
            generateImage(prompt)
        }

        return view
    }

    private fun generateImage(prompt: String) {
        // Show loading spinner
        progressBar.visibility = View.VISIBLE
        statusTextView.text = getString(R.string.status_generating)
        runBlocking {
            ImageGenerationUtils.generateImage(requireContext(), prompt,
                { file ->
                    // On image generation success
                    progressBar.visibility = View.GONE
                    statusTextView.text = getString(R.string.status_complete)
                    // Show the generated image
                    generatedImageView.visibility = View.VISIBLE
                    // Update the ImageView with the generated image
                    generatedImageView.setImageURI(file.toUri())
                },
                { error ->
                    // On image generation error
                    progressBar.visibility = View.GONE
                    statusTextView.text = getString(R.string.status_error)
                    // Return from the function to prevent moving forward
                    return@generateImage
                }
            )
        }
    }
}
