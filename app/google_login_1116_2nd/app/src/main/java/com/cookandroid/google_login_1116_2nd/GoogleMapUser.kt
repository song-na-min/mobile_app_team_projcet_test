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


class GoogleMapUser : AppCompatActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
//    var firestore: FirebaseFirestore? = null
//    var uid: String? = null
//    var auth: FirebaseAuth? = null
//    var currentUserUid: String? = null
//    var long : Double ?= 0.0
//    var lat : Double ?= 0.0
////    var latString : String?=null
////    var lonString : String?=null
//    var lat_list : MutableList<Double> = ArrayList()
//    var lon_list : MutableList<Double> = ArrayList()
//    var location_name : String?=null
//    var Exhibition_name : String?=null
    var location_name_list : Array<String> ?=null
    var long : Array<Double>? = null
    var lat : Array<Double>? = null
    var locsize : Int ?=0
    //    var latString : String?=null
//    var lonString : String?=null
    var location_name : Array<String>? = null
    var Exhibition_name : Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_mapuser)
        location_name = arrayOf("","","","","","","","")
//        Exhibition_name = arrayOf("a", "b", "c")
        lat = arrayOf(0.0, 0.0, 0.0,0.0,0.0,0.0)
        long = arrayOf(0.0, 0.0, 0.0,0.0,0.0,0.0,0.0)

        //uid = arguments?.getString("destinationUid")
        var intent = getIntent()
        Log.d("map", "mappage")

        var latString: String?=null
        var lonString: String?=null
//        if(fragment=="defailviewfragment") {
        val locationDTO: ArrayList<UserDTO.userLocation> = arrayListOf()
//        location_name = intent.getStringExtra("location_name")
//        Exhibition_name = intent.getStringExtra("Exhibition_name")//intent로 data를 받아옴
        var uid = intent.getStringExtra("uid")

        FirebaseFirestore//user의 location_name 값을 읽어오는 부분
            .getInstance()
            .collection("userlocation")//images안에 있는
            .document(uid!!)//document에 접근
            .collection("Location")//comments에 있는 데이터를
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->//하나씩 변경을 확인..?
                locationDTO.clear()//초기화(중복 방지)
                if (querySnapshot == null) return@addSnapshotListener
                var i=0
                for (snapshot in querySnapshot?.documents!!) {//하나씩 읽어오기
                    locationDTO.add(snapshot.toObject(UserDTO.userLocation::class.java)!!)

                }
                locsize=locationDTO.size
                for(i in 0 until locsize!!){
                FirebaseFirestore.getInstance()
                    .collection("location")//images안에 있는
                    .document(locationDTO[i].location_name!!.trim())//document에 접근
                    .get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("location : ", locationDTO[0].location_name!!)
                            Log.d("location : ", task.toString())
                           // Log.d("location : ", location_name!![i])
                            //location_name!![i]=locationDTO[i].location_name!!.trim()
                            latString = ""
                            lonString = ""
                            latString = task.result!!["lat"] as String?
                            lonString = task.result!!["long"] as String?
                            Log.d("insert lat", latString.toString())
                            //lat=document.data
                            //Toast.makeText(this,"lat:"+i.toString()+latString,Toast.LENGTH_LONG).show()
                            if (latString is String) {
                                lat!![i] = latString!!.toDouble()
                                if (lat!![i] is Double) {
                                    Log.d("insert lat double", lat!![i].toString())
                                }
                            }
                            if (lonString is String) {
                                long!![i] = lonString!!.toDouble()
                                //i++
                                Log.d("i:", i.toString())
                            }
                            location_name!![i]=locationDTO[i].location_name!!
                            Log.d("lat : ", lat!![0].toString())
                            Log.d("lon : ", long!![0].toString())
                            Log.d("lat : ", lat!![1].toString())
                            Log.d("lon : ", long!![1].toString())

                            if (lat!![locsize!! -1] != 0.0 && long!![locsize!! -1] != 0.0) {
                                Log.d("location : ", locationDTO[0].location_name!!)
                                Log.d("location : ", locationDTO[1].location_name!!)

                                Log.d("size: ", locationDTO.size.toString())
                                Log.d("lat : ", lat!![0].toString())
                                Log.d("lon : ", long!![0].toString())
                                Log.d("lat : ", lat!![1].toString())
                                Log.d("lon : ", long!![1].toString())
                                val mapFragment =
                                    supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                                mapFragment!!.getMapAsync(this)

                            }
                        }

                    }
                }
//                Log.d("location : ",locationDTO[0].location_name!!)
//                Log.d("location : ",locationDTO[1].location_name!!)

            }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

//        Toast.makeText(this,"lat"+lat.toString()+"long"+long.toString(),Toast.LENGTH_LONG).show()
//        val SEOUL = LatLng(lat!!, long!!)
//        val markerOptions = MarkerOptions()
//        markerOptions.position(SEOUL)
//        markerOptions.title(location_name)
//        markerOptions.snippet(Exhibition_name)
//        mMap!!.addMarker(markerOptions)
        for (j in 0 until locsize!!) {
            val SEOUL = LatLng(lat!![j], long!![j])
            Log.d("j",j.toString())
            val markerOptions = MarkerOptions()
            markerOptions.position(SEOUL)
            markerOptions.title(location_name!!.get(j))
            //markerOptions.snippet(Exhibition_name!![i])
            mMap!!.addMarker(markerOptions)

            // 기존에 사용하던 다음 2줄은 문제가 있습니다.
            // CameraUpdateFactory.zoomTo가 오동작하네요.
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15f))
        }
    }
}

