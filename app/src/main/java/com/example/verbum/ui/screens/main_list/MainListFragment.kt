package com.example.verbum.ui.screens.main_list

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.verbum.R
import com.example.verbum.database.*
import com.example.verbum.models.CommonModel
import com.example.verbum.ui.screens.BaseFragment
import com.example.verbum.utilits.APP_ACTIVITY
import com.example.verbum.utilits.AppValueEventListener
import com.example.verbum.utilits.hideKeyboard
import kotlinx.android.synthetic.main.fragment_main_list.*


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
                mRefUsers.child(model.id)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1 ->
                        val newModel = dataSnapshot1.getCommonModel()

                        // 3 запрос
                        mRefMessages.child(model.id).limitToLast(1)
                            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot2 ->
                                val tempList = dataSnapshot2.children.map { it.getCommonModel() }
                                //newModel.lastMessage = tempList[0].text
                                if (tempList.isEmpty()){
                                    newModel.lastMessage = "Чат очищен"
                                } else {
                                    newModel.lastMessage = tempList[0].text
                                }

                                if (newModel.fullname.isEmpty()) {
                                    newModel.fullname = newModel.phone
                                }
                                mAdapter.updateListItems(newModel)
                            })
                    })
            }
        })

        mRecyclerView.adapter = mAdapter
    }
}