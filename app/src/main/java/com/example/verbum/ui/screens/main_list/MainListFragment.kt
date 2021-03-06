package com.example.verbum.ui.screens.main_list

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.verbum.MyCrypt
import com.example.verbum.R
import com.example.verbum.database.*
import com.example.verbum.models.CommonModel
import com.example.verbum.utilits.*
import kotlinx.android.synthetic.main.fragment_main_list.*

/* Главный фрагмент, содержит все чаты, группы и каналы с которыми взаимодействует пользователь*/
class MainListFragment : Fragment(R.layout.fragment_main_list) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MainListAdapter
    private val mRefMainList = FER_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID)
    private val mRefUsers = FER_DATABASE_ROOT.child(NODE_USERS)
    private val mRefMessages = FER_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    private var mListItems = listOf<CommonModel>()

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Verbum"
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = main_list_recycle_view
        mAdapter = MainListAdapter()

        // 1 запрос
        mRefMainList.addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
            mListItems = dataSnapshot.children.map { it.getCommonModel() }
            mListItems.forEach { model ->

                // 2 запрос
                when (model.type) {
                    TYPE_CHAT -> showChat(model)
                    TYPE_GROUP -> showGroup(model)
                }
            }
        })
        // 3 запрос
        mRecyclerView.adapter = mAdapter
    }
    private fun showGroup(model: CommonModel) {
        val thisCrypt = MyCrypt()
        // 2 запрос
        FER_DATABASE_ROOT.child(NODE_GROUPS).child(model.id)
            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1 ->
                val newModel = dataSnapshot1.getCommonModel()

                // 3 запрос
                FER_DATABASE_ROOT.child(NODE_GROUPS).child(model.id).child(NODE_MESSAGES)
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot2 ->
                        val tempList = dataSnapshot2.children.map { it.getCommonModel() }

                        if (tempList.isEmpty()) {
                            newModel.lastMessage = "Чат очищен"
                        } else {
                            //newModel.lastMessage = tempList[0].text
                            newModel.lastMessage =thisCrypt.decrypt(tempList[0].text)
                        }
                        newModel.type = TYPE_GROUP
                        mAdapter.updateListItems(newModel)
                    })

            })
    }
    private fun showChat(model: CommonModel) {
        val thisCrypt = MyCrypt()
        // 2 запрос
        mRefUsers.child(model.id)
            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1 ->
                val newModel = dataSnapshot1.getCommonModel()

                // 3 запрос
                mRefMessages.child(model.id).limitToLast(1)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot2 ->
                        val tempList = dataSnapshot2.children.map { it.getCommonModel() }

                        if (tempList.isEmpty()) {
                            newModel.lastMessage = "Чат очищен"
                        } else {
                            //newModel.lastMessage = tempList[0].text
                                if (tempList[0].type == "text") {
                                    newModel.lastMessage = thisCrypt.decrypt(tempList[0].text)
                                }
                                else if (tempList[0].type == "image")
                                {
                                    newModel.lastMessage = "Image"
                                }
                                else if (tempList[0].type == "voice")
                                {
                                    newModel.lastMessage = "Voice message"
                                }
                                else if (tempList[0].type == "file")
                                {
                                    newModel.lastMessage = "File"
                                }
                        }


                        if (newModel.fullname.isEmpty()) {
                            newModel.fullname = newModel.phone
                        }
                        newModel.type = TYPE_CHAT
                        mAdapter.updateListItems(newModel)
                    })
            })
    }
}
