package com.example.dormitorymanager.ViewModel

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dormitorymanager.Model.DetailRoomRegister
import com.example.dormitorymanager.Model.Rooms
import com.example.dormitorymanager.Model.StudentInfor
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date

class ViewModelDetailRR : ViewModel() {
    private val db = Firebase.firestore
    private val collectionDetail = db.collection("DetailRoomRegister")
    private val collectionStudent = db.collection("StudentInfo")
    private val usersCollection = db.collection("Users")
    private val collectionRoom = db.collection("Rooms")

    val _detail = MutableLiveData<List<DetailRoomRegister>>()
    val details: LiveData<List<DetailRoomRegister>>
        get() = _detail

    private val _detailRR = MutableLiveData<DetailRoomRegister?>()
    val detailRR: LiveData<DetailRoomRegister?>
        get() = _detailRR

    private val _updateResult = MutableLiveData<Boolean>()
    val updateResult: LiveData<Boolean>
        get() = _updateResult


    fun getDetail(): MutableList<DetailRoomRegister> {
        //lấy hết document trong colection details chuyển nó thành đối tượng DetailRoomRegister rồi thêm vào list, sau đó cập nhật cho multablelivedata
        collectionDetail.get().addOnSuccessListener { snapshot ->
            val detailList = mutableListOf<DetailRoomRegister>()
            for (doc in snapshot!!) {
                val detail = doc.toObject(DetailRoomRegister::class.java)
                detail._id = doc.id
                detailList.add(detail)
            }
            _detail.value = detailList
        }.addOnFailureListener { exception ->
            Log.w("TAG", "Error getting documents: ", exception)
        }

        return _detail.value?.toMutableList() ?: mutableListOf()
        //chuyển từ multablelivedata sang multablelist
    }
    suspend fun getRoomID(id: String, fieldName: String): Any? {
        val document = collectionDetail.document(id).get().await()
        return document[fieldName]
    }

    fun getDate(dateStringRegistered: String, dateStringExpired: String, callback : (Long?) -> Unit){

                val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                val dateRegistered: Date = dateFormat.parse(dateStringRegistered)
                val dateExpired: Date = dateFormat.parse(dateStringExpired)
                // Tính số milliseconds giữa ngày đăng ký và ngày hết hạn
                val millisecondsDiff: Long = dateExpired.time - dateRegistered.time
                // Chuyển đổi milliseconds thành số ngày
                val daysDiff: Long = millisecondsDiff / (24 * 60 * 60 * 1000)
                callback(daysDiff)

    }

    fun RegisterRoom(
        room_id: String,
        user_id: String,
        registerDate: String,
        expirationDate: String,
        status: String,
        price: Long
    ) {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            try {
//                val count = countDoc()+1
                val _id = user_id
                val detail = DetailRoomRegister(
                    _id,
                    room_id,
                    user_id,
                    registerDate,
                    expirationDate,
                    status,
                    price
                )
                val detailDoc = hashMapOf(
                    "_id" to _id,
                    "room_id" to room_id,
                    "user_id" to user_id,
                    "registerDate" to registerDate,
                    "expirationDate" to expirationDate,
                    "status" to status,
                    "price" to price
                )
                collectionDetail.document(_id)
                    .set(detailDoc).addOnSuccessListener {
                        val list = _detail.value?.toMutableList() ?: mutableListOf()
                        list.add(detail)
                        _detail.value = list
                        Log.d("TAG", "Đã thêm tài liệu mới với ID: ${it}")
                        if (status.equals("Đã duyệt")) {
                            collectionRoom.document(room_id).get().addOnSuccessListener {
                                val currentStString = it.getString("status") ?: "0"
                                val curentSt = currentStString.toInt()

                                // Cộng thêm 1 vào giá trị của trường current_students
                                val newCurrentStudents = curentSt + 1

                                collectionRoom.document(room_id)
                                    .update("status", newCurrentStudents.toString())
                                    .addOnSuccessListener {
                                        Log.d("statusRoom", "Update status Room successfully")
                                    }.addOnFailureListener { e ->
                                        Log.e("statusRoom", "Update status Room failed", e)
                                    }
                            }.addOnFailureListener { e ->
                                Log.e("statusRoom", "Get document failed", e)
                            }
                        }

                    }.addOnFailureListener { error ->
                        Log.e("TAG", "Lỗi khi thêm tài liệu: ", error)
                    }
            } catch (e: Exception) {
                // Xử lý lỗi nếu có
            }
        }

    }

    //    fun getRoomID(id: String, callback: (String?) -> Unit) {
