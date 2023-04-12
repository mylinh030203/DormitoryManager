package com.example.dormitorymanager.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dormitorymanager.Model.Rooms
import com.example.dormitorymanager.Model.StudentInfor
import com.example.dormitorymanager.Model.Users
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModelStudent: ViewModel() {
    private val db = Firebase.firestore
    val collectionStudent = db.collection("StudentInfo")


    val _student = MutableLiveData<List<StudentInfor>>()
    val students: LiveData<List<StudentInfor>>
        get() = _student

    private val _updateResultSt = MutableLiveData<Boolean>()
    val updateResultSt: LiveData<Boolean>
        get() = _updateResultSt

    fun getStudent(): MutableList<StudentInfor> {
        //lấy hết document trong colection students chuyển nó thành đối tượng StudentInfor rồi thêm vào list, sau đó cập nhật cho multablelivedata
        collectionStudent.get().addOnSuccessListener { snapshot ->
            val studentsList = mutableListOf<StudentInfor>()
            for (doc in snapshot!!) {
                val student = doc.toObject(StudentInfor::class.java)
                student._id = doc.id
                studentsList.add(student)
            }
            _student.value = studentsList
        }.addOnFailureListener { exception ->
            Log.w("TAG", "Error getting documents: ", exception)
        }

        return _student.value?.toMutableList() ?: mutableListOf()
        //chuyển từ multablelivedata sang multablelist
    }

    fun addStudent(_id:String, fullname:String,  phone : String, gender:String, idStudent:String, classStd: String,avatar : String) {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            try {
                val student = StudentInfor(_id, fullname, phone, gender, idStudent, classStd, avatar)
                val studentDoc = hashMapOf(
                    "_id" to _id,
                    "fullname" to fullname,
                    "phone" to phone,
                    "gender" to gender,
                    "idStudent" to idStudent,
                    "classStd" to classStd,
                    "avatar" to avatar
                )
                collectionStudent.document(_id)
                    .set(studentDoc).addOnSuccessListener {
                        val list = _student.value?.toMutableList() ?: mutableListOf()
                        list.add(student)
                        _student.value = list
                        Log.d("TAG", "Đã thêm tài liệu mới với ID: ${it}")
                    }.addOnFailureListener { error ->
                        Log.e("TAG", "Lỗi khi thêm tài liệu: ", error) }


            }
            catch (e: Exception) {
                // Xử lý lỗi nếu có
            }
        }
    }

    fun updateStudent(_id:String, fullname:String,  phone : String, gender:String, idStudent:String, classStd: String,avatar : String){
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            try {
                val studentDoc = hashMapOf(
                    "_id" to _id,
                    "fullname" to fullname,
                    "phone" to phone,
                    "gender" to gender,
                    "idStudent" to idStudent,
                    "classStd" to classStd,
                    "avatar" to avatar
                )
                val nameUser = hashMapOf("name" to fullname)


                collectionStudent.document(_id).update(studentDoc as Map<String, Any>).addOnSuccessListener {
                    _updateResultSt.value = true
                    Log.d("TAG", "Đã cập nhật trường thành công!")

                    db.collection("Users").document(_id).update(nameUser as Map<String,Any>).addOnSuccessListener {
                        Log.d("TAG", "Đã cập nhật username thành công!")
                    }.addOnFailureListener{e->
                        Log.e("TAG", "Lỗi khi cập nhật user: ", e)
                    }

                    FirebaseDatabase.getInstance().getReference("User").child(_id).child("name").setValue(fullname).
                    addOnSuccessListener{
                        Log.d("TAG", "Đã cập nhật trường thành công trong realtime")
                    }.addOnFailureListener{e->
                        Log.e("TAG", "Lỗi khi cập nhật realtime: ", e)
                    }
                }.addOnFailureListener { error ->
                    _updateResultSt.value = false
                    Log.e("TAG", "Lỗi khi cập nhật trường: ", error)

                }
                val students = StudentInfor(_id, fullname, phone, gender, idStudent, classStd, avatar)
                val list = _student.value?.toMutableList() ?: mutableListOf()
                val room =  list.find { it._id == _id }
                val index = _student.value?.indexOfFirst { it._id == room?._id } ?: return@launch
                list[index] = students
                _student.value = list

            }catch(e:Exception){

            }
        }

    }

    fun deleteStudent(_id:String){

    }
}