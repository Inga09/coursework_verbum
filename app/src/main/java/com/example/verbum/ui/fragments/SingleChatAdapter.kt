package com.example.verbum.ui.fragments

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.verbum.database.CURRENT_UID
import com.example.verbum.models.CommonModel
import com.example.verbum.ui.fragments.message_recyler_view.view_holder.AppHolderFactory
import com.example.verbum.ui.fragments.message_recyler_view.view_holder.HolderImageMessage
import com.example.verbum.ui.fragments.message_recyler_view.view_holder.HolderTextMessage
import com.example.verbum.ui.fragments.message_recyler_view.view_holder.HolderVoiceMessage
import com.example.verbum.ui.fragments.message_recyler_view.views.MessageView
import com.example.verbum.utilits.asTime
import com.example.verbum.utilits.downloadAndSetImage

class SingleChatAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListMessagesCache = mutableListOf<MessageView>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return mListMessagesCache[position].getTypeView()
    }

    override fun getItemCount(): Int = mListMessagesCache.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HolderImageMessage -> holder.drawMessageImage(holder, mListMessagesCache[position])
            is HolderTextMessage -> holder.drawMessageText(holder, mListMessagesCache[position])
            is HolderVoiceMessage -> holder.drawMessageVoice(holder, mListMessagesCache[position])
            else -> {
            }
        }
    }


    fun addItemToBottom(item: MessageView,
                        onSuccess: () -> Unit){
        if (!mListMessagesCache.contains(item)) {
            mListMessagesCache.add(item)
            notifyItemInserted(mListMessagesCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(item: MessageView,
                     onSuccess: () -> Unit){
        if (!mListMessagesCache.contains(item)) {
            mListMessagesCache.add(item)
            mListMessagesCache.sortBy { it.timeStamp.toString() }
            notifyItemInserted(0)
        }
        onSuccess()
    }
}