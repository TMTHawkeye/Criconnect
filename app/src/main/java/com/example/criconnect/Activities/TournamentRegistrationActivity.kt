package com.example.criconnect.Activities

import android.app.Activity
import android.app.ProgressDialog
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
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.ModelClasses.TournamentData
import com.example.criconnect.R
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.ActivityTournamentDataBinding
import com.example.criconnect.databinding.ActivityTournamentRegistrationBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class TournamentRegistrationActivity : AppCompatActivity() {
    lateinit var binding : ActivityTournamentRegistrationBinding

    private val teamViewModel: TeamViewModel by viewModel()
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImagePath: String? = null
    private var selectedImageDrawable: Drawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTournamentRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveButton.setOnClickListener {
//            val base64toDrawable = drawableToBase64(selectedImageDrawable)?:""
            val tournament = TournamentData(
                tournamentName = binding.tournNameET.text.toString(),
                tournamentLocation = binding.groundAddressET.text.toString(),
                tournamentEntryFee = binding.entryFeeET.text.toString(),
                tournamentWinningPrize = binding.winnngET.text.toString()
            )
            val dialog = ProgressDialog.show(
                this@TournamentRegistrationActivity, "",
                "Saving Team Data, Please Wait... ", true
            )
            teamViewModel.saveTournament(tournament,selectedImageDrawable){
                if(it){
                    dialog.dismiss()
                    Toast.makeText(this@TournamentRegistrationActivity, "Tournament has been registered!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else{
                    dialog.dismiss()
                    Toast.makeText(this@TournamentRegistrationActivity, "You have already registered the tournament!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        binding.tournametImage.setOnClickListener {
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
                    binding.tournametImage.setImageDrawable(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // This is called when the image is cleared.
                }
            })
    }
}