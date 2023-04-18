package com.example.dormitorymanager.View.RegisterRoomManagerAdmin

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.findFragment
import androidx.lifecycle.ViewModelProvider
import com.example.dormitorymanager.R
import com.example.dormitorymanager.ViewModel.ViewModelDetailRR
import com.example.dormitorymanager.ViewModel.ViewModelStudent
import com.example.dormitorymanager.ViewModel.ViewModelUser

class AddStudentInRoomFragment : Fragment() {
    private lateinit var viewmodelDetailRRM : ViewModelDetailRR
    private lateinit var viewModel: ViewModelUser
    private lateinit var viewModelStudent: ViewModelStudent

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelStudent = ViewModelProvider(this).get(ViewModelStudent::class.java)
        viewModel = ViewModelProvider(this).get(ViewModelUser::class.java)
        viewmodelDetailRRM = ViewModelProvider(this).get(ViewModelDetailRR::class.java)
        val view = inflater.inflate(R.layout.fragment_add_student_in_room, container, false)
        val btnAddStToRoom = view.findViewById<Button>(R.id.btnAddStToRoom)
        var edtEmail = view.findViewById<EditText>(R.id.edtEmailst)
        var room_id = arguments?.getString("_id", "8").toString()
        btnAddStToRoom.setOnClickListener {
        viewModel.getIDDoccumentUser(edtEmail.text.toString()) { documentId ->
                viewmodelDetailRRM.RegisterRoom(room_id, documentId.toString(),"","","Đã Duyệt",100000)
            }
        }

        return view
    }

}