package com.example.criconnect.Activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.criconnect.R
import com.example.criconnect.databinding.ActivityDetailBinding
import java.io.Serializable

class DetailActivity : AppCompatActivity(),Serializable {
    lateinit var binding : ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val bundle = intent.extras
//        if (bundle != null) {
//            binding.detailDesc.setText(bundle.getInt("Desc"))
//            binding.detailImage.setImageResource(bundle.getInt("Image"))
//            binding.detailTitle.setText(bundle.getString("Title"))
//        }
    }
}