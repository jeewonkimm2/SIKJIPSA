package com.example.sikjipsa.model

//posting contents data class


data class ContentDTO(
    var explain : String? = null, // Content description
    var imageUrl : String? = null, // Manage the address of the image
    var uid : String? = null, //uploaded user id
    var userId : String? = null, //the email of the uploaded user
    var timestamp : Long? = null, // time when the user uploaded the content
    var favoriteCount : Int = 0, // the number of likes
    var nickname : String? = null, // user's nickname
    var favorites : MutableMap<String, Boolean> = HashMap()){ //Prevents duplicate likes
    // Data classes for comment
    data class Comment(var uid : String? = null, // managing uid
                       var userId : String? = null, // managing e-mail
                       var comment : String? = null, // managing comments
                       var timestamp: Long? = null, // time when the user uploaded the comment
                        var nickname: String? = null) // managing nickname

}
