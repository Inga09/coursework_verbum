package com.example.verbum.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.verbum.R
import com.example.verbum.databinding.FragmentChatsBinding
import com.example.verbum.utilits.APP_ACTIVITY


class ChatsFragment : Fragment() {

    class ChatsFragment : BaseFragment(R.layout.fragment_chats) {

        override fun onResume() {
            super.onResume()
            APP_ACTIVITY.title = "Чаты"
        }
    }
}
