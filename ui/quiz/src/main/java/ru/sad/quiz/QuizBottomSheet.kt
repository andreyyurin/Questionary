package ru.sad.quiz

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.BaseBottomSheetDialogFragment
import ru.sad.base.ext.attach
import ru.sad.base.ext.observeAndClear
import ru.sad.base.ext.onError
import ru.sad.base.ext.onLoading
import ru.sad.base.ext.onSuccess
import ru.sad.quiz.adapter.QuizAdapter
import ru.sad.quiz.adapter.QuizPageFragment
import ru.sad.quiz.databinding.FragmentQuizBinding


@AndroidEntryPoint
class QuizBottomSheet : BaseBottomSheetDialogFragment<FragmentQuizBinding>() {

    companion object {
        private const val QUIZ_ID = "QUIZ_ID"
    }

    private lateinit var adapter: QuizAdapter

    private val viewModel: QuizViewModel by activityViewModels()

    override val tagDialog: String
        get() = "QuizBottomSheet"

    override val bindingInflater: (LayoutInflater) -> FragmentQuizBinding =
        FragmentQuizBinding::inflate

    override fun setup() {
        initAdapter()
        observeData()

        showFullscreen()

        val quizId = requireArguments().getInt(QUIZ_ID)
        viewModel.loadQuiz(quizId)

        bindListeners()
    }

    private fun bindListeners() {
        binding.ivSave.setOnClickListener { showPicker() }
    }

    private fun showPicker() {
        createPickerDialog(
            Pair(getString(R.string.quiz_exit_title)) {
            },
            Pair(getString(R.string.quiz_exit_continue)) {
            },
            Pair(getString(R.string.quiz_exit_cancel)) {
                viewModel.checkResults()
            }
        )
    }

    private fun initAdapter() {
        adapter = QuizAdapter(this)

        binding.viewPager.adapter = adapter

        binding.tabDots attach binding.viewPager
    }

    private fun observeData() {
        viewModel.quizLive.observeAndClear(viewLifecycleOwner) {
            with(it) {
                onLoading {
                    showLoading()
                }
                onSuccess {
                    hideLoading()

                    binding.tvTitle.text = this.title

                    val questionFragments = this.questions.mapIndexed { index, question ->
                        QuizPageFragment.newInstance(question, index)
                    }

                    binding.viewPager.offscreenPageLimit = questionFragments.size

                    adapter.addFragments(questionFragments)
                }
                onError {
                    hideLoading()
                }
            }
        }

        viewModel.checkResultsLive.observeAndClear(viewLifecycleOwner) {
            with(it) {
                onLoading {
                    showLoading()
                }
                onSuccess {
                    val quizId = requireArguments().getInt(QUIZ_ID)
                    hideLoading()
                    dismiss()
                    viewModel.openResultScreen(this, quizId.toLong())
                    viewModel.clear()
                }
                onError {
                    hideLoading()
                }
            }
        }
    }

    class Builder {

        fun build(bundles: Bundle = bundleOf()): BottomSheetDialogFragment {
            return QuizBottomSheet().apply {
                arguments = bundles
            }
        }
    }
}