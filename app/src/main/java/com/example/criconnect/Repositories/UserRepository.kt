package com.example.criconnect.Repositories

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.criconnect.Activities.UserProfileActivity
import com.example.criconnect.CustomClasses.ReadWriteUserDetails
import com.example.criconnect.ModelClasses.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepository(val context: Context) {

    fun registerUser(userData: UserData, activity:Activity,callback: (Boolean,Boolean,String)->Unit){
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(userData.email!!, userData.password!!).addOnCompleteListener(
            activity
        ) { task ->
            if (task.isSuccessful) {
                val firebaseuser = auth.currentUser

                //update Display Name of User
                val profileChangeRequest =
                    UserProfileChangeRequest.Builder().setDisplayName(userData.fullName).build()
                firebaseuser!!.updateProfile(profileChangeRequest)
                //Enter User Data into the Firebase  Realtime Database
                val writeUserDetails = ReadWriteUserDetails(userData.dob, userData.gender, userData.mobile)

                //eXTRACTING uSER REFERENCE FROM DATABASE FOR REGISTRED USERS
                val referenceProfile =
                    FirebaseDatabase.getInstance().getReference("Registered Users")
                referenceProfile.child(firebaseuser.uid).setValue(writeUserDetails)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //send verification  Email
                            firebaseuser.sendEmailVerification()
                            callback.invoke(true,false,"")


                        } else {
                            callback.invoke(false,false,"")

                        }

                    }
            } else {
                try {
                    throw task.exception!!
                } catch (e: FirebaseAuthWeakPasswordException) {
                    callback.invoke(false,true,e.message?:"")

                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    callback.invoke(false,true,e.message?:"")

                } catch (e: FirebaseAuthUserCollisionException) {
                    callback.invoke(false,true,e.message?:"")


                } catch (e: Exception) {
                    callback.invoke(false,true,e.message?:"")

                }

            }
        }
    }

    private fun checkIfEmailVerified(firebaseUser: FirebaseUser,callback: (Boolean) -> Unit) {
        if (firebaseUser.isEmailVerified) {
            callback.invoke(true)
        }
        else{
            callback.invoke(false)
        }
    }

    fun showUserProfile(callback: (Boolean,Boolean,UserData?) -> Unit) {
        var firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            checkIfEmailVerified(firebaseUser){
                if(it){
                    val userID = firebaseUser.uid
                    Log.d("TAG_user_uuid", "showUserProfile: ${userID}")
                    val referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users")
                    referenceProfile.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val readUserDetails: ReadWriteUserDetails? =
                                snapshot.getValue(ReadWriteUserDetails::class.java)
                            if (readUserDetails != null) {
                                val userData = UserData(firebaseUser.displayName,firebaseUser.email,readUserDetails.doB,readUserDetails.gender,readUserDetails.mobile,"")
                                callback.invoke(true,true,userData)

                                //                   Uri uri = firebaseUser.getPhotoUrl();
//                   Picasso.with(ProfileActivity.this).load(uri).into(imageView);
                            } else {
                                callback.invoke(true,false,null)

//                                Toast.makeText(this@UserProfileActivity, "Something went wrong!", Toast.LENGTH_LONG)
//                                    .show()
                            }
//                            binding.progressBar.setVisibility(View.GONE)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            callback.invoke(true,false,null)

//                            Toast.makeText(this@UserProfileActivity, "Something went wrong!", Toast.LENGTH_LONG)
//                                .show()
//                            binding.progressBar.setVisibility(View.GONE)
                        }
                    })
                }
                else{
                    callback.invoke(false,false,null)
                }
            }

        }

    }



}