package com.example.verbum.ui.screens

import androidx.fragment.app.Fragment
import com.example.verbum.utilits.APP_ACTIVITY


open class BaseFragment(layout:Int) : Fragment(layout) {

    override fun onStart() {
        super.onStart()
       APP_ACTIVITY.mAppDrawer.disableDrawer()

    }
    //override fun onStop() {
     //   super.onStop()
     //   APP_ACTIVITY.mAppDrawer.enableDrawer()
    //}
}