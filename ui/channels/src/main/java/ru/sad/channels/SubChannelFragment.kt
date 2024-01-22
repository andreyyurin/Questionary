package ru.sad.channels

import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.BaseFragment
import ru.sad.base.ext.initVertical
import ru.sad.base.ext.onError
import ru.sad.base.ext.onLoading
import ru.sad.base.ext.onSuccess
import ru.sad.channels.databinding.FragmentSubChannelBinding
import ru.sad.channels.item.ItemChannel

@AndroidEntryPoint
class SubChannelFragment : BaseFragment<FragmentSubChannelBinding>() {

    companion object {
        private const val IS_SUBSCRIBED_SCREEN = "IS_SUBSCRIBED_SCREEN"

        fun newInstance(isSubscribedScreen: Boolean) = SubChannelFragment().apply {
            arguments = bundleOf(IS_SUBSCRIBED_SCREEN to isSubscribedScreen)
        }
    }

    private val channelsAdapter: GroupAdapter<GroupieViewHolder> by lazy {
        GroupAdapter<GroupieViewHolder>()
    }

    override val isShowBottomMenu: Boolean = true

    override val bindingInflater: (LayoutInflater) -> FragmentSubChannelBinding =
        FragmentSubChannelBinding::inflate

    private val viewModel: ChannelsViewModel by viewModels()

    override fun setup(savedInstanceState: Bundle?) {
        setupAdapter()
        observeData()

        loadData()
    }

    fun update() {
        loadData()
    }

    private fun setupAdapter() {
        binding.recyclerChannels.initVertical(channelsAdapter)
    }

    private fun loadData() {
        val isSubscribed = arguments?.getBoolean(IS_SUBSCRIBED_SCREEN, false) ?: false

        when {
            isSubscribed -> viewModel.loadSubscribedChannels()
            else -> viewModel.loadCreatedChannels()
        }
    }

    private fun observeData() {
        viewModel.channelsLive.observe(viewLifecycleOwner) {
            with(it) {
                onError {
                    showError(this)
                    hideLoading()
                }
                onSuccess {
                    hideLoading()
                    channelsAdapter.updateAsync(
                        this.map { response ->
                            ItemChannel(response).apply {
                                itemClicks = {
                                    viewModel.openChannelScreen(response.id)
                                }
                            }
                        }
                    )
                }
                onLoading {
                    showLoading()
                }
            }
        }
    }
}