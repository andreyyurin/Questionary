package ru.sad.subscriptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.BaseFragment
import ru.sad.base.ext.initVertical
import ru.sad.base.ext.load
import ru.sad.base.ext.onError
import ru.sad.base.ext.onLoading
import ru.sad.base.ext.onSuccess
import ru.sad.base.ext.onTextChanged
import ru.sad.base.ext.smoothHide
import ru.sad.base.ext.smoothShow
import ru.sad.subscriptions.adapter.SubscriptionAdapter
import ru.sad.subscriptions.databinding.FragmentSubscriptionBinding
import ru.sad.subscriptions.dialog.createProfileDialog
import ru.sad.subscriptions.followers.FollowersFragment
import ru.sad.subscriptions.following.FollowingFragment
import ru.sad.subscriptions.item.ItemUser

@AndroidEntryPoint
class SubscriptionFragment : BaseFragment<FragmentSubscriptionBinding>() {

    override val isShowBottomMenu: Boolean = true

    private val viewModel: SubscriptionViewModel by viewModels()

    private lateinit var subscriptionAdapter: SubscriptionAdapter

    private val searchAdapter: GroupAdapter<GroupieViewHolder> by lazy {
        GroupAdapter<GroupieViewHolder>()
    }

    private val followingFragment: FollowingFragment by lazy {
        FollowingFragment()
    }

    private val followersFragment: FollowersFragment by lazy {
        FollowersFragment()
    }

    override val bindingInflater: (LayoutInflater) -> FragmentSubscriptionBinding =
        FragmentSubscriptionBinding::inflate

    override fun setup(savedInstanceState: Bundle?) {
        setupViewPager()
        setupRecycler()
        observeData()
        bindListenersSearch()
    }

    fun updateFollowingFragment() {
        followingFragment.update()
    }

    private fun bindListenersSearch() {
        binding.etSearch.onTextChanged {
            if (it.isEmpty()) {
                binding.viewPager.smoothShow()
                binding.layoutTabs.smoothShow()
                binding.nestedSearchUsers.smoothHide()

                context?.let { ctx ->
                    binding.ivSearch.setImageDrawable(
                        AppCompatResources.getDrawable(
                            ctx,
                            R.drawable.ic_search
                        )
                    )
                }

                binding.ivSearch.setOnClickListener(null)

                searchAdapter.clear()
            } else {
                binding.nestedSearchUsers.smoothShow()
                binding.viewPager.smoothHide()
                binding.layoutTabs.smoothHide()

                context?.let { ctx ->
                    binding.ivSearch.setImageDrawable(
                        AppCompatResources.getDrawable(
                            ctx,
                            ru.sad.base.R.drawable.ic_cancel_dialog
                        )
                    )
                }

                binding.ivSearch.setOnClickListener {
                    binding.etSearch.setText("")
                }

                viewModel.search(it)
            }
        }
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
                    followersFragment.update()
                    followingFragment.update()
                }
                onLoading {
                    this?.first?.loading()
                }
            }
        }

        viewModel.searchLive.observe(viewLifecycleOwner) {
            with(it) {
                onSuccess {
                    searchAdapter.updateAsync(this.map { user ->
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
                                    this@SubscriptionFragment.createProfileDialog(
                                        this.user.id,
                                        this.user.username
                                    ) { id ->
                                        viewModel.openProfile(id)
                                    }
                                }
                            }
                    })
                }
            }
        }
    }

    private fun setupRecycler() {
        binding.recyclerSearch.initVertical(searchAdapter)
    }

    private fun setupViewPager() {
        this.activity?.let {
            subscriptionAdapter =
                SubscriptionAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)

            binding.viewPager.offscreenPageLimit = 2
            binding.viewPager.adapter = subscriptionAdapter

            with(binding.tabLayoutSubscriptions) {
                addTab(newTab().setText(getString(R.string.subscription_tab_followers)))
                addTab(newTab().setText(getString(R.string.subscription_tab_following)))

                tabRippleColor = null

                addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabReselected(tab: TabLayout.Tab?) {
                    }

                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        if (tab != null) binding.viewPager.setCurrentItem(tab.position, true)
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {

                    }
                })
            }

            binding.viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.tabLayoutSubscriptions.selectTab(
                        binding.tabLayoutSubscriptions.getTabAt(
                            position
                        )
                    )
                }
            })
            subscriptionAdapter.setupFragments(followersFragment, followingFragment)
        }
    }
}