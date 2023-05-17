package com.example.dormitorymanager.View.InformationUser

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.dormitorymanager.MainActivity
import com.example.dormitorymanager.R
import com.example.dormitorymanager.ViewModel.ViewModelStudent
import com.example.dormitorymanager.ViewModel.ViewModelUser
import com.squareup.picasso.Picasso

class InformationUserUpdateFragment : Fragment() {


    private lateinit var viewModelStudent: ViewModelStudent
    private lateinit var viewModel: ViewModelUser
    private lateinit var imageUri: Uri
    private lateinit var imageBitmap: Bitmap
    private lateinit var imageView: ImageView
    val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                imageUri = uri
                imageBitmap =
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                imageView.setImageBitmap(imageBitmap)
            }

        }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelStudent = ViewModelProvider(this).get(ViewModelStudent::class.java)
        viewModel = ViewModelProvider(this).get(ViewModelUser::class.java)
        var view = inflater.inflate(R.layout.fragment_information_user_update, container, false)
        imageView = view.findViewById(R.id.imgSt)
        var rgGenderSt = view.findViewById<RadioGroup>(R.id.rgGenderSt)
        val rbMaleSt = view.findViewById<RadioButton>(R.id.rbMaleSt)
        val rbFemaleSt = view.findViewById<RadioButton>(R.id.rbFemaleSt)
        var edtfullname = view.findViewById<EditText>(R.id.edtFullnameSt)
        var edtphone = view.findViewById<EditText>(R.id.phoneSt)
        var edtidst = view.findViewById<EditText>(R.id.edtIdSt)
        var edtClass = view.findViewById<EditText>(R.id.edtClass)
        var edtAvatar = view.findViewById<Button>(R.id.edtAvatar)
        var btnupdateSt = view.findViewById<Button>(R.id.btnupdateSt)
        var btndeleteSt = view.findViewById<Button>(R.id.btndeleteSt)

        var id = viewModel.getCurrentUser()
        viewModelStudent.getStudent()
        viewModelStudent.students.observe(viewLifecycleOwner, { students ->
            // Xử lý dữ liệu sinh viên đã được cập nhật
            // Students là danh sách sinh viên
            val studentId = id // Thay đổi giá trị studentId tùy vào nhu cầu
            val student = viewModelStudent.getStudentById(studentId)

            var _id = student?._id


            edtfullname.setText(student?.fullname)
            edtphone.setText(student?.phone)
            var genderst = student?.gender
            if (genderst == "Female") {
                rbFemaleSt.isChecked = true
            } else if (genderst == "Male") {
                rbMaleSt.isChecked = true
            }
            edtidst.setText(student?.idStudent)
            edtClass.setText(student?.classStd)

            var avt = student?.avatar
            val imageUrl = avt
            Picasso.get().load(imageUrl).into(imageView)
            edtAvatar.setOnClickListener {
                pickImageLauncher.launch("image/*")
            }
        })

            btnupdateSt.setOnClickListener {
                var selected: Int = rgGenderSt.checkedRadioButtonId
                var Gender: String = when (selected) {
                    R.id.rbMaleSt -> "Male"
                    R.id.rbFemaleSt -> "Female"
                    else -> "other"
                }
                // Gọi hàm update để upload ảnh lên Firebase
                viewModelStudent.updateStudent(
                    id.toString(),
                    edtfullname.text.toString(),
                    edtphone.text.toString(),
                    Gender.toString(),
                    edtidst.text.toString(),
                    edtClass.text.toString(),
                    imageUri
                )


            }

        viewModelStudent.updateResultSt.observe(viewLifecycleOwner, Observer { updateSuccess ->
            if (updateSuccess == true) {
                Toast.makeText(context, "Update success", Toast.LENGTH_SHORT).show()
                viewModel.checkAdmin { isAdmin ->
                    if (isAdmin){
                        view.findNavController()
                            .navigate(R.id.action_updateStudentFragment_to_studentFragment)
                    }else{
                        val intent = Intent(activity, MainActivity::class.java)
                        startActivity(intent)
                    }

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
            else{
                btndeleteSt.setOnClickListener {
                    Toast.makeText(context, "You can't delete. You aren't admin", Toast.LENGTH_LONG).show()
                }
            }

        }

        return view
    }

}