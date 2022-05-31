package com.example.verbum

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.verbum.database.AUTH
import com.example.verbum.database.initFirebase
import com.example.verbum.database.initUser
import com.example.verbum.databinding.ActivityMainBinding
import com.example.verbum.ui.screens.main_list.MainListFragment
import com.example.verbum.ui.screens.register.EnterPhoneNumberFragment
import com.example.verbum.ui.objects.AppDrawer
import com.example.verbum.utilits.*
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


//import com.example.verbum.database.getAUTH
//import com.example.verbum.database.initFirebase
//import com.example.verbum.utilits.replaceActivity
//import com.example.verbum.utilits.replaceFragment
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.ValueEventListener

/* Главная активность*/
class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    internal lateinit var mAppDrawer: AppDrawer
    lateinit var mToolbar: Toolbar
    // gjhyj


    override fun onCreate(savedInstanceState: Bundle?) {
        /* Функция запускается один раз, при создании активити */
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        APP_ACTIVITY = this
        initFirebase()
        initUser{
            CoroutineScope(Dispatchers.IO).launch {
                initContacts()
            }
            initFields()
            initFunc()
        }

    }




    private fun initFunc() {
        /* Функция инициализирует функциональность приложения */
        setSupportActionBar(mToolbar)
        if (AUTH.currentUser != null) {
            mAppDrawer.create()
            replaceFragment(MainListFragment(), false)

        } else {
            replaceFragment(EnterPhoneNumberFragment(),false)

        }


    }


    private fun initFields() {
        /* Функция инициализирует переменные */
        mToolbar = mBinding.mainToolbar
        mAppDrawer = AppDrawer()
        //AUTH = FirebaseAuth.getInstance()

    }

    override fun onStart() {
        super.onStart()
        AppStates.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        AppStates.updateState(AppStates.OFFLINE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(APP_ACTIVITY, READ_CONTACTS)== PackageManager.PERMISSION_GRANTED){
            initContacts()
        }
    }
// в AVD проверить api 23
}





