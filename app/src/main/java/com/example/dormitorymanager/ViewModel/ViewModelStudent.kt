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
import java.io.File
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

    val _CountstudentByRoomID = MutableLiveData<Int>()
    val CountstudentByRoomID: LiveData<Int>
        get() = _CountstudentByRoomID

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
//student._id = doc.id để lưu trữ ID của tài liệu trong đối tượng StudentInfor. Việc này cho phép chúng ta lưu trữ thông tin của sinh viên liên quan đến tài liệu cụ thể đó, chẳng hạn như ID của sinh viên.
//Nếu không sử dụng câu lệnh này, thuộc tính _id của đối tượng StudentInfor sẽ không được gán giá trị, gây ra lỗi khi truy xuất thông tin của sinh viên hoặc thực hiện các thao tác khác trên cơ sở dữ liệu.
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
    interface ImageUriCallback {
        fun onImageUriReceived(imageUri: Uri?)
        fun onError(message: String)
    }

    fun getAvatar(id:String, callback: (String?)->Unit){
        collectionStudent.document(id).get().addOnSuccessListener {
                doccumentSnapshot->
            if(doccumentSnapshot.exists()){
                val avatar = doccumentSnapshot.getString("avatar")
                callback(avatar)
            }else{
                callback(null)
                Log.e("avatar","null")
            }
        }.addOnFailureListener {
                exception->callback(null)
            Log.e("avatar", "exception")
        }
    }

    fun getUriImage(id:String, callback: ImageUriCallback){
        collectionStudent.document(id).get().addOnCompleteListener  {
            if(it.isSuccessful){
                val document = it.result
                if(document!=null && document.exists()){
                    val imagePath = document.getString("avatar")
                    if(!imagePath.isNullOrEmpty()){
//                        var imageUri = Uri.parse(imagePath)
//                        callback(imageUri)
                        val storageRef = FirebaseStorage.getInstance().reference
                        val tempFileName = "${id}.jpeg"
                        val tempFile = File.createTempFile(tempFileName, "jpeg")

                        storageRef.child(imagePath).getFile(tempFile)
                            .addOnSuccessListener {
                                val imageUri = tempFile.toUri()
                                tempFile.delete()
                                callback.onImageUriReceived(imageUri)
                            }
                            .addOnFailureListener { exception ->
                                callback.onError("Error downloading image: ${exception.message}")
                            }
                    } else {
                        callback.onError("Image path is empty")
                    }
                } else {
                    callback.onError("Document does not exist")
                }
            } else {
                callback.onError("Error getting document: ${it.exception?.message}")
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
                    val ViewModelUser = ViewModelUser(Application())
                    ViewModelUser.deleteUser(_id)
                    val viewmodeldetail = ViewModelDetailRR()
                    viewmodeldetail.deleteDetail(_id)
                    Log.d("TAG", "Đã xóa tài liệu thành công!")
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

    fun countStudentInRoom(roomID: String): Int? {
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
                            var count = 0
                            for (studentdoc in studentDoc) {
                                val student = studentdoc.toObject(StudentInfor::class.java)
                                count++
                                studentList.add(student)
                            }
                            _CountstudentByRoomID.value = count
                        }
                        .addOnFailureListener { }
                } else
                    _CountstudentByRoomID.value = 0
            }.addOnFailureListener {

            }
        val count = _CountstudentByRoomID.value
        return count
    }
    fun getStudentById(studentId: String): StudentInfor? {
        return _student.value?.find { student -> student._id == studentId }
    }


}