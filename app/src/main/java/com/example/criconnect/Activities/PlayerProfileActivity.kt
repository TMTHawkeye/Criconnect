package com.example.criconnect.Activities

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.criconnect.HelperClasses.drawableToBase64
import com.example.criconnect.ModelClasses.PlayerData
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.R
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.ActivityPlayerProfileBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerProfileActivity : AppCompatActivity() {
    lateinit var binding : ActivityPlayerProfileBinding
    private val teamViewModel: TeamViewModel by viewModel()
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageDrawable: Drawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPlayerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveButton.setOnClickListener {
            val base64Img = drawableToBase64(selectedImageDrawable)
            val player = PlayerData(
                base64Img,
                binding.playerName.text.toString(),
                binding.fatherName.text.toString(),
                binding.playerCity.text.toString(),
                binding.playerAge.text.toString(),
                binding.playerPhone.text.toString()
            )
            teamViewModel.savePlayer(player){
                if(it){
                    Toast.makeText(this@PlayerProfileActivity, "Player has been added to team!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else{
                    Toast.makeText(this@PlayerProfileActivity, "Problem registering player, Try again!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        binding.changeprofile.setOnClickListener {
            openGallery()

        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            selectedImageUri?.let {
                loadImageIntoDrawable(it)
            }
        }
    }

    private fun loadImageIntoDrawable(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    selectedImageDrawable = resource
                    binding.profileImg.setImageDrawable(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // This is called when the image is cleared.
                }
            })
    }
}