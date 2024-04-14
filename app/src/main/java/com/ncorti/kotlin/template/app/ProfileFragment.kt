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
import com.ncorti.kotlin.template.app.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Indicate that this fragment wants to add items to the options menu
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Use view binding to inflate the layout
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // No need to find the toolbar by ID because you can access it directly through binding
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        // Observe LiveData
        // Initialize ViewModel
        // Initialize ViewModel
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.fetchUserData()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                // Handle sign out action here
                // For example, sign out from Firebase Auth and navigate to the login screen
                FirebaseAuth.getInstance().signOut()
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding when the view is destroyed
    }


}
