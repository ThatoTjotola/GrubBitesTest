package com.example.grubbites

import android.app.ProgressDialog
import com.example.grubbites.HomeScreen
import com.example.grubbites.RegisterScreen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.grubbites.databinding.ActivityLoginScreenBinding
import com.google.firebase.auth.FirebaseAuth

class LoginScreen : AppCompatActivity() {

    private lateinit var binding:ActivityLoginScreenBinding
    private lateinit var progressBar: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressBar = ProgressDialog(this)
        progressBar.setTitle("Logging in")
        progressBar.setCanceledOnTouchOutside(false)

        binding.BtnRegister.setOnClickListener {
            intent = Intent(this, RegisterScreen::class.java)
            startActivity(intent)
        }
        binding.BtnLogin.setOnClickListener {
            val email = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()
            progressBar.show()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        progressBar.dismiss()
                        intent = Intent(this, HomeScreen::class.java)
                        startActivity(intent)
                    } else {
                        progressBar.dismiss()
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                progressBar.dismiss()
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

    }
}