package com.example.dormitorymanager.View.RegisterRoomManagerAdmin

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.dormitorymanager.R
import com.example.dormitorymanager.View.RoomManagerAdmin.RoomActivity
import com.example.dormitorymanager.ViewModel.ViewModelDetailRR
import com.example.dormitorymanager.ViewModel.ViewModelRoom
import com.example.dormitorymanager.ViewModel.ViewModelStudent
import com.example.dormitorymanager.ViewModel.ViewModelUser
import com.example.dormitorymanager.databinding.FragmentUpdateStudentInRoomBinding

class UpdateStudentInRoomFragment : Fragment() {
    private lateinit var binding:FragmentUpdateStudentInRoomBinding
    private lateinit var viewmodelDetailRRM: ViewModelDetailRR
    private lateinit var viewModel: ViewModelUser
    private lateinit var viewModelStudent: ViewModelStudent
    private var selectedDateRegist: String? = null
    private var selectedDateExpirate: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModelStudent = ViewModelProvider(this).get(ViewModelStudent::class.java)
        viewModel = ViewModelProvider(this).get(ViewModelUser::class.java)
        viewmodelDetailRRM = ViewModelProvider(this).get(ViewModelDetailRR::class.java)
        binding = FragmentUpdateStudentInRoomBinding.inflate(inflater, container, false)
        binding.tvRoomNameUpdate.text = arguments?.getString("room_name","")
        Log.e("room_name",arguments?.getString("room_name","").toString() )
        binding.tvFullnameUp.text = arguments?.getString("fullname","")
        binding.tvPriceOfStUp.text = arguments?.getString("price","")
        binding.tvDateRegisterup.text = arguments?.getString("registrationDate","a")
        binding.tvStatusRRMup.text = arguments?.getString("status","a")
        binding.tvDateExpirateup.text = arguments?.getString("expirationDate","a")
        var id = arguments?.getString("id","a").toString()
        var room_id = arguments?.getString("room_id","a").toString()
        binding.btnRegistDateup.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = "$dayOfMonth/${month + 1}/$year"
                    // Lưu giá trị ngày được chọn vào biến selectedDateRegist
                    selectedDateRegist = selectedDate
                    // Cập nhật giá trị của tvDateRegister
                    binding.tvDateRegisterup.text = if (selectedDateRegist != null) selectedDateRegist else "Please select a date"
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }


        binding.btnexpirateup.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = "$dayOfMonth/${month + 1}/$year"
                    // Lưu giá trị ngày được chọn vào biến selectedDateRegist
                    selectedDateExpirate = selectedDate
                    // Cập nhật giá trị của tvDateRegister
                    binding.tvDateExpirateup.text = if (selectedDateExpirate != null) selectedDateExpirate else "Please select a date"
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
        binding.btnStatusup.setOnClickListener {
            binding.tvStatusRRMup.text = "Đã duyệt"
        }

        viewModelStudent.countStudentInRoom(room_id.toString())
        val viewModelRoom = ViewModelRoom()
        binding.btnUpdateStInRoom.setOnClickListener {
            val member =viewModelStudent.CountstudentByRoomID.value?.toInt()
            viewmodelDetailRRM.getDate(binding.tvDateRegisterup.text.toString(),binding.tvDateExpirateup.text.toString(),{daysDiff ->
                viewModelRoom.getRoomPrice(room_id.toString(), {price->
                    val priceInADayforaStudent = member?.let { it1 ->
                        (price?.toDouble()?.div(30))?.div(
                            it1
                        )

                    }
                    Log.e("price",price.toString())
                    Log.e("daysDiff",daysDiff.toString())
                    Log.e("priceInADayforaStudent",priceInADayforaStudent.toString())

                    priceInADayforaStudent?.let { it1 -> daysDiff?.times(it1)
                        Log.e("daysDiff",daysDiff?.times(it1).toString())
                        viewmodelDetailRRM.updateDetail(id,room_id, id,binding.tvDateRegisterup.text.toString(),
                            binding.tvDateExpirateup.text.toString(), binding.tvStatusRRMup.text.toString(),daysDiff?.times(it1)?.toLong() ?: 100000)
                    }
                })
            })


            Toast.makeText(context, "Update student Success", Toast.LENGTH_SHORT).show()
            val inten  = Intent(context, RoomActivity::class.java)
            startActivity(inten)
        }
        return binding.root
    }
}