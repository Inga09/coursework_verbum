package com.example.verbum.database

import android.net.Uri
import com.example.verbum.R
import com.example.verbum.models.CommonModel
import com.example.verbum.models.UserModel
import com.example.verbum.utilits.*
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
    typeMessage: String,
    filename: String
) {

    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"

    val mapMessage = hashMapOf<String, Any>()

    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = typeMessage
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_URL] = fileUrl
    mapMessage[CHILD_TEXT] = filename


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

 fun uploadFileToStorage(
    uri: Uri,
    messageKey: String,
    receivedID: String,
    typeMessage: String,
    filename: String = ""
) {
    val path = FER_STORAGE_ROOT.child(
        FOLDER_FILES
    ).child(messageKey)
    putFileToStorage(uri, path) {
        getUrlFromStorage(path) {
            sendMessageAsFile(
                receivedID,
                it,
                messageKey,
                typeMessage,
                filename
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

fun saveToMainList(id: String, type: String) {
    val refUser = "$NODE_MAIN_LIST/$CURRENT_UID/$id"
    val refReceived = "$NODE_MAIN_LIST/$id/$CURRENT_UID"

    //val mapUser = hashMapOf<String,Any>()
    //val mapReceived = hashMapOf<String,Any>()
    val mapUser = hashMapOf<String, Any>()
    val mapReceived = hashMapOf<String, Any>()

    mapUser[CHILD_ID] = id
    mapUser[CHILD_TYPE] = type

    mapReceived[CHILD_ID] = CURRENT_UID
    mapReceived[CHILD_TYPE] = type

    val commonMap = hashMapOf<String, Any>()
    commonMap[refUser] = mapUser
    commonMap[refReceived] = mapReceived

    FER_DATABASE_ROOT.updateChildren(commonMap)
        .addOnFailureListener { showToast(it.message.toString()) }
}


fun deleteChat(id: String, function: () -> Unit) {
    FER_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID).child(id).removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener { function() }
}

fun clearChat(id: String, function: () -> Unit) {
    FER_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(id)
        .removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener {
            FER_DATABASE_ROOT.child(NODE_MESSAGES).child(id)
                .child(CURRENT_UID)
                .removeValue()
                .addOnSuccessListener { function() }
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun createGroupToDatabase(
    nameGroup: String,
    uri: Uri,
    listContacts: List<CommonModel>,
    function: () -> Unit
) {

    val keyGroup = FER_DATABASE_ROOT.child(NODE_GROUPS).push().key.toString()
    val path = FER_DATABASE_ROOT.child(NODE_GROUPS).child(keyGroup)
    val pathStorage = FER_STORAGE_ROOT.child(FOLDER_GROUPS_IMAGE).child(keyGroup)

    val mapData = hashMapOf<String, Any>()
    mapData[CHILD_ID] = keyGroup
    mapData[CHILD_FULLNAME] = nameGroup
    mapData[CHILD_PHOTO_URL] = "empty"

    val mapMembers = hashMapOf<String, Any>()
    listContacts.forEach {
        mapMembers[it.id] = USER_MEMBER
    }
    mapMembers[CURRENT_UID] = USER_CREATOR

    mapData[NODE_MEMBERS] = mapMembers

    path.updateChildren(mapData)
        .addOnSuccessListener {
            //function()
            if (uri != Uri.EMPTY) {
                putFileToStorage(uri, pathStorage) {
                    getUrlFromStorage(pathStorage) {
                        path.child(CHILD_PHOTO_URL).setValue(it)
                        addGroupsToMainList(mapData, listContacts) {
                            function()
                    }
                }
            }

        }else {
                addGroupsToMainList(mapData, listContacts) {
                    function()



                }
            }
        }
}

fun addGroupsToMainList(
    mapData: HashMap<String, Any>,
    listContacts: List<CommonModel>,
    function: () -> Unit
) {
    val path = FER_DATABASE_ROOT.child(NODE_MAIN_LIST)
    val map = hashMapOf<String, Any>()

    map[CHILD_ID] = mapData[CHILD_ID].toString()
    map[CHILD_TYPE] = TYPE_GROUP
    listContacts.forEach {
        path.child(it.id).child(map[CHILD_ID].toString()).updateChildren(map)
    }
    path.child(CURRENT_UID).child(map[CHILD_ID].toString()).updateChildren(map)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }

}

fun sendMessageToGroup(message: String, groupID: String, typeText: String, function: () -> Unit) {

    var refMessages = "$NODE_GROUPS/$groupID/$NODE_MESSAGES"
    val messageKey = FER_DATABASE_ROOT.child(refMessages).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] =
        CURRENT_UID
    mapMessage[CHILD_TYPE] = typeText
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMP] =
        ServerValue.TIMESTAMP

    FER_DATABASE_ROOT.child(refMessages).child(messageKey.toString())
        .updateChildren(mapMessage)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }

}