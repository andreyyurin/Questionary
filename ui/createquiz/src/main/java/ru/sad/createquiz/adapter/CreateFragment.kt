package ru.sad.createquiz.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.BaseFragment
import ru.sad.base.base.BaseItem
import ru.sad.base.ext.addAll
import ru.sad.base.ext.initVertical
import ru.sad.createquiz.CreateQuizViewModel
import ru.sad.createquiz.R
import ru.sad.createquiz.databinding.FragmentCreateBinding
import ru.sad.createquiz.item.QuestionAddItem
import ru.sad.createquiz.item.QuestionItem
import ru.sad.createquiz.item.QuestionTitleItem
import ru.sad.data.exceptions.QuestionaryException
import ru.sad.domain.model.quiz.QuizAnswer
import ru.sad.domain.model.quiz.QuizQuestion

@AndroidEntryPoint
class CreateFragment : BaseFragment<FragmentCreateBinding>() {

    companion object {
        private const val PAGE_NUMBER = "PAGE_NUMBER"

        fun newInstance(pageNumber: Int) = CreateFragment().apply {
            arguments = bundleOf(PAGE_NUMBER to pageNumber)
        }
    }

    private lateinit var adapter: GroupAdapter<GroupieViewHolder>

    private val viewModel: CreateQuizViewModel by activityViewModels()

    private var lastSelectedItem: QuestionItem? = null

    override val isShowBottomMenu: Boolean? = null

    override val isSaveState: Boolean = true

    override val bindingInflater: (LayoutInflater) -> FragmentCreateBinding =
        FragmentCreateBinding::inflate

    override fun setup(savedInstanceState: Bundle?) {
        if (!isRestore) {
            initAdapter()
            setupStartItems()
        }
    }

    private fun initAdapter() {
        adapter = GroupAdapter<GroupieViewHolder>()
        binding.recyclerQuestions.initVertical(adapter)
    }

    private fun setupStartItems() {
        val questionTitleItem = QuestionTitleItem(::updateQuestionTitle)

        viewModel.currentQuiz?.questions?.get(requireArguments().getInt(PAGE_NUMBER))?.answers?.add(
            QuizAnswer(false, "")
        )

        val currentPosition =
            viewModel.currentQuiz?.questions?.get(requireArguments().getInt(PAGE_NUMBER))?.answers?.size

        val questionAddItem = createQuestionAddItem()

        adapter.addAll(
            questionTitleItem,
            QuestionItem(
                (currentPosition ?: 1) - 1,
                ::openMenu,
                ::updateQuestionAnswer
            ),
            questionAddItem
        )
    }

    private fun createQuestionAddItem(): BaseItem = QuestionAddItem {
        viewModel.currentQuiz?.questions?.get(requireArguments().getInt(PAGE_NUMBER))?.answers?.add(
            QuizAnswer(false, "")
        )

        val currentPosition =
            viewModel.currentQuiz?.questions?.get(requireArguments().getInt(PAGE_NUMBER))?.answers?.size

        adapter.add(
            adapter.itemCount - 1,
            QuestionItem(
                (currentPosition ?: 1) - 1,
                ::openMenu,
                ::updateQuestionAnswer
            )
        )
    }

    private fun updateQuestionTitle(title: String) {
        viewModel.setupQuestionTitle(title, requireArguments().getInt(PAGE_NUMBER))
    }

    private fun updateQuestionAnswer(title: String, position: Int) {
        viewModel.setupQuestionAnswer(title, requireArguments().getInt(PAGE_NUMBER), position)
    }

    private fun openMenu(item: QuestionItem, view: View, currentPosition: Int) {
        createDropdownDialog(
            Pair(getString(R.string.create_quiz_item_picker_select_true)) {
                lastSelectedItem?.let {
                    it.unselectTrue()
                    viewModel.selectAnswer(
                        false,
                        requireArguments().getInt(PAGE_NUMBER),
                        it.currentPosition
                    )
                }
                lastSelectedItem = item
                viewModel.selectAnswer(
                    true,
                    requireArguments().getInt(PAGE_NUMBER),
                    currentPosition
                )
                item.selectTrue()
            },
            Pair(getString(R.string.create_quiz_item_picker_remove)) {
                if (lastSelectedItem == item) {
                    lastSelectedItem = null
                }
                item.clear()
                viewModel.removeAnswer(requireArguments().getInt(PAGE_NUMBER), currentPosition)
                adapter.remove(item)
                updateItems(currentPosition)
            }, view = view
        )
    }

    private fun updateItems(removedItemIndex: Int) {
        val count = adapter.itemCount

        for (index in (removedItemIndex + 1) until count - 1) {
            (adapter.getItem(index) as QuestionItem).currentPosition--
        }
    }
}