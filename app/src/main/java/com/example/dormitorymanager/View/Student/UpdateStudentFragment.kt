package com.example.dormitorymanager.View.Student

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.dormitorymanager.R
import com.example.dormitorymanager.ViewModel.ViewModelStudent
import kotlinx.android.synthetic.main.fragment_update_student.*

class UpdateStudentFragment : Fragment() {
    private lateinit var viewModelStudent: ViewModelStudent
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelStudent = ViewModelProvider(this).get(ViewModelStudent::class.java)
        var view = inflater.inflate(R.layout.fragment_update_student, container, false)
        // Inflate the layout for this fragment
        var edtfullname = view.findViewById<EditText>(R.id.edtFullnameSt)
        var edtphone = view.findViewById<EditText>(R.id.phoneSt)
        var edtGender = view.findViewById<EditText>(R.id.genderSt)
        var edtidst = view.findViewById<EditText>(R.id.edtIdSt)
        var edtClass = view.findViewById<EditText>(R.id.edtClass)
        var edtAvatar = view.findViewById<EditText>(R.id.edtAvatar)
        var btnupdateSt = view.findViewById<Button>(R.id.btnupdateSt)
        var id =  arguments?.getString("id", "8")
        edtfullname.setText(arguments?.getString("fullname", "8"))
        edtphone.setText(arguments?.getString("phone", "8"))
        edtGender.setText(arguments?.getString("gender", "8"))
        edtidst.setText(arguments?.getString("idStudent", "8"))
        edtClass.setText(arguments?.getString("classStd", "8"))
        edtAvatar.setText(arguments?.getString("avatar", "8"))
        btnupdateSt.setOnClickListener {
            viewModelStudent.updateStudent(
                id.toString(),
                edtfullname.text.toString(),
                edtphone.text.toString(),
                edtGender.text.toString(),
                edtidst.text.toString(),
                edtClass.text.toString(),
                edtAvatar.text.toString()
            )
        }
        viewModelStudent.updateResultSt.observe(viewLifecycleOwner, Observer{updateSuccess->
            if(updateSuccess==true){
                Toast.makeText(context,"Update success", Toast.LENGTH_SHORT).show()
                view.findNavController().navigate(R.id.action_updateStudentFragment_to_studentFragment)
            }else{
                Toast.makeText(context, "Update failure", Toast.LENGTH_SHORT).show()
            }
        })
        return view
    }

}