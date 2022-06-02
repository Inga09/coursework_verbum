package com.example.verbum.ui.screens.message_recyler_view.view_holder

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.verbum.MyCrypt
import com.example.verbum.database.CURRENT_UID
import com.example.verbum.ui.screens.message_recyler_view.views.MessageView
import com.example.verbum.utilits.asTime
import kotlinx.android.synthetic.main.message_item_text.view.*

class HolderTextMessage(view: View): RecyclerView.ViewHolder(view),MessageHolder {
    val blocUserMessage: ConstraintLayout = view.bloc_user_message
    val chatUserMessage: TextView = view.chat_user_message
    val chatUserMessageTime: TextView = view.chat_user_message_time
    val blocReceivedMessage: ConstraintLayout = view.bloc_received_message
    val chatReceivedMessage: TextView = view.chat_received_message
    val chatReceivedMessageTime: TextView = view.chat_received_message_time

    override fun drawMessage(view: MessageView) {
        val thisCrypt = MyCrypt()
        if (view.from == CURRENT_UID) {
            blocUserMessage.visibility = View.VISIBLE
            blocReceivedMessage.visibility = View.GONE
            chatUserMessage.text = view.text
            chatUserMessage.text = thisCrypt.decrypt(view.text)
            chatUserMessageTime.text =
                view.timeStamp.asTime()
        } else {
            blocUserMessage.visibility = View.GONE
            blocReceivedMessage.visibility = View.VISIBLE
            chatReceivedMessage.text = view.text
            chatReceivedMessage.text = thisCrypt.decrypt(view.text)
            chatReceivedMessageTime.text =
                view.timeStamp.asTime()
        }

    }

    override fun onAttach(view: MessageView) {
    }

    override fun onDetach() {
    }
}