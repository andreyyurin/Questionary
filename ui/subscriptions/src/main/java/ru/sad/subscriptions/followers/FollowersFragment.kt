package ru.sad.subscriptions.followers

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
import ru.sad.subscriptions.SubscriptionFragment
import ru.sad.subscriptions.databinding.FragmentFollowersBinding
import ru.sad.subscriptions.dialog.createProfileDialog
import ru.sad.subscriptions.item.ItemUser

@AndroidEntryPoint
class FollowersFragment : BaseFragment<FragmentFollowersBinding>() {

    override val isShowBottomMenu = null

    override val bindingInflater: (LayoutInflater) -> FragmentFollowersBinding =
        FragmentFollowersBinding::inflate

    private val viewModel: FollowersViewModel by viewModels()

    private val adapter: GroupAdapter<GroupieViewHolder> by lazy {
        GroupAdapter<GroupieViewHolder>()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupAdapter()
        observeData()
        loadFollowers()
    }

    private fun loadFollowers() {
        viewModel.loadSubscriptions()
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
                    passUpdateToSubscriptionFragment()
                }
                onLoading {
                    this?.first?.loading()
                }
            }
        }

        viewModel.subscriptionsLive.observe(viewLifecycleOwner) {
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
                                    this@FollowersFragment.createProfileDialog(
                                        this.user.id,
                                        this.user.username
                                    ) { id ->
                                        viewModel.openProfile(id)
                                    }
                                }
                            }
                    })

                    if(this.isEmpty()) binding.layoutEmptyView.isVisible = true
                }
                onError {
                    binding.layoutEmptyView.isVisible = true
                }
            }
        }
    }

    private fun passUpdateToSubscriptionFragment() {
        (parentFragment as? SubscriptionFragment)?.updateFollowingFragment()
    }

    private fun setupAdapter() {
        binding.recyclerFollowers.initVertical(adapter)
    }

    fun update() {
        viewModel.loadSubscriptions()
    }
}