package com.ncorti.kotlin.template.app
import ImagesAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var imagesRecyclerView: RecyclerView
    private lateinit var imagesAdapter: ImagesAdapter
    private var imageItems: MutableList<ImageItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imagesRecyclerView = view.findViewById(R.id.imagesRecyclerView)
        imagesRecyclerView.layoutManager = LinearLayoutManager(context)
        imagesAdapter = ImagesAdapter(imageItems, requireContext())

        imagesAdapter.listener = object : ImagesAdapter.OnItemClickListener {
            override fun onItemClick(imageItem: ImageItem) {
                // Create a Bundle to pass the imageItem properties
                val bundle = Bundle().apply {
                    putString("imageUrl", imageItem.url)
                    putString("prompt", imageItem.prompt)
                }
                // Use the action ID to navigate
                findNavController().navigate(R.id.action_homeFragment_to_imageDetailFragment, bundle)
            }
        }
        imagesRecyclerView.adapter = imagesAdapter
        fetchImages()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchImages() {
        val db = FirebaseFirestore.getInstance()
        db.collection("images")
            .get()
            .addOnSuccessListener { documents ->
                imageItems.clear()
                for (document in documents) {
                    val imageItem = document.toObject(ImageItem::class.java)
                    imageItems.add(imageItem)
                }
                imagesAdapter.notifyDataSetChanged() // Notify the adapter of data change
            }
            .addOnFailureListener { exception ->
                // Handle any errors here
            }
    }
}
