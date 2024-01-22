package ru.sad.domain.model.channels

import java.io.Serializable

data class ChannelResponse(
    var name: String = "",
    var id: Int = -1,
    val messages: ArrayList<ChannelMessage> = arrayListOf(),
    var isAuthor: Boolean = false
): Serializable

data class ChannelMessage(val id: Int, val message: String, val date: Long): Serializable