package com.example.criconnect

 import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
 import android.text.method.HideReturnsTransformationMethod
 import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
 import android.view.MotionEvent
 import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.criconnect.Activities.ForgetPasswordActivity
import com.example.criconnect.Activities.RegisterUserActivity
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.ActivitySplashLoginBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel


class SplashLoginActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashLoginBinding
    private var authProfile: FirebaseAuth? = null
    private val TAG = "LoginActivity"

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(
                this@SplashLoginActivity,
                "You can reset password now!",
                Toast.LENGTH_SHORT
            )
                .show()
            startActivity(Intent(this@SplashLoginActivity, ForgetPasswordActivity::class.java))
        }

        binding.editTextLoginPwd.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Check if the click is within the drawable end bounds
                if (event.rawX >= (binding.editTextLoginPwd.right - binding.editTextLoginPwd.compoundDrawables[2].bounds.width())) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }


        binding.tvLoginbutton.setOnClickListener {
            val textEmail: String = binding.editTextLoginEmail.getText().toString()
            val textPwd: String = binding.editTextLoginPwd.getText().toString()
            if (TextUtils.isEmpty(textEmail)) {
                Toast.makeText(
                    this@SplashLoginActivity,
                    "Please enter your email",
                    Toast.LENGTH_SHORT
                )
                    .show()
                binding.editTextLoginEmail.setError("Email is required")
                binding.editTextLoginEmail.requestFocus()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                Toast.makeText(
                    this@SplashLoginActivity,
                    "Please re_enter your email",
                    Toast.LENGTH_SHORT
                )
                    .show()
                binding.editTextLoginEmail.setError("Valid Email is required")
                binding.editTextLoginEmail.requestFocus()
            } else if (TextUtils.isEmpty(textPwd)) {
                Toast.makeText(
                    this@SplashLoginActivity,
                    "Please enter your password",
                    Toast.LENGTH_SHORT
                )
                    .show()
                binding.editTextLoginPwd.setError("password is required")
                binding.editTextLoginPwd.requestFocus()
            } else {
                binding.progressBar.setVisibility(View.VISIBLE)
                loginUser(textEmail, textPwd)
            }
        }


        binding.tvRegister.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SplashLoginActivity, RegisterUserActivity::class.java)
            startActivity(intent)
        })
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Password is visible, hide it
            binding.editTextLoginPwd.transformationMethod = PasswordTransformationMethod.getInstance()
            isPasswordVisible = false
        } else {
            // Password is hidden, show it
            binding.editTextLoginPwd.transformationMethod = HideReturnsTransformationMethod.getInstance()
            isPasswordVisible = true
        }
        // Move cursor to the end of the text
        binding.editTextLoginPwd.setSelection(binding.editTextLoginPwd.text.length)
    }


    private fun loginUser(email: String, pwd: String) {
        binding.progressBar.visibility = View.VISIBLE
        try {
            authProfile?.signInWithEmailAndPassword(email, pwd)
            val firebaseUser = authProfile?.currentUser

            if (firebaseUser != null) {
                if (firebaseUser.isEmailVerified) {
//                    dataViewModel.getTeamData {teamData,isRegistered->
//                        CoroutineScope(Dispatchers.IO).launch {
//                            saveRegisteredState(isRegistered, this@SplashLoginActivity)
//                        }
//                    }
                    startActivity(Intent(this@SplashLoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    firebaseUser.sendEmailVerification()
                    authProfile!!.signOut()
                    showAlertDialog()
                }
            }
            else{
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@SplashLoginActivity, "Unknown error occured, Please try again or create account!", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
             Log.e(TAG, "Login failed: ${e.message}")
            binding.progressBar.visibility = View.GONE
            Toast.makeText(
                this@SplashLoginActivity,
                "Login failed: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this@SplashLoginActivity)
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

        //Create the AlertDialog
        val alertDialog = builder.create()

        // Show the AlertDialog
        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        authProfile = FirebaseAuth.getInstance()

    }

}