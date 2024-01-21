package ru.sad.subscriptions.following

import android.os.Bundle
import android.view.LayoutInflater
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
import ru.sad.subscriptions.databinding.FragmentFollowingBinding
import ru.sad.subscriptions.dialog.createProfileDialog
import ru.sad.subscriptions.item.ItemUser

@AndroidEntryPoint
class FollowingFragment : BaseFragment<FragmentFollowingBinding>() {

    override val isShowBottomMenu = null

    override val bindingInflater: (LayoutInflater) -> FragmentFollowingBinding =
        FragmentFollowingBinding::inflate

    private val viewModel: FollowingViewModel by viewModels()

    private val adapter: GroupAdapter<GroupieViewHolder> by lazy {
        GroupAdapter<GroupieViewHolder>()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupAdapter()
        observeData()
        loadFollowings()
    }

    private fun loadFollowings() {
        viewModel.loadFollowingUsers()
    }

    private fun observeData() {
        viewModel.actionLive.observe(viewLifecycleOwner) {
            with(it) {
                onError({
                    this?.first?.updateText()
                }, {
                    showError(this)
                })
                onSuccess {
                    this.first.user.subscribed = this.first.user.subscribed != true
                    this.first.updateText()
                }
                onLoading {
                    this?.first?.loading()
                }
            }
        }

        viewModel.followingLive.observe(viewLifecycleOwner) {
            with(it) {
                onLoading()
                onSuccess {
                    adapter.updateAsync(this.map { user ->
                        ItemUser(
                            user,
                            onFollowClick = { item ->
                                viewModel.follow(user.id, item)
                            },
                            onUnFollowClick = { item ->
                                viewModel.unfollow(user.id, item)
                            })
                            .apply {
                                itemClicks = {
                                    this@FollowingFragment.createProfileDialog(
                                        this.user.id,
                                        this.user.username
                                    ) { id ->
                                        viewModel.openProfile(id)
                                    }
                                }
                            }
                    })

                    binding.layoutEmptyView.isVisible = this.isEmpty()
                }
                onError {
                    binding.layoutEmptyView.isVisible = true
                }
            }
        }
    }

    private fun setupAdapter() {
        binding.recyclerFollowing.initVertical(adapter)
    }

    fun update() {
        viewModel.loadFollowingUsers()
    }
}