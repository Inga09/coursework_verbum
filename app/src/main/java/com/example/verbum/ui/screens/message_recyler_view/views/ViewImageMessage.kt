package com.example.verbum.ui.screens.message_recyler_view.views

data class ViewImageMessage(
    override var id: String,
    override var from: String,
    override var timeStamp: String,
    override var fileUrl: String,
    override var text: String = ""
) : MessageView {
    override fun getTypeView(): Int {
        return MessageView.MESSAGE_IMAGE
    }

    override fun equals(other: Any?): Boolean {
        return (other as MessageView).id == id
    }
}