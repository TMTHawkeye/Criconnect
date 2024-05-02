package com.example.criconnect.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.criconnect.R
import com.example.criconnect.databinding.ActivityEditPlayerDetailsBinding

class EditPlayerDetailsActivity : AppCompatActivity() {
    lateinit var binding : ActivityEditPlayerDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPlayerDetailsBinding.inflate(layoutInflater)
         setContentView(binding.root)



    }
}