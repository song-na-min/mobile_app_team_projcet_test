package com.cookandroid.google_login_1116_2nd.navigation

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.google_login_1116_2nd.*
import com.cookandroid.google_login_1116_2nd.navigation.model.ContentDTO
import com.cookandroid.google_login_1116_2nd.navigation.model.FollowDTO
import com.cookandroid.google_login_1116_2nd.navigation.model.UserDTO
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class UserFragment : Fragment() {

    var fragmentView: View? = null
    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var auth: FirebaseAuth? = null
    var currentUserUid: String? = null // 현재 user의 uid => 나의 계정인지 다른 user의 계정인지 판단
    var location_name_list :MutableList<String> = ArrayList()
    //var location_name_list : Array<String>? = null
    companion object {
        var PICK_PROFILE_FROM_ALBUM = 10
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        var fragmentView =
            LayoutInflater.from(activity).inflate(R.layout.fragment_user, container, false)
        uid = arguments?.getString("destinationUid")//이전 화면에서 넘어온 uid를 받아온다. MainActivity의 action_account에서 받아오고,
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserUid = auth?.currentUser?.uid//현재 user의 uid

        var account_btn_follow_signout =
            fragmentView.findViewById<Button>(R.id.account_btn_follow_signout)
        var UserId= fragmentView?.findViewById<TextView>(R.id.UserId)
        var map_move = fragmentView.findViewById<ImageView>(R.id.map_move)
        map_move.setOnClickListener {//user fragment에서 지도버튼 클릭
//            for(i in 0 .. location_name_list!!.size-1 step (1)){
//                Log.d(location_name_list!![i] , " ")
//            }
            Log.d("map","selected")
//            for(i in 0 .. 2){
//                Log.d(location_name_list!![i] , " ")
//            }
            val intent = Intent(activity, GoogleMapUser::class.java)
            //val Location_name: ArrayList<UserDTO.Location> = arrayListOf()
            intent.putExtra("uid",uid)

            Log.d("map","go?")
            //intent.putExtra("location_name_list",location_name_list.toList().toTypedArray())
            //intent.putExtra("fragment","userfragment")
            startActivity(intent)

        }
        if (uid == currentUserUid) {//나의 페이지
            account_btn_follow_signout?.text = getString(R.string.signout)
            UserId?.text="MyPage"
            account_btn_follow_signout?.setOnClickListener {
                activity?.finish()
                startActivity(Intent(activity, LoginActivity::class.java))//로그인 화면으로 이동
                auth?.signOut()//로그아웃
            }
        } else {//다른 user 페이지
            account_btn_follow_signout?.text = getString(R.string.follow)
            var mainactivity =
                LayoutInflater.from(activity).inflate(R.layout.activity_main, container, false)
            var toolbar_username = mainactivity?.findViewById<TextView>(R.id.toolbar_username)
            toolbar_username?.text = arguments?.getString("userId")
            UserId?.text= arguments?.getString("userId")
            var toolbar_btn_back = mainactivity?.findViewById<ImageView>(R.id.toolbar_btn_back)
            var bottom_naviagtion =
                mainactivity?.findViewById<BottomNavigationView>(R.id.bottom_naviagtion)
            var toolbar_title_image =
                mainactivity?.findViewById<ImageView>(R.id.toolbar_title_image)
            toolbar_btn_back?.setOnClickListener {//뒤로가기 버튼 이벤트
                bottom_naviagtion?.selectedItemId = R.id.action_home//홈화면으로 이동
            }
            toolbar_title_image?.visibility = View.GONE//안보이도록 세팅
            toolbar_username?.visibility = View.VISIBLE
            toolbar_btn_back?.visibility = View.VISIBLE
            account_btn_follow_signout?.setOnClickListener{
                //account_btn_follow_signout?.text="fow"
                requestFollow()
            }
        }
        var account_recyclerview =
            fragmentView.findViewById<RecyclerView>(R.id.account_recyclerview)
        var account_iv_profile = fragmentView.findViewById<ImageView>(R.id.account_iv_profile)
        account_recyclerview?.adapter = UserFragmentRecyclerViewAdapter()
        account_recyclerview?.layoutManager = GridLayoutManager(activity, 3)//3개씩 girdlayout으로 뜨도록

//        account_iv_profile?.setOnClickListener {//프로필이미지를 선택하면 이미지를 가져오기 위한 event
//            var photoPickerIntent = Intent(Intent.ACTION_PICK)
//            photoPickerIntent.type = "image/*"
//            activity?.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM)
//        }
        //getProfileImage()
        getFollowerAndFollowing()
        return fragmentView
    }

    fun requestFollow() {


        var tsDocFollowing = firestore!!.collection("users")?.document(currentUserUid!!)
        firestore?.runTransaction { transaction ->

            var followDTO = transaction.get(tsDocFollowing).toObject(FollowDTO::class.java)
            if (followDTO == null) {

                followDTO = FollowDTO()
                followDTO.followingCount = 1
                followDTO.followings[uid!!] = true

                transaction.set(tsDocFollowing, followDTO)
                return@runTransaction

            }
            // Unstar the post and remove self from stars
            if (followDTO?.followings?.containsKey(uid)) {

                followDTO?.followingCount = followDTO?.followingCount - 1
                followDTO?.followings.remove(uid)
            } else {

                followDTO?.followingCount = followDTO?.followingCount + 1
                followDTO?.followings[uid!!] = true
                //followerAlarm(uid!!)
            }
            transaction.set(tsDocFollowing, followDTO)
            return@runTransaction
        }

        var tsDocFollower = firestore!!.collection("users")?.document(uid!!)
        firestore?.runTransaction { transaction ->

            var followDTO = transaction.get(tsDocFollower!!).toObject(FollowDTO::class.java)
            if (followDTO == null) {

                followDTO = FollowDTO()
                followDTO!!.followerCount = 1
                followDTO!!.followers[currentUserUid!!] = true


                transaction.set(tsDocFollower, followDTO!!)
                return@runTransaction
            }

            if (followDTO!!.followers.containsKey(currentUserUid)) {


                followDTO!!.followerCount = followDTO!!.followerCount - 1
                followDTO!!.followers.remove(currentUserUid!!)
            } else {
                followDTO!!.followerCount = followDTO!!.followerCount + 1
                followDTO!!.followers[currentUserUid!!] = true
            }// Star the post and add self to stars

            transaction.set(tsDocFollower, followDTO!!)
            return@runTransaction
        }
    }
    //현재 data가 firebase에 저장되는것은 확인하였지만 앱에 반영되지 않는 상황 더 수정 예정
    fun getFollowerAndFollowing(){
        firestore?.collection("users")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firestoreFirestoreExceptino ->
            if(documentSnapshot == null)return@addSnapshotListener
            var followDTO=documentSnapshot.toObject(FollowDTO::class.java)
            if(followDTO?.followerCount!=null){var account_tv_follower_count =fragmentView?.findViewById<TextView>(R.id.account_tv_follower_count)

                account_tv_follower_count?.text=followDTO?.followerCount?.toString()
                var account_btn_follow_signout =
                    fragmentView?.findViewById<Button>(R.id.account_btn_follow_signout)
                if(followDTO?.followers?.containsKey(currentUserUid!!)){
                    account_btn_follow_signout?.text=getString(R.string.follow_cancel)
                    //account_btn_follow_signout?.background?.setColorFilter(ContextCompat.getColor(activity!!,R.color.colorLightGray),PorterDuff.Mode.MULTIPLY)
                }else{
                    if(uid!=currentUserUid){
                        account_btn_follow_signout?.text=getString(R.string.follow)
                        //account_btn_follow_signout?.background?.colorFilter=null
                    }
                }
            }

            if(followDTO?.followingCount!=null){
                var account_tv_following_count =fragmentView?.findViewById<TextView>(R.id.account_tv_following_count)
                account_tv_following_count?.text=followDTO?.followingCount?.toString()


            }
        }
    }
    //프로필이미지를 불러오는 함수 오류나서 실행안함
