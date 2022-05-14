package com.example.verbum.ui.screens.groups

import androidx.recyclerview.widget.RecyclerView
import com.example.verbum.R
import com.example.verbum.models.CommonModel
import com.example.verbum.ui.screens.base.BaseFragment
import com.example.verbum.utilits.APP_ACTIVITY
import com.example.verbum.utilits.getPlurals
import com.example.verbum.utilits.hideKeyboard
import com.example.verbum.utilits.showToast
import kotlinx.android.synthetic.main.fragment_create_group.*

class CreateGroupFragment(private var listContacts:List<CommonModel>)
    :BaseFragment(R.layout.fragment_create_group) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.create_group)
        //APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()
        initRecyclerView()
        create_group_btn_complete.setOnClickListener { showToast("Click") }
        create_group_input_name.requestFocus()
        create_group_counts.text = getPlurals(listContacts.size)
    }

    private fun initRecyclerView() {
        mRecyclerView = create_group_recycle_view
        mAdapter = AddContactsAdapter()
        mRecyclerView.adapter = mAdapter
        listContacts.forEach {  mAdapter.updateListItems(it) }
    }

}