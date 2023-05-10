package com.example.dormitorymanager.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dormitorymanager.Model.Rooms
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ViewModelRoom : ViewModel() {
    private val db = Firebase.firestore
    val collectionRoom = db.collection("Rooms")
    val _rooms = MutableLiveData<List<Rooms>>()
    val rooms: LiveData<List<Rooms>>
        get() = _rooms

    val _roomsIsEmpty = MutableLiveData<List<Rooms>>()
    val roomsIsEmpty: LiveData<List<Rooms>>
        get() = _roomsIsEmpty

    private val _updateResult = MutableLiveData<Boolean>()
    val updateResult: LiveData<Boolean>
        get() = _updateResult

    fun setUpdateResult(result: Boolean) {
        _updateResult.value = result
    }

    private val _enoughRooms = MutableLiveData<Int>()
    val enoughRooms: LiveData<Int> = _enoughRooms

    private val _nearEnoughRooms = MutableLiveData<Int>()
    val nearEnoughRooms: LiveData<Int> = _nearEnoughRooms

    private val _lackRooms = MutableLiveData<Int>()
    val lackRooms: LiveData<Int> = _lackRooms


   fun getRoomData() {
            collectionRoom.get()
                .addOnSuccessListener { documents ->
                    var enoughCount = 0
                    var nearEnoughCount = 0
                    var lackCount = 0
                    for (doc in documents) {
                        val currentSt = doc.getString("status") ?: "0"
                        val currentStudents = currentSt.toInt()
                        val maxSt = doc.getString("beds") ?: "0"
                        val maxStudents = maxSt.toInt()
                        when {
                            currentStudents >= maxStudents -> enoughCount++
                            currentStudents >= maxStudents * 0.8 -> nearEnoughCount++
                            else -> lackCount++
                        }
                    }
                    _enoughRooms.value = enoughCount
                    _nearEnoughRooms.value = nearEnoughCount
                    _lackRooms.value = lackCount
                }
    }

    fun getRoom(): MutableList<Rooms> {
        //lấy hết document trong colection room chuyển nó thành đối tượng Rooms rồi thêm vào list, sau đó cập nhật cho multablelivedata
        collectionRoom.get().addOnSuccessListener { snapshot ->
            val roomsList = mutableListOf<Rooms>()
            for (doc in snapshot!!) {
                val room = doc.toObject(Rooms::class.java)
                room._id = doc.id
                roomsList.add(room)
            }
            _rooms.value = roomsList
        }.addOnFailureListener { exception ->
            Log.w("TAG", "Error getting documents: ", exception)
        }

        return _rooms.value?.toMutableList() ?: mutableListOf()
        //chuyển từ multablelivedata sang multablelist
    }
//    fun getRoom():MutableList<Rooms>{
//    val roomsList = mutableListOf<Rooms>()
//    return roomsList
    //dùng để test
//    }

    fun getRoomName(id:String, callback: (String?) -> Unit){
        collectionRoom.document(id).get().addOnSuccessListener {
            doccumentSnapshot->
            if(doccumentSnapshot.exists()){
                val roomName = doccumentSnapshot.getString("name")
                callback(roomName)
            }else{
                callback(null)
                Log.e("roomname","null")
            }
        }.addOnFailureListener {
            exception->callback(null)
            Log.e("roomName", "exception")
        }
    }


    fun addRoom( beds:String, description:String, location: String, name:String, prices:Long, status: String) {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            try {
                val count = countDoc()+1
                val _id = (count).toString()
                val room = Rooms(_id, beds, description, location, name, prices, status)
                val roomDoc = hashMapOf(
                    "_id" to _id,
                    "beds" to beds,
                    "description" to description,
                    "location" to location,
                    "name" to name,
                    "price" to prices,
                    "status" to status
                )
                collectionRoom.document(_id)
                    .set(roomDoc).addOnSuccessListener {
                    val list = _rooms.value?.toMutableList() ?: mutableListOf()
                    list.add(room)
                    _rooms.value = list
                    Log.d("TAG", "Đã thêm tài liệu mới với ID: ${it}")
                }.addOnFailureListener { error ->
                    Log.e("TAG", "Lỗi khi thêm tài liệu: ", error) }
            }
            catch (e: Exception) {
                // Xử lý lỗi nếu có
            }
        }
    }

    fun deleteRoom(_id: String) {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            try {
                var docDelete = collectionRoom.document(_id)
                docDelete.delete() .addOnSuccessListener {
                    Log.d("TAG", "Đã xóa tài liệu thành công!")
                }
                    .addOnFailureListener { error ->
                        Log.e("TAG", "Lỗi khi xóa tài liệu: ", error)
                    }
                val list = _rooms.value?.toMutableList() ?: mutableListOf()
                val removedRoom = list.find { it._id == _id } // Tìm kiếm đối tượng có _id trùng với _id trong danh sách
                if (removedRoom != null) { // Nếu tìm thấy đối tượng cần xóa
                    list.remove(removedRoom) // Xóa đối tượng khỏi danh sách
                    _rooms.value = list // Cập nhật lại giá trị của _rooms.value
                }
            }catch (e: Exception){

            }

        }

    }

    fun updateRoom(
        _id: String,
        beds:String, description:String, location: String, name:String, prices:Long, status: String) {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            try {
                val roomDoc = hashMapOf(
                    "_id" to _id,
                    "beds" to beds,
                    "description" to description,
                    "location" to location,
                    "name" to name,
                    "price" to prices,
                    "status" to status
                )
                collectionRoom.document(_id).update(roomDoc as Map<String, Any>).addOnSuccessListener {
                    _updateResult.value = true
                    Log.d("TAG", "Đã cập nhật trường thành công!")

                }.addOnFailureListener { error ->
                    _updateResult.value = false
                    Log.e("TAG", "Lỗi khi cập nhật trường: ", error)

                }
                val rooms = Rooms(_id, beds, description, location, name, prices, status)
                val list = _rooms.value?.toMutableList() ?: mutableListOf()
                val room =  list.find { it._id == _id }
                val index = _rooms.value?.indexOfFirst { it._id == room?._id } ?: return@launch
                list[index] = rooms
                _rooms.value = list
            }catch (e: Exception){

            }
        }
    }

    suspend fun countDoc(): Int {
        val querySnapshot = collectionRoom.get().await()
        return querySnapshot.size()
    }


    fun roomIsEmpty():MutableList<Rooms>{
        collectionRoom
            .get().addOnSuccessListener {
                val roomsListEmp = mutableListOf<Rooms>()
                for(document in it!!){
                    val statusStr = document.getString("status") // lấy giá trị trường "status" dưới dạng String
                    val bedStr = document.getString("beds") // lấy giá trị trường "bed" dưới dạng String

                    // Chuyển đổi giá trị của "status" và "bed" thành số
                    val statusNum = statusStr?.trim()?.toInt()?:0
                    val bedNum = bedStr?.trim()?.toInt()?:0
                    Log.e("roomcompare", statusNum.toString())
                    if (statusNum < bedNum) {
                        val room = document.toObject(Rooms::class.java)
                        room._id = document.id
                        roomsListEmp.add(room)
                        Log.w("roomcompare", "ok")
                    }
                }
                _roomsIsEmpty.value = roomsListEmp
                Log.w("roomisempty", "ok")
            }.addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }
        return _roomsIsEmpty.value?.toMutableList() ?: mutableListOf()
        //chuyển từ multablelivedata sang multablelist
    }
}