package ru.sad.channels

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.BaseFragment
import ru.sad.base.ext.onError
import ru.sad.base.ext.onLoading
import ru.sad.base.ext.onSuccess
import ru.sad.channels.adapter.SubscriptionAdapter
import ru.sad.channels.databinding.FragmentChannelsBinding

@AndroidEntryPoint
class ChannelsFragment : BaseFragment<FragmentChannelsBinding>() {

    override val bindingInflater: (LayoutInflater) -> FragmentChannelsBinding =
        FragmentChannelsBinding::inflate

    override val isShowBottomMenu: Boolean = true

    private lateinit var subscriptionAdapter: SubscriptionAdapter

    private val viewModel: ChannelsViewModel by viewModels()

    private val subscribedChannelsFragment: SubChannelFragment by lazy {
        SubChannelFragment.newInstance(true)
    }

    private val createdChannelsFragment: SubChannelFragment by lazy {
        SubChannelFragment.newInstance(false)
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupViewPager()
        observeData()
        bindListeners()
    }

    private fun bindListeners() {
        binding.ivCreateChannel.setOnClickListener {
            viewModel.createChannel("TEST")
        }
    }

    private fun observeData() {
        viewModel.createChannelLive.observe(viewLifecycleOwner) {
            with(it) {
                onError {
                    hideLoading()
                    showError(this)
                }
                onLoading() {
                    showLoading()
                }
                onSuccess {
                    hideLoading()
                    createdChannelsFragment.update()
                }
            }
        }
    }

    private fun setupViewPager() {
        this.activity?.let {
            subscriptionAdapter =
                SubscriptionAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)

            binding.viewPager.offscreenPageLimit = 2
            binding.viewPager.adapter = subscriptionAdapter

            with(binding.tabLayoutSubscriptions) {
                addTab(newTab().setText(getString(R.string.channels_tab_created)))
                addTab(newTab().setText(getString(R.string.channels_tab_subscribed)))

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
            subscriptionAdapter.setupFragments(createdChannelsFragment, subscribedChannelsFragment)
        }
    }
}