package com.example.criconnect

 import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
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

    val dataViewModel: TeamViewModel by viewModel()
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authProfile = FirebaseAuth.getInstance()

        binding.imageViewVisibility.setOnClickListener(View.OnClickListener { // Toggle password visibility
            if (isPasswordVisible) {
                // Hide password
                binding.editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance())
                binding.imageViewVisibility.setImageResource(R.drawable.ic_baseline_visibility_off_24)
            } else {
                // Show password
                binding.editTextLoginPwd.setTransformationMethod(null)
                binding.imageViewVisibility.setImageResource(R.drawable.ic_baseline_visibility_24)
            }
            isPasswordVisible = !isPasswordVisible
            // Move cursor to the end of the text
            binding.editTextLoginPwd.setSelection(binding.editTextLoginPwd.getText().length)
        })

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(
                this@SplashLoginActivity,
                "You can reset password now!",
                Toast.LENGTH_SHORT
            )
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

    /*
        private fun loginUser(email: String, pwd: String) {
            authProfile?.signInWithEmailAndPassword(email, pwd)
                ?.addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
                    override fun onComplete(task: Task<AuthResult?>) {
                        if (task.isSuccessful()) {
                             val firebaseUser: FirebaseUser = authProfile?.getCurrentUser()!!
                            //check if email is verified before user can access their profile
                            if (firebaseUser.isEmailVerified()) {
    //                            Toast.makeText(
    //                                this@SplashLoginActivity,
    //                                "You are logged in now",
    //                                Toast.LENGTH_SHORT
    //                            ).show()

                                val sharedPreferences = getSharedPreferences("YOUR_PREFERENCE_NAME", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("USER_UUID", firebaseUser.uid)
                                editor.apply()

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
    */

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

        } catch (e: Exception) {
            // Handle exceptions here
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

    suspend fun saveRegisteredState(isRegistered: Boolean, context: Context) {
        withContext(Dispatchers.IO) {
            val sharedPreferences =
                context.getSharedPreferences("USERPREFERENCE", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("isTeamRegistered", isRegistered)
            editor.apply()
        }
    }
}