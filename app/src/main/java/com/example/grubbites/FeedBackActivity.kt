package com.example.grubbites

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.grubbites.databinding.ActivityFeedBackBinding

class FeedBackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle button click to navigate back to HomeScreen
        binding.buttonSubmit.setOnClickListener {
            startActivity(Intent(applicationContext, HomeScreen::class.java))
            finish()
        }

        // Bottom navigation handling
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(applicationContext, HomeScreen::class.java))
                    true
                }
                R.id.cart -> {
                    startActivity(Intent(applicationContext, CartScreen::class.java))
                    true
                }
                R.id.profile -> {
                    // Handle profile action, you can add your logic here
                    true
                }
                R.id.search -> {
                    // Handle search action, you can add your logic here
                    true
                }
                else -> false
            }
        }
    }
}
