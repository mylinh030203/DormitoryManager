package com.example.dormitorymanager.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.dormitorymanager.Model.Users
import com.example.dormitorymanager.R
import com.example.dormitorymanager.ViewModel.ViewModelUser
import com.example.dormitorymanager.databinding.ActivityRegisterBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import java.util.concurrent.TimeUnit

class RegisterActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel : ViewModelUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_register)
        viewModel = ViewModelProvider(this@RegisterActivity).get(ViewModelUser::class.java)
        btnRegister.setOnClickListener {
            var email = edtemail.text.toString()
            var password = edtPassword.text.toString()
            var fullName = edtFullname.text.toString()
            var roleID = "2"
            if(email.isEmpty()){
                edtemail.error ="Enter your email"
                return@setOnClickListener
            }
            if(password.isEmpty()){
                edtPassword.error ="Enter your password"
                return@setOnClickListener
            }
            if(fullName.isEmpty()){
                edtFullname.error ="Enter your full name"
                return@setOnClickListener
            }
            viewModel.RegisterUsers(email,fullName,password, roleID)
        }
    }


}