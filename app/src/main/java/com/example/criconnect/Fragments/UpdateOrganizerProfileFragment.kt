package com.example.criconnect.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.criconnect.ModelClasses.UserData
import com.example.criconnect.R
import com.example.criconnect.ViewModels.UserViewModel
import com.example.criconnect.databinding.FragmentUpdateOrganizerProfileBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import io.paperdb.Paper
import org.koin.androidx.viewmodel.ext.android.viewModel


class UpdateOrganizerProfileFragment : Fragment() {
    val userViewModel: UserViewModel by viewModel()

    private var authProfile: FirebaseAuth? = null
    private var fullName: String? = null
    private var email: kotlin.String? = null
    private var doB: kotlin.String? = null
    private var gender: kotlin.String? = null
    private var mobile: kotlin.String? = null
    private var userType: String? = null
    var userData: UserData? = null

    lateinit var binding : FragmentUpdateOrganizerProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateOrganizerProfileBinding.inflate(layoutInflater)
        userType = Paper.book().read<String>("INTENTFROM")

        fetchUserData()

        binding.saveButton.setOnClickListener {
            updateUserData()
        }


        return binding.root
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
            Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
            if (isDone) {
                Snackbar.make(binding.root,"Profile Updated Successfully!",Snackbar.LENGTH_SHORT).show()
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
                        requireContext(),
                        "Something went wrong!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }

        }
    }


}