package com.example.criconnect.Fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.criconnect.ModelClasses.tournamentDataClass
import com.example.criconnect.R
import com.example.criconnect.ViewModels.TournamentViewModel
import com.example.criconnect.databinding.FragmentOrganizerHomeBinding
import com.example.criconnect.databinding.FragmentOrganizerTornamentHandlingBinding
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class OrganizerHomeFragment : Fragment() {

    lateinit var binding :FragmentOrganizerHomeBinding
    private var picker: DatePickerDialog? = null
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageDrawable: Drawable? = null

    val tournamentViewModel :TournamentViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentOrganizerHomeBinding.inflate(layoutInflater)
        selectedImageDrawable = requireContext().getDrawable(R.drawable.circlelogo)
        binding.saveButton.setOnClickListener{
            saveTournament()
        }

        binding.startDate.setOnClickListener(View.OnClickListener {
            val calender = Calendar.getInstance()
            val day = calender[Calendar.DAY_OF_MONTH]
            val month = calender[Calendar.MONTH]
            val year = calender[Calendar.YEAR]
            picker = DatePickerDialog(
                requireContext(),
                { view, year, month, dayofMonth -> binding.startDate.setText(dayofMonth.toString() + "/" + (month + 1) + "/" + year) },
                year,
                month,
                day
            )
            picker!!.show()
        })

        binding.endDate.setOnClickListener(View.OnClickListener {
            val calender = Calendar.getInstance()
            val day = calender[Calendar.DAY_OF_MONTH]
            val month = calender[Calendar.MONTH]
            val year = calender[Calendar.YEAR]
            picker = DatePickerDialog(
                requireContext(),
                { view, year, month, dayofMonth -> binding.endDate.setText(dayofMonth.toString() + "/" + (month + 1) + "/" + year) },
                year,
                month,
                day
            )
            picker!!.show()
        })

        binding.uploadImage.setOnClickListener {
            openGallery()
        }


        return binding.root
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
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    selectedImageDrawable = resource
                    binding.uploadImage.setImageDrawable(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // This is called when the image is cleared.
                }
            })
    }




    private fun saveTournament() {
        val tournamentName = binding.tournamentNameET.text.toString()
        val organizerName = binding.organizerNameET.text.toString()
        val city = binding.cityET.text.toString()
        val ground = binding.cityET.text.toString()
        val organizerEmail = binding.organizerEmailET.text.toString()
        val eventStartDate = binding.startDate.text.toString()
        val eventEndDate = binding.endDate.text.toString()
        val ballType = when {
            binding.radioTennis.isChecked -> "Tennis Ball"
            binding.radioHard.isChecked -> "Leather Ball"
            else -> "Other Ball"
        }
        val pitchType = when {
            binding.roughRadio.isChecked -> "Rough"
            binding.cementRadio.isChecked -> "Cement"
            else -> "Turf"
        }
        val details = binding.detailsET.text.toString()

         val tournamentData = tournamentDataClass(
             name =tournamentName,
            organizerName = organizerName,
            city = city,
            ground = ground,
            organizerEmail = organizerEmail,
            eventStartDate = eventStartDate,
            eventEndDate = eventEndDate,
            ballType = ballType,
            pitchType = pitchType,
            details = details
        )

        val dialog = ProgressDialog.show(
            requireContext(), "",
            "Registering, Please Wait... ", true
        )

        // Call the registerTournament method in the view model
        tournamentViewModel.registerTournament(selectedImageDrawable,tournamentData){
            if(it){
                Snackbar.make(requireView(),"Tournament Registered Successfully", Snackbar.LENGTH_SHORT).show()
            }
            else{
                Snackbar.make(requireView(),"Error Registering team in Tournament", Snackbar.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
    }


}