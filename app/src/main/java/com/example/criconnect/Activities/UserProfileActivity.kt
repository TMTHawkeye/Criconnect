package com.example.criconnect.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.criconnect.CustomClasses.ReadWriteUserDetails
import com.example.criconnect.R
import com.example.criconnect.SplashLoginActivity
import com.example.criconnect.ViewModels.UserViewModel
import com.example.criconnect.databinding.ActivityUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserProfileBinding
    private var authProfile: FirebaseAuth? = null
    private var fullName: String? = null
    private var email: kotlin.String? = null
    private var doB: kotlin.String? = null
    private var gender: kotlin.String? = null
    private var mobile: kotlin.String? = null

    val userViewModel: UserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                        this@UserProfileActivity,
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
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this@UserProfileActivity)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.common_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_refresh) {
            startActivity(intent)
            finish()
            overridePendingTransition(0, 0)

        } else if (id == R.id.menu_logout) {
            authProfile!!.signOut()
            Toast.makeText(this@UserProfileActivity, "Logged Out", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@UserProfileActivity, SplashLoginActivity::class.java)

             intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this@UserProfileActivity, "Something went wrong!", Toast.LENGTH_SHORT)
                .show()
        }
        return super.onOptionsItemSelected(item)
    }
}