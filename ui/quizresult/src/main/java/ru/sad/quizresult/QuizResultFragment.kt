package ru.sad.quizresult

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.BaseFragment
import ru.sad.base.ext.onError
import ru.sad.base.ext.onLoading
import ru.sad.base.ext.onSuccess
import ru.sad.base.ext.smoothShow
import ru.sad.domain.model.quiz.CheckResultsResponse
import ru.sad.quizresult.databinding.FragmentQuizResultBinding

@AndroidEntryPoint
class QuizResultFragment : BaseFragment<FragmentQuizResultBinding>() {

    override val isShowBottomMenu: Boolean = false

    companion object {
        private const val QUIZ_RESULT = "QUIZ_RESULT"
        private const val QUIZ_ID = "QUIZ_ID"
    }

    private val viewModel: QuizResultViewModel by viewModels()

    override val bindingInflater: (LayoutInflater) -> FragmentQuizResultBinding =
        FragmentQuizResultBinding::inflate

    override fun setup(savedInstanceState: Bundle?) {
        bindListeners()
        setupData()
        observeData()
    }

    private fun setupData() {
        val resultData = requireArguments().getSerializable(QUIZ_RESULT) as CheckResultsResponse

        val percentOfTrue =
            (resultData.trueQuestions?.toFloat() ?: 1f) /
                    (resultData.totalQuestions?.toFloat() ?: 1f)

        //  calcBackgroundColor(percentOfTrue)
        animateProgress(
            (percentOfTrue * 100).toInt(),
            resultData.totalQuestions ?: 1,
            resultData.trueQuestions ?: 1
        )
    }

    private fun observeData() {
        viewModel.rateQuizLive.observe(viewLifecycleOwner) {
            with(it) {
                onLoading {
                    showLoading()
                }
                onSuccess {
                    hideLoading()
                }
                onError {
                    hideLoading()
                }
            }
        }
    }

    private fun animateProgress(result: Int, totalCount: Int, completeCount: Int) {
        val value = if (result < 10) 10 else result

        val animation = ObjectAnimator.ofInt(
            binding.progressBarResult,
            "progress",
            binding.progressBarResult.progress,
            value
        )

        animation.duration = 500
        animation.setAutoCancel(true)
        animation.interpolator = DecelerateInterpolator()

        animation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                showResultText(totalCount, completeCount)
                showRateView()
            }

            override fun onAnimationRepeat(animation: Animator) {

            }

            override fun onAnimationStart(animation: Animator) {
            }
        })
        animation.start()
    }

    private fun showResultText(totalCount: Int, completeCount: Int) {
        binding.tvResultScore.text =
            getString(R.string.quiz_result_complete_score, completeCount, totalCount)
        binding.tvResultScore.smoothShow()
    }

    private fun showRateView() {
        binding.feedback.smoothShow()
        binding.feedback.setText(getString(R.string.quiz_result_rate))
        binding.feedback.setOnStarListener {
            binding.btnFinish.smoothShow()
        }
        binding.btnFinish.setOnClickListener {
            viewModel.sendRate(requireArguments().getLong(QUIZ_ID), binding.feedback.maxStar)
        }
    }

    private fun bindListeners() {
        /* */
    }
}