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
import com.example.dormitorymanager.Model.StudentInfor
import com.example.dormitorymanager.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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

    fun sendNotificationToStudents(recipients: List<String>, title: String, message: String) {
        val notification = Notification(title = title, message = message, id_user = recipients)
        collectionNotification
            .add(notification)
            .addOnSuccessListener { documentReference ->
                val notificationId = documentReference.id
                recipients.forEach { recipientId ->
                    // Tạo intent để mở ứng dụng khi người dùng nhấn vào thông báo
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://myapp.com/notification/$notificationId"))
                    val pendingIntent = PendingIntent.getActivity(getApplication(), 0, intent, 0)

                    // Xây dựng thông báo
                    val notificationBuilder = NotificationCompat.Builder(getApplication(), "channel_id")
                        .setSmallIcon(R.drawable.ic_notifications)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

                    // Gửi thông báo
                    val notificationManager = NotificationManagerCompat.from(getApplication())
                    val notificationIdInt = notificationId.hashCode()
                    notificationManager.notify(notificationIdInt, notificationBuilder.build())
                }
            }
            .addOnFailureListener { error ->
                Log.e("notification", "Error sending notification", error)
            }
    }
}