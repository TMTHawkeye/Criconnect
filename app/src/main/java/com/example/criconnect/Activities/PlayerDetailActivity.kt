package com.example.criconnect.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.criconnect.R
import com.example.criconnect.databinding.ActivityPlayerDetailBinding

class PlayerDetailActivity : AppCompatActivity() {
    lateinit var binding : ActivityPlayerDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPlayerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}