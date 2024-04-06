package com.ncorti.kotlin.template.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import com.google.firebase.auth.FirebaseAuth
import com.ncorti.kotlin.template.app.databinding.ActivityMainBinding
import kotlin.time.Duration.Companion.seconds

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    lateinit var openai: OpenAI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        val auth = FirebaseAuth.getInstance()

//        // Initialize OpenAI client
//        openai = OpenAI(
//            token = "your-api-key",
//            timeout = Timeout(socket = 60.seconds),
//            // additional configurations...
//        )

        // Setup navigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Check if user is logged in and navigate accordingly
        if (auth.currentUser == null) {
            // User not logged in, navigate to LoginFragment
            navController.navigate(R.id.loginFragment)
        } else {
            // User is logged in, navigate to HomeFragment
            navController.navigate(R.id.homeFragment)
        }
    }
}
