package com.example.dormitorymanager.View.RegisterRoomManagerAdmin

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.dormitorymanager.R
import com.example.dormitorymanager.ViewModel.ViewModelDetailRR
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
        binding.tvRoomNameUpdate.text = arguments?.getString("room_name","a")
        binding.tvFullnameUp.text = arguments?.getString("fullname","a")
        binding.tvPriceOfStUp.text = arguments?.getString("price","a")
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
        binding.btnUpdateStInRoom.setOnClickListener {
            viewmodelDetailRRM.updateDetail(id,room_id, id,binding.tvDateRegisterup.text.toString(),
                binding.tvDateExpirateup.text.toString(), binding.tvStatusRRMup.text.toString(), (binding.tvPriceOfStUp.text.toString()).toLong() )
            Toast.makeText(context, "Update student Success", Toast.LENGTH_SHORT).show()
//            val navController = view?.findNavController()
//            navController?.navigate(
//                R.id.action_updateStudentInRoomFragment_to_registerRMFragment)
        }
        return binding.root
    }
}