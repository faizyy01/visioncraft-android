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
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider


class HomeFragment : Fragment() {

    private lateinit var imagesRecyclerView: RecyclerView
    private lateinit var imagesAdapter: ImagesAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        initializeAdapter() // Handles adapter initialization with click listener
        setupRecyclerView(view) // Just setup the RecyclerView
        setupSearchView(view)

        // Observe allImages LiveData from ViewModel
        viewModel.allImages.observe(viewLifecycleOwner) { images ->
            imagesAdapter.updateData(images)
        }
    }

    private fun initializeAdapter() {
        // Initialize the adapter with the click listener here
        imagesAdapter = ImagesAdapter(mutableListOf(), requireContext(), object : ImagesAdapter.OnItemClickListener {
            override fun onItemClick(imageItem: ImageItem) {
                val bundle = Bundle().apply {
                    putString("imageUrl", imageItem.url)
                    putString("prompt", imageItem.prompt)
                }
                findNavController().navigate(R.id.action_homeFragment_to_imageDetailFragment, bundle)
            }
        })
    }

    private fun setupRecyclerView(view: View) {
        imagesRecyclerView = view.findViewById(R.id.imagesRecyclerView)
        imagesRecyclerView.layoutManager = LinearLayoutManager(context)
        imagesRecyclerView.adapter = imagesAdapter // Use the adapter initialized in initializeAdapter
    }

    private fun setupSearchView(view: View) {
        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterImages(newText.orEmpty())
                return true
            }
        })
    }
}
