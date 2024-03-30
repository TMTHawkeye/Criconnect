package com.example.criconnect.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.criconnect.R
import com.example.criconnect.SplashLoginActivity
import com.example.criconnect.ViewModels.UserViewModel
import com.example.criconnect.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment : Fragment() {
    lateinit var binding : FragmentSettingsBinding

    private var authProfile: FirebaseAuth? = null
    private var fullName: String? = null
    private var email: kotlin.String? = null
    private var doB: kotlin.String? = null
    private var gender: kotlin.String? = null
    private var mobile: kotlin.String? = null
    val userViewModel: UserViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSettingsBinding.inflate(layoutInflater,container,false)

        userViewModel.showUserProfile { isEmailVerified, isuserDetailsAvailable, userData ->
            if (isEmailVerified) {
                if (isuserDetailsAvailable) {
                    fullName = userData?.fullName
                    email = userData?.email
                    doB = userData?.dob
                    gender = userData?.gender
                    mobile = userData?.mobile

                    binding.textViewShowWelcome.setText("Welcome,$fullName!")
                    binding.textViewShowFullName.setText(fullName)
                    binding.textViewShowEmail.setText(email)
                    binding.textViewShowDob.setText(doB)
                    binding.textViewShowGender.setText(gender)
                    binding.textViewShowMobile.setText(mobile)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            } else {
                showAlertDialog()
            }

            binding.progressBar.setVisibility(View.GONE)

        }

        return binding.root
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Email not verified")
        builder.setMessage("Please verify your email now.You can not login without email verification")
        builder.setPositiveButton(
            "Continue"
        ) { dialog, which ->
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_APP_EMAIL)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }


}