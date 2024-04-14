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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.bumptech.glide.Glide

class AiImageGeneratorFragment : Fragment() {

    private lateinit var promptEditText: EditText
    private lateinit var generateButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var statusTextView: TextView
    private lateinit var generatedImageView: ImageView
    private lateinit var viewModel: MainViewModel

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
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        generateButton.setOnClickListener {
            val prompt = promptEditText.text.toString()
            lifecycleScope.launch {
                generateImage(prompt)
            }
        }

        return view
    }

    private suspend fun generateImage(prompt: String) {
        // Show loading spinner
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return // Or handle the null case more gracefully

        progressBar.visibility = View.VISIBLE
        statusTextView.text = getString(R.string.status_generating)
        ImageGenerationUtils.generateImage(requireContext(), prompt, userId,
            onSuccess = { imageUrl ->
                // On image generation and upload success
                requireActivity().runOnUiThread {
                    progressBar.visibility = View.GONE
                    statusTextView.text = getString(R.string.status_complete)
                    generatedImageView.visibility = View.VISIBLE

                    // Use Glide to load the image
                    Glide.with(this@AiImageGeneratorFragment)
                        .load(imageUrl)
                        .into(generatedImageView)

                    // Create an ImageItem from the generated image URL and prompt
                    val newImageItem = ImageItem(url = imageUrl, prompt = prompt, userId = userId)
                    // Add this new image item to the ViewModel's list
                    viewModel.addImage(newImageItem)
                }
            },
            onError = { error ->
                // On image generation or upload error
                requireActivity().runOnUiThread {
                    progressBar.visibility = View.GONE
                    statusTextView.text = getString(R.string.status_error)
                }
            }
        )
    }
}
