package com.example.verbum.utilits

import com.example.verbum.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

lateinit var AUTH:FirebaseAuth
lateinit var CURRENT_UID:String
lateinit var FER_DATABASE_ROOT:DatabaseReference
lateinit var FER_STORAGE_ROOT: StorageReference
lateinit var USER: User

const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"


const val FOLDER_PROFILE_IMAGE = "profile_image"


const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_FULLNAME = "fullname"
const val CHILD_BIO = "bio"


fun initFirebase(){
    AUTH = FirebaseAuth.getInstance()
    FER_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = User()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
    FER_STORAGE_ROOT = FirebaseStorage.getInstance().reference
}