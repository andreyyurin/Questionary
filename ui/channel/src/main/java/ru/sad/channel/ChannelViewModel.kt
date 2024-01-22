package ru.sad.channel

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.sad.base.base.BaseFragmentViewModel
import ru.sad.base.ext.State
import ru.sad.base.ext.error
import ru.sad.base.ext.loading
import ru.sad.base.ext.postSuccess
import ru.sad.data.repository.channels.ChannelsRepositoryImpl
import ru.sad.domain.model.channels.ChannelResponse
import ru.sad.domain.model.channels.PostMessageResponse
import javax.inject.Inject

@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val channelsRepository: ChannelsRepositoryImpl
) : BaseFragmentViewModel() {

    val channelLive = MutableLiveData<State<ChannelResponse>>()

    val postMessageLive = MutableLiveData<State<PostMessageResponse>>()

    fun loadChannel(id: Int, page: Int, pageSize: Int) {
        launchIO({
            channelLive.error(it)
        }) {
            channelLive.loading()
            channelLive.postSuccess(channelsRepository.getChannel(id, page, pageSize))
        }
    }

    fun postMessage(channelId: Int, message: String) {
        if (message.isEmpty()) return

        launchIO({
            postMessageLive.error(it)
        }) {
            postMessageLive.loading()
            postMessageLive.postSuccess(channelsRepository.postMessage(channelId, message))
        }
    }
}