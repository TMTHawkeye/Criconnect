package com.example.criconnect

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.criconnect.Activities.ForgetPasswordActivity
import com.example.criconnect.Activities.RegisterUserActivity
import com.example.criconnect.databinding.ActivitySplashLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser

class SplashLoginActivity : AppCompatActivity() {
    lateinit var binding : ActivitySplashLoginBinding
    private var authProfile: FirebaseAuth? = null
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySplashLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authProfile = FirebaseAuth.getInstance()
        val buttonLogin = findViewById<Button>(R.id.tv_loginbutton)
        val buttonForgotPassword = findViewById<Button>(R.id.tv_forgotPassword)

         buttonForgotPassword.setOnClickListener {
            Toast.makeText(this@SplashLoginActivity, "You can reset password now!", Toast.LENGTH_SHORT)
                .show()
            startActivity(Intent(this@SplashLoginActivity, ForgetPasswordActivity::class.java))
        }


//         Show hide password using eye icon

//        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd);
//        imageViewShowHidePwd.setImageResource(R.drawable.baseline_visibility_off_24);
//        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
//                    editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    imageViewShowHidePwd.setImageResource(R.drawable.baseline_visibility_off_24);
//                }else{
//                    editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    imageViewShowHidePwd.setImageResource(R.drawable.ic_baseline_visibility_24);
//                }
//            }
//        });
        buttonLogin.setOnClickListener {
            val textEmail: String = binding.editTextLoginEmail.getText().toString()
            val textPwd: String = binding.editTextLoginPwd.getText().toString()
            if (TextUtils.isEmpty(textEmail)) {
                Toast.makeText(this@SplashLoginActivity, "Please enter your email", Toast.LENGTH_SHORT)
                    .show()
                binding.editTextLoginEmail.setError("Email is required")
                binding.editTextLoginEmail.requestFocus()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                Toast.makeText(this@SplashLoginActivity, "Please re_enter your email", Toast.LENGTH_SHORT)
                    .show()
                binding.editTextLoginEmail.setError("Valid Email is required")
                binding.editTextLoginEmail.requestFocus()
            } else if (TextUtils.isEmpty(textPwd)) {
                Toast.makeText(this@SplashLoginActivity, "Please enter your password", Toast.LENGTH_SHORT)
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

    private fun loginUser(email: String, pwd: String) {
        authProfile?.signInWithEmailAndPassword(email, pwd)
            ?.addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
                override fun onComplete(task: Task<AuthResult?>) {
                    if (task.isSuccessful()) {
                         val firebaseUser: FirebaseUser = authProfile?.getCurrentUser()!!
                        //check if email is verified before user can access their profile
                        if (firebaseUser.isEmailVerified()) {
                            Toast.makeText(
                                this@SplashLoginActivity,
                                "You are logged in now",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@SplashLoginActivity, MainActivity::class.java))
                            finish()
                        } //open user profile
                        else {
                            firebaseUser.sendEmailVerification()
                            authProfile!!.signOut() //sign out user
                            showAlertDialog()
                        }
                    } else {
                        try {
                            throw task.getException()!!
                        } catch (e: FirebaseAuthInvalidUserException) {
                            binding.editTextLoginEmail.setError("User does not exists or is no longer valid.please register again.")
                            binding.editTextLoginEmail.requestFocus()
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            binding.editTextLoginEmail.setError("invalid credentials. Kindly check and reenter")
                            binding.editTextLoginEmail.requestFocus()
                        } catch (e: Exception) {
                            Log.e("TAG", e.message!!)
                            Toast.makeText(this@SplashLoginActivity, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    binding.progressBar.setVisibility(View.GONE)
                }
            })
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
}