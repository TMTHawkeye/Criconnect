package com.example.criconnect.Activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.criconnect.CustomClasses.ReadWriteUserDetails
import com.example.criconnect.ModelClasses.UserData
import com.example.criconnect.R
import com.example.criconnect.SplashLoginActivity
import com.example.criconnect.ViewModels.UserViewModel
import com.example.criconnect.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterUserActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    private var picker: DatePickerDialog? = null
    val userViewModel: UserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (supportActionBar != null) {
            supportActionBar!!.title = "Register"
        }
        Toast.makeText(this@RegisterUserActivity, "You can registered now", Toast.LENGTH_LONG)
            .show()

        binding.radioGroupRegisterGender.clearCheck()

        binding.editTextRegisterDob.setOnClickListener(View.OnClickListener {
            val calender = Calendar.getInstance()
            val day = calender[Calendar.DAY_OF_MONTH]
            val month = calender[Calendar.MONTH]
            val year = calender[Calendar.YEAR]
            picker = DatePickerDialog(
                this@RegisterUserActivity,
                { view, year, month, dayofMonth -> binding.editTextRegisterDob.setText(dayofMonth.toString() + "/" + (month + 1) + year) },
                year,
                month,
                day
            )
            picker!!.show()
        })
        val buttonRegister = findViewById<Button>(R.id.button_register)
        buttonRegister.setOnClickListener {
            val selectedGenderId: Int = binding.radioGroupRegisterGender.getCheckedRadioButtonId()
            val textFullName: String = binding.editTextRegisterFullName.getText().toString()
            val textEmail: String = binding.editTextRegisterEmail.getText().toString()
            val textDoB: String = binding.editTextRegisterDob.getText().toString()
            val textMobile: String = binding.editTextRegisterMobile.getText().toString()
            val textPwd: String = binding.editTextRegisterPassword.getText().toString()
            val textConfirmPwd: String =
                binding.editTextRegisterConfirmPassword.getText().toString()
            val textGender: String

            //Validate Mobile Number using Matcher and pattern (Regular Expression)
            val mobileRegex = "[6−9][0−9][9]"
            val mobileMatcher: Matcher
            val mobilePattern = Pattern.compile(mobileRegex)
            mobileMatcher = mobilePattern.matcher(textMobile)
            if (TextUtils.isEmpty(textFullName)) {
                Toast.makeText(
                    this@RegisterUserActivity,
                    "please enter full name",
                    Toast.LENGTH_LONG
                )
                    .show()
                binding.editTextRegisterFullName.setError("Full name is required")
                binding.editTextRegisterFullName.requestFocus()
            } else if (TextUtils.isEmpty(textEmail)) {
                Toast.makeText(
                    this@RegisterUserActivity,
                    "please enter your email",
                    Toast.LENGTH_LONG
                )
                    .show()
                binding.editTextRegisterEmail.setError("Email is required")
                binding.editTextRegisterEmail.requestFocus()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                Toast.makeText(
                    this@RegisterUserActivity,
                    "please re_enter your email",
                    Toast.LENGTH_LONG
                ).show()
                binding.editTextRegisterEmail.setError("Valid Email is required")
                binding.editTextRegisterEmail.requestFocus()
            } else if (TextUtils.isEmpty(textDoB)) {
                Toast.makeText(
                    this@RegisterUserActivity,
                    "please enter your date of birth",
                    Toast.LENGTH_LONG
                ).show()
                binding.editTextRegisterDob.setError("Date of birth is required")
                binding.editTextRegisterDob.requestFocus()
            } else if (binding.radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                Toast.makeText(
                    this@RegisterUserActivity,
                    "please select your gender",
                    Toast.LENGTH_LONG
                ).show()
                binding.editTextRegisterDob.setError("Gender is required")
                binding.editTextRegisterDob.requestFocus()
            } else if (TextUtils.isEmpty(textPwd)) {
                Toast.makeText(
                    this@RegisterUserActivity,
                    "please enter your password",
                    Toast.LENGTH_SHORT
                ).show()
                binding.editTextRegisterPassword.setError("password is required")
                binding.editTextRegisterPassword.requestFocus()
            } else if (textPwd.length < 6) {
                Toast.makeText(
                    this@RegisterUserActivity,
                    "password should be at least 6 digits",
                    Toast.LENGTH_LONG
                ).show()
                binding.editTextRegisterPassword.setError("password to weak")
                binding.editTextRegisterPassword.requestFocus()
            } else if (TextUtils.isEmpty(textConfirmPwd)) {
                Toast.makeText(
                    this@RegisterUserActivity,
                    "please confirm your password",
                    Toast.LENGTH_LONG
                ).show()
                binding.editTextRegisterConfirmPassword.setError("Password Confirmation is required")
                binding.editTextRegisterConfirmPassword.requestFocus()
            } else if (textPwd != textConfirmPwd) {
                Toast.makeText(
                    this@RegisterUserActivity,
                    "please re_enter your email",
                    Toast.LENGTH_LONG
                ).show()
                binding.editTextRegisterConfirmPassword.requestFocus()
            } else if (textPwd != textConfirmPwd) {
                Toast.makeText(
                    this@RegisterUserActivity,
                    "please enter same password",
                    Toast.LENGTH_LONG
                ).show()
                binding.editTextRegisterConfirmPassword.setError("password confirmation is required")
                binding.editTextRegisterConfirmPassword.requestFocus()
                //Clear the enterned password
                binding.editTextRegisterPassword.clearComposingText()
                binding.editTextRegisterConfirmPassword.clearComposingText()
            } else {
                textGender = binding.textViewRegisterGender.getText().toString()
                binding.progressBar.setVisibility(View.VISIBLE)
                registerUser(textFullName, textEmail, textDoB, textGender, textMobile, textPwd)
            }
        }
    }

    // Registering user using the credentials given
    private fun registerUser(
        textFullName: String,
        textEmail: String,
        textDoB: String,
        textGender: String,
        textMobile: String,
        textPwd: String
    ) {
        val userData = UserData(textFullName, textEmail, textDoB, textGender, textMobile, textPwd)
        userViewModel.registerUser(userData, this) { isSuccessful, isExceptionOccured , exceptionMessage->
            if(!isExceptionOccured) {
                if (isSuccessful) {
                    val intent = Intent(this@RegisterUserActivity, SplashLoginActivity::class.java)
                    //to prevent user from returning back to register Activity on pressing back button after registration
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                    Toast.makeText(
                        this@RegisterUserActivity,
                        "User Registrated successfully please verify your email",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@RegisterUserActivity,
                        "User Registration failed plz try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                binding.progressBar.setVisibility(View.GONE)
            }
            else{
                binding.editTextRegisterPassword.setError(exceptionMessage)
                binding.editTextRegisterPassword.requestFocus()
                binding.progressBar.setVisibility(View.GONE)
            }

        }

    }
}
