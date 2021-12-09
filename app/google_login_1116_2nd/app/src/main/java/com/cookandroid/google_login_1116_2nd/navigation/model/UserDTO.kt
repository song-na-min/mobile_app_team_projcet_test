package com.cookandroid.google_login_1116_2nd.navigation.model

data class UserDTO (
    var uid:String?=null,
    var userId:String?=null
) {
    data class userLocation(//댓글 데이터 관리
        var uid:String?=null,
        var userId:String?=null,
        var location_name : String?=null
        //var timestamp:Long?=null//언제
    )
}
