package com.example.dormitorymanager.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel : ViewModelUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        binding.navLeftmenu.itemIconTintList = null
//        //lắng nghe sự kiện click lên các sự kiện menu
//        binding.navLeftmenu.setNavigationItemSelectedListener {
//            when (it.itemId) {
//                R.id.home -> Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
//                R.id.nav_mess -> Toast.makeText(this, "message", Toast.LENGTH_SHORT).show()
//                R.id.exit -> Toast.makeText(this, "exit", Toast.LENGTH_SHORT).show()
//            }
//            true
//        }
        viewModel = ViewModelProvider(this@RegisterActivity).get(ViewModelUser::class.java)
        btnRegister.setOnClickListener {
            var email = binding.edtemail.text.toString()
            var password = binding.edtPassword.text.toString()
            var fullName = binding.edtFullname.text.toString()
            var roleID = "2"
            if(email.isEmpty()){
                binding.edtemail.error ="Enter your email"
                return@setOnClickListener
            }
            if(password.isEmpty()){
                binding.edtPassword.error ="Enter your password"
                return@setOnClickListener
            }
            if(fullName.isEmpty()){
                binding.edtFullname.error ="Enter your full name"
                return@setOnClickListener
            }
            viewModel.RegisterUsers(email,fullName,password, roleID)

            binding.edtemail.setText("")
            binding.edtPassword.setText("")
            binding.edtFullname.setText("")
        }
        binding.txtlogin.setOnClickListener{
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }


}