package com.example.criconnect.Activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.criconnect.MainActivity
import com.example.criconnect.SplashLoginActivity
import com.example.criconnect.databinding.ActivityForgetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class ForgetPasswordActivity : AppCompatActivity() {
    private var authProfile: FirebaseAuth? = null

    lateinit var binding : ActivityForgetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // progressBar = findViewById(R.id.progressBar);
        binding.btnReset.setOnClickListener(View.OnClickListener {
            val email: String = binding.emailBox.getText().toString()
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(
                    this@ForgetPasswordActivity,
                    "Please enter your registered email",
                    Toast.LENGTH_SHORT
                ).show()
                binding.emailBox.setError("Email is required")
                binding.emailBox.requestFocus()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(
                    this@ForgetPasswordActivity,
                    "Please enter your valid email",
                    Toast.LENGTH_SHORT
                ).show()
                binding.emailBox.setError("Valid Email is required")
                binding.emailBox.requestFocus()
            } else {
                 resetPassword(email)
            }
        })
    }

    private fun resetPassword(email: String) {
        authProfile = FirebaseAuth.getInstance()
        authProfile?.sendPasswordResetEmail(email)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    this@ForgetPasswordActivity,
                    "Please check your inbox for password reset link",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this@ForgetPasswordActivity, SplashLoginActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                try {
                    throw task.exception!!
                } catch (e: FirebaseAuthInvalidUserException) {
                    binding.emailBox.setError("User does not exist or is no longer valid.Please register again")
                } catch (e: Exception) {
//                        Log.e(TAG, e.getMessage());
                    Toast.makeText(this@ForgetPasswordActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }
            //                progressBar.setVisibility(View.GONE);
        }
    }

}