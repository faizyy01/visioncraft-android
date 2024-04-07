package com.ncorti.kotlin.template.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel : ViewModel() {

    private val _userImages = MutableLiveData<List<ImageItem>>()
    val userImages: LiveData<List<ImageItem>> = _userImages

    private val _allImages = MutableLiveData<List<ImageItem>>()
    val allImages: LiveData<List<ImageItem>> = _allImages

    private var fullImageList: List<ImageItem> = listOf()

    init {
        fetchAllImages()
    }

    private fun fetchAllImages() {
        FirebaseFirestore.getInstance().collection("images")
            .get()
            .addOnSuccessListener { documents ->
                val imageList = documents.map { document ->
                    document.toObject(ImageItem::class.java)
                }
                fullImageList = imageList // Store all images
                _allImages.value = imageList // Update LiveData with all images
            }
            .addOnFailureListener {
                // Handle any errors
            }
    }

    fun filterImages(query: String) {
        if (query.isEmpty()) {
            // Reset to all images if query is empty
            _allImages.value = fullImageList
        } else {
            // Apply filter
            val filteredList = fullImageList.filter { it.prompt.contains(query, ignoreCase = true) }
            _allImages.value = filteredList
        }
    }

    private fun fetchImages() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        if (currentUser != null) {
            db.collection("images")
                .whereEqualTo("userId", currentUser.uid)
                .get()
                .addOnSuccessListener { documents ->
                    val imageList = documents.map { document ->
                        document.toObject(ImageItem::class.java)
                    }
                    _userImages.value = imageList
                }
                .addOnFailureListener {
                    // Handle any errors
                }
        }
    }

    // Method to clear data on logout
    fun fetchUserData() {
        this.fetchImages()
    }
    // Add more functions as needed for your UI interactions

    fun addImage(imageItem: ImageItem) {
        val currentList = _userImages.value?.toMutableList() ?: mutableListOf()
        currentList.add(imageItem)
        _userImages.value = currentList
    }
}
