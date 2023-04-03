package com.example.dormitorymanager.Model

data class Rooms(var _id:String,var beds: String,
                var description:String, var location: String
                ,var name:String,
                 var prices:Double,
                 var status: String) {

    // Hàm khởi tạo không đối số
    constructor() : this("", "", "8", "1000000.0", "", 100000.0, "")
}