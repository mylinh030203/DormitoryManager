package com.example.dormitorymanager.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.*
import com.example.dormitorymanager.MainActivity
import com.example.dormitorymanager.ViewModel.ViewModelUser
import com.example.dormitorymanager.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: ViewModelUser
    private lateinit var binding: ActivityLoginBinding

    // Khai báo biến để kiểm tra trạng thái đăng nhập
    private var isLoggedIn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this@LoginActivity).get(ViewModelUser::class.java)

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.txtregis.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun login() {
        val email = binding.edtemail.text.toString()
        val password = binding.edtPassword.text.toString()
        if (email.isEmpty()) {
            binding.edtemail.error = "Enter your email"
            return
        }
        if (password.isEmpty()) {
            binding.edtPassword.error = "Enter your password"
            return
        }
        viewModel.LoginUser(email, password)
        // Sử dụng viewModelScope để theo dõi quá trình đăng nhập và xử lý kết quả
        viewModel.viewModelScope.launch {
            // Gọi hàm checkLogin() sau khi loginUser() đã hoàn thành
            viewModel.user.observe(this@LoginActivity, {
                if (it != null) {
                    // Xử lý khi đăng nhập thành công
                    // Ví dụ: Chuyển sang màn hình chính, hiển thị thông tin người dùng đã đăng nhập, ...
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // Xử lý khi đăng nhập thất bại
                    // Ví dụ: Hiển thị thông báo lỗi đăng nhập
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }

            })

        }
        binding.edtemail.setText("")
        binding.edtPassword.setText("")


    }

}


