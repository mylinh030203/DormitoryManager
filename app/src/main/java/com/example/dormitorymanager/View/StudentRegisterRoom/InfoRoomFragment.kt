package com.example.dormitorymanager.View.StudentRegisterRoom

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.dormitorymanager.R
import com.example.dormitorymanager.ViewModel.ViewModelDetailRR
import com.example.dormitorymanager.ViewModel.ViewModelRoom
import com.example.dormitorymanager.ViewModel.ViewModelStudent
import com.example.dormitorymanager.ViewModel.ViewModelUser
import com.example.dormitorymanager.databinding.FragmentInfoRoomBinding


class InfoRoomFragment : Fragment() {
    private lateinit var binding : FragmentInfoRoomBinding
    private lateinit var viewModelRoom: ViewModelRoom
    private lateinit var viewmodelDetailRRM: ViewModelDetailRR
    private lateinit var viewModel: ViewModelUser
    private lateinit var viewModelStudent: ViewModelStudent
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        var view = inflater.inflate(R.layout.fragment_info_room, container,false)
        binding = FragmentInfoRoomBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ViewModelUser::class.java)
        viewModelRoom = ViewModelProvider(this).get(ViewModelRoom::class.java)
        viewmodelDetailRRM = ViewModelProvider(this).get(ViewModelDetailRR::class.java)
        viewModelStudent = ViewModelProvider(this).get(ViewModelStudent::class.java)
        binding.tvbed.setText(arguments?.getString("bed","8"))
        binding.tvDescription.setText(arguments?.getString("decs","8"))
        binding.tvLoc.setText(arguments?.getString("loc","8"))
        binding.tvname.setText(arguments?.getString("name","8"))
        binding.tvPrice.setText(arguments?.getString("price", "9"))
        val roomID = arguments?.getString("id","8")
        val userID = viewModel.getCurrentUser()
        binding.tvnumberOfCurrentMenber.setText(arguments?.getString("status","8"))
       val bundle = Bundle()
        bundle.putString("room_id",roomID)
        bundle.putString("user_id",userID)
        bundle.putString("email",viewModel.getEmailCurrent())
        binding.btnRegister.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_infoRoomFragment_to_addStudentInRoomFragment3, bundle)
        }
        return binding.root
    }

}