package com.example.sikjipsa

import android.graphics.Bitmap
import android.net.Uri

//이 DB클래스에 사용자 정보가 들어갈 것임
data class User(
    var name: String,
    var email: String,
    var uId: String,
    var nickname: String,
//    var pic: String
)


//{
//    constructor(): this("","","","")
//}
