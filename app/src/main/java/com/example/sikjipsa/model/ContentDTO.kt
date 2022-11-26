package com.example.sikjipsa.model

//게시글 컨텐츠


data class ContentDTO(
    var explain : String? = null, //컨텐츠의 설명관리
    var imageUrl : String? = null, //이미지의 주소관리
    var uid : String? = null, //어느 유저가 올린건지
    var userId : String? = null, //올린 이미지의 유저
    var timestamp : Long? = null, //몇시 몇분에 컨텐츠를 올렸는지
    var favoriteCount : Int = 0, //좋아요의 갯수
    var nickname : String? = null,
    var favorites : MutableMap<String, Boolean> = HashMap()){ //중복 좋아요 방지
    //댓글 관리용
    data class Comment(var uid : String? = null, //uid를 관리하는
                       var userId : String? = null, //이메일을 관리하는 유저 아이디
                       var comment : String? = null, //코멘트를 관리해주는
                       var timestamp: Long? = null) //몇시 몇분에 코멘트를 달았는지

}
