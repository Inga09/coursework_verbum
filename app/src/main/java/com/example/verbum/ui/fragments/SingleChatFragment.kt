package com.example.verbum.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.verbum.R
import com.example.verbum.models.CommonModel
import com.example.verbum.models.UserModel
import com.example.verbum.utilits.*
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_single_chat.*
import kotlinx.android.synthetic.main.toolbar_info.view.*


class SingleChatFragment(private val contact: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {

        private lateinit var mListenerInfoToolbar: AppValueEventListener
        private lateinit var mReceivingUser: UserModel
        private lateinit var mToolbarInfo:View
        private lateinit var mRefUser: DatabaseReference

        override fun onResume() {
        super.onResume()
            mToolbarInfo = APP_ACTIVITY.mToolbar.toolbar_info
            mToolbarInfo.visibility = View.VISIBLE
            mListenerInfoToolbar = AppValueEventListener {
                mReceivingUser = it.getUserModel()
                initInfoToolbar()
            }

            mRefUser = FER_DATABASE_ROOT.child(NODE_USERS).child(contact.id)
            mRefUser.addValueEventListener(mListenerInfoToolbar)
            chat_btn_send_message.setOnClickListener {
                val message = chat_input_message.text.toString()
                if (message.isEmpty()){
                    showToast("ВВедите сообщение")
                } else sendMessage(message,contact.id, TYPE_TEXT){
                    chat_input_message.setText("")
                }
            }
        }



    private fun initInfoToolbar() {
            if (mReceivingUser.fullname.isEmpty()) {
                mToolbarInfo.toolbar_chat_fullname.text = contact.fullname
            } else mToolbarInfo.toolbar_chat_fullname.text = mReceivingUser.fullname
            mToolbarInfo.toolbar_chat_image.downloadAndSetImage(mReceivingUser.photoUrl)
            mToolbarInfo.toolbar_chat_status.text = mReceivingUser.state
    }

    override fun onPause() {
        super.onPause()
        mToolbarInfo.visibility = View.GONE
        mRefUser.removeEventListener(mListenerInfoToolbar)

    }
}