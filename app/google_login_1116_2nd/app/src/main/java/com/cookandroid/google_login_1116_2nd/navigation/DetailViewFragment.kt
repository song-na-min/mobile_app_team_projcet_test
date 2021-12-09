package com.cookandroid.google_login_1116_2nd.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cookandroid.google_login_1116_2nd.GoogleMap
import com.cookandroid.google_login_1116_2nd.R
import com.cookandroid.google_login_1116_2nd.databinding.FragmentDetailBinding
import com.cookandroid.google_login_1116_2nd.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class DetailViewFragment : Fragment() {
    var firestore : FirebaseFirestore?=null
    var uid=FirebaseAuth.getInstance().currentUser?.uid//현재 로그인한 user의 uid
    private var _binding: FragmentDetailBinding?=null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentDetailBinding.inflate(inflater,container,false)

        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)//fragment_detail.xml연결

        firestore=FirebaseFirestore.getInstance()

        var detailviewfragment_recyclerview=view.findViewById<RecyclerView>(R.id.detailviewfragment_recyclerview)
        detailviewfragment_recyclerview.adapter=DetailViewREcyclerViewAdapter()
        detailviewfragment_recyclerview.layoutManager=LinearLayoutManager(activity)//화면을 세로로 배치->LinearLayoutManager, activity로 context를 넘겨줌

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
    inner class DetailViewREcyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> =arrayListOf()//contentDTO를 담을 arrayList
        var contentUidList : ArrayList<String> =arrayListOf()//contentUID를 담을 arrayList

        init{
            //database에 접근해서 데이터 받아오기 쿼리(시간순으로,내림차순 받아옴)
            firestore?.collection("images")?.orderBy("timestamp",
                Query.Direction.DESCENDING)?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                //firestore?.collection(...).document(...) : 데이터베이스 주소
                //addSnapshotListener : 변경 감지..?
                contentDTOs.clear()//받고 초기화 시키기
                contentUidList.clear()
                if(querySnapshot==null)return@addSnapshotListener
                for(snapshot in querySnapshot!!.documents){//snapsho에 넘어오는 data들을 하나씩 읽기
                    var item=snapshot.toObject(ContentDTO::class.java)//contentDTO방식으로 캐스팅?
                    contentDTOs.add(item!!)//contentDTOs에 담기
                    contentUidList.add(snapshot.id)
                }
                notifyDataSetChanged()//값 새로고침되도록
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view=LayoutInflater.from(parent.context).inflate(R.layout.item_detail,parent,false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)//메모리를 적게 사용하기 위해 사용

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {//서버에서 넘어온 데이터 매핑_출력

            var viewholder = (holder as CustomViewHolder).itemView

            var detailviewitem_profile_textView = viewholder.findViewById<TextView>(R.id.detailviewitem_profile_textview)
            var detailviewitem_profile_image=viewholder.findViewById<ImageView>(R.id.detailviewitem_profile_image)
            var detailviewitemimageview_content=viewholder.findViewById<ImageView>(R.id.detailviewitem_imageview_content)
            var detailviewitem_explain_textview=viewholder.findViewById<TextView>(R.id.detailviewitem_explain_textview)
            var detailviewitem_favoritecounter_textview=viewholder.findViewById<TextView>(R.id.detailviewitem_favoritecounter_textview)
            var detailviewitem_favorite_imageview=viewholder.findViewById<ImageView>(R.id.detailviewitem_favorite_imageview)
            var Exhibition_name_textview=viewholder.findViewById<TextView>(R.id.Exhibition_name_textview)
            var detailviewitem_map=viewholder.findViewById<ImageView>(R.id.detailviewitem_map)
            //userid
            if(contentDTOs!![position].RootorUser=="Root"){//root가 저장했으면 전시회 이름 출력
                detailviewitem_profile_textView.text = contentDTOs!![position].Exhibition_name
            }else {//user가 저장하면 userid출력
                detailviewitem_profile_textView.text = contentDTOs!![position].userId
                Exhibition_name_textview.visibility=View.VISIBLE
                Exhibition_name_textview.text="#"+contentDTOs!![position].Exhibition_name
            }
            //profile image
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUri).into(detailviewitem_profile_image)
            //explain of content
            detailviewitem_explain_textview.text=contentDTOs!![position].explain
            //likes
            detailviewitem_favoritecounter_textview.text="Likes"+contentDTOs!![position].favoriteCount
            //image
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUri).into(detailviewitemimageview_content)

            detailviewitem_favorite_imageview.setOnClickListener{//좋아요 버튼을 누르면
                favoriteEvent(position)
            }
            if(contentDTOs!![position].favorites.containsKey(uid)){//좋아요 버튼 색 변경 event
                detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite)
            }else {
                detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite_border)
            }

            //프로필이미지를 누르면 상대방페이지로으로 이동?
            detailviewitem_profile_image.setOnClickListener{
                var fragment=UserFragment()
                var bundle=Bundle()
                bundle.putString("destinationUid",contentDTOs[position].uid)//선택한 상대 user의 uid
                bundle.putString("userId",contentDTOs[position].userId)
                fragment.arguments=bundle
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()
            }
            var detailviewitem_imageview_content=viewholder.findViewById<ImageView>(R.id.detailviewitem_imageview_content)
            detailviewitem_imageview_content.setOnClickListener{//이미지 클릭으로 변경해봄
                var intent= Intent(activity,CommentActivity::class.java)
                intent.putExtra("contentUid", contentUidList[position])
                intent.putExtra("destinationUid", contentDTOs[position].uid)
                intent.putExtra("imageUri", contentDTOs[position].imageUri)
                intent.putExtra("explain", contentDTOs[position].explain)
                intent.putExtra("Exhibition_name", contentDTOs[position].Exhibition_name)//넘겨줄 데이터들
                intent.putExtra("location_name", contentDTOs[position].location_name)
                startActivity(intent)
            }
            var detailviewitem_comment_imageview=viewholder.findViewById<ImageView>(R.id.detailviewitem_comment_imageview)
            detailviewitem_comment_imageview.setOnClickListener{//댓글버튼 클릭
                var intent= Intent(activity,CommentActivity::class.java)
                intent.putExtra("contentUid", contentUidList[position])
                intent.putExtra("destinationUid", contentDTOs[position].uid)
                intent.putExtra("imageUri", contentDTOs[position].imageUri)
                intent.putExtra("explain", contentDTOs[position].explain)
                intent.putExtra("Exhibition_name", contentDTOs[position].Exhibition_name)//넘겨줄 데이터들
                intent.putExtra("location_name", contentDTOs[position].location_name)
                startActivity(intent)
            }
            detailviewitem_map.setOnClickListener{
                var intent= Intent(activity,GoogleMap::class.java)
                intent.putExtra("contentUid", contentUidList[position])
                intent.putExtra("long", contentDTOs[position].long)
                intent.putExtra("lat", contentDTOs[position].lat)
                intent.putExtra("location_name", contentDTOs[position].location_name)
                intent.putExtra("Exhibition_name", contentDTOs[position].Exhibition_name)
                startActivity(intent)
            }//map activity로 선택한 객체의 data를 전달하면서 지도 켜기
        }
        //좋아요 버튼 누르면
        fun favoriteEvent(position: Int){
            //내가 선택한 image의 uid값을 넣고
            var tsDoc=firestore?.collection("images")?.document(contentUidList[position])

            firestore?.runTransaction { transaction->//데이터를 입력하기 위해

                var contentDTO=transaction.get(tsDoc!!).toObject(ContentDTO::class.java)
                if(contentDTO!!.favorites.containsKey(uid)){//좋아요 버튼이 클릭되어있으면(검은하트) 좋아요 취소
                    contentDTO?.favoriteCount=contentDTO?.favoriteCount-1
                    contentDTO?.favorites.remove(uid)
                }else{//좋아요 버튼이 흰 하트이면
                    contentDTO?.favoriteCount=contentDTO?.favoriteCount+1
                    contentDTO?.favorites[uid!!]=true
                }
                transaction.set(tsDoc,contentDTO)//이 transaction을 다시 서버로 돌려줌
            }

        }

        override fun getItemCount(): Int {
            return contentDTOs.size//recycler view 개수 넘김
        }

    }
}