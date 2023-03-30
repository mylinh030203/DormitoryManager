package com.example.dormitorymanager.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.dormitorymanager.MainActivity
import com.example.dormitorymanager.Model.Users
import com.example.dormitorymanager.ViewModel.ViewModelUser
import com.example.dormitorymanager.databinding.ActivityLoginBinding
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel : ViewModelUser
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this@LoginActivity).get(ViewModelUser::class.java)
        btnLogin.setOnClickListener {
            val email = binding.edtemail.text.toString()
            val password = binding.edtPassword.text.toString()
            if(email.isEmpty()){
                binding.edtemail.error ="Enter your email"
                return@setOnClickListener
            }
            if(password.isEmpty()){
                binding.edtPassword.error ="Enter your password"
                return@setOnClickListener
            }
            viewModel.LoginUser(email,password)
            binding.edtemail.setText("")
            binding.edtPassword.setText("")

            if(viewModel.user!=null){
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.putExtra("fullname", viewModel.user?.email.toString() )
                startActivity(intent)

            }
        }
        binding.txtregis.setOnClickListener{
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

}