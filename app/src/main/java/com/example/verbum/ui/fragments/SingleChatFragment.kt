package com.example.verbum.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.recyclerview.widget.RecyclerView
import com.example.verbum.R
import com.example.verbum.models.CommonModel
import com.example.verbum.models.UserModel
import com.example.verbum.utilits.*
import com.google.firebase.database.ChildEventListener
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
        private lateinit var mRefMessages: DatabaseReference
        private lateinit var mAdapter: SingleChatAdapter
        private lateinit var mRecyclerView: RecyclerView
        //private lateinit var mMessagesListener: ChildEventListener
        //private var mListMessages = mutableListOf<CommonModel>()
        private lateinit var mMessagesListener: AppChildEventListener
        private var mCountMessages = 10
        private var mIsScrolling = false
        private var mSmoothScrollToPosition = true
        private var mListListeners = mutableListOf<AppChildEventListener>()

    override fun onResume() {
        super.onResume()
        initToolbar()
        initRecycleView()
    }
    private fun initRecycleView() {
        mRecyclerView = chat_recycle_view
        mAdapter = SingleChatAdapter()

        mRefMessages = FER_DATABASE_ROOT
            .child(NODE_MESSAGES)
            .child(CURRENT_UID)
            .child(contact.id)
        mRecyclerView.adapter = mAdapter
            mMessagesListener = AppChildEventListener{
                mAdapter.addItem(it.getCommonModel())
                if (mSmoothScrollToPosition) {
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
                //mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
            }



            mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
             mListListeners.add(mMessagesListener)

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mIsScrolling && dy < 0) {
                    updateData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolling = true
                }
            }
        })
        }
    private fun updateData() {
        mSmoothScrollToPosition = false
        mIsScrolling = false
        mCountMessages += 10
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
        mListListeners.add(mMessagesListener)
    }




        private fun initToolbar() {
            mToolbarInfo = APP_ACTIVITY.mToolbar.toolbar_info
            mToolbarInfo.visibility = View.VISIBLE
            mListenerInfoToolbar = AppValueEventListener {
                mReceivingUser = it.getUserModel()
                initInfoToolbar()
            }
            mRefUser = FER_DATABASE_ROOT.child(
                NODE_USERS
            ).child(contact.id)
            mRefUser.addValueEventListener(mListenerInfoToolbar)

            chat_btn_send_message.setOnClickListener {
                mSmoothScrollToPosition = true
                val message = chat_input_message.text.toString()
                if (message.isEmpty()) {
                    showToast("ВВедите сообщение")
                } else sendMessage(
                    message,
                    contact.id,
                    TYPE_TEXT
                ) {
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
            //mRefMessages.removeEventListener(mMessagesListener)
            mListListeners.forEach {
                mRefMessages.removeEventListener(it)
            }

            println()

        }
    }