package com.cookandroid.google_login_1116_2nd

//import android.support.v7.app.AppCompatActivity
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
//import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cookandroid.google_login_1116_2nd.navigation.model.ContentDTO
import com.cookandroid.google_login_1116_2nd.navigation.model.UserDTO
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*
////import com.company.howl.howlstagram.R
////import com.company.howl.howlstagram.model.ContentDTO
//import com.cookandroid.google_login_1116_2nd.R
//import com.google.firebase.firestore.FieldValue
//import com.google.firebase.firestore.FirebaseFirestore


class AddPhotoActivity : AppCompatActivity() {

    val PICK_IMAGE_FROM_ALBUM = 0 //request code
    var storage: FirebaseStorage? = null //firebase storage 변수
    var photoUri: Uri? = null //image Uri를 담을 변수
    var auth : FirebaseAuth?=null //사용자 인증을 위해
    var firestore:FirebaseFirestore?=null//firebase database 사용

    lateinit var addphoto_image:ImageView
    lateinit var addphoto_btn_upload : Button
    lateinit var addphoto_edit_explain : EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        var addphoto_btn_upload=findViewById<Button>(R.id.addphoto_btn_upload)

        storage = FirebaseStorage.getInstance()//initiate storage
        auth= FirebaseAuth.getInstance()//firebase auth
        firestore = FirebaseFirestore.getInstance()//firebase database

        val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)//AddPhotoActivity를 실행하면(카메라모양 버튼을 누르면)
        photoPickerIntent.type = "image/*"
       startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)//background에서 device의 파일 앱을 열어서 이미지를 가져오도록

//        addphoto_image.setOnClickListener {
//            val photoPickerIntent = Intent(Intent.ACTION_PICK)
//            photoPickerIntent.type = "image/*"
//            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
//        }

        addphoto_btn_upload.setOnClickListener {//올리기 버튼을 클릭하면
            //Toast.makeText(this,"button click",Toast.LENGTH_LONG).show()
            contentUpload() }//이미지 업로드
    }
//
//     fun onCreateView(inflater : LayoutInflater,container:ViewGroup?,
//        savedInstanceState: Bundle?):View?{
//        viewProfile=inflater.inflate(R.layout.activity_add_photo,container,false)
//        storage= FirebaseStorage.getInstance()
//        addphoto_btn_upload.setOnClickListener{
//        val photoPickerIntent = Intent(Intent.ACTION_PICK)
//        photoPickerIntent.type = "image/*"
//        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
//        }
//        return viewProfile
//    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {//background에서 선택한 image를 받는 함수
    var addphoto_image=findViewById<ImageView>(R.id.addphoto_image)//가져온 이미지를 출력할 imageview
    super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==PICK_IMAGE_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {//사진을 선택했을 때
                photoUri = data?.data //photoUri에 이미지 경로를 넣는다
                addphoto_image.setImageURI(photoUri) // imageview에 이미지 경로로 이미지를 출력
                //Toast.makeText(this,photoUri.toString(),Toast.LENGTH_LONG).show()
            } else {
                //Toast.makeText(this,"onactivityresult_else_finish",Toast.LENGTH_LONG).show()
               finish()//activity 종료
            }
        }
    }

    fun contentUpload(){//데이터 업로드
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())//현재 시간값을 구하고
        var imageFileName = "IMAGE_"+timestamp+"_.jpg"//현재 시간을 파일이름으로 해서 중복불가능하도록
        var storageReference=storage?.reference //storage reference
        var imageRef=storageReference?.child(imageFileName)
        var storageRef=storage?.reference?.child("images")?.child(imageFileName)

        var addphoto_edit_explain=findViewById<EditText>(R.id.addphoto_edit_explain)
        var addphoto_location = findViewById<EditText>(R.id.addphoto_location)
        var addphoto_exhibition_name = findViewById<EditText>(R.id.addphoto_exhibition_name)

        //promise method 방식 storage에 이미지 업로드하고, database에 데이터 업로드
        storageRef?.putFile(photoUri!!)?.continueWithTask(){//업로드
            task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl //storageref의 downloadurl 을 return
        }?.addOnSuccessListener { uri->//이미지 주소를 받아와서
                var contentDTO=ContentDTO()//데이터 모델 생성
                var userLocation = UserDTO.userLocation()

                contentDTO.imageUri=uri.toString()//downloadUrl of image 입력
                contentDTO.uid=auth?.currentUser?.uid
                contentDTO.userId=auth?.currentUser?.email
                contentDTO.explain=addphoto_edit_explain.text.toString()
                contentDTO.timestamp=System.currentTimeMillis()
                contentDTO.Exhibition_name=addphoto_exhibition_name.text.toString()
                contentDTO.location_name=addphoto_location.text.toString()
               // var comment=ContentDTO.Comment()//데이터구조 생성
                userLocation.userId = FirebaseAuth.getInstance().currentUser?.email
                userLocation.uid = FirebaseAuth.getInstance().currentUser?.uid
                userLocation.location_name=addphoto_location.text.toString()
                //userLocation.timestamp=System.currentTimeMillis()//comment데이터 입력
                FirebaseFirestore.getInstance().collection("userlocation").document(userLocation.uid.toString()).collection("Location").document().set(userLocation)
                if(auth?.currentUser?.uid=="wV1D6rgdaxfzYkMVKxmBKaY4LHP2"){
                    contentDTO.RootorUser="Root"
                }else{
                    contentDTO.RootorUser="User"
                }
                //firebase database의 images 컬렉션에 만든 데이터모델(값을 넣은)을 저장
                firestore?.collection("images")?.document()?.set(contentDTO)
                setResult(Activity.RESULT_OK)//정상적으로 닫힘을 알리기 위한 .
                finish()
            }
        }
}




