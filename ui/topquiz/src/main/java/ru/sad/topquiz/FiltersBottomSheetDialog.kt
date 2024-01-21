package ru.sad.topquiz

import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.BaseBottomSheetDialogFragment
import ru.sad.base.ext.onError
import ru.sad.base.ext.onLoading
import ru.sad.base.ext.onSuccess
import ru.sad.base.simple.SimpleListBottomSheet
import ru.sad.domain.model.quiz.QuizCategory
import ru.sad.domain.model.simple.SimpleTypeScreenEnum
import ru.sad.topquiz.adapter.CategoriesAdapter
import ru.sad.topquiz.databinding.DialogFiltersBinding

@AndroidEntryPoint
class FiltersBottomSheetDialog : BaseBottomSheetDialogFragment<DialogFiltersBinding>() {

    override val bindingInflater: (LayoutInflater) -> DialogFiltersBinding =
        DialogFiltersBinding::inflate

    override val tagDialog: String = "FiltersBottomSheetDialog"

    private val viewModel: TopQuizViewModel by viewModels()

    private lateinit var categoriesDialog: BottomSheetDialogFragment

    override fun setup() {
        bindListeners()
        observeData()

        viewModel.loadCategories()

        categoriesDialog =
            SimpleListBottomSheet
                .Builder<QuizCategory>()
                .setData(title = "HAHA", data = emptyList(), type = SimpleTypeScreenEnum.TOP_QUIZ)
                .build()
    }

    private fun observeData() {
        viewModel.quizSelectedCategoryLive.observe(viewLifecycleOwner) {
            binding.autoCategories.text = it?.name
        }
    }

    private fun bindListeners() {
        binding.ivBtnClose.setOnClickListener { dismiss() }
        binding.autoCategories.setOnClickListener {
            if (this::categoriesDialog.isInitialized) {
                openDialog(categoriesDialog as BaseBottomSheetDialogFragment<*>)
            }
        }
    }

    class Builder {

        fun build(bundles: Bundle = bundleOf()): BottomSheetDialogFragment {
            return FiltersBottomSheetDialog().apply {
                arguments = bundles
            }
        }
    }
}