//    fun getProfileImage(){
//        firestore?.collection("profileImages")?.document(uid!!)?.addSnapshotListener { documentSnapshot,firebaseFirestoreException ->
//            if(documentSnapshot == null) return@addSnapshotListener
//            if(documentSnapshot?.data !=null)
//            {
//                var account_iv_profile=fragmentView?.findViewById<ImageView>(R.id.account_iv_profile)
//                var url=Glide.with(activity!!).load(url).apply(RequestOptions().circleCrop()).into(account_iv_profile!!)mentSnapshot?.data!!["image"]
//
//            }
//        }
//    }
//    fun getProfileImage(){
//        firestore?.collection("profileImages")?.document(uid!!)?.get()?.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val url= task.result!!["image"]
//                    var account_iv_profile=fragmentView?.findViewById<ImageView>(R.id.account_iv_profile)
//                    var url=Glide.with(activity!!).load(url).apply(RequestOptions().circleCrop()).into(account_iv_profile!!)
//            }
//        }
//    }
    inner class UserFragmentRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){//recyclerview에 넣을 adapter
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()//contentDTO를 담을 array

        init{
            firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener{//uid값이 내 uid일때만 읽어오도록
                querySnapshot,firebaseFirestore->//database의 값들을 읽어오기
                if(querySnapshot==null) return@addSnapshotListener //프로그램 안정성을 위해 null일 때 종료

                    for(snapshot in querySnapshot.documents){//데이터를 하나씩 받아오는 것
                        contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)//받아온 snapshot을 ContentDTO로 캐스팅 후 contentDTOs에 담기

                }
                var account_tv_post_count=fragmentView?.findViewById<TextView>(R.id.account_tv_post_count)//fragment_user에 있는 게시물 개수 표시 textview
                account_tv_post_count?.text=contentDTOs.size.toString()//게시물 개수 표시

                notifyDataSetChanged()//새로고침
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var width=resources.displayMetrics.widthPixels/3 //화면 폭을 3으로 나눠서
            var imageview=ImageView(parent.context)//폭의 1/3크기의 정사각형 이미지view 생성
            imageview.layoutParams=LinearLayoutCompat.LayoutParams(width,width)//
            return CustomViewHolder(imageview)//로 넘김
        }

        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview) { }//위에서 넘어온 imageview를 recyclerView.ViewHolder로 넘김

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {//데이터 매핑
            //location_name_list.add(contentDTOs[position].location_name!!)
            var imageview=(holder as CustomViewHolder).imageview // CustomViewHolder에서 imageview를 받아오고(CustomViewHolder가 위의 변수와 같도록 해야한다)
            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUri).apply(RequestOptions().centerCrop()).into(imageview)
            //받은 imageview에 contentDTOs에 저장된 imageUri들을 매핑한다.

        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }
}

