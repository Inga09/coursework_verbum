package com.example.verbum.ui



import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.example.verbum.MainActivity
import com.example.verbum.R
import com.example.verbum.activities.RegisterActivity
import com.example.verbum.ui.fragments.BaseFragment
import com.example.verbum.ui.fragments.ChangeNameFragment
import com.example.verbum.utilits.AUTH
import com.example.verbum.utilits.replaceActivity
import com.example.verbum.utilits.replaceFragment

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_action_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.settings_menu_exit -> {
                AUTH.signOut()
                (activity as MainActivity).replaceActivity(RegisterActivity())
            }
            R.id.settings_menu_change_name -> replaceFragment(ChangeNameFragment())
        }
        return true
    }
}

