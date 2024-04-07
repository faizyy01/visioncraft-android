package com.ncorti.kotlin.template.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.google.firebase.auth.FirebaseAuth
import com.ncorti.kotlin.template.app.databinding.ActivityMainBinding
import kotlin.time.Duration.Companion.seconds

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        val auth = FirebaseAuth.getInstance()



        // Setup navigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Setup BottomNavigationView
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        // Initially hide BottomNavigationView
        bottomNavigationView.visibility = View.GONE

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment -> bottomNavigationView.visibility = View.GONE
                R.id.signUpFragment -> bottomNavigationView.visibility = View.GONE
                else -> bottomNavigationView.visibility = View.VISIBLE
            }
        }
        // Check if user is logged in and navigate accordingly
        if (auth.currentUser == null) {
            // User not logged in, navigate to LoginFragment
            navController.navigate(R.id.loginFragment)
        } else {
            // User is logged in, navigate to HomeFragment
            navController.navigate(R.id.homeFragment)
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_feed -> {
                    // Handle home navigation
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.navigation_create -> {
                    // Handle dashboard navigation
                    navController.navigate(R.id.aiImageGeneratorFragment)
                    true
                }
                R.id.navigation_profile -> {
                    // Handle notifications navigation
                    navController.navigate(R.id.profileFragment)
                    true
                }
                else -> false
            }
        }

    }
}
