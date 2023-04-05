package com.example.dormitorymanager.ViewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dormitorymanager.Model.Users
import com.example.dormitorymanager.View.RegisterActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
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
import java.util.concurrent.TimeUnit

class ViewModelUser(application: Application): AndroidViewModel(application) {

    val dbRef = FirebaseDatabase.getInstance().getReference("User")
    val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val usersCollection = db.collection("Users")
    var user = auth.currentUser
    private val _userCreated = MutableLiveData<Boolean>()
    val userCreated: LiveData<Boolean>
        get() = _userCreated


    fun RegisterUsers(email: String, name: String, password: String, roleID:String){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task->
            if(task.isSuccessful){
                //thêm user vào Realtime db
                var id : String = task.result?.user?.uid.toString()
                if(id!=null){
                    val userData = Users(id,email, name, password, roleID)
                    dbRef.child(id).setValue(userData)
                    _userCreated.value = true
                }

            }else{
                Log.e("TAG","Failes to create user", task.exception)
                _userCreated.value = false
            }
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (childSnapshot in dataSnapshot.children) {
                        val user = childSnapshot.getValue(Users::class.java)
                        user?.let {
//                                val firestoreUser = hashMapOf(
//                                    "_id" to user._id,
//                                    "email" to user.email,
//                                    "name" to user.Name,
//                                    "password" to user.Password,
//                                    "role_id" to user.Role_id)
                            // Thêm dữ liệu vào Firestore collection
                            usersCollection.document(user._id).set(user)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        }

    }
    fun LoginUser(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{ task->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("TAG", "signInWithEmail:success")
                //lưu thông tin đang nhập
                val sharedPreferences = getApplication<Application>().getSharedPreferences("User", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("email", email)
                editor.putString("password", password)
                editor.putBoolean("isLoggedIn", true)
                editor.apply()
                //
            } else {
                // If sign in fails, display a message to the user.
                Log.w("TAG", "signInWithEmail:failure", task.exception)
                Toast.makeText(getApplication(), "Authentication failed.",
                    Toast.LENGTH_SHORT).show()
            }
        }

    }
    fun Logout(){
        Firebase.auth.signOut()
    }
//xem người dùng đã đăng nhập chưa
    fun checkLogin():Boolean{
        val sharedPreferences = getApplication<Application>().getSharedPreferences("User", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            // Người dùng đã đăng nhập, thực hiện các tác vụ liên quan đến đăng nhập ở đây
//            val email = sharedPreferences.getString("email", "")
//            val password = sharedPreferences.getString("password", "")
//            auth.signInWithEmailAndPassword(email.toString(),password.toString())
            return true
        }else{
            return false
        }
    }
}