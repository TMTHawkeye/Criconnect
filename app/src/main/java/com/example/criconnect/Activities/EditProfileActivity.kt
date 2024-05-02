package com.example.criconnect.Activities

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.criconnect.HelperClasses.Constants
import com.example.criconnect.ModelClasses.UserData
import com.example.criconnect.ViewModels.UserViewModel
import com.example.criconnect.databinding.ActivityEditProfileBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import io.paperdb.Paper
import org.koin.androidx.viewmodel.ext.android.viewModel


class EditProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditProfileBinding
    val userViewModel: UserViewModel by viewModel()

    private var authProfile: FirebaseAuth? = null
    private var fullName: String? = null
    private var email: kotlin.String? = null
    private var doB: kotlin.String? = null
    private var gender: kotlin.String? = null
    private var mobile: kotlin.String? = null
    private var userType: String? = null

    var userData: UserData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userType = Paper.book().read<String>("INTENTFROM")

        fetchUserData()

        binding.saveButton.setOnClickListener {
            updateUserData()
        }

    }

    private fun updateUserData() {
        var newData = UserData(
            fullName = binding.editName.text.toString(),
            email = binding.editEmail.text.toString(),
            mobile = binding.editPhone.text.toString(),
            dob = binding.editdob.text.toString(),
            gender = userData?.gender?:"Male",
            userType = userType
        )
        userViewModel.updateUserProfile(newData, userType) { isDone, status ->
            Toast.makeText(this@EditProfileActivity, status, Toast.LENGTH_SHORT).show()
            if (isDone) {
                finish()
            }
        }

    }

    private fun fetchUserData() {
        userViewModel.showUserProfile(userType) { isEmailVerified, isuserDetailsAvailable, userData ->
            if (isEmailVerified) {
                if (isuserDetailsAvailable) {
                    this.userData = userData
                    fullName = userData?.fullName
                    email = userData?.email
                    doB = userData?.dob
                    gender = userData?.gender
                    mobile = userData?.mobile

                    binding.editName.setText(fullName)
                    binding.editEmail.setText(email)
                    binding.editPhone.setText(mobile)
                    binding.editdob.setText(doB)
                } else {
                    Toast.makeText(
                        this@EditProfileActivity,
                        "Something went wrong!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }

        }
    }

}