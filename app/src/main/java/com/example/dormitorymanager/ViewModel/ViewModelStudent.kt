package com.example.dormitorymanager.ViewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dormitorymanager.Model.Rooms
import com.example.dormitorymanager.Model.StudentInfor
import com.example.dormitorymanager.Model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class ViewModelStudent : ViewModel() {
    private val db = Firebase.firestore
    val auth = FirebaseAuth.getInstance()
    val dbRef = FirebaseDatabase.getInstance().getReference("User")
    val collectionStudent = db.collection("StudentInfo")
    val collectionRegisterRoom = db.collection("DetailRoomRegister")
    private val usersCollection = db.collection("Users")


    val _student = MutableLiveData<List<StudentInfor>>()
    val students: LiveData<List<StudentInfor>>
        get() = _student

    val _studentByRoomID = MutableLiveData<List<StudentInfor>>()
    val studentByRoomID: LiveData<List<StudentInfor>>
        get() = _studentByRoomID

    val _studentUnapproved = MutableLiveData<List<StudentInfor>>()
    val StudentUnapproved: LiveData<List<StudentInfor>>
        get() = _studentUnapproved

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

    fun getStudentInRoom(roomID: String): MutableList<StudentInfor> {
        collectionRegisterRoom.whereEqualTo("room_id", roomID).get()
            .addOnSuccessListener { registeRoomDoc ->
                if (!registeRoomDoc.isEmpty) {
                    val userIdList = mutableListOf<String>()
                    for (registerRoomDocument in registeRoomDoc) {
                        val userId = registerRoomDocument.getString("user_id")
                        userId?.let { userIdList.add(it) }
                    }

                    collectionStudent.whereIn("_id", userIdList).get()
                        .addOnSuccessListener { studentDoc ->
                            val studentList = mutableListOf<StudentInfor>()
                            for (studentdoc in studentDoc) {
                                val student = studentdoc.toObject(StudentInfor::class.java)
                                studentList.add(student)
                            }
                            _studentByRoomID.value = studentList
                        }
                        .addOnFailureListener { }
                } else
                    _studentByRoomID.value = emptyList()
            }.addOnFailureListener {

            }
        return _studentByRoomID.value?.toMutableList() ?: mutableListOf()
    }


    fun getStudentUnapproved(): MutableList<StudentInfor> {
        collectionRegisterRoom.whereEqualTo("status", "Chưa duyệt").get()
            .addOnSuccessListener { registerRoomDoc ->
                if (!registerRoomDoc.isEmpty) {
                    Log.e("status", "abc")
                    val userIdList = mutableListOf<String>()
                    for (registerRoomDocument in registerRoomDoc) {
                        val userId = registerRoomDocument.getString("user_id")
                        userId?.let { userIdList.add(it) }
                    }
                    collectionStudent.whereIn("_id", userIdList).get()
                        .addOnSuccessListener { studentDoc ->
                            val studentList = mutableListOf<StudentInfor>()
                            for (studentdoc in studentDoc) {
                                val student = studentdoc.toObject(StudentInfor::class.java)
                                studentList.add(student)
                            }
                            _studentUnapproved.value = studentList
                            Log.e("status", "abc")
                        }
                        .addOnFailureListener { }
                } else
                    _studentUnapproved.value = emptyList()
            }.addOnFailureListener {

            }

        return _studentUnapproved.value?.toMutableList() ?: mutableListOf()
    }


    fun addStudent(
        _id: String,
        fullname: String,
        phone: String,
        gender: String,
        idStudent: String,
        classStd: String,
        avatar: String
    ) {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            try {
                val student =
                    StudentInfor(_id, fullname, phone, gender, idStudent, classStd, avatar)
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
                        Log.e("TAG", "Lỗi khi thêm tài liệu: ", error)
                    }


            } catch (e: Exception) {
                // Xử lý lỗi nếu có
            }
        }
    }

    fun updateStudent(
        _id: String,
        fullname: String,
        phone: String,
        gender: String,
        idStudent: String,
        classStd: String,
        avatar: Uri
    ) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images/${_id}")
        val uploadTask = storageRef.putFile(avatar)
        uploadTask.addOnSuccessListener { task ->
            // Nếu upload ảnh thành công, lấy đường dẫn ảnh từ task
            storageRef.downloadUrl.addOnSuccessListener { Uri ->
// Lưu đường dẫn ảnh vào Firestore
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
                            "avatar" to Uri.toString()
                        )

                        val nameUser = hashMapOf("name" to fullname)


                        collectionStudent.document(_id).update(studentDoc as Map<String, Any>)
                            .addOnSuccessListener {
                                _updateResultSt.value = true
                                Log.d("TAG", "Đã cập nhật trường thành công!")

                                db.collection("Users").document(_id)
                                    .update(nameUser as Map<String, Any>)
                                    .addOnSuccessListener {
                                        Log.d("TAG", "Đã cập nhật username thành công!")
                                    }.addOnFailureListener { e ->
                                        Log.e("TAG", "Lỗi khi cập nhật user: ", e)
                                    }

                                FirebaseDatabase.getInstance().getReference("User").child(_id)
                                    .child("name")
                                    .setValue(fullname).addOnSuccessListener {
                                        Log.d("TAG", "Đã cập nhật trường thành công trong realtime")
                                    }.addOnFailureListener { e ->
                                        Log.e("TAG", "Lỗi khi cập nhật realtime: ", e)
                                    }
                            }.addOnFailureListener { error ->
                                _updateResultSt.value = false
                                Log.e("TAG", "Lỗi khi cập nhật trường: ", error)

                            }
                        val students =
                            StudentInfor(
                                _id,
                                fullname,
                                phone,
                                gender,
                                idStudent,
                                classStd,
                                avatar.toString()
                            )
                        val list = _student.value?.toMutableList() ?: mutableListOf()
                        val room = list.find { it._id == _id }
                        val index =
                            _student.value?.indexOfFirst { it._id == room?._id } ?: return@launch
                        list[index] = students
                        _student.value = list

                    } catch (e: Exception) {

                    }
                }
            }

        }.addOnFailureListener { exception ->
            // Nếu upload ảnh thất bại, hiển thị thông báo lỗi
            Log.e("img", "Error uploading image", exception)
            // ...
        }

    }


    fun deleteStudent(_id: String) {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            try {
                var docDelete = collectionStudent.document(_id)
                docDelete.delete().addOnSuccessListener {
                    Log.d("TAG", "Đã xóa tài liệu thành công!")
                    try {
                        var docDelete = usersCollection.document(_id)
                        docDelete.delete().addOnSuccessListener {
                            Log.d("TAG", "Đã xóa tài liệu thành công!")
                            val childIdToDelete = _id
                            // Sử dụng phương thức removeValue() trên DatabaseReference để xóa phần tử
                            dbRef.child(childIdToDelete).removeValue()
                        }
                            .addOnFailureListener { error ->
                                Log.e("TAG", "Lỗi khi xóa tài liệu: ", error)
                            }

                    } catch (e: Exception) {

                    }

                }
                    .addOnFailureListener { error ->
                        Log.e("TAG", "Lỗi khi xóa tài liệu: ", error)
                    }
                val list = _student.value?.toMutableList() ?: mutableListOf()
                val removedSt =
                    list.find { it._id == _id } // Tìm kiếm đối tượng có _id trùng với _id trong danh sách
                if (removedSt != null) { // Nếu tìm thấy đối tượng cần xóa
                    list.remove(removedSt) // Xóa đối tượng khỏi danh sách
                    _student.value = list // Cập nhật lại giá trị của _rooms.value
                }
            } catch (e: Exception) {

            }

        }

    }


    fun getStudentById(studentId: String): StudentInfor? {
        return _student.value?.find { student -> student._id == studentId }
    }


}