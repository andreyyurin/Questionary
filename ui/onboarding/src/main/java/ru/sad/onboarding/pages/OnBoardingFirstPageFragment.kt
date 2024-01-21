package ru.sad.onboarding.pages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.ViewTreeObserver.OnPreDrawListener
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionLayout.TransitionListener
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.AnimationEdge
import ru.sad.base.base.BaseFragment
import ru.sad.base.ext.postSuccess
import ru.sad.onboarding.OnBoardingViewModel
import ru.sad.onboarding.R
import ru.sad.onboarding.databinding.FragmentPageFirstBinding

@AndroidEntryPoint
class OnBoardingFirstPageFragment : BaseFragment<FragmentPageFirstBinding>() {

    private val viewModel: OnBoardingViewModel by activityViewModels()

    private val rightScene = R.xml.onboarding_scene_page_1_right
    private val leftScene = R.xml.onboarding_scene_page_1_left

    override val bindingInflater: (LayoutInflater) -> FragmentPageFirstBinding =
        FragmentPageFirstBinding::inflate

    private val transitionListener = object : TransitionListener {
        override fun onTransitionChange(
            motionLayout: MotionLayout?,
            startId: Int,
            endId: Int,
            progress: Float
        ) {
        }

        override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
            viewModel.transitionLive.postSuccess(true)
            binding.motionLayoutFirst.loadLayoutDescription(leftScene)
            animate(AnimationEdge.LEFT, 1.0f)
            binding.btnContinue.requestLayout()
            binding.motionLayoutFirst.removeTransitionListener(this)
        }

        override fun onTransitionStarted(
            motionLayout: MotionLayout?,
            startId: Int,
            endId: Int
        ) {

        }

        override fun onTransitionTrigger(
            motionLayout: MotionLayout?,
            triggerId: Int,
            positive: Boolean,
            progress: Float
        ) {

        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        //enterAnimation()
        bindListeners()
    }

    private fun bindListeners() {
        binding.btnContinue.setOnClickListener {
            viewModel.onButtonClickLive.postValue(true)
        }
    }

    override fun animate(edge: AnimationEdge, percent: Float) {
        with(binding.motionLayoutFirst) {
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

    // не работает как надо, кнопка сьезжает
    private fun enterAnimation() {
        binding.motionLayoutFirst.addTransitionListener(transitionListener)
        binding.motionLayoutFirst.loadLayoutDescription(R.xml.onboarding_scene_page_1_enter)
        binding.motionLayoutFirst.transitionToEnd()
    }
}