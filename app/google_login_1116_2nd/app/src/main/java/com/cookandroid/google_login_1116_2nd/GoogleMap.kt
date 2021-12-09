package com.cookandroid.google_login_1116_2nd

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.model.MarkerOptions

import com.google.android.gms.maps.model.LatLng

import com.google.android.gms.maps.GoogleMap

import androidx.lifecycle.Transformations.map
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.google_login_1116_2nd.navigation.model.ContentDTO
import com.cookandroid.google_login_1116_2nd.navigation.model.LocationDTO
import com.cookandroid.google_login_1116_2nd.navigation.model.UserDTO

import com.google.android.gms.maps.SupportMapFragment

import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.stream.LongStream

class GoogleMap : AppCompatActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
//    var firestore: FirebaseFirestore? = null
//    var uid: String? = null
//    var auth: FirebaseAuth? = null
//    var currentUserUid: String? = null
    var long : Double ?= 0.0
    var lat : Double ?= 0.0
////    var latString : String?=null
////    var lonString : String?=null
//    var lat_list : MutableList<Double> = ArrayList()
//    var lon_list : MutableList<Double> = ArrayList()
    var location_name : String?=null
    var Exhibition_name : String?=null
    var location_name_list : Array<String> ?=null
//var long : Array<Double>? = null
//    var lat : Array<Double>? = null
    //    var latString : String?=null
//    var lonString : String?=null
//    var location_name : Array<String>? = null
//    var Exhibition_name : Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)


        //uid = arguments?.getString("destinationUid")
        var intent = getIntent()
        Log.d("map","mappage")
//        var uid=intent.getStringExtra("uid")
//        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
//        var userDTOs : ArrayList<UserDTO.Location> = arrayListOf()
//        var locationDTOs : ArrayList<LocationDTO> = arrayListOf()
//        firestore?.collection("userlocation")?.document(uid!!)?.collection("Location")?.addSnapshotListener{
//                querySnapshot,firebaseFirestore->
//            if(querySnapshot==null) return@addSnapshotListener
//            for(snapshot in querySnapshot.documents){
//                userDTOs.add(snapshot.toObject(UserDTO.Location::class.java)!!)
//            }
//        }
         //long= intent?.getFloatExtra("long",37.0F)!!
         //lat=intent?.getFloatExtra("lat", 128.0F)!!
        //var fragment= intent.getStringArrayListExtra("fragment")
//        location_name_list=intent.getStringArrayListExtra("location_name_list")
//        for(i in 0 .. location_name_list!!.size-1 step (1)){
//            Log.d(location_name_list!![i] , " ")
//        }
//        lat=38.0
//        long=127.0
//        location_name= location_name_list!![0]
//        Exhibition_name="hi"
//        location_name = arrayOf("1", "2", "3")
//        Exhibition_name = arrayOf("a", "b", "c")
//        lat = arrayOf(10.0, 20.0, 30.0)
//        long = arrayOf(10.0, 20.0, 30.0)
//        Log.d("map", fragment.toString())
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

            location_name = intent.getStringExtra("location_name")
            Exhibition_name = intent.getStringExtra("Exhibition_name")//intent로 data를 받아옴
            FirebaseFirestore.getInstance()
                .collection("location")
                .document(location_name!!)//프로필 사진의 주소
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var latString: String? =
                            task.result!!["lat"] as String?//이미지 url을 받아오는 함수 .... 이전까지의 snapshot을 사용하는 경우와 다르다 확인해보기
                        var lonString: String? = task.result!!["long"] as String?
                        //lat=document.data
                        //Toast.makeText(this,"lat:"+latString,Toast.LENGTH_LONG).show()
                        if (latString is String) {
                            lat = latString.toDouble()
                            if (lat is Double) {
//                                Toast.makeText(this, "todouble" + lat.toString(), Toast.LENGTH_LONG)
//                                    .show()
                            }
                        }
                        if (lonString is String) {
                            long = lonString.toDouble()
                        }
//                        Toast.makeText(
//                            this,
//                            "1.lat" + lat.toString() + "long" + long.toString(),
//                            Toast.LENGTH_LONG
//                        ).show()
                        val mapFragment =
                            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                        if (lat != 0.0 && long != 0.0) {
//                            Toast.makeText(
//                                this,
//                                "2.lat" + lat.toString() + "long" + long.toString(),
//                                Toast.LENGTH_LONG
//                            ).show()
                            mapFragment!!.getMapAsync(this)
                        }
                    }
                }
        }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

   //     Toast.makeText(this,"lat"+lat.toString()+"long"+long.toString(),Toast.LENGTH_LONG).show()
        val SEOUL = LatLng(lat!!, long!!)
        val markerOptions = MarkerOptions()
        markerOptions.position(SEOUL)
        markerOptions.title(location_name)
        markerOptions.snippet(Exhibition_name)
        mMap!!.addMarker(markerOptions)
//        for (i in 0 until lat!!.size) {
//            val SEOUL = LatLng(lat!![i], long!![i])
//            val markerOptions = MarkerOptions()
//            markerOptions.position(SEOUL)
//            //markerOptions.title(location_name!![i])
//            markerOptions.snippet(Exhibition_name!![i])
//            mMap!!.addMarker(markerOptions)

            // 기존에 사용하던 다음 2줄은 문제가 있습니다.
            // CameraUpdateFactory.zoomTo가 오동작하네요.
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15f))
        }
    }


