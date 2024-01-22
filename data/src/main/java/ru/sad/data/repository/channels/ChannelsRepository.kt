package ru.sad.data.repository.channels

import android.app.Application
import ru.sad.data.api.QuestionaryApi
import ru.sad.data.extensions.withContext
import ru.sad.data.extensions.withContextSingle
import ru.sad.domain.model.channels.ChannelResponse
import ru.sad.domain.model.channels.ChannelShortResponse
import ru.sad.domain.model.channels.CreateChannelRequest
import ru.sad.domain.model.channels.CreateChannelResponse
import ru.sad.domain.model.channels.PostMessageRequest
import ru.sad.domain.model.channels.PostMessageResponse

class ChannelsRepository(
    private val application: Application,
    private val api: QuestionaryApi
) : ChannelsRepositoryImpl {

    override suspend fun getSubscribedChannels(
        page: Int,
        pageSize: Int
    ): List<ChannelShortResponse> = withContext {
        api.getSubscribedChannels(page, pageSize)
    }

    override suspend fun postMessage(channelId: Int, message: String): PostMessageResponse =
        withContextSingle {
            api.postMessage(PostMessageRequest(channelId, message))
        }

    override suspend fun getCreatedChannels(): List<ChannelShortResponse> = withContext {
        api.getCreatedChannels()
    }

    override suspend fun getChannel(id: Int, page: Int, pageSize: Int): ChannelResponse =
        withContextSingle {
            api.getChannel(id, page, pageSize)
        }

    override suspend fun createChannel(name: String): CreateChannelResponse = withContextSingle {
        api.createChannel(CreateChannelRequest(name))
    }
}