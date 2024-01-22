package ru.sad.channels

import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.sad.base.base.BaseFragmentViewModel
import ru.sad.base.ext.State
import ru.sad.base.ext.error
import ru.sad.base.ext.loading
import ru.sad.base.ext.postSuccess
import ru.sad.base.navigation.NavigationKey
import ru.sad.data.repository.channels.ChannelsRepositoryImpl
import ru.sad.domain.model.channels.ChannelShortResponse
import ru.sad.domain.model.channels.CreateChannelResponse
import javax.inject.Inject

@HiltViewModel
class ChannelsViewModel @Inject constructor(
    private val channelsRepository: ChannelsRepositoryImpl
) : BaseFragmentViewModel() {

    companion object {
        private const val CHANNEL_ID = "CHANNEL_ID"
    }

    val channelsLive = MutableLiveData<State<List<ChannelShortResponse>>>()

    val createChannelLive = MutableLiveData<State<CreateChannelResponse>>()

    fun loadSubscribedChannels() {
        launchIO({
            channelsLive.error(it)
        }) {
            channelsLive.loading()

            channelsLive.postSuccess(channelsRepository.getSubscribedChannels(0, 10))
        }
    }

    fun loadCreatedChannels() {
        launchIO(channelsLive::error) {
            channelsLive.loading()

            channelsLive.postSuccess(channelsRepository.getCreatedChannels())
        }
    }

    fun createChannel(name: String) {
        launchIO({
            createChannelLive.error(it)
        }) {
            createChannelLive.loading()
            createChannelLive.postSuccess(channelsRepository.createChannel(name))
        }
    }

    fun openChannelScreen(id: Int) {
        navigate(NavigationKey.CHANNEL_SCREEN, bundleOf(CHANNEL_ID to id))
    }
}