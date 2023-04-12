package com.example.dormitorymanager.Model

import android.widget.ImageView

data class StudentInfor(var _id:String, var fullname:String, var phone : String, var gender:String,
var idStudent:String, var classStd: String, var avatar : String){
    constructor() : this("", "", "021346688788", "female", "", "", "")
}