package com.example.grubbites

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.grubbites.databinding.ActivityHomeScreenBinding

class HomeScreen : AppCompatActivity() {
    private lateinit var binding: ActivityHomeScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView5.setOnClickListener{
            val intent = Intent(this, RecyclerViewActivity::class.java)
            startActivity(intent)
        }

        binding.bottomNavigationView.setOnItemSelectedListener {item ->
            when (item.itemId){
                R.id.home -> {
                    startActivity(Intent(applicationContext,HomeScreen::class.java))
                    true
                }

                R.id.cart -> {
                    startActivity(Intent(applicationContext,CartScreen::class.java))
                    true
                }
                R.id.profile -> true
                R.id.search -> true

                else -> false
            }
        }
    }
}