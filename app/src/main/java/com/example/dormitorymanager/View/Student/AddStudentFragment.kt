package com.example.dormitorymanager.View.Student

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.dormitorymanager.R
import com.example.dormitorymanager.ViewModel.ViewModelUser
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_update_student.*

class AddStudentFragment : Fragment() {
    private lateinit var viewModel: ViewModelUser

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_add_student, container, false)
        var edtemail = view.findViewById<EditText>(R.id.edtemail)
        var edtPassword = view.findViewById<EditText>(R.id.edtPassword)
        var edtFullname = view.findViewById<EditText>(R.id.edtFullname)
        var gender = view.findViewById<RadioGroup>(R.id.gender)
        val rbMale = view.findViewById<RadioButton>(R.id.rbMale)
        val rbFemale = view.findViewById<RadioButton>(R.id.rbFemale)
        var btnAddAccount = view.findViewById<Button>(R.id.btnAddAccount)
        viewModel = ViewModelProvider(this).get(ViewModelUser::class.java)
        btnAddAccount.setOnClickListener {
            var email = edtemail.text.toString()
            var passwords = edtPassword.text.toString()
            var fullNames = edtFullname.text.toString()
            var roleID = "2"

            var selected: Int = gender.checkedRadioButtonId
            var Gender: String = when (selected) {
                R.id.rbMale -> "Male"
                R.id.rbFemale -> "Female"
                else -> "other"
            }
            if (email.isEmpty()) {
                edtemail.error = "Enter your email"
                return@setOnClickListener
            }
            if (passwords.isEmpty()) {
                edtPassword.error = "Enter your password"
                return@setOnClickListener
            }

            if (fullNames.isEmpty()) {
                edtFullname.error = "Enter your full name"
                return@setOnClickListener
            }

            viewModel.RegisterUsers(email, fullNames, passwords, roleID, Gender)

            edtemail.setText("")
            edtPassword.setText("")
            edtFullname.setText("")
            val navController = view?.findNavController()
            navController?.navigate(R.id.action_addStudentFragment_to_studentFragment)
        }
        return view

    }
}