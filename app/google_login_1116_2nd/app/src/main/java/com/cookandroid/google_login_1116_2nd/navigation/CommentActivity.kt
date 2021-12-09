package com.cookandroid.google_login_1116_2nd.navigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.google_login_1116_2nd.GoogleMap
import com.cookandroid.google_login_1116_2nd.R
import com.cookandroid.google_login_1116_2nd.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommentActivity : AppCompatActivity() {
    var contentUid : String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        contentUid = intent.getStringExtra("contentUid")//댓글창을 누를 때 DetailViewFragment에서 해당 전시회의 data들을 전달하고, 받아서
        var imageUri = intent.getStringExtra("imageUri")
        var Exhibition_name = intent.getStringExtra("Exhibition_name")
        var explain = intent.getStringExtra("explain")
        var destinationUid = intent.getStringExtra("destinationUid")
        var location_name = intent.getStringExtra("location_name")
        var detailviewitemimageview_content=findViewById<ImageView>(R.id.detailviewitem_imageview_content)
        var detailviewitem_explain_textview=findViewById<TextView>(R.id.detailviewitem_explain_textview)
        var Exhibition_name_textview=findViewById<TextView>(R.id.Exhibition_name_textview)
        var layout1 = findViewById<LinearLayout>(R.id.layout1)
        var layout2 = findViewById<LinearLayout>(R.id.layout2)
        var toUser=findViewById<ImageView>(R.id.toUser)
        var comment_recyclerview=findViewById<RecyclerView>(R.id.comment_recyclerview)
        detailviewitem_explain_textview.text=explain
        Glide.with(this).load(imageUri).into(detailviewitemimageview_content)
        Exhibition_name_textview.text="#"+Exhibition_name//받은 정보를 출력
        toUser.setOnClickListener{
            detailviewitemimageview_content.visibility=View.INVISIBLE
            layout1.visibility=View.INVISIBLE
            layout2.visibility=View.INVISIBLE
            comment_recyclerview.visibility=View.INVISIBLE
            detailviewitem_explain_textview.visibility=View.INVISIBLE

            val userFragment = UserFragment()//UserFragment를 실행하는데
            var bundle=Bundle()
            bundle.putString("destinationUid",destinationUid)//선택한 상대 user의 uid
            userFragment.arguments=bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_content, userFragment)
                .commit()
        }


        comment_recyclerview.adapter = CommentRecyclerViewAdapter()
        comment_recyclerview.layoutManager = LinearLayoutManager(this)


        var comment_btn_send= findViewById<Button>(R.id.comment_btn_send)
        var comment_edit_message= findViewById<EditText>(R.id.comment_edit_message)
        comment_btn_send.setOnClickListener {//보내기 버튼 클릭
            var comment=ContentDTO.Comment()//데이터구조 생성
            comment.userId = FirebaseAuth.getInstance().currentUser?.email
            comment.uid = FirebaseAuth.getInstance().currentUser?.uid
            comment.comment= comment_edit_message.text.toString()
            comment.timestamp=System.currentTimeMillis()//comment데이터 입력

            FirebaseFirestore.getInstance().collection("images").document(contentUid!!).collection("comments").document().set(comment)
            //database에 입력

            comment_edit_message.setText("")//입력칸 초기화
        }
        var detailviewitem_map= findViewById<ImageView>(R.id.detailviewitem_map)
        detailviewitem_map.setOnClickListener{
            var intent= Intent(this, GoogleMap::class.java)
            //intent.putExtra("contentUid", contentUidList[position])
            intent.putExtra("location_name", location_name)
            intent.putExtra("Exhibition_name", Exhibition_name)
            startActivity(intent)
        }//map activity로 선택한 객체의 data를 전달하면서 지도 켜기

    }
    inner class CommentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val comments: ArrayList<ContentDTO.Comment> = arrayListOf()//contentDTO의 Comment 데이터 구조 생성

        init {

            FirebaseFirestore//값을 읽어오는 부분
                .getInstance()
                .collection("images")//images안에 있는
                .document(contentUid!!)//document에 접근
                .collection("comments").orderBy("timestamp")//comments에 있는 데이터를
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->//하나씩 변경을 확인..?
                    comments.clear()//초기화(중복 방지)
                    if (querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot?.documents!!) {//하나씩 읽어오기
                        comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                    }
                    notifyDataSetChanged()//새로고침

                }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_comment, parent, false)
            return CustomViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            var view = holder.itemView//가져온 것을 매핑
            var commentviewitem_imageview_profile=view.findViewById<ImageView>(R.id.commentviewitem_imageview_profile)
            var commentviewitem_textview_profile=view.findViewById<TextView>(R.id.commentviewitem_textview_profile)
            var commentviewitem_textview_comment=view.findViewById<TextView>(R.id.commentviewitem_textview_comment)
            // Profile Image 매핑
            FirebaseFirestore.getInstance()
                .collection("profileImages")
                .document(comments[position].uid!!)//프로필 사진의 주소
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val url = task.result!!["image"]//이미지 url을 받아오는 함수 .... 이전까지의 snapshot을 사용하는 경우와 다르다 확인해보기
                        
                        Glide.with(holder.itemView.context)
                            .load(url)
                            .apply(RequestOptions().circleCrop()).into(commentviewitem_imageview_profile)
                    }
                }

            commentviewitem_textview_profile.text = comments[position].userId//댓글과 userid 출력
            commentviewitem_textview_comment.text = comments[position].comment
        }

        override fun getItemCount(): Int {

            return comments.size
        }

        private inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }
}