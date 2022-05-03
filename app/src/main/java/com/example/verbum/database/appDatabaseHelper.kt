package com.example.verbum.utilits

import android.annotation.SuppressLint
import android.net.Uri
import android.provider.ContactsContract
import com.example.verbum.R
import com.example.verbum.models.CommonModel
import com.example.verbum.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

lateinit var AUTH:FirebaseAuth
lateinit var CURRENT_UID:String
lateinit var FER_DATABASE_ROOT:DatabaseReference
lateinit var FER_STORAGE_ROOT: StorageReference
lateinit var USER: UserModel

const val TYPE_TEXT = "text"

const val NODE_USERS = "users"
const val NODE_MESSAGES = "messages"
const val NODE_USERNAMES = "usernames"

const val NODE_PHONES = "phones"
const val NODE_PHONES_CONTACTS = "phones_contacts"
const val FOLDER_PROFILE_IMAGE = "profile_image"


const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_FULLNAME = "fullname"
const val CHILD_BIO = "bio"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_STATE = "state"
const val CHILD_TEXT = "text"
const val CHILD_TYPE = "type"
const val CHILD_FROM = "from"
const val CHILD_TIMESTAMP = "timeStamp"

fun initFirebase(){
    AUTH = FirebaseAuth.getInstance()
    FER_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = UserModel()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
    FER_STORAGE_ROOT = FirebaseStorage.getInstance().reference
}

inline fun putUrlToDatabase(url: String,crossinline function: () -> Unit) {
    FER_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .child(CHILD_PHOTO_URL).setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }

}

inline fun getUrlFromStorage(path: StorageReference,crossinline function: (url:String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}
inline fun putImageToStorage(uri: Uri, path: StorageReference,crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun initUser(crossinline function: () -> Unit) {
    FER_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getValue(UserModel::class.java) ?: UserModel()
            if (USER.username.isEmpty()){
                USER.username = CURRENT_UID
            }
            function()
        })

}




fun updatePhonesToDatabase(arrayContacts: ArrayList<CommonModel>) {
    if (AUTH.currentUser != null) {
        FER_DATABASE_ROOT.child(NODE_PHONES).addListenerForSingleValueEvent(AppValueEventListener {
            it.children.forEach { snapshot ->
                arrayContacts.forEach { contact ->
                    if (snapshot.key == contact.phone) {
                        FER_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                            .child(snapshot.value.toString()).child(CHILD_ID)
                            .setValue(snapshot.value.toString())
                            .addOnFailureListener { showToast(it.message.toString()) }

                        FER_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                            .child(snapshot.value.toString()).child(CHILD_FULLNAME)
                            .setValue(contact.fullname)
                            .addOnFailureListener { showToast(it.message.toString()) }
                    }
                }
            }
        })
    }
}
fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java)?: CommonModel()

fun DataSnapshot.getUserModel(): UserModel =
    this.getValue(UserModel::class.java)?: UserModel()


 fun sendMessage(message: String, receivingUserID: String, typeText: String, function: () -> Unit) {
     val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
     val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"
     val messageKey = FER_DATABASE_ROOT.child(refDialogUser).push().key

     val mapMessage = hashMapOf<String, Any>()
     mapMessage[CHILD_FROM] = CURRENT_UID
     mapMessage[CHILD_TYPE] = typeText
     mapMessage[CHILD_TEXT] = message
     mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

     val mapDialog = hashMapOf<String,Any>()
     mapDialog["$refDialogUser/$messageKey"] = mapMessage
     mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

     FER_DATABASE_ROOT
         .updateChildren(mapDialog)
         .addOnSuccessListener { function() }
         .addOnFailureListener { showToast(it.message.toString()) }

}


fun updateCurrentUsername(newUserName:String) {
    FER_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_USERNAME)
        .setValue(newUserName)
        .addOnCompleteListener {
            if (it.isSuccessful){
                showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
                deleteOldUsername(newUserName)
            } else {
                showToast(it.exception?.message.toString())
            }
        }
}



private fun deleteOldUsername(newUserName:String) {
    FER_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
        .addOnSuccessListener {
                showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
                APP_ACTIVITY.supportFragmentManager.popBackStack()
                USER.username = newUserName
        }.addOnFailureListener { showToast(it.message.toString()) }
}


 fun setBioToDateBase(newBio: String) {
    FER_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_BIO).setValue(newBio)
        .addOnSuccessListener {
                showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
                USER.bio = newBio
                APP_ACTIVITY.supportFragmentManager.popBackStack()

        }.addOnFailureListener { showToast(it.message.toString()) }
}

fun setNameToDatebase(fullname: String) {
    FER_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FULLNAME)
        .setValue(fullname).addOnSuccessListener {
                showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
                USER.fullname = fullname
                APP_ACTIVITY.mAppDrawer.updateHeader()
                APP_ACTIVITY.supportFragmentManager.popBackStack()

        }.addOnFailureListener { showToast(it.message.toString()) }
}