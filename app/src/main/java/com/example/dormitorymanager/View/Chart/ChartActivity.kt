package com.example.dormitorymanager.View.Chart

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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


class ChartActivity: AppCompatActivity(), OnChartValueSelectedListener{
    private lateinit var mChart: PieChart
    private lateinit var binding: ActivityChartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChartBinding.inflate(layoutInflater)


        mChart = binding.piechart
        mChart.isRotationEnabled = true
        mChart.description = Description()
        mChart.setHoleRadius(35f)
        mChart.setTransparentCircleAlpha(0)
        mChart.centerText = "PieChart"
        mChart.setCenterTextSize(10f)
        mChart.isDrawEntryLabelsEnabled

        addDataSet(mChart)

        mChart.setOnChartValueSelectedListener(this)
        setContentView(binding.root)
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        Toast.makeText(
            this, "Value: " + e?.y
                    + ", index: " + h?.x
                    + ", DataSet index: " + h?.dataSetIndex, Toast.LENGTH_SHORT
        ).show()
    }

    override fun onNothingSelected() {}

    private fun addDataSet(pieChart: PieChart) {
        val yEntrys = ArrayList<PieEntry>()
        val xEntrys = ArrayList<String>()
        val yData = floatArrayOf(25f, 40f, 70f)
        val xData = arrayOf("January", "February", "January")

        for (i in yData.indices) {
            yEntrys.add(PieEntry(yData[i], i))
        }
        for (i in xData.indices) {
            xEntrys.add(xData[i])
        }

        val pieDataSet = PieDataSet(yEntrys, "Employee Sales")
        pieDataSet.sliceSpace = 2f
        pieDataSet.valueTextSize = 12f

        val colors = ArrayList<Int>()
        colors.add(Color.GRAY)
        colors.add(Color.BLUE)
        colors.add(Color.RED)

        pieDataSet.colors = colors

        val legend = pieChart.legend
        legend.form = Legend.LegendForm.CIRCLE
//        legend.set
//        legend.position = Legend.LegendPosition.LEFT_OF_CHART

        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.invalidate()
    }
}