package com.example.verbum.ui.screens.settings

import com.example.verbum.R
import com.example.verbum.database.USER
import com.example.verbum.database.setBioToDateBase
import com.example.verbum.ui.screens.base.BaseChangeFragment
import kotlinx.android.synthetic.main.fragment_change_bio.*
/* Фрагмент для изменения информации о пользователе */

class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {
    override fun onResume() {
        super.onResume()
        settings_input_bio.setText(USER.bio)
    }
    override fun change() {
        super.change()
        val newBio = settings_input_bio.text.toString()
        setBioToDateBase(newBio)
    }


}