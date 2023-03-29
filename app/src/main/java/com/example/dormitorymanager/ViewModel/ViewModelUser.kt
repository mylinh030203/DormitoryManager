package com.example.dormitorymanager.ViewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dormitorymanager.Model.Users
import com.example.dormitorymanager.View.RegisterActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class ViewModelUser(application: Application): AndroidViewModel(application) {

    private val dbRef = FirebaseDatabase.getInstance().getReference("User")
    private val auth = FirebaseAuth.getInstance()
    private val _userCreated = MutableLiveData<Boolean>()
    val userCreated: LiveData<Boolean>
        get() = _userCreated


    fun RegisterUsers(email: String, name: String, password: String, roleID:String){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task->
            if(task.isSuccessful){
                //thêm user vào Realtime db
                var id = dbRef.push().key!!
                val userData = Users(id,email, name, password, roleID)
                dbRef.child(id).setValue(userData)
                _userCreated.value = true
            }else{
                Log.e("TAG","Failes to create user", task.exception)
                _userCreated.value = false
            }
        }
    }
}