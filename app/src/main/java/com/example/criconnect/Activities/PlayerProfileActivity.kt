package com.example.criconnect.Activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
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

        selectedImageDrawable=getDrawable(R.drawable.circlelogo)

        binding.saveButton.setOnClickListener {
            if (validateFields()) {
                val base64Img = drawableToBase64(selectedImageDrawable)
                val specialtyId = binding.dailyWeeklyButtonView.checkedRadioButtonId
                val batsmanHandId = binding.handsbatSide.checkedRadioButtonId


                val specialtyText = findViewById<RadioButton>(specialtyId)?.text.toString()
                val batsmanHandText = findViewById<RadioButton>(batsmanHandId)?.text.toString()

                val player = PlayerData(
                    playerId = base64Img,
                    playerName = binding.playerName.text.toString(),
                    fatherName = binding.fatherName.text.toString(),
                    playerCity = binding.playerCity.text.toString(),
                    playerAge = binding.playerAge.text.toString(),
                    playerPhone = binding.playerPhone.text.toString(),
                    speciality = specialtyText,
                    batsmanhand = batsmanHandText,
                    details = binding.playerDetails.text.toString()
                )
                val dialog = ProgressDialog.show(
                    this@PlayerProfileActivity, "",
                    "Saving Player Data, Please Wait... ", true
                )
                teamViewModel.savePlayer(player,selectedImageDrawable) { success ->
                    val message = if (success) "Player has been added to team!" else "Problem registering player, Try again!"
                    Toast.makeText(this@PlayerProfileActivity, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        dialog.dismiss()
                        finish()
                    }
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

    private fun validateFields(): Boolean {
        val fields = listOf(
            binding.playerName,
            binding.fatherName,
            binding.playerCity,
            binding.playerAge,
            binding.playerPhone,
            binding.playerDetails
        )

        for (field in fields) {
            if (field.text.isNullOrBlank()) {
                field.error = "This field is required"
                field.requestFocus()
                return false
            }
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        Glide.with(applicationContext).clear(binding.profileImg)
    }


}