//        collectionDetail.document(id).get().addOnSuccessListener { doccumentSnapshot ->
//            if (doccumentSnapshot.exists()) {
//                val roomID = doccumentSnapshot.getString("room_id")
//                callback(roomID)
//            } else {
//                callback(null)
//                Log.e("roomID", "null")
//            }
//        }.addOnFailureListener { exception ->
//            callback(null)
//            Log.e("roomID", "exception")
//        }
//    }



    fun deleteDetail(_id: String) {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            val room_id = getRoomID(_id, "room_id").toString()
            try {
                var docDelete = collectionDetail.document(_id)
                docDelete.delete().addOnSuccessListener {

                    if (room_id != null) {
                        collectionRoom.document(room_id).get().addOnSuccessListener {
                            Log.e("room_id", room_id)
                            val currentStString = it.getString("status") ?: "0"
                            val curentSt = currentStString.toInt()

                            // Cộng thêm 1 vào giá trị của trường current_students
                            val newCurrentStudents = curentSt - 1

                            collectionRoom.document(room_id)
                                .update("status", newCurrentStudents.toString())
                                .addOnSuccessListener {
                                    Log.d("statusRoom", "Update status Room successfully")
                                }.addOnFailureListener { e ->
                                    Log.e("statusRoom", "Update status Room failed", e)
                                }
                        }.addOnFailureListener { e ->
                            Log.e("statusRoom", "Get document failed", e)
                        }
                    }
                    Log.d("TAG", "Đã xóa tài liệu thành công!")
                }
                .addOnFailureListener { error ->
                    Log.e("TAG", "Lỗi khi xóa tài liệu: ", error)
                }
            val list = _detail.value?.toMutableList() ?: mutableListOf()
            val removedDetail =
                list.find { it._id == _id } // Tìm kiếm đối tượng có _id trùng với _id trong danh sách
            if (removedDetail != null) { // Nếu tìm thấy đối tượng cần xóa
                list.remove(removedDetail) // Xóa đối tượng khỏi danh sách
                _detail.value = list // Cập nhật lại giá trị của _rooms.value
            }
        } catch (e: Exception) {

        }

    }

}


fun updateDetail(
    _id: String,
    room_id: String,
    user_id: String,
    registerDate: String,
    expirationDate: String,
    status: String,
    price: Long
) {
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    coroutineScope.launch {
        try {
            val DetailDoc = hashMapOf(
                "_id" to _id,
                "room_id" to room_id,
                "user_id" to user_id,
                "registerDate" to registerDate,
                "expirationDate" to expirationDate,
                "status" to status,
                "price" to price
            )
            collectionDetail.document(_id).update(DetailDoc as Map<String, Any>)
                .addOnSuccessListener {
                    _updateResult.value = true
                    Log.d("TAG", "Đã cập nhật trường thành công!")

                    if (status.equals("Đã duyệt")) {
                        collectionRoom.document(room_id).get().addOnSuccessListener {
                            val currentStString = it.getString("status") ?: "0"
                            val curentSt = currentStString.trim().toInt()

                            // Cộng thêm 1 vào giá trị của trường current_students
                            val newCurrentStudents = curentSt + 1

                            collectionRoom.document(room_id)
                                .update("status", newCurrentStudents.toString())
                                .addOnSuccessListener {
                                    Log.d("statusRoom", "Update status Room successfully")
                                }.addOnFailureListener { e ->
                                    Log.e("statusRoom", "Update status Room failed", e)
                                }
                        }.addOnFailureListener { e ->
                            Log.e("statusRoom", "Get document failed", e)
                        }
                    }

                }.addOnFailureListener { error ->
                    _updateResult.value = false
                    Log.e("TAG", "Lỗi khi cập nhật trường: ", error)

                }
            val detail = DetailRoomRegister(
                _id,
                room_id,
                user_id,
                registerDate,
                expirationDate,
                status,
                price
            )
            val list = _detail.value?.toMutableList() ?: mutableListOf()
            val details = list.find { it._id == _id }
            val index = _detail.value?.indexOfFirst { it._id == details?._id } ?: return@launch
            list[index] = detail
            _detail.value = list
        } catch (e: Exception) {

        }
    }
}


fun getDeltail(_id: String) {
    collectionDetail.document(_id).get().addOnSuccessListener { documentSnapshot ->
        if (documentSnapshot.exists()) {
            val detail = documentSnapshot.toObject(DetailRoomRegister::class.java)
            _detailRR.value = detail
        } else {
            Log.d("getDetail", "Không tìm thấy tài liệu")
        }
    }.addOnFailureListener { exception ->
        Log.w("getDetail", "Lỗi khi lấy tài liệu", exception)
    }
}
}