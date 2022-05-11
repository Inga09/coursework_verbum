package com.example.verbum.ui.screens

import androidx.fragment.app.Fragment
import com.example.verbum.R
import com.example.verbum.utilits.APP_ACTIVITY
import com.example.verbum.utilits.hideKeyboard


class MainFragment : Fragment() {

    class ChatsFragment : BaseFragment(R.layout.fragment_chats) {

        override fun onResume() {
            super.onResume()
            APP_ACTIVITY.title = "Verbum"
            APP_ACTIVITY.mAppDrawer.enableDrawer()
            hideKeyboard()
        }
    }
}
