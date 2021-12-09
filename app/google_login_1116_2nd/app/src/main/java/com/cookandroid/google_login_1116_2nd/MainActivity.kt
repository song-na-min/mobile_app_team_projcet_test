package com.cookandroid.google_login_1116_2nd

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager

import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
//import kotlin.android.synthetic.main.activity_main.*

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cookandroid.google_login_1116_2nd.navigation.*
import com.google.android.gms.tasks.Task


import com.google.android.material.bottomnavigation.BottomNavigationView.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener{//하단바에 event를 넣기 위해
//    private var firebaseAuth: FirebaseAuth? = null
//    private var googleSignInClient: GoogleSignInClient? = null
//   // private var binding: ActivityMainBinding? = null
var uid=FirebaseAuth.getInstance().currentUser?.uid
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       val bottom_naviagtion=findViewById<BottomNavigationView>(R.id.bottom_naviagtion)

       bottom_naviagtion.setOnNavigationItemSelectedListener(this)
       var uid=FirebaseAuth.getInstance().currentUser?.uid
       // 앨범 접근 권한 요청
       ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)


       //푸시토큰 서버 등록
      // registerPushToken()

       //fragment가 main화면에 뜨도록-그러니까 그 사용자의 image view를 주르륵 보여주는 화면을 home버튼을 누르면 뜨도록 했다
       //다른 아이콘으로 바꾸려면 밑에 있는 item id중 원하는 거로 변경하기
       bottom_naviagtion.selectedItemId=R.id.action_home
        //처음에 나오는 기본 페이지를 설정하는 것 같다.확인 필요

    }
//    fun registerPushToken(){
//        var pushToken = FirebaseInstanceId.getInstance().token
//        var uid = FirebaseAuth.getInstance().currentUser?.uid
//        var map = mutableMapOf<String,Any>()
//        map["pushtoken"] = pushToken!!
//        FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)
//    }
override fun onNavigationItemSelected(item: MenuItem): Boolean {
    setToolbarDefault()//위 이미지 있는 부분(상단바,Toolbar)의 보이는 것을 수정하는 함수이다,
    when (item.itemId) {//item.itemId : menu/bottom_navigation_main.xml의 메뉴 버튼? 의 id들 (하단 바) 해당 버튼을 클릭하면 event
        R.id.action_home -> {
            //홈 버튼을 클릭했을때
            val detailViewFragment = DetailViewFragment() //DefailViewFragment.kt파일을
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_content, detailViewFragment) //activity_main.xml의 id가 main_content인 부분에 넣는다
                .commit()
            return true
        }
        R.id.action_search -> {
            val gridFragment = GridFragment()//GridFragment.kt를 main_content에 넣는다
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_content, gridFragment)
                .commit()
            return true
        }
        R.id.action_add_photo -> {//하단바의  카메라모양 버튼을 눌렀을 때

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //저장소 읽기 권한이 있는지 확인하고
            startActivity(Intent(this, AddPhotoActivity::class.java))// AddPhotoActivity 실행
               // Toast.makeText(this, "add_photo.", Toast.LENGTH_LONG).show()
            } else {//없으면 오류 메세지
                Toast.makeText(this, "스토리지 읽기 권한이 없습니다.", Toast.LENGTH_LONG).show()
           }

            return true
        }
        R.id.action_favorite_alarm -> {
            //현 user의 지도로 이동
            Log.d("map","selected")
//            for(i in 0 .. 2){
//                Log.d(location_name_list!![i] , " ")
//            }
            val intent = Intent(this, GoogleMapUser::class.java)
            //val Location_name: ArrayList<UserDTO.Location> = arrayListOf()
            intent.putExtra("uid",uid)

            Log.d("map","go?")
            //intent.putExtra("location_name_list",location_name_list.toList().toTypedArray())
            //intent.putExtra("fragment","userfragment")
            startActivity(intent)
            return true
        }
        R.id.action_account -> {

            val userFragment = UserFragment()//UserFragment를 실행하는데
            var bundle=Bundle()

            bundle.putString("destinationUid",uid)//userFragment로 uid값을 destinationUid라는 이름으로 넘겨준다.
            userFragment.arguments=bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_content, userFragment)
                .commit()
            return true
        }
    }
    return false
}
    fun setToolbarDefault(){
        var toolbar_username=findViewById<TextView>(R.id.toolbar_username)
        var toolbar_btn_back=findViewById<ImageView>(R.id.toolbar_btn_back)
        var toolbar_title_image=findViewById<ImageView>(R.id.toolbar_title_image)
        toolbar_username.visibility= View.GONE//username과 뒤로가기 버튼은 보이지 않도록 하고
        toolbar_btn_back.visibility=View.GONE
        toolbar_title_image.visibility=View.VISIBLE//howlstagram이미지는 보이도록 한다.

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode===UserFragment.PICK_PROFILE_FROM_ALBUM && resultCode== Activity.RESULT_OK){//사진을 선택했을 경우
            var imageUri=data?.data
            var uid=FirebaseAuth.getInstance().currentUser?.uid
            var storageRef=FirebaseStorage.getInstance().reference.child("userProfileImages").child(uid!!)
            storageRef.putFile(imageUri!!).continueWithTask { task : Task<UploadTask.TaskSnapshot> ->//이미지 uri를 datavase에 넣기
                return@continueWithTask storageRef.downloadUrl
            }.addOnSuccessListener { uri->
                var m =HashMap<String,Any>()
                m["image"]=uri.toString()
                FirebaseFirestore.getInstance().collection("profileImages").document(uid).set(m)
            }
        }
    }

}

