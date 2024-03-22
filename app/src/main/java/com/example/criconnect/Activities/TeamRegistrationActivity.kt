package com.example.criconnect.Activities

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.R
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.ActivityTeamRegistrationBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class TeamRegistrationActivity : AppCompatActivity() {
    lateinit var binding : ActivityTeamRegistrationBinding
    private val teamViewModel: TeamViewModel by viewModel()
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImagePath: String? = null
    private var selectedImageDrawable: Drawable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTeamRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.saveButton.setOnClickListener {
            val team = TeamModel(
                selectedImageDrawable,
                binding.teamNameET.text.toString(),
                binding.captainNameET.text.toString(),
                binding.teamCityET.text.toString(),
                binding.homeGroundET.text.toString()
            )
            teamViewModel.saveTeam(team){
                if(it){
                    Toast.makeText(this@TeamRegistrationActivity, "Team has been created!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else{
                    Toast.makeText(this@TeamRegistrationActivity, "You have already created the team!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        binding.uploadImage.setOnClickListener {
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
                    binding.uploadImage.setImageDrawable(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // This is called when the image is cleared.
                }
            })
    }

    private fun createTemporalFileFrom(inputStream: InputStream?): File? {
        var targetFile: File? = null
        if (inputStream != null) {
            var read: Int
            val buffer = ByteArray(8 * 1024)
            try {
                targetFile = File.createTempFile("tempImage", ".jpg")
                val outputStream = FileOutputStream(targetFile)
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
            } catch (e: IOException) {
                Log.e("ImageFileError", "Error creating temporal file", e)
            } finally {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    Log.e("ImageFileError", "Error closing input stream", e)
                }
            }
        }
        return targetFile
    }



}