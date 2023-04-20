package com.example.dormitorymanager.Model

import java.util.Date

data class DetailRoomRegister(var _id:String, val room_id:String, val user_id:String, var registerDate: String, var expirationDate: String,var status:String, var price:Long){

    // Hàm khởi tạo không đối số
    constructor() : this("", "", "", "", "", "", 10000)
}