package com.ncorti.kotlin.template.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel : ViewModel() {

    private val _userImages = MutableLiveData<List<ImageItem>>()
    val userImages: LiveData<List<ImageItem>> = _userImages

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
