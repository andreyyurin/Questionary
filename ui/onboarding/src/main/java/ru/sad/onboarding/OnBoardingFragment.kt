package ru.sad.onboarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.AnimationEdge
import ru.sad.base.base.BaseFragment
import ru.sad.base.ext.observeAndClear
import ru.sad.base.ext.onSuccess
import ru.sad.onboarding.adapter.OnBoardingPagerAdapter
import ru.sad.onboarding.databinding.FragmentOnBoardingBinding

@AndroidEntryPoint
class OnBoardingFragment : BaseFragment<FragmentOnBoardingBinding>() {

    private val viewModel: OnBoardingViewModel by activityViewModels()

    private val adapter: OnBoardingPagerAdapter by lazy {
        OnBoardingPagerAdapter(childFragmentManager)
    }

    override val bindingInflater: (LayoutInflater) -> FragmentOnBoardingBinding =
        FragmentOnBoardingBinding::inflate

    override fun setup(savedInstanceState: Bundle?) {
        bindListeners()
        setupViewPager()
        observeData()
        addOnPageListener()
    }

    private fun observeData() {
//        viewModel.transitionLive.observeAndClear(viewLifecycleOwner) {
//            with(it) {
//                onSuccess {
//                    addOnPageListener()
//                }
//            }
//        }

        viewModel.onButtonClickLive.observe(viewLifecycleOwner) {
            val currentItemPosition = binding.viewPager.currentItem

            if (it == true && currentItemPosition + 1 < adapter.count) {
                binding.viewPager.setCurrentItem(currentItemPosition + 1, true)
            } else if (currentItemPosition == adapter.count - 1) {
                checkPushes()
                viewModel.openLogin()
            }
        }
    }

    private fun setupViewPager() {
        binding.viewPager.offscreenPageLimit = 4
        binding.viewPager.adapter = adapter
    }

    private fun addOnPageListener() {
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                val nextItem = position + 1

                val currentPage = (adapter.getFragment(position))

                currentPage.animate(AnimationEdge.LEFT, 1 - positionOffset)

                if (nextItem < adapter.count) {
                    adapter.getFragment(nextItem).animate(AnimationEdge.RIGHT, positionOffset)
                }
            }

            override fun onPageSelected(position: Int) {

            }
        })
    }

    private fun bindListeners() {
        binding.imageViewClose.setOnClickListener {
            viewModel.openLogin()
        }
    }
}