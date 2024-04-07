package com.ncorti.kotlin.template.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.ncorti.kotlin.template.app.databinding.FragmentImageDetailBinding

class ImageDetailFragment : Fragment() {

    private var _binding: FragmentImageDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentImageDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve arguments directly using Bundle
        val imageUrl = arguments?.getString("imageUrl")
        val prompt = arguments?.getString("prompt")

        if (imageUrl != null) {
            Glide.with(this)
                .load(imageUrl)
                .into(binding.fullImageView)
        }

        // Set the prompt text if it's not null
        binding.promptTextView.text = prompt ?: "No description available"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
