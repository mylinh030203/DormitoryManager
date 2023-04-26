package com.example.dormitorymanager.View.RegisterRoomManagerAdmin

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.findFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.dormitorymanager.R
import com.example.dormitorymanager.ViewModel.ViewModelDetailRR
import com.example.dormitorymanager.ViewModel.ViewModelStudent
import com.example.dormitorymanager.ViewModel.ViewModelUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Locale

class AddStudentInRoomFragment : Fragment() {
    private lateinit var viewmodelDetailRRM: ViewModelDetailRR
    private lateinit var viewModel: ViewModelUser
    private lateinit var viewModelStudent: ViewModelStudent
    private var selectedDateRegist: String? = null
    private var selectedDateExpirate: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelStudent = ViewModelProvider(this).get(ViewModelStudent::class.java)
        viewModel = ViewModelProvider(this).get(ViewModelUser::class.java)
        viewmodelDetailRRM = ViewModelProvider(this).get(ViewModelDetailRR::class.java)
        val view = inflater.inflate(R.layout.fragment_add_student_in_room, container, false)
        val btnRegistDate = view.findViewById<Button>(R.id.btnRegistDate)
        val btnexpirate = view.findViewById<Button>(R.id.btnexpirate)
        val btnStatus = view.findViewById<Button>(R.id.btnStatus)

        var edtEmail = view.findViewById<EditText>(R.id.edtEmailst)
        var room_id = arguments?.getString("_id", "8").toString()
        var tvDateRegister = view.findViewById<TextView>(R.id.tvDateRegister)

        btnRegistDate.setOnClickListener {
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
                    tvDateRegister.text =
                        if (selectedDateRegist != null) selectedDateRegist else "Please select a date"
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }


        var tvDateExpirate = view.findViewById<TextView>(R.id.tvDateExpirate)
        btnexpirate.setOnClickListener {
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
                    tvDateExpirate.text =
                        if (selectedDateExpirate != null) selectedDateExpirate else "Please select a date"
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
        val btnAddStToRoom = view.findViewById<Button>(R.id.btnAddStToRoom)
        viewModel.checkAdmin { isAdmin ->
            if (isAdmin) {
                btnAddStToRoom.setOnClickListener {
                    viewModel.getIDDoccumentUser(edtEmail.text.toString()) { documentId ->
                        viewmodelDetailRRM.RegisterRoom(
                            room_id,
                            documentId.toString(),
                            tvDateRegister.text.toString(),
                            tvDateExpirate.text.toString(),
                            "Đã Duyệt",
                            100000
                        )
                    }
                    Toast.makeText(context, "Add student Success", Toast.LENGTH_SHORT).show()
                    val navController = view?.findNavController()
                    navController?.navigate(
                        R.id.action_addStudentInRoomFragment_to_registerRMFragment
                    )
                }
            } else {
                val room_id = arguments?.getString("room_id", "")
                val user_id = arguments?.getString("user_id", "")
                edtEmail.setText(arguments?.getString("email", ""))
                btnStatus.visibility = View.GONE
                btnAddStToRoom.setOnClickListener {
                    viewmodelDetailRRM.RegisterRoom(
                        room_id.toString(), user_id.toString(), tvDateRegister.text.toString(),
                        tvDateExpirate.text.toString(), "Chưa duyệt",
                        100000
                    )
                }
            }
        }
        return view
    }


}