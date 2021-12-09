package com.cookandroid.google_login_1116_2nd.navigation.model

data class ContentDTO(
  var explain:String?=null,//설명 관리
  var imageUri:String?=null,//url저장
  var uid:String?=null,//어떤 유저가 올렸는지
  var userId:String?=null,//user email
  var RootorUser : String?=null, // user가 root인지 아닌지
  var location_name : String?=null,//전시회 장소 이름(지도 출력용)
  var lat : Float = 0.0F,//위도
  var long : Float = 0.0F,//경도
  var Exhibition_name : String?=null,//전시회 이름
  var timestamp:Long?=null,//언제 올렸는지
  var favoriteCount:Int=0,//몇명이 좋아요 눌렀는지
  var favorites:MutableMap<String,Boolean> = HashMap()//누가 좋아요 눌렀는지
) {
    data class Comment(//댓글 데이터 관리
        var uid:String?=null,//누가
        var userId:String?=null,//유저 id
        var comment:String?=null,//댓글
        var timestamp:Long?=null//언제
 )
}