package ru.sad.data.repository.channels

import ru.sad.domain.model.channels.ChannelResponse
import ru.sad.domain.model.channels.ChannelShortResponse
import ru.sad.domain.model.channels.CreateChannelResponse
import ru.sad.domain.model.channels.PostMessageResponse

interface ChannelsRepositoryImpl {

    suspend fun getSubscribedChannels(
        page: Int,
        pageSize: Int,
    ): List<ChannelShortResponse>

    suspend fun postMessage(channelId: Int, message: String): PostMessageResponse

    suspend fun getCreatedChannels(
    ): List<ChannelShortResponse>


    suspend fun getChannel(
        id: Int,
        page: Int,
        pageSize: Int,
    ): ChannelResponse

    suspend fun createChannel(
        name: String
    ): CreateChannelResponse
}