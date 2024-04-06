package com.ncorti.kotlin.template.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        view.findViewById<Button>(R.id.signUpButton).setOnClickListener {
            signUpUser()
        }

        view.findViewById<TextView>(R.id.alreadyHaveAccountTextView).setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        return view
    }

    private fun signUpUser() {
        val email = view?.findViewById<EditText>(R.id.emailEditText)?.text.toString().trim()
        val password = view?.findViewById<EditText>(R.id.passwordEditText)?.text.toString().trim()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            // Perform sign-up operation
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign-up success, update UI with the signed-in user's information
                        findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
                    } else {
                        // If sign-up fails, display a message to the user.
                        Toast.makeText(context, "Authentication failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, "Email and password cannot be empty",
                Toast.LENGTH_SHORT).show()
        }
    }
}
