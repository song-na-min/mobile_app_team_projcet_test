package com.cookandroid.google_login_1116_2nd.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.google_login_1116_2nd.R
import com.cookandroid.google_login_1116_2nd.navigation.model.ContentDTO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

//userFragment와 같다
class GridFragment : Fragment(){
    var firestore : FirebaseFirestore?=null
    var fragmentView : View?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView=LayoutInflater.from(activity).inflate(R.layout.fragment_grid,container,false)
        firestore= FirebaseFirestore.getInstance()
        var gridfragment_recyclerview =
            fragmentView?.findViewById<RecyclerView>(R.id.gridfragment_recyclerview)
        gridfragment_recyclerview?.adapter = GridFragmentRecyclerViewAdapter()
        gridfragment_recyclerview?.layoutManager = GridLayoutManager(activity,3)
        return fragmentView
    }
    inner class GridFragmentRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()

        init{
            //root 가 올린 것만 보이도록
            firestore?.collection("images")?.whereEqualTo("uid","wV1D6rgdaxfzYkMVKxmBKaY4LHP2")?.addSnapshotListener{querySnapshot,firebaseFirestoreException ->
//            firestore?.collection("images")?.orderBy("timestamp",
//                Query.Direction.DESCENDING)?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(querySnapshot==null) return@addSnapshotListener
                for(snapshot in querySnapshot.documents){
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                notifyDataSetChanged()
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var width=resources.displayMetrics.widthPixels/3
            var imageview= ImageView(parent.context)
            imageview.layoutParams= LinearLayoutCompat.LayoutParams(width,width)
            return CustomViewHolder(imageview)
        }

        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview) {

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageview=(holder as CustomViewHolder).imageview
            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUri).apply(
                RequestOptions().centerCrop()).into(imageview)
//            imageview.setOnClickListener{//이미지 클릭으로 변경해봄
//                var intent= Intent(activity,CommentActivity::class.java)
//               // intent.putExtra("contentUid", contentUidList[position])
//                intent.putExtra("destinationUid", contentDTOs[position].uid)
//                intent.putExtra("imageUri", contentDTOs[position].imageUri)
//                intent.putExtra("explain", contentDTOs[position].explain)
//                intent.putExtra("Exhibition_name", contentDTOs[position].Exhibition_name)//넘겨줄 데이터들
//                //intent.putExtra("fragment","defailviewfragment")
//                startActivity(intent)
//            }
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }

}