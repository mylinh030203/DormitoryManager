package com.example.dormitorymanager.View.StudentManagerAdmin

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.dormitorymanager.MainActivity
import com.example.dormitorymanager.R
import com.example.dormitorymanager.ViewModel.ViewModelStudent
import com.example.dormitorymanager.ViewModel.ViewModelUser
import kotlinx.android.synthetic.main.fragment_update_student.*

class UpdateStudentFragment : Fragment() {
    private lateinit var viewModelStudent: ViewModelStudent
    private lateinit var viewModel: ViewModelUser

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelStudent = ViewModelProvider(this).get(ViewModelStudent::class.java)
        viewModel = ViewModelProvider(this).get(ViewModelUser::class.java)
        var view = inflater.inflate(R.layout.fragment_update_student, container, false)
        var rgGenderSt = view.findViewById<RadioGroup>(R.id.rgGenderSt)
        val rbMaleSt = view.findViewById<RadioButton>(R.id.rbMaleSt)
        val rbFemaleSt = view.findViewById<RadioButton>(R.id.rbFemaleSt)
        // Inflate the layout for this fragment

        var edtfullname = view.findViewById<EditText>(R.id.edtFullnameSt)
        var edtphone = view.findViewById<EditText>(R.id.phoneSt)
        var edtidst = view.findViewById<EditText>(R.id.edtIdSt)
        var edtClass = view.findViewById<EditText>(R.id.edtClass)
        var edtAvatar = view.findViewById<EditText>(R.id.edtAvatar)
        var btnupdateSt = view.findViewById<Button>(R.id.btnupdateSt)
        var btndeleteSt = view.findViewById<Button>(R.id.btndeleteSt)
        var id = arguments?.getString("id", "8")
        edtfullname.setText(arguments?.getString("fullname", "8"))
        edtphone.setText(arguments?.getString("phone", "8"))
        var genderst = arguments?.getString("gender", "other")
        if (genderst == "Female") {
            rbFemaleSt.isChecked = true
        } else if (genderst == "Male") {
            rbMaleSt.isChecked = true
        }
        edtidst.setText(arguments?.getString("idStudent", "8"))
        edtClass.setText(arguments?.getString("classStd", "8"))
        edtAvatar.setText(arguments?.getString("avatar", "8"))
        btnupdateSt.setOnClickListener {
            var selected: Int = rgGenderSt.checkedRadioButtonId
            var Gender: String = when (selected) {
                R.id.rbMaleSt -> "Male"
                R.id.rbFemaleSt -> "Female"
                else -> "other"
            }
            viewModelStudent.updateStudent(
                id.toString(),
                edtfullname.text.toString(),
                edtphone.text.toString(),
                Gender.toString(),
                edtidst.text.toString(),
                edtClass.text.toString(),
                edtAvatar.text.toString()
            )
        }
        viewModelStudent.updateResultSt.observe(viewLifecycleOwner, Observer { updateSuccess ->
            if (updateSuccess == true) {
                Toast.makeText(context, "Update success", Toast.LENGTH_SHORT).show()
                viewModel.checkAdmin { isAdmin ->
                    if (isAdmin)
                        view.findNavController()
                            .navigate(R.id.action_updateStudentFragment_to_studentFragment)
                }
            } else {
                Toast.makeText(context, "Update failure", Toast.LENGTH_SHORT).show()
            }

        })
        viewModel.checkAdmin { isAdmin ->
            if (isAdmin){
                btndeleteSt.setOnClickListener {
                    viewModelStudent.deleteStudent(id.toString())
                    var ad = AlertDialog.Builder(requireContext())
                    ad.setTitle("Delete record")
                    ad.setMessage("Delete success")
                    ad.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                        Toast.makeText(context, "Delete success", Toast.LENGTH_SHORT).show()
                    }).show()
                    view.findNavController().navigate(R.id.action_updateStudentFragment_to_studentFragment)
                }
            }

        }

        return view
    }

}