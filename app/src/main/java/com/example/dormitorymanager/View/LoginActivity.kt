package com.example.dormitorymanager.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.dormitorymanager.MainActivity
import com.example.dormitorymanager.ViewModel.ViewModelUser
import com.example.dormitorymanager.databinding.ActivityLoginBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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

            val uid = viewModel.user?.uid.toString()
            val users = viewModel.dbRef.child(uid)

            if(viewModel.user!=null){
                //lấy thông tin đăng nhập
                users.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            val name:String = snapshot.child("name").value.toString()
                            var role_id : String = snapshot.child("role_id").value.toString()

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("fullname", name )
                            intent.putExtra("role",role_id)
                            startActivity(intent)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

            }
        }

        binding.txtregis.setOnClickListener{
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}