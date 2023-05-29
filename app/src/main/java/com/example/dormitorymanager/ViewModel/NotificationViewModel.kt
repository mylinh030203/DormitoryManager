package com.example.dormitorymanager.ViewModel

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dormitorymanager.MainActivity
import com.example.dormitorymanager.Model.Notification
import com.example.dormitorymanager.Model.Rooms
import com.example.dormitorymanager.Model.StudentInfor
import com.example.dormitorymanager.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationViewModel(application: Application): AndroidViewModel(application) {
    private val db = Firebase.firestore
    val auth = FirebaseAuth.getInstance()
    val dbRef = FirebaseDatabase.getInstance().getReference("User")
    val collectionStudent = db.collection("StudentInfo")
    val collectionRegisterRoom = db.collection("DetailRoomRegister")
    val collectionNotification = db.collection("Notifications")
    private val usersCollection = db.collection("Users")

    val _studentByRoomID = MutableLiveData<List<StudentInfor>>()
    val studentByRoomID: LiveData<List<StudentInfor>>
        get() = _studentByRoomID

    val _notification = MutableLiveData<List<Notification>>()
    val notification : LiveData<List<Notification>>
        get() = _notification

    fun getStudentInRoom(roomID: String, callback: (List<String>) -> Unit) {
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

                            // Gọi callback với danh sách ID của sinh viên
                            val studentIdList = studentList.map { it._id }
                            callback(studentIdList)
                        }
                        .addOnFailureListener { }

                } else {
                    _studentByRoomID.value = emptyList()
                    callback(emptyList())
                }
            }
            .addOnFailureListener { }

        // Không trả về giá trị từ hàm này nữa
    }

    fun sendNotificationToStudents(id: String, recipients: List<String>, title: String, message: String, timestamp: Timestamp) {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            try {
                val Notification = Notification(id = id,title = title, message = message, id_user = recipients, timestamp = timestamp)
                val notification = hashMapOf(
                    "id" to id,
                    "id_user" to recipients,
                    "message" to message,
                    "timestamp" to timestamp,
                    "title" to title
                )
                collectionNotification.document(id).set(notification).addOnSuccessListener{
                    val list = _notification.value?.toMutableList() ?: mutableListOf()
                    list.add(Notification)
                    _notification.value = list
                    Log.d("TAG", "Đã thêm tài liệu mới với ID: ${it}")
                }.addOnFailureListener { error ->
                    Log.e("TAG", "Lỗi khi thêm tài liệu: ", error) }

            }catch (e: Exception){

            }

        }

    }

    fun selectNotification(): MutableList<Notification>{
        collectionNotification.get().addOnSuccessListener { snapshot ->
            val notificationList = mutableListOf<Notification>()
            for (doc in snapshot!!) {
                val notification = doc.toObject(Notification::class.java)
                notification.id = doc.id
                notificationList.add(notification)
            }
            _notification.value = notificationList
        }.addOnFailureListener { exception ->
            Log.w("TAG", "Error getting documents: ", exception)
        }

        return _notification.value?.toMutableList() ?: mutableListOf()
        //chuyển từ multablelivedata sang multablelist
    }

}