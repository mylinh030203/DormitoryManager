package com.example.dormitorymanager.View.Student

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dormitorymanager.Model.StudentInfor
import com.example.dormitorymanager.R
import com.example.dormitorymanager.View.AdapterRoom
import com.example.dormitorymanager.View.RegisterActivity
import com.example.dormitorymanager.View.rvInter
import com.example.dormitorymanager.ViewModel.ViewModelStudent

class StudentFragment : Fragment() {
    private lateinit var adapter: AdapterStudent
    private lateinit var rvStudent : RecyclerView
    private lateinit var viewModelStudent: ViewModelStudent
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelStudent = ViewModelProvider(this).get(ViewModelStudent::class.java)
        var view:View = inflater.inflate(R.layout.fragment_student,container,false)
        var btnaddSt = view.findViewById<Button>(R.id.btnAddStudent)
        rvStudent = view.findViewById(R.id.rvStudent)
        selectStudent(viewModelStudent.getStudent())
        // Inflate the layout for this fragment
        btnaddSt.setOnClickListener {
            val navController = view?.findNavController()
            navController?.navigate(R.id.action_studentFragment_to_addStudentFragment)
        }
        return view
    }

    fun selectStudent(list: MutableList<StudentInfor>){
        adapter = AdapterStudent(list,object : rvInter {
            override fun onClickStudent(position: Int){
                val bundle = Bundle()
                bundle.putString("id", adapter.currentList[position]._id)
                bundle.putString("fullname", adapter.currentList[position].fullname)
                bundle.putString("phone", adapter.currentList[position].phone)
                bundle.putString("gender", adapter.currentList[position].gender)
                bundle.putString("idStudent", adapter.currentList[position].idStudent)
                bundle.putString("classStd", adapter.currentList[position].classStd)
                bundle.putString("avatar", adapter.currentList[position].avatar)
                val navController = view?.findNavController()
                navController?.navigate(R.id.action_studentFragment_to_updateStudentFragment, bundle)

            }
        },this)
        rvStudent.adapter = adapter
        rvStudent.layoutManager = GridLayoutManager(context,
            4,
            GridLayoutManager.HORIZONTAL,
            false)
        //Hàm viewModelRoom.rooms.observe() được sử dụng để đăng ký một Observer cho LiveData được trả về từ ViewModel. Khi dữ liệu trong LiveData được cập nhật, Observer sẽ nhận được thông báo và có thể thực hiện một số hành động liên quan đến dữ liệu đó.
        viewModelStudent.students.observe(viewLifecycleOwner, { students ->
            adapter.setData(students.toMutableList())
        })

    }


}