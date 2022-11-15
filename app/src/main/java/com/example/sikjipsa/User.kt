package com.example.sikjipsa
//이 DB클래스에 사용자 정보가 들어갈 것임
data class User(
    var name: String,
    var email: String,
    var uId: String

){
    constructor(): this("","","")
}
