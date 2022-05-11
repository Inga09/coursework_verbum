package com.example.verbum.ui.screens

import com.example.verbum.R
import com.example.verbum.database.*
import com.example.verbum.utilits.*
import kotlinx.android.synthetic.main.fragment_change_username.*
import java.util.*


class ChangeUsernameFragment : BaseChangeFragment(R.layout.fragment_change_username){
    lateinit var mNewUsername: String
    override fun onResume() {
        super.onResume()
        settings_input_username.setText(USER.username)
    }



    override fun change() {
        mNewUsername = settings_input_username.text.toString().toLowerCase(Locale.getDefault())
        if (mNewUsername.isEmpty()) {
            showToast("Поле пустое")
        } else {
            FER_DATABASE_ROOT.child(NODE_USERNAMES)
                .addListenerForSingleValueEvent(AppValueEventListener {
                    if (it.hasChild(mNewUsername)) {
                        showToast("Такой пользователь уже существует")
                    } else {
                        changeUsername()
                    }
                })
        }
    }

private fun changeUsername() {

    FER_DATABASE_ROOT.child(NODE_USERNAMES).child(mNewUsername).setValue(CURRENT_UID)
        .addOnCompleteListener {
            if (it.isSuccessful){
                updateCurrentUsername(mNewUsername)
            }
        }
}



}