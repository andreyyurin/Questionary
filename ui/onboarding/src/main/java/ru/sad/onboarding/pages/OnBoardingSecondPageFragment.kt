package ru.sad.onboarding.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.AnimationEdge
import ru.sad.base.base.BaseFragment
import ru.sad.onboarding.OnBoardingViewModel
import ru.sad.onboarding.R
import ru.sad.onboarding.databinding.FragmentPageSecondBinding

@AndroidEntryPoint
class OnBoardingSecondPageFragment : BaseFragment<FragmentPageSecondBinding>() {

    private val rightScene = R.xml.onboarding_scene_page_2_right
    private val leftScene = R.xml.onboarding_scene_page_2_left

    private val viewModel: OnBoardingViewModel by activityViewModels()

    override val bindingInflater: (LayoutInflater) -> FragmentPageSecondBinding =
        FragmentPageSecondBinding::inflate

    override fun setup(savedInstanceState: Bundle?) {

        bindListeners()
    }

    private fun bindListeners() {
        binding.btnContinue.setOnClickListener {
            viewModel.onButtonClickLive.postValue(true)
        }
    }

    override fun animate(edge: AnimationEdge, percent: Float) {
        with(binding.motionLayoutSecond) {
            when {
                edge == AnimationEdge.RIGHT && this.tag != AnimationEdge.RIGHT -> {
                    this.loadLayoutDescription(rightScene)
                    this.tag = AnimationEdge.RIGHT
                }

                edge == AnimationEdge.LEFT && this.tag != AnimationEdge.LEFT -> {
                    this.loadLayoutDescription(leftScene)
                    this.tag = AnimationEdge.LEFT
                }
            }

            this.progress = percent
        }
    }
}