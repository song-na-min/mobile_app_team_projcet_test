package com.cookandroid.google_login_1116_2nd.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cookandroid.google_login_1116_2nd.R

class AlarmFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view=LayoutInflater.from(activity).inflate(R.layout.fragment_alarm,container,false)
        return view
    }

}