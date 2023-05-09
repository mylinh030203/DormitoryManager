package com.example.dormitorymanager.View.Chart

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Color.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.dormitorymanager.R
import com.example.dormitorymanager.ViewModel.ViewModelRoom
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter


class ChartFragment : Fragment() {

    private lateinit var viewModel: ViewModelRoom
    private lateinit var pieChart: PieChart
    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chart, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(ViewModelRoom::class.java)
        pieChart = view.findViewById(R.id.piechart)
        viewModel.getRoomData()

        viewModel.enoughRooms.observe(viewLifecycleOwner, { enoughRooms ->
            viewModel.nearEnoughRooms.observe(viewLifecycleOwner, { nearEnoughRooms ->
                viewModel.lackRooms.observe(viewLifecycleOwner, { lackRooms ->
                    val entries = listOf(
                        PieEntry(enoughRooms.toFloat(), "Đủ sinh viên"),
                        PieEntry(nearEnoughRooms.toFloat(), "Gần đủ sinh viên"),
                        PieEntry(lackRooms.toFloat(), "Thiếu sinh viên")
                    )

                    val colors = listOf(
                        ContextCompat.getColor(requireContext(), R.color.teal_200),
                        ContextCompat.getColor(requireContext(), R.color.purple_200),
                        ContextCompat.getColor(requireContext(), R.color.ic_dm_background)
                    )

                    val dataSet = PieDataSet(entries, "aaa")
                    dataSet.colors = colors

                    val data = PieData(dataSet)
                    data.setValueFormatter(PercentFormatter())
                    data.setValueTextSize(14f)
                    data.setValueTextColor(Color.WHITE)

                    pieChart.data = data
                    pieChart.description.isEnabled = false
                    pieChart.legend.isEnabled = false
                    pieChart.setDrawEntryLabels(false)
                    pieChart.setDrawMarkers(false)
                    pieChart.holeRadius = 75f
                    pieChart.transparentCircleRadius = 0f
                    pieChart.animateY(1000, Easing.EaseInOutQuad)
                })
            })
        })

        return view
    }


}