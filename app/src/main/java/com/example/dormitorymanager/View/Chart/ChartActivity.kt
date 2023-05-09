package com.example.dormitorymanager.View.Chart

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.dormitorymanager.R
import com.example.dormitorymanager.ViewModel.ViewModelRoom
import com.example.dormitorymanager.databinding.ActivityChartBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate


class ChartActivity: AppCompatActivity() {
    private lateinit var binding: ActivityChartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChartBinding.inflate(layoutInflater)

        setContentView(binding.root)
        val sub2 = ChartFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_Chartlayout,sub2)
            commit()
        }
    }
}