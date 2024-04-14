package com.ncorti.kotlin.template.app
import ImagesAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.ncorti.kotlin.template.app.databinding.FragmentLikesBinding

class LikesFragment : Fragment() {

    private var _binding: FragmentLikesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Use view binding to inflate the layout
        _binding = FragmentLikesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // No need to find the toolbar by ID because you can access it directly through binding
//        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        // Observe LiveData
        // Initialize ViewModel
        // Initialize ViewModel
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        // Setup RecyclerView and Adapter
        val imagesAdapter = ImagesAdapter(mutableListOf(), requireContext(), null)
        binding.imagesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.imagesRecyclerView.adapter = imagesAdapter

        // Observe LiveData from ViewModel
        viewModel.userImages.observe(viewLifecycleOwner) { imageItems ->
            // Update adapter data
            imagesAdapter.updateData(imageItems)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding when the view is destroyed
    }


}
