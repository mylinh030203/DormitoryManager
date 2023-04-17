package com.example.dormitorymanager.View.StudentRegisterRoom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dormitorymanager.databinding.ActivityRegisterRoomBinding

class RegisterRoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterRoomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding =  ActivityRegisterRoomBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}