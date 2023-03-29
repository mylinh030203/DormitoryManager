package com.example.dormitorymanager.Model

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

data class Users (var _id: String ="", var phoneNumber: String = "", var Name:String = "", var Password:String = "", var Role_id:String = "2")
