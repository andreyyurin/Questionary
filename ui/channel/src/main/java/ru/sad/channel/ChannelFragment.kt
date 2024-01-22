package ru.sad.channel

import android.os.Bundle
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.BaseFragment
import ru.sad.base.ext.initVertical
import ru.sad.base.ext.onError
import ru.sad.base.ext.onLoading
import ru.sad.base.ext.onSuccess
import ru.sad.channel.databinding.FragmentChannelBinding
import ru.sad.channel.item.ItemMessage

@AndroidEntryPoint
class ChannelFragment : BaseFragment<FragmentChannelBinding>() {

    override val isShowBottomMenu: Boolean = false

    private val adapter: GroupAdapter<GroupieViewHolder> by lazy {
        GroupAdapter()
    }

    companion object {
        private const val CHANNEL_ID = "CHANNEL_ID"
    }

    private val viewModel: ChannelViewModel by viewModels()

    override val bindingInflater: (LayoutInflater) -> FragmentChannelBinding =
        FragmentChannelBinding::inflate

    override fun setup(savedInstanceState: Bundle?) {
        setupRecycler()
        loadChannel()
        observeData()
        bindListeners()
    }

    private fun bindListeners() {
        val channelId = arguments?.getInt(CHANNEL_ID, -1) ?: -1

        binding.ivSendMessage.setOnClickListener {
            viewModel.postMessage(channelId, binding.etMessage.text.toString())
        }
    }

    private fun setupRecycler() {
        binding.recyclerMessages.initVertical(adapter)
    }

    private fun loadChannel() {
        val channelId = arguments?.getInt(CHANNEL_ID, -1) ?: return

        viewModel.loadChannel(channelId, 0, 10)
    }

    private fun observeData() {
        viewModel.channelLive.observe(viewLifecycleOwner) {
            with(it) {
                onLoading() {
                    showLoading()
                }
                onError {
                    hideLoading()
                    showError(this)
                }
                onSuccess {
                    hideLoading()

                    binding.etMessage.isVisible = this.isAuthor

                    binding.tvTitle.text = this.name

                    adapter.updateAsync(this.messages.map { message ->
                        ItemMessage(message.message)
                    })
                }
            }
        }

        viewModel.postMessageLive.observe(viewLifecycleOwner) {
            with(it) {
                onError(::showError)
                onSuccess {
                    loadChannel()
                    binding.etMessage.setText("")
                }
            }
        }
    }
}