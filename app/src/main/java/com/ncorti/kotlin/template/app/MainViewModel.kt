package com.ncorti.kotlin.template.app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class MainViewModel : ViewModel() {
    val USERS_COLLECTION = "users"
    val LIKES_FIELD = "favorites"

    private val _userImages = MutableLiveData<List<ImageItem>>()
    val userImages: LiveData<List<ImageItem>> = _userImages

    private val _allImages = MutableLiveData<List<ImageItem>>()
    val allImages: LiveData<List<ImageItem>> = _allImages

    private var fullImageList: List<ImageItem> = listOf()

    private val _favoriteImages = MutableLiveData<List<ImageItem>?>()
    val favoriteImages: MutableLiveData<List<ImageItem>?> = _favoriteImages


    init {
        fetchAllImages()
    }

    private fun fetchAllImages() {
        FirebaseFirestore.getInstance().collection("images")
            .get()
            .addOnSuccessListener { documents ->

                val imageList = documents.map { document ->
                    ImageItem(
                        url = document.toObject(ImageItem::class.java).url,
                        prompt = document.toObject(ImageItem::class.java).prompt,
                        owner = document.toObject(ImageItem::class.java).owner,
                        documentId = document.id,  // Set document ID
                        path = document.reference.path  // Set document path
                    )
                }
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
                        ImageItem(
                            url = document.toObject(ImageItem::class.java).url,
                            prompt = document.toObject(ImageItem::class.java).prompt,
                            owner = document.toObject(ImageItem::class.java).owner,
                            documentId = document.id,  // Set document ID
                            path = document.reference.path  // Set document path
                        )
                    }
                    Log.d("image_item", imageList.toString())
                    _userImages.value = imageList
                }
                .addOnFailureListener {
                    // Handle any errors
                }
        }
    }

    fun fetchFavoriteImages() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        if (currentUser != null) {
            val userDocRef = db.collection(USERS_COLLECTION).document(currentUser.uid)
            userDocRef.get().addOnSuccessListener { document ->
                // Casting directly to List<DocumentReference>
                val favoritesList = document.get(LIKES_FIELD) as? List<DocumentReference> ?: listOf()
                fetchImagesByPaths(favoritesList)
            }.addOnFailureListener { e ->
                Log.e("FetchFavorites", "Failed to fetch favorites: ${e.localizedMessage}")
            }
        }
    }

    fun reFetchFav() {
        _favoriteImages.value = listOf()  // Clear the list to reflect a loading state in the UI
        this.fetchFavoriteImages()
    }

    private fun fetchImagesByPaths(references: List<DocumentReference>) {
        val db = FirebaseFirestore.getInstance()
        val imageFetchTasks = references.map { ref ->
            ref.get()  // ref is already a DocumentReference
        }

        // Aggregate and wait for all fetches to complete
        Tasks.whenAllSuccess<DocumentSnapshot>(imageFetchTasks)
            .addOnSuccessListener { documentSnapshots ->
                val favoriteImageList = documentSnapshots.mapNotNull { document ->
                    if (document.exists()) {
                        document.toObject(ImageItem::class.java)?.apply {
                            documentId = document.id
                            path = document.reference.path
                        }
                    } else null
                }
                _favoriteImages.value = favoriteImageList
            }.addOnFailureListener { e ->
                Log.e("FetchImageDetails", "Error fetching image details: ${e.localizedMessage}")
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
