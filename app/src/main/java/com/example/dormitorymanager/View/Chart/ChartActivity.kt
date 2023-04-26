package com.example.dormitorymanager.View.Chart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dormitorymanager.databinding.ActivityChartBinding

class ChartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChartBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}