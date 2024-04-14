package com.ncorti.kotlin.template.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.ncorti.kotlin.template.app.databinding.FragmentImageDetailBinding
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

const val USERS_COLLECTION = "users"
const val LIKES_FIELD = "favorites"
class ImageDetailFragment : Fragment() {
    private lateinit var viewModel: MainViewModel

    private var _binding: FragmentImageDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentImageDetailBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageUrl = arguments?.getString("imageUrl")
        val prompt = arguments?.getString("prompt")
        val imagePath = arguments?.getString("path")
        val imageOwner = arguments?.getString("ownerId")
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        Log.d("onViewCreated", imageUrl.toString())
        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(binding.fullImageView)
        }

        binding.btnCopyLink.setOnClickListener {
            val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Image URL", imageUrl)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(activity, "Link copied to clipboard!", Toast.LENGTH_SHORT).show()
        }

        checkFavoriteStatus(userId, imagePath)  // Check if the image is already a favorite and update button appearance

        binding.btnLike.setOnClickListener {
            if (imagePath != null) {
                toggleFavorite(userId, imagePath)
            } else {
                Toast.makeText(activity, "Invalid image data", Toast.LENGTH_SHORT).show()
            }
        }

        binding.delBtn.setOnClickListener {
            if (imagePath != null && userId == imageOwner) {
                deleteImage(imagePath)
            } else {
                Toast.makeText(activity, "You do not have permission to delete this image.", Toast.LENGTH_SHORT).show()
            }
        }

        // Disable the delete button if the user is not the owner
        binding.delBtn.isEnabled = userId == imageOwner

        binding.promptTextView.text = prompt ?: "No description available"

        binding.promptTextView.text = prompt ?: "No description available"
    }

    private fun checkFavoriteStatus(userId: String, imagePath: String?) {
        val db = FirebaseFirestore.getInstance()
        val userDocRef = db.collection(USERS_COLLECTION).document(userId)
        userDocRef.get().addOnSuccessListener { documentSnapshot ->
            val currentFavorites = documentSnapshot.get(LIKES_FIELD) as? List<DocumentReference> ?: listOf()
            val imageRef = imagePath?.let { db.document(it) }
            updateFavoriteButton(currentFavorites.contains(imageRef))
        }
    }

    private fun updateFavoriteButton(isFavorited: Boolean) {
        val iconResource = if (isFavorited) {
            R.drawable.baseline_favorite_24  // Favorited state icon
        } else {
            R.drawable.baseline_favorite_border_24  // Unfavorited state icon
        }
        binding.btnLike.setIconResource(iconResource)
    }

    private fun toggleFavorite(userId: String, imagePath: String) {
        val db = FirebaseFirestore.getInstance()
        val imageRef = db.document(imagePath)
        val userDocRef = db.collection(USERS_COLLECTION).document(userId)

        userDocRef.get().addOnSuccessListener { documentSnapshot ->
            val currentFavorites = documentSnapshot.get(LIKES_FIELD) as? List<DocumentReference> ?: listOf()

            if (currentFavorites.contains(imageRef)) {
                // If already a favorite, remove it
                userDocRef.update(LIKES_FIELD, FieldValue.arrayRemove(imageRef))
                    .addOnSuccessListener {
                        Toast.makeText(activity, "Removed from favorites", Toast.LENGTH_SHORT).show()
                        updateFavoriteButton(false)
                    }
            } else {
                // If not a favorite, add it
                userDocRef.update(LIKES_FIELD, FieldValue.arrayUnion(imageRef))
                    .addOnSuccessListener {
                        Toast.makeText(activity, "Added to favorites", Toast.LENGTH_SHORT).show()
                        updateFavoriteButton(true)
                    }
            }
        }
    }
    private fun deleteImage(imagePath: String) {
        val db = FirebaseFirestore.getInstance()
        val imageRef = db.document(imagePath)

        // Perform the delete operation on the Firestore document
        imageRef.delete()
            .addOnSuccessListener {
                Toast.makeText(activity, "Image successfully deleted", Toast.LENGTH_SHORT).show()
                // Optionally navigate back or update UI accordingly
                findNavController().navigate(R.id.action_imageDetailFragment_to_homeFragment)
            }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "Failed to delete image: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("ImageDelete", "Failed to delete image", e)
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
