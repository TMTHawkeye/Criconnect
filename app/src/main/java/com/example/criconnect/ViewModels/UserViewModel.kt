package com.example.criconnect.ViewModels

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.criconnect.ModelClasses.UserData
import com.example.criconnect.Repositories.UserRepository
import com.google.firebase.auth.FirebaseUser

class UserViewModel(val repository: UserRepository) : ViewModel() {

    fun registerUser(userData : UserData, activity: Activity, callback:(Boolean, Boolean, String)->Unit){
        repository.registerUser(userData, activity,callback)
    }

    fun showUserProfile(callback: (Boolean,Boolean,UserData?) -> Unit){
        repository.showUserProfile(callback)
    }
}