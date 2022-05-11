package com.example.verbum.ui.fragments.message_recyler_view.views

import com.example.verbum.models.CommonModel
import com.example.verbum.utilits.TYPE_MESSAGE_IMAGE
import com.example.verbum.utilits.TYPE_MESSAGE_VOICE

class AppViewFactory {
    companion object{
        fun getView(message: CommonModel): MessageView {
            return when(message.type){
                TYPE_MESSAGE_IMAGE -> ViewImageMessage(
                    message.id,
                    message.from,
                    message.timeStamp.toString(),
                    message.fileUrl
                )
                TYPE_MESSAGE_VOICE -> ViewVoiceMessage(
                    message.id,
                    message.from,
                    message.timeStamp.toString(),
                    message.fileUrl
                )
                else -> ViewTextMessage(
                    message.id,
                    message.from,
                    message.timeStamp.toString(),
                    message.fileUrl,
                    message.text
                )
            }
        }
    }
}