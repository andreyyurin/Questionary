package ru.sad.quiz.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.BaseFragment
import ru.sad.base.base.BaseItem
import ru.sad.base.ext.addAll
import ru.sad.base.ext.initVertical
import ru.sad.data.exceptions.QuestionaryException
import ru.sad.domain.model.quiz.QuizAnswer
import ru.sad.domain.model.quiz.QuizQuestion
import ru.sad.quiz.QuizViewModel
import ru.sad.quiz.R
import ru.sad.quiz.databinding.FragmentQuizPageBinding
import ru.sad.quiz.item.QuestionItem
import ru.sad.quiz.item.QuestionTitleItem

@AndroidEntryPoint
class QuizPageFragment : BaseFragment<FragmentQuizPageBinding>() {

    companion object {
        private const val QUIZ_QUESTION = "quiz_question"
        private const val PAGE_NUMBER = "PAGE_NUMBER"

        fun newInstance(quizQuestion: QuizQuestion, pageNumber: Int) = QuizPageFragment().apply {
            arguments = bundleOf(QUIZ_QUESTION to quizQuestion, PAGE_NUMBER to pageNumber)
        }
    }

    private val viewModel: QuizViewModel by activityViewModels()

    private val adapter: GroupAdapter<GroupieViewHolder> by lazy {
        GroupAdapter<GroupieViewHolder>()
    }

    private var lastSelectedItem: QuestionItem? = null

    override val isShowBottomMenu: Boolean? = null

    override val bindingInflater: (LayoutInflater) -> FragmentQuizPageBinding =
        FragmentQuizPageBinding::inflate

    override fun setup(savedInstanceState: Bundle?) {
        initAdapter()
        setupQuestion()
    }

    private fun initAdapter() {
        binding.recyclerQuestions.initVertical(adapter)
    }

    @SuppressLint("MissingPermission")
    private fun setupQuestion() {
        val pageNumber = requireArguments().getInt(PAGE_NUMBER)

        val question =
            viewModel.currentQuiz?.questions?.get(pageNumber) ?: return

        val questionTitleItem = QuestionTitleItem(question.question)

        adapter.updateAsync(
            listOf(questionTitleItem) +
                    question.answers.mapIndexed { index, answer ->
                        QuestionItem(answer, index) { item ->
                            context?.let {
                                val vibrator = it.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                vibrator.vibrate(10)
                            }
                            if (lastSelectedItem != null && lastSelectedItem != item) {
                                lastSelectedItem?.unselectTrue()
                                lastSelectedItem?.let {
                                    question.answers[it.index].isTrue = false
                                }
                            }

                            if (lastSelectedItem != item) {
                                item.selectTrue()
                                question.answers[index].isTrue = true
                                lastSelectedItem = item
                            }
                        }
                    }
        )
    }
}