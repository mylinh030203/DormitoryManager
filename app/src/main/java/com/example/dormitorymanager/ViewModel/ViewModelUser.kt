package com.example.dormitorymanager.ViewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.dormitorymanager.MainActivity
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
    // CoroutineScope để quản lý các coroutine trong ViewModel
//        private val viewModelScope = CoroutineScope(Dispatchers.Default)

    fun RegisterUsers(email: String, name: String, password: String, roleID: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //thêm user vào Realtime db
                var id: String = task.result?.user?.uid.toString()
                if (id != null) {
                    val userData = Users(id, email, name, password, roleID)
                    dbRef.child(id).setValue(userData)
                    _userCreated.value = true
                }

            } else {
                Log.e("TAG", "Failes to create user", task.exception)
                _userCreated.value = false
            }
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (childSnapshot in dataSnapshot.children) {
                        val user = childSnapshot.getValue(Users::class.java)
                        user?.let {
                            usersCollection.document(user._id).set(user)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

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
                    getApplication(), "Authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun getCurrentUser(): String {
        _user.value = auth.currentUser
        return _user.value?.email.toString()
        //        return this.user.toString()
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
}