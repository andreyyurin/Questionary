package ru.sad.quiz

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.BaseFragment
import ru.sad.base.ext.attach
import ru.sad.base.ext.onError
import ru.sad.base.ext.onLoading
import ru.sad.base.ext.onSuccess
import ru.sad.quiz.adapter.QuizAdapter
import ru.sad.quiz.adapter.QuizPageFragment
import ru.sad.quiz.databinding.FragmentQuizBinding

@AndroidEntryPoint
class QuizFragment : BaseFragment<FragmentQuizBinding>() {

    companion object {
        private const val QUIZ_ID = "QUIZ_ID"
    }

    private lateinit var adapter: QuizAdapter

    private val viewModel: QuizViewModel by viewModels()

    override val bindingInflater: (LayoutInflater) -> FragmentQuizBinding =
        FragmentQuizBinding::inflate

    override fun setup(savedInstanceState: Bundle?) {
        initAdapter()
        observeData()

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
        viewModel.quizLive.observe(viewLifecycleOwner) {
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
                    showError(this)
                }
            }
        }
    }
}