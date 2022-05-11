package com.example.verbum.ui.screens.message_recyler_view.view_holder

import com.example.verbum.ui.screens.message_recyler_view.views.MessageView

interface MessageHolder {
    fun drawMessage(view: MessageView)
    fun onAttach(view: MessageView)
    fun onDetach()
}