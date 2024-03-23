package com.example.criconnect.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.example.criconnect.ModelClasses.PlayerData
import com.example.criconnect.R
import com.example.criconnect.databinding.ActivityPlayerDetailsBinding

class PlayerDetailsActivity : AppCompatActivity() {
    lateinit var binding : ActivityPlayerDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPlayerDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val playerData = intent?.getSerializableExtra("model") as PlayerData
        loadImage(playerData)
        binding.playerNameId.text=playerData?.playerName
        binding.cityId.text = playerData?.playerCity
        binding.ageId.text = playerData?.playerAge
        binding.phoneId.text = playerData?.playerPhone
        binding.specialityId.text = playerData?.speciality
        binding.batsmanHandId.text = playerData?.batsmanhand
        binding.descriptionId.text = playerData?.details

        binding.backBtnId.setOnClickListener {
            finish()
        }
    }

    fun loadImage(playerData: PlayerData) {
        val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.circlelogo)
            .error(R.drawable.circlelogo)

        Glide.with(this@PlayerDetailsActivity)
            .load(playerData.playerLogo)
            .transition(DrawableTransitionOptions.withCrossFade(factory))
            .apply(options)
            .into(binding.profilePhotoId)
    }
}