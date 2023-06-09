package com.example.dormitorymanager.ViewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.dormitorymanager.MainActivity
import com.example.dormitorymanager.Model.StudentInfor
import com.example.dormitorymanager.Model.Users
import com.example.dormitorymanager.View.RegisterActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ViewModelUser(application: Application) : AndroidViewModel(application) {

    val dbRef = FirebaseDatabase.getInstance().getReference("User")
    var auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val usersCollection = db.collection("Users")
    private val _userCreated = MutableLiveData<Boolean>()
    val userCreated: LiveData<Boolean>
        get() = _userCreated
    private val _user = MutableLiveData<FirebaseUser?>()
    val user: MutableLiveData<FirebaseUser?> get() = _user

    val _users = MutableLiveData<List<Users>>()
    val users: LiveData<List<Users>>
        get() = _users

    fun getUser(): MutableList<Users> {
        //lấy hết document trong colection students chuyển nó thành đối tượng StudentInfor rồi thêm vào list, sau đó cập nhật cho multablelivedata
        usersCollection.get().addOnSuccessListener { snapshot ->
            val usersList = mutableListOf<Users>()
            for (doc in snapshot!!) {
                val user = doc.toObject(Users::class.java)
                user._id = doc.id
                usersList.add(user)
            }
            _users.value = usersList
        }.addOnFailureListener { exception ->
            Log.w("TAG", "Error getting documents: ", exception)
        }

        return _users.value?.toMutableList() ?: mutableListOf()
        //chuyển từ multablelivedata sang multablelist
    }

    fun RegisterUsers(
        email: String, name: String, password: String, roleID: String, gender: String
    ) {
        val viewModelStudent = ViewModelStudent()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //thêm user vào Realtime db
                var id: String = task.result?.user?.uid.toString()
                if (id != null) {
                    val userData = Users(id, email, name, password, roleID)
                    val userDoc = hashMapOf(
                        "_id" to id,
                        "email" to email,
                        "name" to name,
                        "password" to password,
                        "role_id" to roleID
                    )
                    usersCollection.document(id).set(userDoc).addOnSuccessListener {
                        val list = _users.value?.toMutableList() ?: mutableListOf()
                        list.add(userData)
                        _users.value = list
                        Log.d("TAG", "Đã thêm tài liệu mới với ID: ${it}")
                    }.addOnFailureListener { error ->
                        Log.e("TAG", "Lỗi khi thêm tài liệu: ", error)
                    }
                    dbRef.child(id).setValue(userData)
                    //
                    _userCreated.value = true
                    if(gender.equals("Male"))
                        viewModelStudent.addStudent(id, name, "", gender, "", "", "https://firebasestorage.googleapis.com/v0/b/dormitory-manager-df9f5.appspot.com/o/images%2F06h5kxLhKaXYeB3e66PpuxMtXfs2?alt=media&token=ecebff4d-e34f-40c3-aeec-eb4783a3cd6f")
                    else
                        viewModelStudent.addStudent(id, name, "", gender, "", "", "https://firebasestorage.googleapis.com/v0/b/dormitory-manager-df9f5.appspot.com/o/images%2F0jbTJPXe6FUsa5tMZminvbUeAl13?alt=media&token=556b3498-c037-472e-8c29-9004ecd553ba")
                }

            } else {
                Log.e("TAG", "Failes to create user", task.exception)
                _userCreated.value = false
            }
//            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    for (childSnapshot in dataSnapshot.children) {
//                        val user = childSnapshot.getValue(Users::class.java)
//                        user?.let {
//                            usersCollection.document(user._id).set(user)
//                                .addOnSuccessListener {
//                                    val list = _users.value?.toMutableList() ?: mutableListOf()
//                                    list.add(user)
//                                    _users.value = list
//                                    Log.d("TAG", "Đã thêm tài liệu mới với ID: ${it}")
//                                }.addOnFailureListener { error ->
//                                    Log.e("TAG", "Lỗi khi thêm tài liệu: ", error) }
//                        }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//
//                }
//            })

        }

    }


    fun LoginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _user.value = auth.currentUser
                // Sign in success, update UI with the signed-in user's information
                Log.d("TAG", "signInWithEmail:success")
                Log.e("User current2: ", this.getCurrentUser())
                //lưu thông tin đang nhập
                val sharedPreferences =
                    getApplication<Application>().getSharedPreferences("User", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("email", email)
                editor.putString("password", password)
                editor.putBoolean("isLoggedIn", true)
                editor.apply()
                //
            } else {
                // If sign in fails, display a message to the user.
                Log.w("TAG", "signInWithEmail:failure", task.exception)
                Toast.makeText(
                    getApplication(), "Authentication failed.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun getCurrentUser(): String {
        _user.value = auth.currentUser
        Log.w("TAG", _user.value?.email.toString())
        return _user.value?.uid.toString()
        //        return this.user.toString()
    }

    fun getEmailCurrent(): String {
        _user.value = auth.currentUser
        return _user.value?.email.toString()
    }

    fun Logout() {
        auth.signOut()
    }

    //xem người dùng đã đăng nhập chưa
    fun checkLogin(): Boolean {
        _user.value = auth.currentUser
        Log.e("User current: ", getCurrentUser())
        return auth.currentUser != null;
    }

    fun checkRoleID(callback: (String) -> Unit) {
        _user.value = auth.currentUser
        val id = _user.value?.uid.toString()
        usersCollection.document(id).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documentSnapshot = task.result
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Lấy giá trị của trường roleid từ DocumentSnapshot
                    val roleID = documentSnapshot.getString("role_id")
                    // Kiểm tra nếu roleID không null thì sử dụng giá trị này
                    if (roleID != null) {
                        callback.invoke(roleID) // Gọi callback với giá trị roleID
                    } else {
                        Log.e("role", "không lấy được role")
                    }
                } else {
                    Log.e("role", "document không tồn tại")
                    // Xử lý khi document không tồn tại
                }
            } else {
                // Xử lý khi có lỗi xảy ra
            }
        }
    }

    fun deleteUser(_id: String) {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            try {
                var docDelete = usersCollection.document(_id)
                docDelete.delete().addOnSuccessListener {
                    Log.d("TAG", "Đã xóa tài liệu thành công!")
                    val childIdToDelete = _id

                    // Sử dụng phương thức removeValue() trên DatabaseReference để xóa phần tử
                    dbRef.child(childIdToDelete).removeValue()
                }.addOnFailureListener { error ->
                    Log.e("TAG", "Lỗi khi xóa tài liệu: ", error)
                }
                val list = _users.value?.toMutableList() ?: mutableListOf()
                val removedUser =
                    list.find { it._id == _id } // Tìm kiếm đối tượng có _id trùng với _id trong danh sách
                if (removedUser != null) { // Nếu tìm thấy đối tượng cần xóa
                    list.remove(removedUser) // Xóa đối tượng khỏi danh sách
                    _users.value = list // Cập nhật lại giá trị của _users.value
                }
            } catch (e: Exception) {

            }

        }
    }

    // Hàm checkAdmin
    fun checkAdmin(callback: (Boolean) -> Unit) {
        checkRoleID { roleID ->
            val isAdmin = roleID == "1"
            Log.e("login", roleID)
            callback.invoke(isAdmin) // Gọi callback với giá trị isAdmin
        }
    }

    fun getIDDoccumentUser(value: String, callback: (String?) -> Unit) {
        usersCollection.whereEqualTo("email", value).get().addOnSuccessListener { UserDoc ->
            if (!UserDoc.isEmpty) {
                val documentID = UserDoc.documents[0].getString("_id")
                Log.e("iduser", documentID.toString())
                Log.e("email", value)
                callback(documentID)

            } else {
                Log.e("iduser", "")

                Log.e("email", value)
            }
        }
    }


}