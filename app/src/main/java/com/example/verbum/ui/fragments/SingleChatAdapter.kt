package com.example.verbum.ui.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.verbum.R
import com.example.verbum.database.CURRENT_UID
import com.example.verbum.models.CommonModel
import com.example.verbum.utilits.*
import kotlinx.android.synthetic.main.message_item.view.*

class SingleChatAdapter: RecyclerView.Adapter<SingleChatAdapter.SingleChatHolder>() {
    //private var mListMessagesCache = emptyList<CommonModel>()
    private var mListMessagesCache = mutableListOf<CommonModel>()
    //private lateinit var mDiffResult: DiffUtil.DiffResult

    class SingleChatHolder(view: View) : RecyclerView.ViewHolder(view) {
        val blocUserMessage: ConstraintLayout = view.bloc_user_message
        val chatUserMessage: TextView = view.chat_user_message
        val chatUserMessageTime: TextView = view.chat_user_message_time
        val blocReceivedMessage: ConstraintLayout = view.bloc_received_message
        val chatReceivedMessage: TextView = view.chat_received_message
        //val chatReceivedMeessageTime: TextView = view.chat_received_message_time
        val chatReceivedMessageTime: TextView = view.chat_received_message_time


        val blocReceivedImageMessage:ConstraintLayout = view.bloc_received_image_message
        val blocUserImageMessage:ConstraintLayout = view.bloc_user_image_message
        val chatUserImage:ImageView = view.chat_user_image
        val chatReceivedImage: ImageView = view.chat_received_image
        val chatUserImageMessageTime:TextView = view.chat_user_image_message_time
        val chatReceivedImageMessageTime:TextView = view.chat_received_image_message_time
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return SingleChatHolder(view)
    }
    override fun getItemCount(): Int = mListMessagesCache.size
    override fun onBindViewHolder(holder: SingleChatHolder, position: Int) {
        when(mListMessagesCache[position].type){
            TYPE_MESSAGE_TEXT -> drawMessageText(holder,position)
            TYPE_MESSAGE_IMAGE -> drawMessageImage(holder,position)
        }
    }

    private fun drawMessageImage(holder: SingleChatHolder, position: Int) {
        holder.blocUserMessage.visibility = View.GONE
        holder.blocReceivedMessage.visibility = View.GONE

        if (mListMessagesCache[position].from == CURRENT_UID) {
            holder.blocReceivedImageMessage.visibility = View.GONE
            holder.blocUserImageMessage.visibility = View.VISIBLE
            holder.chatUserImage.downloadAndSetImage(mListMessagesCache[position].fileUrl)
            holder.chatUserImageMessageTime.text = mListMessagesCache[position].timeStamp.toString().asTime()
        } else {
            holder.blocReceivedImageMessage.visibility = View.VISIBLE
            holder.blocUserImageMessage.visibility = View.GONE
            holder.chatReceivedImage.downloadAndSetImage(mListMessagesCache[position].fileUrl)
            holder.chatReceivedImageMessageTime.text = mListMessagesCache[position].timeStamp.toString().asTime()
        }
    }

    private fun drawMessageText(holder: SingleChatHolder, position: Int) {
        holder.blocReceivedImageMessage.visibility = View.GONE
        holder.blocUserImageMessage.visibility = View.GONE

        if (mListMessagesCache[position].from == CURRENT_UID) {
            holder.blocUserMessage.visibility = View.VISIBLE
            holder.blocReceivedMessage.visibility = View.GONE
            holder.chatUserMessage.text = mListMessagesCache[position].text
            holder.chatUserMessageTime.text =
                mListMessagesCache[position].timeStamp.toString().asTime()
        } else {
            holder.blocUserMessage.visibility = View.GONE
            holder.blocReceivedMessage.visibility = View.VISIBLE
            holder.chatReceivedMessage.text = mListMessagesCache[position].text
            holder.chatReceivedMessageTime.text =
                mListMessagesCache[position].timeStamp.toString().asTime()
        }
    }

    fun setList(list: List<CommonModel>) {
       // mListMessagesCache = list
       // notifyDataSetChanged()


        //notifyDataSetChanged()
    }
    fun addItemToBottom(item: CommonModel,
                        onSuccess: () -> Unit){
        if (!mListMessagesCache.contains(item)) {
            mListMessagesCache.add(item)
            notifyItemInserted(mListMessagesCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(item: CommonModel,
                     onSuccess: () -> Unit){
        if (!mListMessagesCache.contains(item)) {
            mListMessagesCache.add(item)
            mListMessagesCache.sortBy { it.timeStamp.toString() }
            notifyItemInserted(0)
        }
        onSuccess()
    }
}