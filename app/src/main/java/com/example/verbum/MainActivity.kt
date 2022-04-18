package com.example.verbum

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import com.example.verbum.activities.RegisterActivity
import com.example.verbum.databinding.ActivityMainBinding
import com.example.verbum.models.User
import com.example.verbum.ui.fragments.ChatsFragment
import com.example.verbum.ui.objects.AppDrawer
import com.example.verbum.utilits.*
import com.theartofdev.edmodo.cropper.CropImage


//import com.example.verbum.utilits.AUTH
//import com.example.verbum.utilits.initFirebase
//import com.example.verbum.utilits.replaceActivity
//import com.example.verbum.utilits.replaceFragment
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    internal lateinit var mAppDrawer: AppDrawer
    private lateinit var mToolbar: Toolbar
    // gjhyj


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY = this
        initFields()
        initFunc()
    }

    private fun initFunc() {
        if (AUTH.currentUser != null) {
            setSupportActionBar(mToolbar)
            mAppDrawer.create()
            replaceFragment(ChatsFragment(), false)

        } else {
            replaceActivity(RegisterActivity())

        }


    }


    private fun initFields() {
        mToolbar = mBinding.mainToolbar
        mAppDrawer = AppDrawer(this, mToolbar)
        //AUTH = FirebaseAuth.getInstance()
        initFirebase()
        initUser()
    }

    private fun initUser() {
        FER_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
            .addListenerForSingleValueEvent(AppValueEventListener {
                USER = it.getValue(User::class.java) ?: User()
            })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == RESULT_OK && data != null){
            val uri = CropImage.getActivityResult(data).uri
            val path = FER_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE)
                .child(CURRENT_UID)
            path.putFile(uri).addOnCompleteListener {
                if (it.isSuccessful){
                    showToast(getString(R.string.toast_data_update))


                }
            }
        }
    }
    fun hideKeyboard(){
        val imm:InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken,0)
    }
}





