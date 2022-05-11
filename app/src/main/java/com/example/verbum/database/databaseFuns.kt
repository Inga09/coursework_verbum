package com.example.verbum.database

import android.net.Uri
import com.example.verbum.R
import com.example.verbum.models.CommonModel
import com.example.verbum.models.UserModel
import com.example.verbum.utilits.APP_ACTIVITY
import com.example.verbum.utilits.AppValueEventListener
import com.example.verbum.utilits.TYPE_MESSAGE_IMAGE
import com.example.verbum.utilits.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

fun initFirebase(){
    AUTH =
        FirebaseAuth.getInstance()
    FER_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER =
        UserModel()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
    FER_STORAGE_ROOT = FirebaseStorage.getInstance().reference
}

inline fun putUrlToDatabase(url: String, crossinline function: () -> Unit) {
    FER_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .child(CHILD_PHOTO_URL).setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }

}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url:String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun putFileToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun initUser(crossinline function: () -> Unit) {
    FER_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getValue(UserModel::class.java)
                ?: UserModel()
            if (USER.username.isEmpty()) {
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
     mapMessage[CHILD_ID] = messageKey.toString()
     mapMessage[CHILD_TIMESTAMP] =
         ServerValue.TIMESTAMP

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
                showToast(
                    APP_ACTIVITY.getString(
                        R.string.toast_data_update
                    )
                )
                deleteOldUsername(newUserName)
            } else {
                showToast(it.exception?.message.toString())
            }
        }
}

private fun deleteOldUsername(newUserName:String) {
    FER_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
        .addOnSuccessListener {
                showToast(
                    APP_ACTIVITY.getString(
                        R.string.toast_data_update
                    )
                )
                APP_ACTIVITY.supportFragmentManager.popBackStack()
                USER.username = newUserName
        }.addOnFailureListener { showToast(it.message.toString()) }
}

fun setBioToDateBase(newBio: String) {
    FER_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_BIO).setValue(newBio)
        .addOnSuccessListener {
                showToast(
                    APP_ACTIVITY.getString(
                        R.string.toast_data_update
                    )
                )
                USER.bio = newBio
                APP_ACTIVITY.supportFragmentManager.popBackStack()

        }.addOnFailureListener { showToast(it.message.toString()) }
}

fun setNameToDatebase(fullname: String) {
    FER_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FULLNAME)
        .setValue(fullname).addOnSuccessListener {
                showToast(
                    APP_ACTIVITY.getString(
                        R.string.toast_data_update
                    )
                )
                USER.fullname = fullname
                APP_ACTIVITY.mAppDrawer.updateHeader()
                APP_ACTIVITY.supportFragmentManager.popBackStack()

        }.addOnFailureListener { showToast(it.message.toString()) }
}

fun sendMessageAsFile(
    receivingUserID: String,
    fileUrl: String,
    messageKey: String,
    typeMessage: String
) {

    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"

    val mapMessage = hashMapOf<String, Any>()
    //mapMessage[CHILD_FROM] = CURRENT_UID
   // mapMessage[CHILD_TYPE] = TYPE_MESSAGE_IMAGE
    mapMessage[CHILD_FROM] =
        CURRENT_UID
    mapMessage[CHILD_TYPE] = typeMessage
    mapMessage[CHILD_ID] = messageKey
    //mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
   // mapMessage[CHILD_FILE_URL] = imageUrl
    mapMessage[CHILD_TIMESTAMP] =
        ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_URL] = fileUrl


    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    FER_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun getMessageKey(id: String) = FER_DATABASE_ROOT.child(
    NODE_MESSAGES
).child(CURRENT_UID)
    .child(id).push().key.toString()

fun uploadFileToStorage(uri: Uri, messageKey: String, receivedID: String, typeMessage: String) {
    val path = FER_STORAGE_ROOT.child(
        FOLDER_FILES
    ).child(messageKey)
    putFileToStorage(uri, path) {
        getUrlFromStorage(path) {
            sendMessageAsFile(
                receivedID,
                it,
                messageKey,
                typeMessage
            )
        }
    }
}
    fun getFileFromStorage(mFile: File, fileUrl: String, function: () -> Unit) {
        val path = FER_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
        path.getFile(mFile)
            .addOnSuccessListener { function() }
            .addOnFailureListener { showToast(it.message.toString()) }
}